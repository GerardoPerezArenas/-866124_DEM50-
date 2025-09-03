<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/tlds/c.tld"       prefix="c"     %>

<%@ page import="es.altia.flexia.integracion.moduloexterno.melanbide11.i18n.MeLanbide11I18n" %>
<%@ page import="es.altia.agora.business.escritorio.UsuarioValueObject" %>
<%@ page import="es.altia.common.service.config.Config" %>
<%@ page import="es.altia.common.service.config.ConfigServiceHelper" %>
<%@ page import="es.altia.flexia.integracion.moduloexterno.melanbide11.vo.ContratacionVO" %>
<%@ page import="es.altia.flexia.integracion.moduloexterno.melanbide11.util.ConfigurationParameter" %>
<%@ page import="es.altia.flexia.integracion.moduloexterno.melanbide11.util.ConstantesMeLanbide11" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    int idiomaUsuario = 1;
    if (request.getParameter("idioma") != null) {
        try {
            idiomaUsuario = Integer.parseInt(request.getParameter("idioma"));
        } catch (Exception ex) { /* ignora */ }
    }

    UsuarioValueObject usuarioVO = new UsuarioValueObject();
    int idioma = 1;
    int apl = 5;
    String css = "";
    if (session.getAttribute("usuario") != null) {
        usuarioVO = (UsuarioValueObject) session.getAttribute("usuario");
        apl = usuarioVO.getAppCod();
        idioma = usuarioVO.getIdioma();
        css = usuarioVO.getCss();
    }

    // I18n
    MeLanbide11I18n meLanbide11I18n = MeLanbide11I18n.getInstance();
    String numExpediente = (String) request.getAttribute("numExp");
%>

<jsp:useBean id="descriptor" scope="request"
             class="es.altia.agora.interfaces.user.web.util.TraductorAplicacionBean"
             type="es.altia.agora.interfaces.user.web.util.TraductorAplicacionBean" />
<jsp:setProperty name="descriptor" property="idi_cod" value="<%=idioma%>" />
<jsp:setProperty name="descriptor" property="apl_cod" value="<%=apl%>" />

<link rel="StyleSheet" media="screen" type="text/css" href="<%=request.getContextPath()%><%=css%>"/>
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<script type="text/javascript">
    // Escapado seguro para volcar valores del servidor en JS
    function s(x){
        x = (x === null || x === undefined || x === '') ? '-' : x;
        return String(x).replace(/\\/g,'\\\\').replace(/'/g,"\\'").replace(/\r?\n/g,' ');
    }

    function pulsarNuevaContratacion() {
        lanzarPopUpModal('<%=request.getContextPath()%>/PeticionModuloIntegracion.do?tarea=preparar&modulo=MELANBIDE11&operacion=cargarNuevaContratacion&tipo=0&numExp=<%=numExpediente%>&nuevo=1', 750, 1200, 'no', 'no', function (result) {
            if (result !== undefined) {
                if (result[0] === '0') {
                    recargarTablaContrataciones(result);
                }
            }
        });
    }

    function pulsarModificarContratacion() {
        if (tabaAccesos.selectedIndex !== -1) {
            lanzarPopUpModal('<%=request.getContextPath()%>/PeticionModuloIntegracion.do?tarea=preparar&modulo=MELANBIDE11&operacion=cargarModificarContratacion&tipo=0&nuevo=0&numExp=<%=numExpediente%>&id=' + listaAccesos[tabaAccesos.selectedIndex][0], 750, 1200, 'no', 'no', function (result) {
                if (result !== undefined) {
                    if (result[0] === '0') {
                        recargarTablaContrataciones(result);
                    }
                }
            });
        } else {
            jsp_alerta('A', '<%=meLanbide11I18n.getMensaje(idiomaUsuario, "msg.msjNoSelecFila")%>');
        }
    }

    function pulsarEliminarContratacion() {
        if (tabaAccesos.selectedIndex !== -1) {
            var resultado = jsp_alerta('', '<%=meLanbide11I18n.getMensaje(idiomaUsuario, "msg.preguntaEliminar")%>');
            if (resultado == 1) {
                var ajax = getXMLHttpRequest();
                var nodos = null;
                var CONTEXT_PATH = '<%=request.getContextPath()%>';
                var url = CONTEXT_PATH + "/PeticionModuloIntegracion.do";
                var parametros = 'tarea=preparar&modulo=MELANBIDE11&operacion=eliminarContratacion&tipo=0&numExp=<%=numExpediente%>&id=' + listaAccesos[tabaAccesos.selectedIndex][0];
                try {
                    ajax.open("POST", url, false);
                    ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    ajax.setRequestHeader("Accept", "text/xml, application/xml, text/plain");
                    ajax.send(parametros);
                    if (ajax.readyState == 4 && ajax.status == 200) {
                        var xmlDoc = null;
                        if (navigator.appName.indexOf("Internet Explorer") != -1) {
                            var text = ajax.responseText;
                            xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
                            xmlDoc.async = "false";
                            xmlDoc.loadXML(text);
                        } else {
                            xmlDoc = ajax.responseXML;
                        }
                        nodos = xmlDoc.getElementsByTagName("RESPUESTA");
                        var listaContratacionesNueva = extraerListaContrataciones(nodos);
                        var codigoOperacion = listaContratacionesNueva[0];

                        if (codigoOperacion === "0") {
                            recargarTablaContrataciones(listaContratacionesNueva);
                        } else if (codigoOperacion === "1") {
                            jsp_alerta("A", '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"error.errorBD")%>');
                        } else if (codigoOperacion === "2") {
                            jsp_alerta("A", '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"error.errorGen")%>');
                        } else if (codigoOperacion === "3") {
                            jsp_alerta("A", '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"error.pasoParametros")%>');
                        } else {
                            jsp_alerta("A", '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"error.errorGen")%>');
                        }
                    }
                } catch (Err) {
                    jsp_alerta("A", '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"error.errorGen")%>');
                }
            }
        } else {
            jsp_alerta('A', '<%=meLanbide11I18n.getMensaje(idiomaUsuario, "msg.msjNoSelecFila")%>');
        }
    }

    function extraerListaContrataciones(nodos) {
        var elemento = nodos[0];
        var hijos = elemento.childNodes;
        var listaNueva = new Array();
        var fila = new Array();
        var nodoFila, hijosFila;

        for (var j = 0; hijos != null && j < hijos.length; j++) {
            if (hijos[j].nodeName == "CODIGO_OPERACION") {
                var codigoOperacion = hijos[j].childNodes[0].nodeValue;
                listaNueva[j] = codigoOperacion;
            } else if (hijos[j].nodeName == "FILA") {
                nodoFila = hijos[j];
                hijosFila = nodoFila.childNodes;
                for (var cont = 0; cont < hijosFila.length; cont++) {
                    if (hijosFila[cont].nodeName == "ID") {
                        fila[0] = (hijosFila[cont].childNodes.length > 0) ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "NOFECONT") {
                        fila[1] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "IDCONT1") {
                        fila[2] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "IDCONT2") {
                        fila[3] = (hijosFila[cont].childNodes.length > 0) ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "DNICONT") {
                        fila[4] = (hijosFila[cont].childNodes.length > 0) ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "NOMCONT") {
                        fila[5] = (hijosFila[cont].childNodes.length > 0) ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "APE1CONT") {
                        fila[6] = (hijosFila[cont].childNodes.length > 0) ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "APE2CONT") {
                        fila[7] = (hijosFila[cont].childNodes.length > 0) ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "FECHNACCONT") {
                        fila[8] = (hijosFila[cont].childNodes.length > 0) ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "EDADCONT") {
                        fila[9] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "SEXOCONT") {
                        fila[10] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "MAY55CONT") {
                        fila[11] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "ACCFORCONT") {
                        fila[12] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "CODFORCONT") {
                        fila[13] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "DENFORCONT") {
                        fila[14] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "PUESTOCONT") {
                        fila[15] = (hijosFila[cont].childNodes.length > 0) ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "CODOCUCONT") {
                        fila[16] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "OCUCONT") {
                        fila[17] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';

                    // --- NUEVOS CAMPOS EN POSICIÓN 18 y 19 ---
                    } else if (hijosFila[cont].nodeName == "TITREQPUESTO") {
                        fila[18] = (hijosFila[cont].childNodes.length > 0 && hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "FUNCIONES") {
                        fila[19] = (hijosFila[cont].childNodes.length > 0 && hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';

                    // --- HISTÓRICOS MOVIDOS AL FINAL: 34 y 35 ---
                    } else if (hijosFila[cont].nodeName == "DESTITULACION") {
                        fila[34] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "TITULACION") {
                        fila[35] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';

                    } else if (hijosFila[cont].nodeName == "CPROFESIONALIDAD") {
                        fila[20] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "MODCONT") {
                        fila[21] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "JORCONT") {
                        fila[22] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "PORCJOR") {
                        if (hijosFila[cont].childNodes[0].nodeValue != "null") {
                            var t23 = hijosFila[cont].childNodes[0].nodeValue.toString().replace(".", ",");
                            fila[23] = t23;
                        } else {
                            fila[23] = '-';
                        }
                    } else if (hijosFila[cont].nodeName == "HORASCONV") {
                        fila[24] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "FECHINICONT") {
                        fila[25] = (hijosFila[cont].childNodes.length > 0) ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "FECHFINCONT") {
                        fila[26] = (hijosFila[cont].childNodes.length > 0) ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "DURCONT") {
                        fila[27] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "GRSS") {
                        fila[28] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "DIRCENTRCONT") {
                        fila[29] = (hijosFila[cont].childNodes.length > 0) ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "NSSCONT") {
                        fila[30] = (hijosFila[cont].childNodes.length > 0) ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "CSTCONT") {
                        if (hijosFila[cont].childNodes[0].nodeValue != "null") {
                            var t31 = hijosFila[cont].childNodes[0].nodeValue.toString().replace(".", ",");
                            fila[31] = t31;
                        } else {
                            fila[31] = '-';
                        }
                    } else if (hijosFila[cont].nodeName == "TIPRSB") {
                        fila[32] = (hijosFila[cont].childNodes[0].nodeValue != "null") ? hijosFila[cont].childNodes[0].nodeValue : '-';
                    } else if (hijosFila[cont].nodeName == "IMPSUBVCONT") {
                        if (hijosFila[cont].childNodes[0].nodeValue != "null") {
                            var t33 = hijosFila[cont].childNodes[0].nodeValue.toString().replace(".", ",");
                            fila[33] = t33;
                        } else {
                            fila[33] = '-';
                        }
                    }
                }
                listaNueva[j] = fila;
                fila = new Array();
            }
        }
        return listaNueva;
    }

    function recargarTablaContrataciones(result) {
        var fila;
        listaAccesos = new Array();
        listaAccesosTabla = new Array();
        for (var i = 1; i < result.length; i++) {
            fila = result[i];
            // Copiamos posiciones 0..35
            listaAccesos[i - 1] = [
                fila[0], fila[1], fila[2], fila[3], fila[4], fila[5], fila[6], fila[7], fila[8], fila[9],
                fila[10], fila[11], fila[12], fila[13], fila[14], fila[15], fila[16], fila[17], fila[18], fila[19],
                fila[20], fila[21], fila[22], fila[23], fila[24], fila[25], fila[26], fila[27], fila[28], fila[29],
                fila[30], fila[31], fila[32], fila[33], fila[34], fila[35]
            ];
            listaAccesosTabla[i - 1] = [
                fila[0], fila[1], fila[2], fila[3], fila[4], fila[5], fila[6], fila[7], fila[8], fila[9],
                fila[10], fila[11], fila[12], fila[13], fila[14], fila[15], fila[16], fila[17], fila[18], fila[19],
                fila[20], fila[21], fila[22], fila[23], fila[24], fila[25], fila[26], fila[27], fila[28], fila[29],
                fila[30], fila[31], fila[32], fila[33], fila[34], fila[35]
            ];
        }

        inicializarTabla();
        tabaAccesos.lineas = listaAccesosTabla;
        tabaAccesos.displayTabla();
    }

    function dblClckTablaContrataciones(rowID, tableName) {
        pulsarModificarContratacion();
    }

    function inicializarTabla() {
        tabaAccesos = new FixedColumnTable(document.getElementById('listaAccesos'), 1600, 1650, 'listaAccesos');

        tabaAccesos.addColumna('50',  'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.id")%>');
        tabaAccesos.addColumna('100', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.nOferta")%>');
        tabaAccesos.addColumna('100', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.idContratoOferta")%>');
        tabaAccesos.addColumna('100', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.idContratoDirecto")%>');
        tabaAccesos.addColumna('100', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.dni_nie")%>');
        tabaAccesos.addColumna('200', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.nombre")%>');
        tabaAccesos.addColumna('200', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.apellido1")%>');
        tabaAccesos.addColumna('200', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.apellido2")%>');
        tabaAccesos.addColumna('70',  'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.fechaNacimiento")%>');
        tabaAccesos.addColumna('70',  'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.edad")%>');
        tabaAccesos.addColumna('100', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.sexo")%>');
        tabaAccesos.addColumna('100', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.may55")%>');
        tabaAccesos.addColumna('100', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.finFormativa")%>');
        tabaAccesos.addColumna('100', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.codFormativa")%>');
        tabaAccesos.addColumna('100', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.denFormativa")%>');
        tabaAccesos.addColumna('200', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.puesto")%>');
        tabaAccesos.addColumna('100', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.codOcupacion")%>');
        tabaAccesos.addColumna('330', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.ocupacion")%>');

        // Nuevas columnas intermedias
        tabaAccesos.addColumna('330', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.titreqpuesto")%>');
        tabaAccesos.addColumna('330', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.funciones")%>');

        tabaAccesos.addColumna('330', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.cProfesionalidad")%>');
        tabaAccesos.addColumna('150', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.modalidadContrato")%>');
        tabaAccesos.addColumna('100', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.jornada")%>');
        tabaAccesos.addColumna('50',  'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.porcJornada")%>');
        tabaAccesos.addColumna('70',  'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.horasConv")%>');
        tabaAccesos.addColumna('70',  'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.fechaInicio")%>');
        tabaAccesos.addColumna('70',  'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.fechaFin")%>');
        tabaAccesos.addColumna('50',  'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.mesesContrato")%>');
        tabaAccesos.addColumna('350', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.grupoCotizacion")%>');
        tabaAccesos.addColumna('250', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.direccionCT")%>');
        tabaAccesos.addColumna('100', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.nSS")%>');
        tabaAccesos.addColumna('70',  'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.costeContrato")%>');
        tabaAccesos.addColumna('70',  'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.tipRetribucion")%>');
        tabaAccesos.addColumna('70',  'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.tablaContrataciones.importeSub")%>');
        // Históricos al final
        tabaAccesos.addColumna('330', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.historico.titSol")%> (consulta)');
        tabaAccesos.addColumna('330', 'center', '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"contratacion.historico.titulacion")%> (consulta)');
    }
</script>

<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/DataTables/datatables.min.js"></script>
<link  rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/scripts/DataTables/datatables.min.css"/>
<link  rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/extension/melanbide11/melanbide11.css"/>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/validaciones.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/popup.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/extension/melanbide11/FixedColumnsTable.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/extension/melanbide11/JavaScriptUtil.js"></script>

<script type="text/javascript">
    var APP_CONTEXT_PATH = '<%=request.getContextPath()%>';
</script>

<div class="tab-page" id="tabPage111" style="height:520px; width: 100%;">
    <h2 class="tab" id="pestana111"><%=meLanbide11I18n.getMensaje(idiomaUsuario,"label.titulo.pestana")%></h2>
    <script type="text/javascript">
        if (window.tp1 && typeof tp1.addTabPage === 'function') {
            tp1.addTabPage(document.getElementById("tabPage111"));
        }
    </script>
    <br/>
    <h2 class="legendAzul" id="pestanaPrinc"><%=meLanbide11I18n.getMensaje(idiomaUsuario,"label.tituloPrincipal")%></h2>
    <div>
        <br/>
        <div id="divGeneral">
            <div id="listaAccesos" align="center"></div>
        </div>
        <br/><br/>
        <div class="botonera" style="text-align: center;">
            <input type="button" id="btnNuevoAcceso"     name="btnNuevoAcceso"     class="botonGeneral" value="<%=meLanbide11I18n.getMensaje(idiomaUsuario, "btn.nuevo")%>"     onclick="pulsarNuevaContratacion();">
            <input type="button" id="btnModificarAcceso" name="btnModificarAcceso" class="botonGeneral" value="<%=meLanbide11I18n.getMensaje(idiomaUsuario, "btn.modificar")%>" onclick="pulsarModificarContratacion();">
            <input type="button" id="btnEliminarAcceso"  name="btnEliminarAcceso"  class="botonGeneral" value="<%=meLanbide11I18n.getMensaje(idiomaUsuario, "btn.eliminar")%>"  onclick="pulsarEliminarContratacion();">
        </div>
    </div>
</div>

<script type="text/javascript">
    // @ts-nocheck
    var tabaAccesos;
    var listaAccesos = [];
    var listaAccesosTabla = [];
    inicializarTabla();
</script>

<%
    ContratacionVO objectVO = null;
    List<ContratacionVO> lista = null;
    if (request.getAttribute("listaAccesos") != null) {
        lista = (List<ContratacionVO>) request.getAttribute("listaAccesos");
    }
    if (lista != null && !lista.isEmpty()) {
        for (int indice = 0; indice < lista.size(); indice++) {
            objectVO = lista.get(indice);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            String oferta = (objectVO.getOferta() != null) ? objectVO.getOferta() : "-";
            String idContrato1 = (objectVO.getIdContrato1() != null) ? objectVO.getIdContrato1() : "-";
            String idContrato2 = (objectVO.getIdContrato2() != null) ? objectVO.getIdContrato2() : "-";
            String dni = (objectVO.getDni() != null) ? objectVO.getDni() : "-";
            String nombre = (objectVO.getNombre() != null) ? objectVO.getNombre() : "-";
            String apellido1 = (objectVO.getApellido1() != null) ? objectVO.getApellido1() : "-";
            String apellido2 = (objectVO.getApellido2() != null) ? objectVO.getApellido2() : "-";
            String fechaNacimiento = (objectVO.getFechaNacimiento() != null) ? dateFormat.format(objectVO.getFechaNacimiento()) : "-";
            String edad = (objectVO.getEdad() != null && !"".equals(objectVO.getEdad())) ? Integer.toString(objectVO.getEdad()) : "-";

            String sexo = "-";
            if (objectVO.getDesSexo() != null) {
                String descripcion = objectVO.getDesSexo();
                String barraSeparadoraDobleIdiomaDesple = ConfigurationParameter.getParameter(ConstantesMeLanbide11.BARRA_SEPARADORA_IDIOMA_DESPLEGABLES, ConstantesMeLanbide11.FICHERO_PROPIEDADES);
                String[] descripcionDobleIdioma = (descripcion != null ? descripcion.split(barraSeparadoraDobleIdiomaDesple) : null);
                if (descripcionDobleIdioma != null && descripcionDobleIdioma.length > 1) {
                    if (idiomaUsuario == ConstantesMeLanbide11.CODIGO_IDIOMA_EUSKERA) {
                        descripcion = descripcionDobleIdioma[1];
                    } else {
                        descripcion = descripcionDobleIdioma[0];
                    }
                }
                sexo = descripcion;
            }

            String mayor55 = (objectVO.getMayor55() != null) ? objectVO.getMayor55() : "-";
            String finFormativa = (objectVO.getFinFormativa() != null) ? objectVO.getFinFormativa() : "-";
            String codFormativa = (objectVO.getCodFormativa() != null) ? objectVO.getCodFormativa() : "-";
            String denFormativa = (objectVO.getDenFormativa() != null) ? objectVO.getDenFormativa() : "-";

            String puesto = (objectVO.getPuesto() != null) ? objectVO.getPuesto() : "-";
            String codOcupacion = (objectVO.getOcupacion() != null) ? objectVO.getOcupacion() : "-";

            String ocupacion = "-";
            if (objectVO.getDesOcupacionLibre() != null) {
                ocupacion = objectVO.getDesOcupacionLibre();
            } else if (objectVO.getDesOcupacion() != null) {
                ocupacion = objectVO.getDesOcupacion();
            }

            String desTitulacion = (objectVO.getDesTitulacionLibre() != null) ? objectVO.getDesTitulacionLibre() : "-";
            String titulacion   = (objectVO.getDesTitulacion() != null) ? objectVO.getDesTitulacion() : "-";

            // Nuevos campos intermedios requeridos por la tabla
            String titReqPuesto = "-";
            if (objectVO.getDesTitReqPuesto() != null) {
                titReqPuesto = objectVO.getDesTitReqPuesto();
            } else if (objectVO.getTitReqPuesto() != null) {
                titReqPuesto = objectVO.getTitReqPuesto();
            }

            String funciones = (objectVO.getFunciones() != null) ? objectVO.getFunciones() : "-";
            String cProfesionalidad = (objectVO.getDesCProfesionalidad() != null) ? objectVO.getDesCProfesionalidad() : "-";
            String modalidadContrato = (objectVO.getModalidadContrato() != null) ? objectVO.getModalidadContrato() : "-";

            String jornada = "-";
            if (objectVO.getDesJornada() != null) {
                String descripcion = objectVO.getDesJornada();
                String barraSeparadoraDobleIdiomaDesple = ConfigurationParameter.getParameter(ConstantesMeLanbide11.BARRA_SEPARADORA_IDIOMA_DESPLEGABLES, ConstantesMeLanbide11.FICHERO_PROPIEDADES);
                String[] descripcionDobleIdioma = (descripcion != null ? descripcion.split(barraSeparadoraDobleIdiomaDesple) : null);
                if (descripcionDobleIdioma != null && descripcionDobleIdioma.length > 1) {
                    if (idiomaUsuario == ConstantesMeLanbide11.CODIGO_IDIOMA_EUSKERA) {
                        descripcion = descripcionDobleIdioma[1];
                    } else {
                        descripcion = descripcionDobleIdioma[0];
                    }
                }
                jornada = descripcion;
            }

            String porcJornada = (objectVO.getPorcJornada() != null) ? String.valueOf(objectVO.getPorcJornada().toString().replace(".", ",")) : "-";
            String horasConv   = (objectVO.getHorasConv() != null && !"".equals(objectVO.getHorasConv())) ? Integer.toString(objectVO.getHorasConv()) : "-";
            String fechaInicio = (objectVO.getFechaInicio() != null) ? dateFormat.format(objectVO.getFechaInicio()) : "-";
            String fechaFin    = (objectVO.getFechaFin() != null) ? dateFormat.format(objectVO.getFechaFin()) : "-";
            String mesesContrato = (objectVO.getMesesContrato() != null && !"".equals(objectVO.getMesesContrato()) && !"0".equals(objectVO.getMesesContrato())) ? objectVO.getMesesContrato() : "-";

            String grupoCotizacion = "-";
            if (objectVO.getDesGrupoCotizacion() != null) {
                String descripcion = objectVO.getDesGrupoCotizacion();
                String barraSeparadoraDobleIdiomaDesple = ConfigurationParameter.getParameter(ConstantesMeLanbide11.BARRA_SEPARADORA_IDIOMA_DESPLEGABLES, ConstantesMeLanbide11.FICHERO_PROPIEDADES);
                String[] descripcionDobleIdioma = (descripcion != null ? descripcion.split(barraSeparadoraDobleIdiomaDesple) : null);
                if (descripcionDobleIdioma != null && descripcionDobleIdioma.length > 1) {
                    if (idiomaUsuario == ConstantesMeLanbide11.CODIGO_IDIOMA_EUSKERA) {
                        descripcion = descripcionDobleIdioma[1];
                    } else {
                        descripcion = descripcionDobleIdioma[0];
                    }
                }
                grupoCotizacion = descripcion;
            }

            String direccionCT = (objectVO.getDireccionCT() != null) ? objectVO.getDireccionCT() : "-";
            String numSS       = (objectVO.getNumSS() != null) ? objectVO.getNumSS() : "-";
            String costeContrato = (objectVO.getCosteContrato() != null) ? String.valueOf(objectVO.getCosteContrato().toString().replace(".", ",")) : "-";

            String tipRetribucion = "-";
            if (objectVO.getDesTipRetribucion() != null) {
                String descripcion = objectVO.getDesTipRetribucion();
                String barraSeparadoraDobleIdiomaDesple = ConfigurationParameter.getParameter(ConstantesMeLanbide11.BARRA_SEPARADORA_IDIOMA_DESPLEGABLES, ConstantesMeLanbide11.FICHERO_PROPIEDADES);
                String[] descripcionDobleIdioma = (descripcion != null ? descripcion.split(barraSeparadoraDobleIdiomaDesple) : null);
                if (descripcionDobleIdioma != null && descripcionDobleIdioma.length > 1) {
                    if (idiomaUsuario == ConstantesMeLanbide11.CODIGO_IDIOMA_EUSKERA) {
                        descripcion = descripcionDobleIdioma[1];
                    } else {
                        descripcion = descripcionDobleIdioma[0];
                    }
                }
                tipRetribucion = descripcion;
            }

            String importeSub = (objectVO.getImporteSub() != null) ? String.valueOf(objectVO.getImporteSub().toString().replace(".", ",")) : "-";
%>
<script type="text/javascript">
// @ts-nocheck
    listaAccesos.push([
        s('<%=objectVO.getId()%>'), s('<%=oferta%>'), s('<%=idContrato1%>'), s('<%=idContrato2%>'), s('<%=dni%>'),
        s('<%=nombre%>'), s('<%=apellido1%>'), s('<%=apellido2%>'), s('<%=fechaNacimiento%>'), s('<%=edad%>'),
        s('<%=sexo%>'), s('<%=mayor55%>'), s('<%=finFormativa%>'), s('<%=codFormativa%>'), s('<%=denFormativa%>'),
        s('<%=puesto%>'), s('<%=codOcupacion%>'), s('<%=ocupacion%>'), s('<%=titReqPuesto%>'), s('<%=funciones%>'),
        s('<%=cProfesionalidad%>'), s('<%=modalidadContrato%>'), s('<%=jornada%>'), s('<%=porcJornada%>'),
        s('<%=horasConv%>'), s('<%=fechaInicio%>'), s('<%=fechaFin%>'), s('<%=mesesContrato%>'),
        s('<%=grupoCotizacion%>'), s('<%=direccionCT%>'), s('<%=numSS%>'), s('<%=costeContrato%>'),
        s('<%=tipRetribucion%>'), s('<%=importeSub%>'), s('<%=desTitulacion%>'), s('<%=titulacion%>')
    ]);

    listaAccesosTabla.push([
        s('<%=objectVO.getId()%>'), s('<%=oferta%>'), s('<%=idContrato1%>'), s('<%=idContrato2%>'), s('<%=dni%>'),
        s('<%=nombre%>'), s('<%=apellido1%>'), s('<%=apellido2%>'), s('<%=fechaNacimiento%>'), s('<%=edad%>'),
        s('<%=sexo%>'), s('<%=mayor55%>'), s('<%=finFormativa%>'), s('<%=codFormativa%>'), s('<%=denFormativa%>'),
        s('<%=puesto%>'), s('<%=codOcupacion%>'), s('<%=ocupacion%>'), s('<%=titReqPuesto%>'), s('<%=funciones%>'),
        s('<%=cProfesionalidad%>'), s('<%=modalidadContrato%>'), s('<%=jornada%>'), s('<%=porcJornada%>'),
        s('<%=horasConv%>'), s('<%=fechaInicio%>'), s('<%=fechaFin%>'), s('<%=mesesContrato%>'),
        s('<%=grupoCotizacion%>'), s('<%=direccionCT%>'), s('<%=numSS%>'), s('<%=costeContrato%>'),
        s('<%=tipRetribucion%>'), s('<%=importeSub%>'), s('<%=desTitulacion%>'), s('<%=titulacion%>')
    ]);
</script>
<%
        } // for
    } // if
%>

<script type="text/javascript">
// @ts-nocheck
    if (typeof tabaAccesos !== 'undefined') {
        tabaAccesos.lineas = listaAccesosTabla;
        tabaAccesos.displayTabla();
    }
</script>

<div id="popupcalendar" class="text"></div>
