
function cargarTipoDocumento() {
    // Limpiar desplegable
    document.getElementById('despTipoDocumento').innerHTML = '<option value="">Seleccionar...</option>';
    
    // Cargar datos vía AJAX
    var parametros = 'tarea=preparar&modulo=MELANBIDE11&operacion=cargarTipoDocumento&tipo=0';
    
    var ajax = CrearObjetoAjax();
    ajax.open("POST", APP_CONTEXT_PATH + "/PeticionModuloIntegracion.do", true);
    ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    ajax.send(parametros);
    
    ajax.onreadystatechange = function() {
        if (ajax.readyState == 4 && ajax.status == 200) {
            try {
                var xmlDoc;
                if (navigator.appName.indexOf("Internet Explorer") != -1) {
                    xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
                    xmlDoc.async = "false";
                    xmlDoc.loadXML(ajax.responseText);
                } else {
                    xmlDoc = ajax.responseXML;
                }
                
                var nodos = xmlDoc.getElementsByTagName("RESPUESTA");
                var lista = extraerTipoDocumento(nodos);
                var codigoOperacion = lista[0];
                
                if (codigoOperacion == "0") {
                    rellenarTipoDocumento(lista);
                } else {
                    console.error("Error cargando TipoDocumento: " + codigoOperacion);
                }
            } catch (err) {
                console.error("Error procesando respuesta TipoDocumento: " + err);
            }
        }
    };
}

function extraerTipoDocumento(nodos) {
    var elemento = nodos[0];
    var hijos = elemento.childNodes;
    var lista = new Array();
    var codigoOperacion = null;
    
    for (var j = 0; hijos != null && j < hijos.length; j++) {
        if (hijos[j].nodeName == "CODIGO_OPERACION") {
            codigoOperacion = hijos[j].childNodes[0].nodeValue;
            lista[0] = codigoOperacion;
        } else if (hijos[j].nodeName == "FILA") {
            var nodoFila = hijos[j];
            var hijosFila = nodoFila.childNodes;
            var fila = new Array();
            
            for (var cont = 0; cont < hijosFila.length; cont++) {
                if (hijosFila[cont].nodeName == "CODIGO") {
                    fila[0] = hijosFila[cont].childNodes.length > 0 ? 
                             hijosFila[cont].childNodes[0].nodeValue : '';
                } else if (hijosFila[cont].nodeName == "DESCRIPCION") {
                    fila[1] = hijosFila[cont].childNodes.length > 0 ? 
                             hijosFila[cont].childNodes[0].nodeValue : '';
                }
            }
            
            lista[lista.length] = fila;
        }
    }
    
    return lista;
}

function rellenarTipoDocumento(lista) {
    var select = document.getElementById('despTipoDocumento');
    
    for (var i = 1; i < lista.length; i++) {
        var option = document.createElement('option');
        option.value = lista[i][0];
        option.text = lista[i][1];
        select.appendChild(option);
    }
}

function getTipoDocumentoDescripcion(codigo) {
    var select = document.getElementById('despTipoDocumento');
    for (var i = 0; i < select.options.length; i++) {
        if (select.options[i].value == codigo) {
            return select.options[i].text;
        }
    }
    return '';
}
