#!/usr/bin/env python3
"""
Generador de Columnas para Tablas del proyecto MELANBIDE
Automatiza la adición de nuevas columnas a tablas existentes
"""

import json
import os
import sys
import re
from datetime import datetime

def load_config():
    """Cargar configuración del proyecto"""
    config_path = os.path.join(os.path.dirname(__file__), "../config/project-config.json")
    with open(config_path, 'r', encoding='utf-8') as f:
        return json.load(f)

def find_vo_file(vo_class):
    """Encontrar archivo VO"""
    config = load_config()
    java_src_path = config['paths']['java_src']
    package_path = config['project']['base_package'].replace('.', '/')
    vo_path = os.path.join(java_src_path, package_path, "vo", f"{vo_class}.java")
    
    if os.path.exists(vo_path):
        return vo_path
    return None

def find_jsp_file(jsp_name):
    """Encontrar archivo JSP"""
    config = load_config()
    jsp_path = config['paths']['jsp_path']
    jsp_file = os.path.join(jsp_path, f"{jsp_name}.jsp")
    
    if os.path.exists(jsp_file):
        return jsp_file
    return None

def get_java_type(field_type):
    """Convertir tipo de BD a tipo Java"""
    config = load_config()
    type_mappings = config['database']['column_types']
    
    # Extraer tipo base (sin tamaño)
    base_type = field_type.split('(')[0].upper()
    return type_mappings.get(base_type, "String")

def generate_field_declaration(field_name, field_type):
    """Generar declaración de campo"""
    java_type = get_java_type(field_type)
    return f"    private {java_type} {field_name};"

def generate_getter_setter(field_name, field_type):
    """Generar getter y setter"""
    java_type = get_java_type(field_type)
    capitalized_name = field_name[0].upper() + field_name[1:]
    
    getter = f"""    public {java_type} get{capitalized_name}() {{
        return {field_name};
    }}"""
    
    setter = f"""    public void set{capitalized_name}({java_type} {field_name}) {{
        this.{field_name} = {field_name};
    }}"""
    
    return getter, setter

def add_field_to_vo(vo_file, field_name, field_type, description=""):
    """Agregar campo al VO"""
    with open(vo_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Generar declaración de campo
    field_declaration = generate_field_declaration(field_name, field_type)
    
    # Generar getters y setters
    getter, setter = generate_getter_setter(field_name, field_type)
    
    # Encontrar última declaración de campo
    field_pattern = r'(\s+private\s+\w+\s+\w+;)'
    matches = list(re.finditer(field_pattern, content))
    
    if matches:
        # Insertar después del último campo
        last_field = matches[-1]
        insert_pos = last_field.end()
        content = content[:insert_pos] + f"\n{field_declaration}" + content[insert_pos:]
    
    # Encontrar última pareja getter/setter
    getter_pattern = r'(\s+public\s+\w+\s+get\w+\(\)\s*\{[^}]+\})'
    setter_pattern = r'(\s+public\s+void\s+set\w+\([^)]+\)\s*\{[^}]+\})'
    
    # Buscar el último setter
    setter_matches = list(re.finditer(setter_pattern, content))
    if setter_matches:
        last_setter = setter_matches[-1]
        insert_pos = last_setter.end()
        content = content[:insert_pos] + f"\n\n{getter}\n\n{setter}" + content[insert_pos:]
    
    # Guardar archivo modificado
    with open(vo_file, 'w', encoding='utf-8') as f:
        f.write(content)
    
    return True

def add_column_to_table_jsp(jsp_file, column_name, column_width="100", i18n_key=None):
    """Agregar columna a tabla en JSP"""
    with open(jsp_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    if i18n_key is None:
        i18n_key = f"tabla.{column_name.lower()}"
    
    # Buscar patrón de addColumna
    column_pattern = r'(\s+\w+\.addColumna\([^)]+\);)'
    matches = list(re.finditer(column_pattern, content))
    
    if matches:
        # Insertar después de la última columna
        last_column = matches[-1]
        insert_pos = last_column.end()
        
        new_column = f'        tabla.addColumna(\'{column_width}\', \'center\', "<%=meLanbide11I18n.getMensaje(idiomaUsuario,"{i18n_key}")%>");'
        content = content[:insert_pos] + f"\n{new_column}" + content[insert_pos:]
    
    # Buscar sección de extracción de datos
    extraction_pattern = r'(String\s+\w+\s*=\s*"";[^%]+objectVO\.get\w+\(\)[^}]+})'
    matches = list(re.finditer(extraction_pattern, content, re.DOTALL))
    
    if matches:
        # Agregar extracción de datos
        last_extraction = matches[-1]
        insert_pos = last_extraction.end()
        
        capitalized_name = column_name[0].upper() + column_name[1:]
        extraction_code = f'''
                    String {column_name}="";
                    if(objectVO.get{capitalized_name}()!=null){{
                        {column_name}=objectVO.get{capitalized_name}().toString();
                    }}else{{
                        {column_name}="-";
                    }}'''
        
        content = content[:insert_pos] + extraction_code + content[insert_pos:]
    
    # Buscar asignaciones a la tabla
    assignment_pattern = r'(\s+lista\w+\[<%=indice%>\]\[\d+\]\s*=\s*[^;]+;)'
    matches = list(re.finditer(assignment_pattern, content))
    
    if matches:
        # Encontrar el índice más alto
        max_index = 0
        for match in matches:
            index_match = re.search(r'\[(\d+)\]', match.group(1))
            if index_match:
                index = int(index_match.group(1))
                max_index = max(max_index, index)
        
        # Insertar nueva asignación
        last_assignment = matches[-1]
        insert_pos = last_assignment.end()
        
        new_index = max_index + 1
        assignment_code = f"\n                    lista[<%=indice%>][{new_index}] = {column_name};"
        
        content = content[:insert_pos] + assignment_code + content[insert_pos:]
    
    # Guardar archivo modificado
    with open(jsp_file, 'w', encoding='utf-8') as f:
        f.write(content)
    
    return True

def generate_sql_alter_table(table_name, column_name, column_type, default_value=None):
    """Generar SQL para agregar columna"""
    sql = f"ALTER TABLE {table_name} ADD {column_name} {column_type}"
    
    if default_value:
        sql += f" DEFAULT {default_value}"
    
    sql += ";"
    
    return sql

def add_column_to_dao_method(dao_file, column_name, table_name, method_name=None):
    """Agregar columna a método DAO (plantilla)"""
    capitalized_name = column_name[0].upper() + column_name[1:]
    
    select_addition = f", {column_name.upper()}"
    vo_setter = f"                vo.set{capitalized_name}(rs.getString(\"{column_name.upper()}\"));"
    
    template = f"""
-- Agregar a la consulta SELECT:
{select_addition}

-- Agregar en el mapeo del ResultSet:
{vo_setter}
"""
    
    return template

def create_column_integration_guide(column_name, field_type, table_name, vo_class, jsp_name):
    """Crear guía de integración"""
    guide = f"""
# Guía de Integración para la columna '{column_name}'

## 1. Base de Datos
```sql
{generate_sql_alter_table(table_name, column_name.upper(), field_type)}
```

## 2. Value Object ({vo_class})
- ✓ Campo agregado automáticamente
- ✓ Getters y setters generados

## 3. JSP ({jsp_name})
- ✓ Columna agregada a la tabla
- ✓ Extracción de datos agregada
- ✓ Asignación a tabla agregada

## 4. DAO (MeLanbide11DAO.java)
Agregar manualmente:
{add_column_to_dao_method(None, column_name, table_name)}

## 5. Internacionalización
Agregar clave:
```
tabla.{column_name.lower()}={column_name.replace('_', ' ').title()}
```

## 6. Validaciones (si aplica)
Considerar agregar validaciones en:
- JavaScript (lado cliente)
- Java (lado servidor)

## 7. Formularios
Si la columna es editable, agregar campo en:
- JSP de nuevo registro
- JSP de modificación
"""
    
    return guide

def main():
    """Función principal"""
    if len(sys.argv) < 5:
        print("Uso: python column-generator.py <vo_class> <jsp_name> <column_name> <column_type> [table_name]")
        print("Ejemplo: python column-generator.py ContratacionVO melanbide11 telefono VARCHAR(20) MELANBIDE11_CONTRATACION")
        sys.exit(1)
    
    vo_class = sys.argv[1]
    jsp_name = sys.argv[2]
    column_name = sys.argv[3]
    column_type = sys.argv[4]
    table_name = sys.argv[5] if len(sys.argv) > 5 else f"MELANBIDE11_{vo_class.replace('VO', '').upper()}"
    
    # Si no termina en VO, agregarlo
    if not vo_class.endswith("VO"):
        vo_class += "VO"
    
    # Encontrar archivos
    vo_file = find_vo_file(vo_class)
    jsp_file = find_jsp_file(jsp_name)
    
    if not vo_file:
        print(f"❌ No se encontró el archivo VO: {vo_class}")
        sys.exit(1)
    
    if not jsp_file:
        print(f"❌ No se encontró el archivo JSP: {jsp_name}")
        sys.exit(1)
    
    print(f"📁 Archivos encontrados:")
    print(f"   VO: {vo_file}")
    print(f"   JSP: {jsp_file}")
    
    try:
        # Modificar VO
        add_field_to_vo(vo_file, column_name, column_type)
        print(f"✓ Campo '{column_name}' agregado al VO")
        
        # Modificar JSP
        add_column_to_table_jsp(jsp_file, column_name)
        print(f"✓ Columna '{column_name}' agregada al JSP")
        
        # Crear guía de integración
        guide_content = create_column_integration_guide(column_name, column_type, table_name, vo_class, jsp_name)
        
        # Crear directorio de salida
        output_dir = os.path.join(os.path.dirname(__file__), "../examples/column-" + column_name.lower())
        os.makedirs(output_dir, exist_ok=True)
        
        # Guardar guía
        guide_file = os.path.join(output_dir, f"integration-guide-{column_name}.md")
        with open(guide_file, 'w', encoding='utf-8') as f:
            f.write(guide_content)
        
        # Generar archivo SQL
        sql_file = os.path.join(output_dir, f"add-column-{column_name}.sql")
        with open(sql_file, 'w', encoding='utf-8') as f:
            f.write(generate_sql_alter_table(table_name, column_name.upper(), column_type))
        
        print(f"✓ Columna '{column_name}' integrada exitosamente")
        print(f"✓ Guía de integración: {guide_file}")
        print(f"✓ Script SQL: {sql_file}")
        
        print(f"\n📋 Próximos pasos:")
        print(f"   1. Ejecutar SQL: {sql_file}")
        print(f"   2. Modificar métodos DAO según la guía")
        print(f"   3. Agregar clave i18n: tabla.{column_name.lower()}")
        print(f"   4. Probar funcionalidad")
        
    except Exception as e:
        print(f"❌ Error procesando columna: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()