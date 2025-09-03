#!/usr/bin/env python3
"""
Generador de Pestañas (Tabs) para el proyecto MELANBIDE
Automatiza la creación de nuevas pestañas con sus respectivos JSPs y funcionalidad
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

def load_jsp_template():
    """Cargar plantilla de JSP"""
    template_path = os.path.join(os.path.dirname(__file__), "../templates/jsp-template.jsp")
    with open(template_path, 'r', encoding='utf-8') as f:
        return f.read()

def generate_javascript_functions(tab_name, vo_class):
    """Generar funciones JavaScript para la pestaña"""
    return f"""
    function pulsar{tab_name}() {{
        lanzarPopUpModal('<%=request.getContextPath()%>/PeticionModuloIntegracion.do?tarea=preparar&modulo=MELANBIDE11&operacion=cargar{tab_name}&tipo=0&nuevo=1&numExp=<%=numExpediente%>', 750, 1200, 'no', 'no', function (result) {{
            if (result === true) {{
                cargarLista{tab_name}();
            }}
        }});
    }}

    function pulsarModificar{tab_name}() {{
        if (tabla{tab_name}.selectedIndex != -1) {{
            lanzarPopUpModal('<%=request.getContextPath()%>/PeticionModuloIntegracion.do?tarea=preparar&modulo=MELANBIDE11&operacion=cargarModificar{tab_name}&tipo=0&nuevo=0&numExp=<%=numExpediente%>&id=' + lista{tab_name}[tabla{tab_name}.selectedIndex][0], 750, 1200, 'no', 'no', function (result) {{
                if (result === true) {{
                    cargarLista{tab_name}();
                }}
            }});
        }} else {{
            jsp_alerta('A', '<%=meLanbide11I18n.getMensaje(idiomaUsuario, "msg.msjNoSelecFila")%>');
        }}
    }}

    function pulsarEliminar{tab_name}() {{
        if (tabla{tab_name}.selectedIndex != -1) {{
            jsp_confirma('<%=meLanbide11I18n.getMensaje(idiomaUsuario, "msg.msjConfEliminar")%>', function() {{
                // Lógica de eliminación
                parametros = 'tarea=preparar&modulo=MELANBIDE11&operacion=eliminar{tab_name}&tipo=0&numExp=<%=numExpediente%>&id=' + lista{tab_name}[tabla{tab_name}.selectedIndex][0];
                cargarAJAX(parametros);
            }});
        }} else {{
            jsp_alerta('A', '<%=meLanbide11I18n.getMensaje(idiomaUsuario, "msg.msjNoSelecFila")%>');
        }}
    }}

    function cargarLista{tab_name}() {{
        parametros = 'tarea=preparar&modulo=MELANBIDE11&operacion=cargarLista{tab_name}&tipo=0&numExp=<%=numExpediente%>';
        cargarAJAX(parametros);
    }}

    function inicializarTabla{tab_name}() {{
        tabla{tab_name} = new FixedColumnTable(document.getElementById('lista{tab_name}'), 1600, 1650, 'lista{tab_name}');
        // Las columnas se definirán según los campos del VO
    }}

    function dblClckTabla{tab_name}(rowID, tableName) {{
        pulsarModificar{tab_name}();
    }}
    """

def generate_table_columns(fields):
    """Generar definición de columnas para la tabla"""
    columns = []
    for field in fields:
        field_name = field['name']
        width = field.get('width', '100')
        i18n_key = f"{field_name.lower()}"
        
        column = f"        tabla{'{tab_name}'}.addColumna('{width}', 'center', \"<%=meLanbide11I18n.getMensaje(idiomaUsuario,\"{'{i18n_prefix}'}.tabla{'{tab_name}'}.{i18n_key}\")%>\");"
        columns.append(column)
    
    return '\n'.join(columns)

def generate_data_extraction(fields, vo_class):
    """Generar código de extracción de datos del VO"""
    extractions = []
    for field in fields:
        field_name = field['name']
        getter_name = f"get{field_name[0].upper() + field_name[1:]}"
        
        extraction = f"""                    String {field_name}="";
                    if(objectVO.{getter_name}()!=null){{
                        {field_name}=objectVO.{getter_name}();
                    }}else{{
                        {field_name}="-";
                    }}"""
        extractions.append(extraction)
    
    return '\n'.join(extractions)

def generate_data_assignment(fields):
    """Generar código de asignación de datos a la tabla"""
    assignments = []
    for i, field in enumerate(fields):
        field_name = field['name']
        assignment = f"                    lista{'{tab_name}'}[<%=indice%>][{i}] = {field_name};"
        assignments.append(assignment)
    
    return '\n'.join(assignments)

def generate_tab_jsp(tab_name, vo_class, fields):
    """Generar JSP completo para la pestaña"""
    config = load_config()
    template = load_jsp_template()
    
    # Generar IDs únicos
    tab_id = f"tabPage{tab_name}"
    tab_title_id = f"pestana{tab_name}"
    main_title_id = f"pestana{tab_name}Princ"
    table_id = f"lista{tab_name}"
    
    # Generar nombres de variables
    table_var = f"tabla{tab_name}"
    list_var = f"lista{tab_name}"
    list_table_var = f"lista{tab_name}Tabla"
    
    # Generar funciones
    new_function = f"pulsar{tab_name}"
    modify_function = f"pulsarModificar{tab_name}"
    delete_function = f"pulsarEliminar{tab_name}"
    init_function = f"inicializarTabla{tab_name}"
    
    # Reemplazar placeholders
    content = template.replace("{{VO_IMPORT}}", f"{config['project']['base_package']}.vo.{vo_class}")
    content = content.replace("{{JAVASCRIPT_FUNCTIONS}}", generate_javascript_functions(tab_name, vo_class))
    content = content.replace("{{TAB_ID}}", tab_id)
    content = content.replace("{{TAB_TITLE_ID}}", tab_title_id)
    content = content.replace("{{I18N_KEY}}", f"label.titulo.pestana.{tab_name.lower()}")
    content = content.replace("{{MAIN_TITLE_ID}}", main_title_id)
    content = content.replace("{{MAIN_I18N_KEY}}", f"label.tituloPrincipal.{tab_name.lower()}")
    content = content.replace("{{TABLE_ID}}", table_id)
    content = content.replace("{{BTN_NEW_ID}}", f"btnNuevo{tab_name}")
    content = content.replace("{{BTN_MODIFY_ID}}", f"btnModificar{tab_name}")
    content = content.replace("{{BTN_DELETE_ID}}", f"btnEliminar{tab_name}")
    content = content.replace("{{NEW_FUNCTION}}", new_function)
    content = content.replace("{{MODIFY_FUNCTION}}", modify_function)
    content = content.replace("{{DELETE_FUNCTION}}", delete_function)
    content = content.replace("{{TABLE_VAR}}", table_var)
    content = content.replace("{{LIST_VAR}}", list_var)
    content = content.replace("{{LIST_TABLE_VAR}}", list_table_var)
    content = content.replace("{{INITIALIZATION_FUNCTION}}", init_function)
    content = content.replace("{{VO_CLASS}}", vo_class)
    content = content.replace("{{LIST_ATTRIBUTE}}", f"lista{tab_name}")
    content = content.replace("{{DATA_EXTRACTION}}", generate_data_extraction(fields, vo_class))
    content = content.replace("{{DATA_ASSIGNMENT}}", generate_data_assignment(fields))
    
    return content

def save_jsp_file(tab_name, content):
    """Guardar archivo JSP"""
    config = load_config()
    
    # Construir ruta de destino
    jsp_path = config['paths']['jsp_path']
    file_path = os.path.join(jsp_path, f"{tab_name.lower()}.jsp")
    
    # Crear directorios si no existen
    os.makedirs(jsp_path, exist_ok=True)
    
    # Guardar archivo
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    return file_path

def generate_i18n_keys(tab_name):
    """Generar claves de internacionalización sugeridas"""
    keys = [
        f"label.titulo.pestana.{tab_name.lower()}={tab_name}",
        f"label.tituloPrincipal.{tab_name.lower()}=Gestión de {tab_name}",
        f"btn.nuevo.{tab_name.lower()}=Nuevo {tab_name}",
        f"btn.modificar.{tab_name.lower()}=Modificar {tab_name}",
        f"btn.eliminar.{tab_name.lower()}=Eliminar {tab_name}"
    ]
    return keys

def main():
    """Función principal"""
    if len(sys.argv) < 3:
        print("Uso: python tab-generator.py <nombre_pestana> <nombre_vo_class>")
        print("Ejemplo: python tab-generator.py Documentacion DocumentacionVO")
        sys.exit(1)
    
    tab_name = sys.argv[1]
    vo_class = sys.argv[2]
    
    # Si no termina en VO, agregarlo
    if not vo_class.endswith("VO"):
        vo_class += "VO"
    
    # Campos por defecto
    default_fields = [
        {"name": "id", "width": "50"},
        {"name": "codigo", "width": "100"},
        {"name": "descripcion", "width": "300"},
        {"name": "fechaCreacion", "width": "100"}
    ]
    
    # Generar JSP
    content = generate_tab_jsp(tab_name, vo_class, default_fields)
    
    # Guardar archivo
    file_path = save_jsp_file(tab_name, content)
    
    # Generar claves i18n
    i18n_keys = generate_i18n_keys(tab_name)
    
    print(f"✓ Pestaña {tab_name} generada exitosamente en: {file_path}")
    print(f"✓ VO asociado: {vo_class}")
    print("\n📝 Claves de internacionalización a agregar:")
    for key in i18n_keys:
        print(f"   {key}")
    
    print(f"\n📋 Próximos pasos:")
    print(f"   1. Agregar las claves i18n al archivo de propiedades")
    print(f"   2. Implementar la lógica en el Manager para operaciones CRUD")
    print(f"   3. Agregar la pestaña al JSP principal (melanbide11.jsp)")
    print(f"   4. Crear JSP de detalle para nuevo/modificar")

if __name__ == "__main__":
    main()