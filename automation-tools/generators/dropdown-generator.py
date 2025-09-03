#!/usr/bin/env python3
"""
Generador de Desplegables (Dropdowns) para el proyecto MELANBIDE
Automatiza la creación de componentes dropdown con sus respectivos VOs y lógica
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

def get_java_type(field_type):
    """Convertir tipo de BD a tipo Java"""
    config = load_config()
    type_mappings = config['database']['column_types']
    
    # Extraer tipo base (sin tamaño)
    base_type = field_type.split('(')[0].upper()
    return type_mappings.get(base_type, "String")

def generate_dropdown_vo(dropdown_name):
    """Generar VO para desplegable"""
    fields = [
        {"name": "codigo", "type": "VARCHAR(20)", "description": f"Código del {dropdown_name}"},
        {"name": "descripcion", "type": "VARCHAR(255)", "description": f"Descripción del {dropdown_name}"},
        {"name": "activo", "type": "VARCHAR(1)", "description": "Indica si está activo (S/N)"},
        {"name": "orden", "type": "INTEGER", "description": "Orden de visualización"}
    ]
    
    return fields

def generate_dropdown_javascript(dropdown_name, var_name):
    """Generar JavaScript para cargar desplegable"""
    return f"""
function cargar{dropdown_name}() {{
    // Limpiar desplegable
    document.getElementById('{var_name}').innerHTML = '<option value="">Seleccionar...</option>';
    
    // Cargar datos vía AJAX
    var parametros = 'tarea=preparar&modulo=MELANBIDE11&operacion=cargar{dropdown_name}&tipo=0';
    
    var ajax = CrearObjetoAjax();
    ajax.open("POST", APP_CONTEXT_PATH + "/PeticionModuloIntegracion.do", true);
    ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    ajax.send(parametros);
    
    ajax.onreadystatechange = function() {{
        if (ajax.readyState == 4 && ajax.status == 200) {{
            try {{
                var xmlDoc;
                if (navigator.appName.indexOf("Internet Explorer") != -1) {{
                    xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
                    xmlDoc.async = "false";
                    xmlDoc.loadXML(ajax.responseText);
                }} else {{
                    xmlDoc = ajax.responseXML;
                }}
                
                var nodos = xmlDoc.getElementsByTagName("RESPUESTA");
                var lista = extraer{dropdown_name}(nodos);
                var codigoOperacion = lista[0];
                
                if (codigoOperacion == "0") {{
                    rellenar{dropdown_name}(lista);
                }} else {{
                    console.error("Error cargando {dropdown_name}: " + codigoOperacion);
                }}
            }} catch (err) {{
                console.error("Error procesando respuesta {dropdown_name}: " + err);
            }}
        }}
    }};
}}

function extraer{dropdown_name}(nodos) {{
    var elemento = nodos[0];
    var hijos = elemento.childNodes;
    var lista = new Array();
    var codigoOperacion = null;
    
    for (var j = 0; hijos != null && j < hijos.length; j++) {{
        if (hijos[j].nodeName == "CODIGO_OPERACION") {{
            codigoOperacion = hijos[j].childNodes[0].nodeValue;
            lista[0] = codigoOperacion;
        }} else if (hijos[j].nodeName == "FILA") {{
            var nodoFila = hijos[j];
            var hijosFila = nodoFila.childNodes;
            var fila = new Array();
            
            for (var cont = 0; cont < hijosFila.length; cont++) {{
                if (hijosFila[cont].nodeName == "CODIGO") {{
                    fila[0] = hijosFila[cont].childNodes.length > 0 ? 
                             hijosFila[cont].childNodes[0].nodeValue : '';
                }} else if (hijosFila[cont].nodeName == "DESCRIPCION") {{
                    fila[1] = hijosFila[cont].childNodes.length > 0 ? 
                             hijosFila[cont].childNodes[0].nodeValue : '';
                }}
            }}
            
            lista[lista.length] = fila;
        }}
    }}
    
    return lista;
}}

function rellenar{dropdown_name}(lista) {{
    var select = document.getElementById('{var_name}');
    
    for (var i = 1; i < lista.length; i++) {{
        var option = document.createElement('option');
        option.value = lista[i][0];
        option.text = lista[i][1];
        select.appendChild(option);
    }}
}}

function get{dropdown_name}Descripcion(codigo) {{
    var select = document.getElementById('{var_name}');
    for (var i = 0; i < select.options.length; i++) {{
        if (select.options[i].value == codigo) {{
            return select.options[i].text;
        }}
    }}
    return '';
}}
"""

def generate_dropdown_html(dropdown_name, var_name, required=False):
    """Generar HTML para el desplegable"""
    required_attr = 'required="required"' if required else ''
    return f"""
<tr>
    <td class="labelObligatorio"><%=meLanbide11I18n.getMensaje(idiomaUsuario,"label.{var_name}")%>:</td>
    <td>
        <select id="{var_name}" name="{var_name}" class="combo" {required_attr}>
            <option value="">Seleccionar...</option>
        </select>
    </td>
</tr>
"""

def generate_manager_method(dropdown_name, vo_class):
    """Generar método en Manager para cargar desplegable"""
    return f"""
    /**
     * Cargar lista de {dropdown_name}
     * @param numExp Número de expediente
     * @return Lista de {vo_class}
     * @throws TechnicalException
     */
    public List<{vo_class}> cargar{dropdown_name}(String numExp) throws TechnicalException {{
        List<{vo_class}> lista = new ArrayList<{vo_class}>();
        
        try {{
            MeLanbide11DAO dao = new MeLanbide11DAO();
            lista = dao.cargar{dropdown_name}(numExp);
            
        }} catch (Exception e) {{
            log.error("Error cargando {dropdown_name}: " + e.getMessage(), e);
            throw new TechnicalException("Error cargando {dropdown_name}", e);
        }}
        
        return lista;
    }}
"""

def generate_dao_method(dropdown_name, vo_class, table_name):
    """Generar método en DAO para cargar desplegable"""
    return f"""
    /**
     * Cargar lista de {dropdown_name} desde base de datos
     * @param numExp Número de expediente
     * @return Lista de {vo_class}
     * @throws BDException
     */
    public List<{vo_class}> cargar{dropdown_name}(String numExp) throws BDException {{
        List<{vo_class}> lista = new ArrayList<{vo_class}>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {{
            conn = getConexion();
            stmt = conn.createStatement();
            
            String sql = "SELECT CODIGO, DESCRIPCION, ACTIVO, ORDEN " +
                        "FROM {table_name} " +
                        "WHERE ACTIVO = 'S' " +
                        "ORDER BY ORDEN, DESCRIPCION";
            
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {{
                {vo_class} vo = new {vo_class}();
                vo.setCodigo(rs.getString("CODIGO"));
                vo.setDescripcion(rs.getString("DESCRIPCION"));
                vo.setActivo(rs.getString("ACTIVO"));
                vo.setOrden(rs.getInt("ORDEN"));
                
                lista.add(vo);
            }}
            
        }} catch (SQLException e) {{
            log.error("Error ejecutando consulta para {dropdown_name}: " + e.getMessage(), e);
            throw new BDException("Error cargando {dropdown_name}", e);
        }} finally {{
            cerrarRecursos(rs, stmt, conn);
        }}
        
        return lista;
    }}
"""

def generate_xml_response_handler(dropdown_name):
    """Generar manejador de respuesta XML en la clase principal"""
    return f"""
        }} else if (operacion.equals("cargar{dropdown_name}")) {{
            try {{
                List<Desplegable{dropdown_name}VO> lista = manager.cargar{dropdown_name}(numExp);
                
                // Generar XML de respuesta
                escritor.println("<RESPUESTA>");
                escritor.println("<CODIGO_OPERACION>0</CODIGO_OPERACION>");
                
                for (Desplegable{dropdown_name}VO vo : lista) {{
                    escritor.println("<FILA>");
                    escritor.println("<CODIGO>" + (vo.getCodigo() != null ? vo.getCodigo() : "") + "</CODIGO>");
                    escritor.println("<DESCRIPCION>" + (vo.getDescripcion() != null ? vo.getDescripcion() : "") + "</DESCRIPCION>");
                    escritor.println("</FILA>");
                }}
                
                escritor.println("</RESPUESTA>");
                
            }} catch (Exception e) {{
                log.error("Error cargando {dropdown_name}: " + e.getMessage(), e);
                escritor.println("<RESPUESTA>");
                escritor.println("<CODIGO_OPERACION>1</CODIGO_OPERACION>");
                escritor.println("</RESPUESTA>");
            }}
"""

def create_dropdown_files(dropdown_name):
    """Crear todos los archivos necesarios para el desplegable"""
    config = load_config()
    
    # Generar nombres
    vo_class = f"Desplegable{dropdown_name}VO"
    table_name = f"MELANBIDE11_DESP_{dropdown_name.upper()}"
    var_name = f"desp{dropdown_name}"
    
    # Crear directorio de salida
    output_dir = os.path.join(os.path.dirname(__file__), "../examples/dropdown-" + dropdown_name.lower())
    os.makedirs(output_dir, exist_ok=True)
    
    # 1. Generar VO usando la función directamente
    fields = generate_dropdown_vo(dropdown_name)
    
    # Crear VO content manualmente
    package = config['project']['base_package'] + ".vo"
    field_declarations = []
    getters_setters = []
    
    for field in fields:
        field_name = field['name']
        field_type = field['type']
        java_type = get_java_type(field_type)
        
        field_declarations.append(f"    private {java_type} {field_name};")
        
        capitalized_name = field_name[0].upper() + field_name[1:]
        getter = f"""    public {java_type} get{capitalized_name}() {{
        return {field_name};
    }}"""
        setter = f"""    public void set{capitalized_name}({java_type} {field_name}) {{
        this.{field_name} = {field_name};
    }}"""
        getters_setters.append(getter + "\n\n" + setter)
    
    vo_content = f"""package {package};

import java.sql.Date;

/*
    VO para desplegable {dropdown_name}
*/

public class {vo_class} {{
    
    private Integer id;
    private String numExp;
    
{chr(10).join(field_declarations)}

    public Integer getId() {{
        return id;
    }}

    public void setId(Integer id) {{
        this.id = id;
    }}

    public String getNumExp() {{
        return numExp;
    }}

    public void setNumExp(String numExp) {{
        this.numExp = numExp;
    }}

{chr(10).join(getters_setters)}
    
}}"""
    
    with open(os.path.join(output_dir, f"{vo_class}.java"), 'w', encoding='utf-8') as f:
        f.write(vo_content)
    
    # 2. Generar JavaScript
    js_content = generate_dropdown_javascript(dropdown_name, var_name)
    with open(os.path.join(output_dir, f"{dropdown_name.lower()}-dropdown.js"), 'w', encoding='utf-8') as f:
        f.write(js_content)
    
    # 3. Generar HTML
    html_content = generate_dropdown_html(dropdown_name, var_name, True)
    with open(os.path.join(output_dir, f"{dropdown_name.lower()}-html.jsp"), 'w', encoding='utf-8') as f:
        f.write(html_content)
    
    # 4. Generar método Manager
    manager_content = generate_manager_method(dropdown_name, vo_class)
    with open(os.path.join(output_dir, f"{dropdown_name}Manager-method.java"), 'w', encoding='utf-8') as f:
        f.write(manager_content)
    
    # 5. Generar método DAO
    dao_content = generate_dao_method(dropdown_name, vo_class, table_name)
    with open(os.path.join(output_dir, f"{dropdown_name}DAO-method.java"), 'w', encoding='utf-8') as f:
        f.write(dao_content)
    
    # 6. Generar manejador XML
    xml_content = generate_xml_response_handler(dropdown_name)
    with open(os.path.join(output_dir, f"{dropdown_name}-xml-handler.java"), 'w', encoding='utf-8') as f:
        f.write(xml_content)
    
    # 7. Generar SQL de creación de tabla
    sql_content = f"""-- Tabla para desplegable {dropdown_name}
CREATE TABLE {table_name} (
    ID INTEGER NOT NULL,
    CODIGO VARCHAR(20) NOT NULL,
    DESCRIPCION VARCHAR(255) NOT NULL,
    ACTIVO VARCHAR(1) DEFAULT 'S',
    ORDEN INTEGER DEFAULT 0,
    FECHA_CREACION DATE DEFAULT SYSDATE,
    CONSTRAINT PK_{table_name} PRIMARY KEY (ID),
    CONSTRAINT UK_{table_name}_CODIGO UNIQUE (CODIGO)
);

-- Secuencia para IDs
CREATE SEQUENCE SEQ_{table_name}
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Datos de ejemplo
INSERT INTO {table_name} (ID, CODIGO, DESCRIPCION, ACTIVO, ORDEN) VALUES 
    (SEQ_{table_name}.NEXTVAL, 'OPC1', 'Opción 1', 'S', 1);
INSERT INTO {table_name} (ID, CODIGO, DESCRIPCION, ACTIVO, ORDEN) VALUES 
    (SEQ_{table_name}.NEXTVAL, 'OPC2', 'Opción 2', 'S', 2);
INSERT INTO {table_name} (ID, CODIGO, DESCRIPCION, ACTIVO, ORDEN) VALUES 
    (SEQ_{table_name}.NEXTVAL, 'OPC3', 'Opción 3', 'S', 3);
"""
    
    with open(os.path.join(output_dir, f"{dropdown_name.lower()}-table.sql"), 'w', encoding='utf-8') as f:
        f.write(sql_content)
    
    return output_dir

def main():
    """Función principal"""
    if len(sys.argv) < 2:
        print("Uso: python dropdown-generator.py <nombre_desplegable>")
        print("Ejemplo: python dropdown-generator.py TipoDocumento")
        sys.exit(1)
    
    dropdown_name = sys.argv[1]
    
    # Crear archivos
    output_dir = create_dropdown_files(dropdown_name)
    
    print(f"✓ Desplegable {dropdown_name} generado exitosamente en: {output_dir}")
    print(f"✓ Archivos generados:")
    print(f"   - VO: Desplegable{dropdown_name}VO.java")
    print(f"   - JavaScript: {dropdown_name.lower()}-dropdown.js")
    print(f"   - HTML: {dropdown_name.lower()}-html.jsp")
    print(f"   - Manager: {dropdown_name}Manager-method.java")
    print(f"   - DAO: {dropdown_name}DAO-method.java")
    print(f"   - XML Handler: {dropdown_name}-xml-handler.java")
    print(f"   - SQL: {dropdown_name.lower()}-table.sql")
    
    print(f"\n📋 Próximos pasos:")
    print(f"   1. Ejecutar el SQL para crear la tabla")
    print(f"   2. Copiar el método al Manager (MeLanbide11Manager.java)")
    print(f"   3. Copiar el método al DAO (MeLanbide11DAO.java)")
    print(f"   4. Agregar el manejador XML a MELANBIDE11.java")
    print(f"   5. Incluir el JavaScript en el JSP correspondiente")
    print(f"   6. Agregar el HTML donde sea necesario")
    print(f"   7. Agregar claves i18n: label.desp{dropdown_name}")

if __name__ == "__main__":
    main()