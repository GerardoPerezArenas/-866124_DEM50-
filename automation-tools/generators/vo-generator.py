#!/usr/bin/env python3
"""
Generador de Value Objects (VOs) para el proyecto MELANBIDE
Automatiza la creación de clases VO siguiendo los patrones del proyecto
"""

import json
import os
import sys
from datetime import datetime

def load_config():
    """Cargar configuración del proyecto"""
    config_path = os.path.join(os.path.dirname(__file__), "../config/project-config.json")
    with open(config_path, 'r', encoding='utf-8') as f:
        return json.load(f)

def load_template():
    """Cargar plantilla de VO"""
    template_path = os.path.join(os.path.dirname(__file__), "../templates/vo-template.java")
    with open(template_path, 'r', encoding='utf-8') as f:
        return f.read()

def generate_field(field_name, field_type, description=""):
    """Generar declaración de campo"""
    java_type = get_java_type(field_type)
    return f"    private {java_type} {field_name};"

def generate_getter_setter(field_name, field_type):
    """Generar getter y setter para un campo"""
    java_type = get_java_type(field_type)
    capitalized_name = field_name[0].upper() + field_name[1:]
    
    getter = f"""    public {java_type} get{capitalized_name}() {{
        return {field_name};
    }}"""
    
    setter = f"""    public void set{capitalized_name}({java_type} {field_name}) {{
        this.{field_name} = {field_name};
    }}"""
    
    return getter + "\n\n" + setter

def get_java_type(field_type):
    """Convertir tipo de BD a tipo Java"""
    config = load_config()
    type_mappings = config['database']['column_types']
    
    # Extraer tipo base (sin tamaño)
    base_type = field_type.split('(')[0].upper()
    return type_mappings.get(base_type, "String")

def generate_vo_class(class_name, fields, comments=""):
    """Generar clase VO completa"""
    config = load_config()
    template = load_template()
    
    # Generar package
    package = config['project']['base_package'] + ".vo"
    
    # Generar campos
    field_declarations = []
    getters_setters = []
    
    for field in fields:
        field_name = field['name']
        field_type = field['type']
        field_desc = field.get('description', '')
        
        field_declarations.append(generate_field(field_name, field_type, field_desc))
        getters_setters.append(generate_getter_setter(field_name, field_type))
    
    # Reemplazar placeholders en la plantilla
    content = template.replace("{{PACKAGE}}", package)
    content = content.replace("{{CLASS_NAME}}", class_name)
    content = content.replace("{{COMMENTS}}", comments)
    content = content.replace("{{FIELDS}}", "\n".join(field_declarations))
    content = content.replace("{{GETTERS_SETTERS}}", "\n\n".join(getters_setters))
    
    return content

def save_vo_file(class_name, content):
    """Guardar archivo VO"""
    config = load_config()
    
    # Construir ruta de destino
    java_src_path = config['paths']['java_src']
    package_path = config['project']['base_package'].replace('.', '/')
    vo_dir = os.path.join(java_src_path, package_path, "vo")
    
    # Crear directorios si no existen
    os.makedirs(vo_dir, exist_ok=True)
    
    # Guardar archivo
    file_path = os.path.join(vo_dir, f"{class_name}.java")
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    return file_path

def main():
    """Función principal"""
    if len(sys.argv) < 2:
        print("Uso: python vo-generator.py <nombre_clase> [archivo_config_campos]")
        print("Ejemplo: python vo-generator.py DocumentacionVO campos_documentacion.json")
        sys.exit(1)
    
    class_name = sys.argv[1]
    
    # Si no termina en VO, agregarlo
    if not class_name.endswith("VO"):
        class_name += "VO"
    
    # Campos por defecto si no se proporciona archivo
    default_fields = [
        {"name": "codigo", "type": "VARCHAR(20)", "description": "Código único"},
        {"name": "descripcion", "type": "VARCHAR(255)", "description": "Descripción"}
    ]
    
    fields = default_fields
    
    # Cargar campos desde archivo si se proporciona
    if len(sys.argv) > 2:
        fields_file = sys.argv[2]
        if os.path.exists(fields_file):
            with open(fields_file, 'r', encoding='utf-8') as f:
                fields = json.load(f)
    
    # Generar comentarios
    comments = f"{class_name.replace('VO', '').upper()}: Generado automáticamente el {datetime.now().strftime('%d/%m/%Y')}"
    
    # Generar clase
    content = generate_vo_class(class_name, fields, comments)
    
    # Guardar archivo
    file_path = save_vo_file(class_name, content)
    
    print(f"✓ Clase {class_name} generada exitosamente en: {file_path}")
    print(f"✓ Campos generados: {len(fields)}")

if __name__ == "__main__":
    main()