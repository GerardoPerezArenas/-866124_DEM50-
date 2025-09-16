<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>

<%@page import="es.altia.flexia.integracion.moduloexterno.melanbide11.i18n.MeLanbide11I18n" %>
<%@page import="es.altia.agora.business.escritorio.UsuarioValueObject" %>
<%@page import="es.altia.common.service.config.Config"%>
<%@page import="es.altia.common.service.config.ConfigServiceHelper"%>
<%@page import="{{VO_IMPORT}}" %>
<%@page import="es.altia.flexia.integracion.moduloexterno.melanbide11.util.ConfigurationParameter"%>
<%@page import="es.altia.flexia.integracion.moduloexterno.melanbide11.util.ConstantesMeLanbide11"%>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.List" %>
<%@page import="java.text.DateFormat" %>
<%@page import="java.text.SimpleDateFormat" %>

<%
int idiomaUsuario = 1;
    if(request.getParameter("idioma") != null)
    {
        try
        {
            idiomaUsuario = Integer.parseInt(request.getParameter("idioma"));
        }
        catch(Exception ex)
        {}
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

    //Clase para internacionalizar los mensajes de la aplicación.
    MeLanbide11I18n meLanbide11I18n = MeLanbide11I18n.getInstance();
    String numExpediente = (String)request.getAttribute("numExp");
    
%>
<jsp:useBean id="descriptor" scope="request" class="es.altia.agora.interfaces.user.web.util.TraductorAplicacionBean"  type="es.altia.agora.interfaces.user.web.util.TraductorAplicacionBean" />
<jsp:setProperty name="descriptor"  property="idi_cod" value="<%=idioma%>" />
<jsp:setProperty name="descriptor"  property="apl_cod" value="<%=apl%>" />
<link rel="StyleSheet" media="screen" type="text/css" href="<%=request.getContextPath()%><%=css%>"/>
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
{{JAVASCRIPT_FUNCTIONS}}
</script>

<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/DataTables/datatables.min.js"></script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/scripts/DataTables/datatables.min.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/extension/melanbide11/melanbide11.css"/>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/validaciones.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/popup.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/extension/melanbide11/FixedColumnsTable.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/extension/meLanbide11/JavaScriptUtil.js"></script>
<script type="text/javascript">
    var APP_CONTEXT_PATH = '<%=request.getContextPath()%>';
</script>

<div class="tab-page" id="{{TAB_ID}}" style="height:520px; width: 100%;">
    <h2 class="tab" id="{{TAB_TITLE_ID}}"><%=meLanbide11I18n.getMensaje(idiomaUsuario,"{{I18N_KEY}}")%></h2> 
    <script type="text/javascript">tp1.addTabPage(document.getElementById("{{TAB_ID}}"));</script>
    <br/>
    <h2 class="legendAzul" id="{{MAIN_TITLE_ID}}"><%=meLanbide11I18n.getMensaje(idiomaUsuario,"{{MAIN_I18N_KEY}}")%></h2>
    <div>    
        <br>
        <div id="divGeneral">     
            <div id="{{TABLE_ID}}"  align="center"></div>
        </div>
        <br/><br>
        <div class="botonera" style="text-align: center;">
            <input type="button" id="{{BTN_NEW_ID}}" name="{{BTN_NEW_ID}}" class="botonGeneral"  value="<%=meLanbide11I18n.getMensaje(idiomaUsuario, "btn.nuevo")%>" onclick="{{NEW_FUNCTION}}();">
            <input type="button" id="{{BTN_MODIFY_ID}}" name="{{BTN_MODIFY_ID}}" class="botonGeneral" value="<%=meLanbide11I18n.getMensaje(idiomaUsuario, "btn.modificar")%>" onclick="{{MODIFY_FUNCTION}}();">
            <input type="button" id="{{BTN_DELETE_ID}}" name="{{BTN_DELETE_ID}}"   class="botonGeneral" value="<%=meLanbide11I18n.getMensaje(idiomaUsuario, "btn.eliminar")%>" onclick="{{DELETE_FUNCTION}}();">
        </div>
    </div>  
</div>

<script  type="text/javascript">
    var {{TABLE_VAR}};
    var {{LIST_VAR}} = new Array();
    var {{LIST_TABLE_VAR}} = new Array();

    {{INITIALIZATION_FUNCTION}}();

    <%  		
            {{VO_CLASS}} objectVO = null;
            List<{{VO_CLASS}}> List = null;
            if(request.getAttribute("{{LIST_ATTRIBUTE}}")!=null){
                List = (List<{{VO_CLASS}}>)request.getAttribute("{{LIST_ATTRIBUTE}}");
            }													
            if (List!= null && List.size() >0){
                for (int indice=0;indice<List.size();indice++)
                {
                    objectVO = List.get(indice);
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    
{{DATA_EXTRACTION}}
%>
                    {{LIST_VAR}}[<%=indice%>] = new Array();
{{DATA_ASSIGNMENT}}
<%
                }
            }
%>
        {{LIST_TABLE_VAR}} = {{LIST_VAR}};
        {{TABLE_VAR}}.lineas = {{LIST_TABLE_VAR}};
        {{TABLE_VAR}}.displayTabla();
</script>