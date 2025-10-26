<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>

<%@page import="es.altia.flexia.integracion.moduloexterno.melanbide11.i18n.MeLanbide11I18n" %>
<%@page import="es.altia.flexia.integracion.moduloexterno.melanbide11.vo.MinimisVO" %>
<%@page import="es.altia.flexia.integracion.moduloexterno.melanbide11.vo.DesplegableAdmonLocalVO" %>
<%@page import="es.altia.agora.business.escritorio.UsuarioValueObject" %>
<%@page import="es.altia.flexia.integracion.moduloexterno.melanbide11.util.ConstantesMeLanbide11" %>
<%@page import="es.altia.flexia.integracion.moduloexterno.melanbide11.util.ConfigurationParameter" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.List" %>
<%@page import="java.text.SimpleDateFormat" %>
<html>
    <head>
        <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%
            MinimisVO datModif = new MinimisVO();
            MinimisVO objectVO = new MinimisVO();
        
            String codOrganizacion = "";
            String nuevo = "";
        
            String expediente = "";
        
            String fecha = "";
        
            MeLanbide11I18n meLanbide11I18n = MeLanbide11I18n.getInstance();

            expediente = (String)request.getAttribute("numExp");
            int idiomaUsuario = 1;
            int apl = 5;
            String css = "";
            try
            {
                UsuarioValueObject usuario = new UsuarioValueObject();
                try
                {
                    if (session != null) 
                    {
                        if (usuario != null) 
                        {
                            usuario = (UsuarioValueObject) session.getAttribute("usuario");
                            idiomaUsuario = usuario.getIdioma();
                            apl = usuario.getAppCod();
                            css = usuario.getCss();
                        }
                    }
                }
                catch(Exception ex)
                {

                }

                codOrganizacion  = request.getParameter("codOrganizacionModulo");
                nuevo = (String)request.getAttribute("nuevo");
                if(request.getAttribute("datModif") != null)
                {
                    datModif = (MinimisVO)request.getAttribute("datModif");
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                
                    if (datModif.getFecha()!=null){
                        fecha = formatoFecha.format(datModif.getFecha());
                    }
                }
            }
            catch(Exception ex)
            {
            }
        %>

        <jsp:useBean id="descriptor" scope="request" class="es.altia.agora.interfaces.user.web.util.TraductorAplicacionBean"  type="es.altia.agora.interfaces.user.web.util.TraductorAplicacionBean" />
        <jsp:setProperty name="descriptor"  property="idi_cod" value="<%=idiomaUsuario%>" />
        <jsp:setProperty name="descriptor"  property="apl_cod" value="<%=apl%>" />
        <link rel="StyleSheet" media="screen" type="text/css" href="<%=request.getContextPath()%><%=css%>">
        <link rel="stylesheet" type="text/css" href="<c:url value='/css/font-awesome-4.6.2/css/font-awesome.min.css'/>" media="screen">
        <link rel="stylesheet" type="text/css" href="<c:url value='/css/font-awesome-4.6.2/less/animated.less'/>" media="screen">
        <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery/jquery-1.9.1.min.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/DataTables/datatables.min.js"></script>
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/scripts/DataTables/datatables.min.css"/>
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/extension/melanbide11/melanbide11.css"/>
        <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/general.js"></script>

        <script type="text/javascript" src="<c:url value='/scripts/calendario.js'/>"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/validaciones.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/popup.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/extension/melanbide11/JavaScriptUtil.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/extension/melanbide11/Parsers.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/extension/melanbide11/InputMask.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/extension/melanbide11/lanbide.js"></script>        
        <script type="text/javascript">
            var APP_CONTEXT_PATH = '<%=request.getContextPath()%>';
///////////////////////////////////////////////////////////////////////////////////////////////////////
// INICIO OBJETO COMBOBOX
///////////////////////////////////////////////////////////////////////////////////////////////////////

            CB_RowHeight = 19;
            CB_Borde = 1;
            CB_Scroll = 15;

            var cursor;
            var operadorConsulta = "";
            if (document.all)
                cursor = 'hand';
            else if (document.getElemenById)
                cursor = 'pointer';

            function CB_OcultarCombo(combo) {
                var cmb = document.getElementById('desc' + combo);
                if (cmb && cmb.combo) {
                    cmb.combo.ocultar();
                } else if (window["combo" + combo] != undefined) {
                    window["combo" + combo].ocultar();
                }
            }

            function Combo(nombre, idiomaUsuario) {
                this.id = nombre;
                this.idioma = 0;
                if (idiomaUsuario != undefined && idiomaUsuario != null) {
                    this.idioma = idiomaUsuario;
                }
                //Referenciamos los inputs del codigo y la descripcion.  
                var codigos = document.getElementsByName("cod" + nombre);
                var descripciones = document.getElementsByName("desc" + nombre);
                var anchors = document.getElementsByName("anchor" + nombre);

                if (codigos != null && codigos[0] != null)
                    this.cod = codigos[0];
                else {
                    this.cod = document.getElementById("cod" + nombre);
                }

                if (descripciones != null && descripciones[0] != null)
                    this.des = descripciones[0];
                else {
                    this.des = document.getElementById("desc" + nombre);
                }

                if (anchors != null && anchors[0] != null) {
                    this.anchor = anchors[0];
                } else
                    this.anchor = document.getElementById("anchor" + nombre);
                var hijos = new Array();
                hijos = this.anchor.children;
                if (hijos != null && hijos.length >= 1)
                    this.boton = hijos[0];

                this.selectedIndex = -1;
                this.timer = null;

                this.des.introducido = "";
                this.original = null;

                this.codigos = new Array();
                this.items = new Array();
                this.auxItems = new Array();
                this.auxCodigos = new Array();
                this.i_codigos = new Array();
                this.i_items = new Array();

                //Creamos la vista del combo, que será un DIV que incluirá la tabla con los elementos de la lista.
                this.base = document.createElement("DIV");
                this.base.combo = this;
                this.base.style.position = 'absolute';
                this.base.style.display = "none";
                this.base.onblur = function (event) {
                    var event = (event) ? event : ((window.event) ? window.event : "");
                    this.combo.timer = setTimeout('CB_OcultarCombo("' + this.combo.id + '")', 150);
                };
                this.base.onkeydown = function (event) {
                    var event = (event) ? event : ((window.event) ? window.event : "");
                    var tecla = "";

                    if (event.keyCode)
                        tecla = event.keyCode;
                    else
                        tecla = event.which;

                    if (tecla == 8) {
                        this.combo.buscaItem("-1");
                    } else if (tecla == 40) {
                        this.combo.selectItem(this.combo.selectedIndex + 1);
                    } else if (tecla == 38) {
                        this.combo.selectItem(this.combo.selectedIndex - 1);
                    } else if (tecla == 13) {
                        this.combo.ocultar();
                        if (this.combo.cod)
                            this.combo.cod.select();
                        else
                            this.combo.des.select();
                    } else {
                        if (tecla > 95)
                            tecla = tecla - 48;
                        var letra = String.fromCharCode(tecla);
                        this.combo.buscaItem(letra);
                    }
                    if (window.event) {
                        event.cancelBubble = true;
                        event.returnValue = false;
                    } else {
                        event.stopPropagation();
                        event.preventDefault();
                    }
                    return false;
                };

                this.view = document.createElement("DIV");
                this.base.appendChild(this.view);
                this.view.combo = this;
                this.view.className = 'xC';
                this.view.style.overflowY = 'auto';
                this.view.style.position = 'relative';
                this.view.onselectstart = function () {
                    return false;
                };
                this.view.ondblclick = function () {
                    return false;
                };
                this.view.onclick = function (event) {
                    event = (event) ? event : ((window.event) ? window.event : "");

                    var padre = "";
                    if (window.event)
                        padre = event.srcElement;
                    else
                        padre = event.target;

                    if (padre.tagName != 'DIV')
                        return;
                    var rowID = 1;

                    if (!!navigator.userAgent.match(/Trident.*rv[ :]*11\./) || navigator.appName.indexOf("Internet Explorer") != -1) {
                        // Se calcula la posición de item del combo que ha sido seleccionado
                        var i = padre.parentElement.sourceIndex;
                        var j = padre.sourceIndex;
                        rowID = (j - i - 1);

                    } else {
                        // Firefox u otro navegador

                        /** Se obtiene el valor del item de menú seleccionado, para a partir de él, obtener la posición en el combo y seleccionar dicha fila **/
                        var hijos = padre.childNodes;
                        var valorFilaSeleccionada = "";
                        if (hijos != null) {
                            valorFilaSeleccionada = hijos[0].nodeValue;
                        }

                        var padreRaiz = padre.parentNode;
                        var hijosRaiz = padreRaiz.childNodes;
                        for (z = 0; z < hijosRaiz.length; z++) {
                            var nietos = hijosRaiz[z].childNodes;
                            if (nietos != null && nietos.length > 0) {
                                if (nietos[0].nodeValue == valorFilaSeleccionada) {
                                    break;
                                }
                            }
                        }
                        // En z está la posición de la fila seleccionada por el usuario
                        rowID = z;
                    }// else       

                    this.combo.selectItem(rowID);
                    this.combo.ocultar();
                    if (this.combo.cod)
                        this.combo.cod.select();
                    else
                        this.combo.des.select();
                    return false;
                };
                this.view.onfocus = function (event) {
                    event = (event) ? event : ((window.event) ? window.event : "");
                    if (this.combo.timer != null)
                        clearTimeout(this.combo.timer);
                    this.combo.timer = null;
                    this.combo.base.focus();
                };

                //*************************************************  
                this.resize = CB_resize;

                this.addItems = CB_addItems;
                this.addItems2 = CB_addItems2;
                this.clearItems = CB_clearItems;
                this.restoreIndex = CB_restoreIndex;
                this.selectItem = CB_selectItem;
                this.buscaCodigo = CB_buscaCodigo;
                this.buscaItem = CB_buscaItem;
                this.scroll = CB_scroll;
                this.display = CB_display;
                this.ocultar = CB_ocultar;
                this.init = CB_init;
                this.activate = CB_activate;
                this.deactivate = CB_deactivate;
                this.obligatorio = CB_obligatorio;
                this.buscaLinea = CB_buscaLinea;
                this.contieneOperadoresConsulta = CB_contieneOperadoresConsulta;
                this.clearSelected = CB_clearSelected;
                this.change = function () {};

                this.init();
            }

            function CB_init() {
                //Guardamos una referencia del combo en el imput de la descripcion
                if (this.cod) {
                    this.cod.combo = this;
                    this.cod.onfocus = function () {
                        this.select();
                    };
                    this.cod.onblur = function (event) {
                        if (!this.combo.contieneOperadoresConsulta(this))
                            this.combo.buscaCodigo(this.value);
                        else {
                            var codOld = this.value;
                            this.combo.selectItem(-1);
                            this.value = codOld;
                            this.combo.change();
                        }
                    };
                    this.cod.onkeydown = function (event) {
                        var event = (event) ? event : ((window.event) ? window.event : "");
                        var tecla = "";
                        if (event.keyCode)
                            tecla = event.keyCode;
                        else
                            tecla = event.which;
                        if (tecla == 40) {
                            this.combo.selectItem(this.combo.selectedIndex + 1);
                            if (window.event) {
                                event.cancelBubble = true;
                                event.returnValue = false;
                            } else {
                                event.stopPropagation = true;
                                event.preventDefault = false;
                            }
                        } else if (tecla == 38) {
                            this.combo.selectItem(this.combo.selectedIndex - 1);
                            if (window.event) {
                                event.cancelBubble = true;
                                event.returnValue = false;
                            } else {
                                event.stopPropagation = true;
                                event.preventDefault = false;
                            }
                        } else if (tecla == 13) {

                            this.combo.display();
                            if (window.event) {
                                event.cancelBubble = true;
                                event.returnValue = false;
                            } else {
                                event.stopPropagation = true;
                                event.preventDefault = false;
                            }
                        } else if (tecla == 9)
                        {
                            this.combo.ocultar();
                        }

                    };
                }

                if (this.des != null)
                    this.des.combo = this;

                this.des.onfocus = function () {
                    this.select();
                };
                this.des.onclick = function (event) {
                    this.introducido = "";
                    if (this.combo.auxCodigos.length > 0)
                        this.combo.addItems(this.combo.auxCodigos, this.combo.auxItems);

                    event = (event) ? event : ((window.event) ? window.event : "");

                    if (this.combo.cod) {

                        if (!this.combo.cod.readOnly) {

                            if (!this.combo.contieneOperadoresConsulta(this.combo.cod))
                                this.combo.display();
                        }
                    } else
                    {
                        this.combo.display();
                    }
                    event.stopPropagation();
                    return false;
                };

                this.des.onkeydown = function (event) {
                    event = (event) ? event : ((window.event) ? window.event : "");
                    var tecla = "";
                    if (event.keyCode)
                        tecla = event.keyCode;
                    else
                        tecla = event.which;

                    if (tecla == 8) {
                        this.combo.buscaItem("-1");
                        if (window.event) {
                            event.cancelBubble = true;
                            event.returnValue = false;
                        } else {
                            event.stopPropagation();
                            event.preventDefault();
                        }
                    } else if (tecla == 40) {
                        this.combo.selectItem(this.combo.selectedIndex + 1);
                        if (window.event) {
                            event.cancelBubble = true;
                            event.returnValue = false;
                        } else {
                            event.stopPropagation();
                            event.preventDefault();
                        }
                    } else if (tecla == 38) {
                        this.combo.selectItem(this.combo.selectedIndex - 1);
                        if (window.event) {
                            event.cancelBubble = true;
                            event.returnValue = false;
                        } else {
                            event.stopPropagation();
                            event.preventDefault();
                        }
                    } else if (tecla == 13) {
                        this.combo.display();
                        if (window.event) {
                            event.cancelBubble = true;
                            event.returnValue = false;
                        } else {
                            event.stopPropagation();
                            event.preventDefault();
                        }
                    } else if (tecla == 9) {
                        this.combo.ocultar();
                    }
                };

                this.des.onkeypress = function (event) {
                    var event = (event) ? event : ((window.event) ? window.event : "");
                    var tecla = "";
                    if (event.keyCode)
                        tecla = event.keyCode;
                    else
                        tecla = event.which;
                    var letra = String.fromCharCode(tecla);
                    if (this.readOnly)
                        this.combo.buscaItem(letra);
                };

                this.des.onblur = function (event) {
                    if (!this.readOnly && this.value.length != 0) {
                        if (this.combo.cod) {
                            if (!this.combo.contieneOperadoresConsulta(this))
                                this.combo.buscaCodigo(this.combo.cod.value);

                            else {
                                var codOld = this.value;
                                this.combo.selectItem(-1);
                                this.value = codOld;
                                this.combo.change();
                            }
                        }
                    }
                    var isChromium = window.chrome,
                            vendorName = window.navigator.vendor,
                            isOpera = window.navigator.userAgent.indexOf("OPR") > -1;
                    if (isChromium !== null && isChromium !== undefined && vendorName === "Google Inc." && isOpera == false) {
                        this.combo.timer = setTimeout('CB_OcultarCombo("' + this.combo.id + '")', 150);
                    } else if (navigator.userAgent.indexOf("Firefox") > 0) {
                        this.combo.timer = setTimeout('CB_OcultarCombo("' + this.combo.id + '")', 150);
                    }

                };

                if (this.anchor) {
                    this.anchor.combo = this;
                    this.anchor.onkeydown = function (event) {

                        var event = (event) ? event : ((window.event) ? window.event : "");
                        var tecla = "";
                        if (event.keyCode)
                            tecla = event.keyCode;
                        else
                            tecla = event.which;

                        //if(event.keyCode == 40){
                        if (tecla == 40) {
                            this.combo.selectItem(this.combo.selectedIndex + 1);
                            if (window.event) {
                                event.cancelBubble = true;
                                event.returnValue = false;
                            } else {
                                event.stopPropagation = true;
                                event.preventDefault = false;
                            }
                        } else if (tecla == 38) {
                            this.combo.selectItem(this.combo.selectedIndex - 1);
                            if (window.event) {
                                event.cancelBubble = true;
                                event.returnValue = false;
                            } else {
                                event.stopPropagation = true;
                                event.preventDefault = false;
                            }
                        } else if (tecla == 13) {

                            this.combo.display();
                            if (window.event) {
                                event.cancelBubble = true;
                                event.returnValue = false;
                            } else {
                                event.stopPropagation = true;
                                event.preventDefault = false;
                            }
                        } else if (tecla == 9)
                        {
                            this.combo.ocultar();
                        }
                    };
                    this.anchor.onclick = function (event) {
                        if (this.combo.cod) {
                            if (!this.combo.contieneOperadoresConsulta(this.combo.cod))
                                this.combo.display();
                        } else
                            this.combo.display();
                        event.stopPropagation();
                        return false;
                    };
                }
                var contenidoPantalla = document.getElementsByClassName("contenidoPantalla")[0];
                if (contenidoPantalla) {
                    contenidoPantalla.appendChild(this.base);
                } else {
                    // Fallback to body if contenidoPantalla doesn't exist
                    document.body.appendChild(this.base);
                }
                this.addItems([], []);
            }

            // Add all the CB_ functions here
            function CB_buscaCodigo(cod) {
                if (cod == null || cod == undefined)
                    return true;
                var str = cod;
                if (str == '') {
                    this.selectItem(0);
                } else if (this.codigos[this.selectedIndex] != str) {
                    var i = this.i_codigos[str + ''];
                    if (i != null && i != undefined) {
                        this.selectItem(i);
                    } else {
                        if (this.des.readOnly)
                            jsp_alerta('A', 'Código inexistente');
                        this.selectItem(-1);
                        return false;
                    }
                }
                return true;
            }

            function CB_buscaLinea(cod) {
                if (cod == null || cod == 'undefined')
                    return true;
                var str = cod;

                if (this.selectedIndex >= 0 && this.selectedIndex < this.items.length) {
                    if (this.view.children[this.selectedIndex].className != 'xCDisabled') {
                        this.view.children[this.selectedIndex].className = 'xCSelected';
                    }
                }

                if (str == '') {
                    this.selectedIndex = 0;
                } else if (this.codigos[this.selectedIndex] != str) {
                    var i = this.i_codigos[str + ''];
                    if (i != null && i != undefined) {
                        this.selectedIndex = i;
                    } else {
                        this.selectedIndex = -1;
                    }
                }

                if (this.selectedIndex >= 0 && this.selectedIndex < this.items.length) {
                    if (this.view.children[this.selectedIndex].className != 'xCDisabled') {
                        this.view.children[this.selectedIndex].className = 'xCSelected';
                    }
                    this.scroll();
                    if (this.cod)
                        this.cod.value = this.codigos[this.selectedIndex];
                    this.des.value = this.items[this.selectedIndex];
                } else {
                    if (this.cod)
                        this.cod.value = "";
                    this.des.value = "";
                }

                return true;
            }

            function quitarTildes(st) {
                st = st.replace(new RegExp(/[àáâãäå]/g), "a");
                st = st.replace(new RegExp(/[èéêë]/g), "e");
                st = st.replace(new RegExp(/[ìíîï]/g), "i");
                st = st.replace(new RegExp(/[òóôõö]/g), "o");
                st = st.replace(new RegExp(/[ùúûü]/g), "u");

                return st;
            }

            function CB_buscaItem(letra) {
                if (letra) {
                    if (letra == "-1") {
                        if (this.des.introducido.length > 0)
                            this.des.introducido = this.des.introducido.substr(0, this.des.introducido.length - 1);
                        if (this.auxItems.length > 0) {
                            this.items = this.auxItems;
                            this.codigos = this.auxCodigos;
                        }
                    } else {
                        var regex = new RegExp("[a-z]");
                        if (regex.test(letra))
                            letra = letra.toUpperCase();
                        this.des.introducido += letra;
                    }
                }
                if (this.des.introducido == "") {
                    this.selectItem(0);
                    return true;
                }
                this.des.value = this.des.introducido;
                var novoItems = [];
                var novoCodigos = [];

                for (var i = 0; i < this.items.length; i++) {
                    // se fuerza a que sea string (en el pantallaCatalogacion.jsp de catalogación no hizo falta)
                    var itemTemp = this.items[i].toString().toLowerCase();
                    itemTemp = quitarTildes(itemTemp).toUpperCase();
                    //if (this.items[i].toString().toUpperCase().indexOf(this.des.introducido.toUpperCase()) >= 0) {
                    if (itemTemp.toUpperCase().indexOf(this.des.introducido.toUpperCase()) >= 0) {
                        novoItems.push(this.items[i]);
                        novoCodigos.push(this.codigos[i]);
                    }
                }
                if (this.auxItems.length == 0) {
                    this.auxItems = this.items;
                    this.auxCodigos = this.codigos;
                }
                this.addItems(novoCodigos, novoItems);

                return true;
            }

            function CB_display() {
                if (this.base.style.display != "") {
                    this.resize();
                    this.original = this.selectedIndex;
                    this.base.style.display = "";
                    if (this.cod)
                        this.buscaCodigo(this.cod.value);
                    else {
                        for (i = 0; i < this.items.length; i++) {
                            if (this.items[i].toUpperCase() == this.des.value.toUpperCase()) {
                                this.selectItem(i);
                                break;
                            }
                        }
                        if (this.selectedIndex < 0)
                            this.selectItem(0);
                    }
                    this.base.focus();
                } else
                    this.base.style.display = "none";
            }

            function CB_ocultar() {
                if (this.selectedIndex >= this.items.length)
                    this.selectedIndex = -1;
                this.base.style.display = "none";
                if (this.selectedIndex != this.original)
                    this.change();
                this.original = this.selectedIndex;
            }

            function CB_resize() {
                var alto = 0;
                var altoElemento = this.des.offsetHeight;
                var altoVentana = document.documentElement.clientHeight;
                var altoEncima = getOffsetTop(this.des);
                var altoDebajo = altoVentana - (altoEncima + altoElemento);
                var altoMayor = (altoDebajo > altoEncima ? altoDebajo : altoEncima);
                var numItems = this.items.length;
                var maxi = ((10 * CB_RowHeight) + 1) + (2 * CB_Borde) + CB_Scroll;
                var maxDiv = (maxi < altoDebajo ? maxi : (maxi < altoEncima ? maxi : altoMayor));
                var ctrlMayor = (maxi < altoDebajo ? 1 : (maxi < altoEncima ? -1 : (altoDebajo > altoEncima ? 2 : -2)));

                if (numItems > 10)
                    numItems = 10;

                for (var i = 0; i < numItems; i++) {
                    if ((alto + CB_RowHeight) < maxDiv)
                        alto += CB_RowHeight;
                }
                if (numItems == 0)
                    alto = CB_RowHeight;
                pX = getOffsetLeft(this.des);
                pY = (((ctrlMayor == 1) || (ctrlMayor == 2)) ? altoEncima + altoElemento : altoEncima - (alto + 2 * CB_Borde + CB_Scroll));
                if (isTabPage(this.des)) {
                    pX++;
                    pY++;
                }

                var contenidoPantalla = document.getElementsByClassName("contenidoPantalla")[0];
                var offsetTop = contenidoPantalla ? contenidoPantalla.getBoundingClientRect().top : 0;

                if (typeof (this.base.style.posTop) !== "undefined") //es IE 9
                {
                    this.base.style.posLeft = pX;
                    this.base.style.posTop = pY - offsetTop;
                    this.base.style.posHeight = this.view.style.posHeight = (alto + 2 * CB_Borde + CB_Scroll);
                    this.base.style.posWidth = this.view.style.posWidth = this.des.offsetWidth + ((this.view.scrollHeight == this.view.clientHeight) ? 0 : 16);
                } else {
                    this.base.style.left = +pX + "px";
                    this.base.style.top = pY - offsetTop + "px";
                    this.base.style.height = this.view.style.height = (alto + 2 * CB_Borde + CB_Scroll) + "px";
                    this.base.style.width = this.view.style.width = this.des.offsetWidth + ((this.view.scrollHeight == this.view.clientHeight) ? 0 : 16) + "px";
                }
            }

            function CB_restoreIndex() {
                this.selectedIndex = -1;
            }

            function CB_obligatorio(esObligatorio) {
                if (esObligatorio) {
                    if ('inputTextoObligatorio' != this.des.className) {
                        this.codigos.shift();
                        this.items.shift();
                        if (this.cod)
                            this.cod.className = 'inputTextoObligatorio';
                        this.des.className = 'inputTextoObligatorio';
                        if (this.selectedIndex > 0)
                            this.selectedIndex--;
                    } else {
                        return;
                    }
                } else {
                    if ('inputTextoObligatorio' == this.des.className) {
                        this.codigos = [""].concat(this.codigos);
                        this.items = [""].concat(this.items);
                        if (this.cod)
                            this.cod.className = 'inputTexto';
                        this.des.className = 'inputTexto';
                        if (this.selectedIndex >= 0)
                            this.selectedIndex++;
                    } else {
                        return;
                    }
                }
                var str = '';
                for (var i = 0; i < this.codigos.length; i++) {
                    this.i_codigos[this.codigos[i] + ''] = i;
                    str += '<DIV>' + ((this.items[i]) ? this.items[i] : '&nbsp;') + '</DIV>';
                }
                this.view.innerHTML = str;
                this.selectItem(this.selectedIndex);
                return;
            }

            function CB_addItems(listaCodigos, listaItems) {
                this.codigos = listaCodigos;
                this.items = listaItems;
                if (this.des.className.indexOf('inputTextoObligatorio') < 0) {
                    this.codigos = [""].concat(this.codigos);
                    this.items = [""].concat(this.items);
                } else if (this.codigos == null || this.codigos.length == 0) {
                    this.codigos = [""];
                    this.items = [""];
                }
                var str = '';
                for (var i = 0; i < this.codigos.length; i++) {
                    this.i_codigos[this.codigos[i] + ''] = i;

                    if (this.items[i]) {
                        var auxItem = (this.items[i]);
                        if (this.idioma > 1) {
                            if (auxItem.indexOf("|") > -1)
                                auxItem = auxItem.split("|")[1];
                        } else if (this.idioma == 1) {
                            auxItem = auxItem.split("|")[0];
                        }
                        this.items[i] = auxItem;
                    }
                    str += '<DIV>' + ((this.items[i]) ? this.items[i] : '&nbsp;') + '</DIV>';
                }
                this.view.innerHTML = str;
                this.selectedIndex = -1;
                return true;
            }

            function CB_addItems2(listaCodigos, listaItems, listaEstados) {
                this.codigos = listaCodigos;
                this.items = listaItems;
                this.estados = listaEstados;
                var estados = listaEstados;
                if (this.des.className.indexOf('inputTextoObligatorio') < 0) {
                    this.codigos = [""].concat(this.codigos);
                    this.items = [""].concat(this.items);
                    this.estados = [""].concat(this.estados);
                } else if (this.codigos == null || this.codigos.length == 0) {
                    this.codigos = [""];
                    this.items = [""];
                    this.estados = [""];
                }
                var str = '';
                for (var i = 0; i < this.codigos.length; i++) {
                    this.i_codigos[this.codigos[i] + ''] = i;
                    if (this.items[i]) {
                        var auxItem = (this.items[i]);
                        if (this.idioma > 1) {
                            if (auxItem.indexOf("|") > -1)
                                auxItem = auxItem.split("|")[1];
                        } else if (this.idioma == 1) {
                            auxItem = auxItem.split("|")[0];
                        }
                        this.items[i] = auxItem;
                    }
                    var est = estados[i];
                    if (this.estados[i] != "B") {
                        str += '<DIV>' + ((this.items[i]) ? this.items[i] : '&nbsp;') + '</DIV>';
                    } else {
                        str += '<DIV  class="xCDisabled">' + ((this.items[i]) ? this.items[i] : '&nbsp;') + '</DIV>';
                    }
                }
                this.view.innerHTML = str;
                this.selectedIndex = -1;
                return true;
            }

            function CB_clearItems() {
                this.addItems([""], [""]);
                if (this.cod)
                    this.cod.value = '';
                this.des.value = '';
            }

            function CB_selectItem(rowID) {
                arglen = arguments.length;
                var old = this.selectedIndex;
                var index = (arglen != 0) ? rowID : this.selectedIndex;
                if (this.selectedIndex >= 0 && this.selectedIndex < this.items.length) {
                    if (this.view.children[this.selectedIndex].className != 'xCDisabled') {
                        this.view.children[this.selectedIndex].className = 'xC';
                    } else {
                        var disabled = this.selectedIndex;
                    }
                }
                if (index >= 0 && index < this.items.length && this.view.children[index].className != 'xCDisabled') {
                    this.view.children[index].className = 'xCSelected';
                    this.selectedIndex = index;
                    this.scroll();
                    if (this.cod)
                        this.cod.value = this.codigos[index];
                    this.des.value = this.items[index];
                } else if (index >= 0 && this.view.children[index].className == 'xCDisabled') {
                    if (old > 0) {
                        this.selectedIndex = old;
                        this.scroll();
                        if (this.cod)
                            this.cod.value = this.codigos[old];
                        this.des.value = this.items[old];
                    } else {
                        this.selectedIndex = -1;
                        if (this.cod)
                            this.cod.value = "";
                        this.des.value = "";
                    }
                    if (this.view.children[this.selectedIndex].className != 'xCDisabled') {
                        this.view.children[this.selectedIndex].className = 'xCSelected';
                    }
                } else {
                    if (index < 0) {
                        this.selectedIndex = -1;
                    } else if (index >= this.items.length)
                        this.selectedIndex = this.items.length;
                    if (this.cod)
                        this.cod.value = "";
                    this.des.value = "";
                }
                if (this.selectedIndex != old && this.base.style.display != '' && this.selectedIndex != disabled)
                    this.change();
            }

            function CB_scroll() {
                var selRow = this.view.children[this.selectedIndex];
                var selDiv = this.view;

                if (selRow.offsetTop < selDiv.scrollTop)
                    selDiv.scrollTop = selRow.offsetTop;
                else if (selRow.offsetTop > (selDiv.scrollTop + selDiv.clientHeight - selRow.offsetHeight))
                    selDiv.scrollTop = (selRow.offsetTop - selDiv.clientHeight + selRow.offsetHeight);
            }

            function CB_activate() {
                var clase = new Array();
                if (this.cod) {
                    clase = this.cod.className.split(" ");
                    if (clase[clase.length - 1] == "inputTextoDeshabilitado") {
                        this.cod.disabled = false;
                        CB_removeClass(this.cod);
                    }
                }

                clase = this.des.className.split(" ");
                if (clase[clase.length - 1] == "inputTextoDeshabilitado") {
                    this.des.disabled = false;
                    CB_removeClass(this.des);
                }

                if (this.anchor) {
                    this.anchor.disabled = false;
                    this.anchor.onclick = function () {
                        this.combo.display();
                        return false;
                    };
                }

                if (this.boton) {
                    this.boton.style.cursor = 'hand';
                    this.boton.className = this.boton.className.replace(new RegExp('(?:^|\\s)' + 'faDeshabilitado' + '(?:\\s|$)'), "");
                }
            }

            function CB_deactivate() {
                var clase = new Array();
                if (this.cod) {
                    clase = this.cod.className.split(" ");
                    if (clase[clase.length - 1] != "inputTextoDeshabilitado") {
                        this.cod.disabled = true;
                        this.cod.className += " inputTextoDeshabilitado";
                    }
                }

                clase = this.des.className.split(" ");
                if (clase[clase.length - 1] != "inputTextoDeshabilitado") {
                    this.des.disabled = true;
                    this.des.className += " inputTextoDeshabilitado";
                }

                if (this.anchor) {
                    this.anchor.disabled = true;
                    this.anchor.onclick = function () {
                        return false;
                    };
                }

                if (this.boton) {
                    this.boton.style.cursor = 'default';
                    if (this.boton.className.indexOf("faDeshabilitado") < 0)
                        this.boton.className += " faDeshabilitado";
                }
            }

            function CB_removeClass(ele) {
                var clase = ele.className.split(" ");
                if (clase.length > 1) {
                    ele.className = "";
                    for (i = 0; i < clase.length - 1; i++) {
                        if (i == 0)
                            ele.className += clase[i];
                        else
                            ele.className += " " + clase[i];
                    }
                }
            }

            function CB_contieneOperadoresConsulta(campo) {
                var contiene = false;
                var v = campo.value;
                for (i = 0; i < v.length; i++) {
                    var c = v.charAt(i);
                    if (operadorConsulta.indexOf(c) != -1)
                        contiene = true;
                }
                return contiene;
            }

            function CB_clearSelected() {
                this.buscaLinea(-1);

                if (this.items) {
                    for (var i = 0; i < this.items.length; i++) {
                        this.view.children[i].className = '';
                    }
                }

                return true;
            }

            function getOffsetLeft(el) {
                var ol = el.offsetLeft;
                while ((el = el.offsetParent) != null)
                    ol += el.offsetLeft;
                return ol;
            }

            function getOffsetTop(el) {
                var ot = el.offsetTop;
                while ((el = el.offsetParent) != null)
                    ot += el.offsetTop;
                return ot;
            }

            function isTabPage(el) {
                var pane = false;
                while ((el = el.parentElement) != null) {
                    if (el.className == 'tab-page')
                        pane = true;
                }
                return pane;
            }

            function CB_addElement(lista, elemento) {
                var i = lista.length;
                lista[i] = elemento;
            }

            function CB_deleteElement(lista, index) {
                if (index < 0 || index >= lista.length)
                    return null;
                var val = lista[index];
                var i, j;
                for (i = eval(index); i < (lista.length - 1); i++) {
                    j = i + 1;
                    lista[i] = lista[j];
                }
                lista.length--;
                return val;
            }

///////////////////////////////////////////////////////////////////////////////////////////////////////
// FIN OBJETO COMBO
///////////////////////////////////////////////////////////////////////////////////////////////////////

            var mensajeValidacion = '';

            var comboListaEstado;
            var listaCodigosEstado = new Array();
            var listaDescripcionesEstado = new Array();
            function buscaCodigoEstado(codEstado) {
                comboListaEstado.buscaCodigo(codEstado);
            }
            function cargarDatosEstado() {
                var codEstadoSeleccionado = document.getElementById("codListaEstado").value;
                buscaCodigoEstado(codEstadoSeleccionado);
            }


            function reemplazarPuntos(campo) {
                try {
                    var valor = campo.value;
                    if (valor != null && valor != '') {
                        valor = valor.replace(/\./g, ',');
                        campo.value = valor;
                    }
                } catch (err) {
                }
            }

            function rellenardatModificar() {
                if ('<%=datModif%>' != null) {
                    buscaCodigoEstado('<%=datModif.getEstado() != null ? datModif.getEstado() : ""%>');
                } else
                    alert('No hemos podido cargar los datos para modificar');
            }

            function getXMLHttpRequest() {
                var aVersions = ["MSXML2.XMLHttp.5.0",
                    "MSXML2.XMLHttp.4.0", "MSXML2.XMLHttp.3.0",
                    "MSXML2.XMLHttp", "Microsoft.XMLHttp"
                ];

                if (window.XMLHttpRequest) {
                    // para IE7, Mozilla, Safari, etc: que usen el objeto nativo
                    return new XMLHttpRequest();
                } else if (window.ActiveXObject) {
                    // de lo contrario utilizar el control ActiveX para IE5.x y IE6.x
                    for (var i = 0; i < aVersions.length; i++) {
                        try {
                            var oXmlHttp = new ActiveXObject(aVersions[i]);
                            return oXmlHttp;
                        } catch (error) {
                            //no necesitamos hacer nada especial
                        }
                    }
                } else {
                    return null;
                }
            }

            function guardarDatos() {

                if (validarDatosNumericosVacios() && validarDatos()) {
                    var ajax = getXMLHttpRequest();
                    var nodos = null;
                    var url = APP_CONTEXT_PATH + "/PeticionModuloIntegracion.do";
                    var parametros = "";
                    var nuevo = "<%=nuevo%>";
                    var organismo = "";
                    var objeto = "";
                    if (document.getElementById('organismo').value == null || document.getElementById('organismo').value == '') {
                        organismo = "";
                    } else {
                        organismo = document.getElementById('organismo').value.replace(/\'/g, "''");
                    }
                    if (document.getElementById('objeto').value == null || document.getElementById('objeto').value == '') {
                        objeto = "";
                    } else {
                        objeto = document.getElementById('objeto').value.replace(/\'/g, "''");
                    }

                    if (nuevo != null && nuevo == "1") {
                        parametros = "tarea=preparar&modulo=MELANBIDE11&operacion=crearNuevaMinimis&tipo=0"
                                + "&expediente=<%=expediente%>"
                                + "&estado=" + document.getElementById('codListaEstado').value
                                + '&organismo=' + organismo
                                + '&objeto=' + objeto
                                + "&importe=" + document.getElementById('importe').value
                                + "&fecha=" + document.getElementById('fecha').value
                                ;
                    } else {
                        parametros = "tarea=preparar&modulo=MELANBIDE11&operacion=modificarMinimis&tipo=0"
                                + "&id=<%=datModif != null && datModif.getId() != null ? datModif.getId().toString() : ""%>"
                                + "&estado=" + document.getElementById('codListaEstado').value
                                + '&organismo=' + organismo
                                + '&objeto=' + objeto
                                + "&importe=" + document.getElementById('importe').value
                                + "&fecha=" + document.getElementById('fecha').value
                                ;
                    }
                    try {
                        ajax.open("POST", url, false);
                        ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                        ajax.setRequestHeader("Accept", "text/xml, application/xml, text/plain");
                        ajax.send(parametros);
                        if (ajax.readyState == 4 && ajax.status == 200) {
                            var xmlDoc = null;
                            if (navigator.appName.indexOf("Internet Explorer") != -1) {
                                // En IE el XML viene en responseText y no en la propiedad responseXML
                                var text = ajax.responseText;
                                xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
                                xmlDoc.async = "false";
                                xmlDoc.loadXML(text);
                            } else {
                                // En el resto de navegadores el XML se recupera de la propiedad responseXML
                                xmlDoc = ajax.responseXML;
                            }//if(navigator.appName.indexOf("Internet Explorer")!=-1)
                        }//if (ajax.readyState==4 && ajax.status==200)
                        nodos = xmlDoc.getElementsByTagName("RESPUESTA");
                        var elemento = nodos[0];
                        var hijos = elemento.childNodes;
                        var codigoOperacion = null;
                        var lista = new Array();
                        var fila = new Array();
                        var nodoFila;
                        var hijosFila;
                        for (j = 0; hijos != null && j < hijos.length; j++) {
                            if (hijos[j].nodeName == "CODIGO_OPERACION") {
                                codigoOperacion = hijos[j].childNodes[0].nodeValue;
                                lista[j] = codigoOperacion;
                            }//if(hijos[j].nodeName=="CODIGO_OPERACION")                      
                            else if (hijos[j].nodeName == "FILA") {
                                nodoFila = hijos[j];
                                hijosFila = nodoFila.childNodes;
                                for (var cont = 0; cont < hijosFila.length; cont++) {
                                    if (hijosFila[cont].nodeName == "ID") {
                                        if (hijosFila[cont].childNodes.length > 0) {
                                            fila[0] = hijosFila[cont].childNodes[0].nodeValue;
                                        } else {
                                            fila[0] = '-';
                                        }
                                    } else if (hijosFila[cont].nodeName == "ESTADO") {
                                        if (hijosFila[cont].childNodes[0].nodeValue != "null") {
                                            fila[1] = hijosFila[cont].childNodes[0].nodeValue;
                                        } else {
                                            fila[1] = '-';
                                        }
                                    } else if (hijosFila[cont].nodeName == "ORGANISMO") {
                                        if (hijosFila[cont].childNodes.length > 0) {
                                            fila[2] = hijosFila[cont].childNodes[0].nodeValue;
                                        } else {
                                            fila[2] = '-';
                                        }
                                    } else if (hijosFila[cont].nodeName == "OBJETO") {
                                        if (hijosFila[cont].childNodes.length > 0) {
                                            fila[3] = hijosFila[cont].childNodes[0].nodeValue;
                                        } else {
                                            fila[3] = '-';
                                        }
                                    } else if (hijosFila[cont].nodeName == "IMPORTE") {
                                        if (hijosFila[cont].childNodes[0].nodeValue != "null") {
                                            fila[4] = hijosFila[cont].childNodes[0].nodeValue;
                                            var tex = fila[4].toString();
                                            tex = tex.replace(".", ",");
                                            fila[4] = tex;
                                        } else {
                                            fila[4] = '-';
                                        }
                                    } else if (hijosFila[cont].nodeName == "FECHA") {
                                        if (hijosFila[cont].childNodes.length > 0) {
                                            fila[5] = hijosFila[cont].childNodes[0].nodeValue;
                                        } else {
                                            fila[5] = '-';
                                        }
                                    }
                                }// for elementos de la fila
                                lista[j] = fila;
                                fila = new Array();
                            }
                        }//for(j=0;hijos!=null && j<hijos.length;j++)
                        if (codigoOperacion == "0") {
                            //jsp_alerta("A",'Correcto');
                            self.parent.opener.retornoXanelaAuxiliar(lista);
                            cerrarVentana();
                        } else if (codigoOperacion == "1") {
                            jsp_alerta("A", '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"error.errorBD")%>');
                        } else if (codigoOperacion == "2") {
                            jsp_alerta("A", '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"error.errorGen")%>');
                        } else if (codigoOperacion == "3") {
                            jsp_alerta("A", '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"error.pasoParametros")%>');
                        } else {
                            jsp_alerta("A", '<%=meLanbide11I18n.getMensaje(idiomaUsuario,"error.errorGen")%>');
                        }
                    } catch (Err) {
                    }//try-catch
                } else {
                    jsp_alerta("A", mensajeValidacion);
                }
            }

            function cancelar() {
                var resultado = jsp_alerta('', '<%=meLanbide11I18n.getMensaje(idiomaUsuario, "msg.preguntaCancelar")%>');
                if (resultado == 1) {
                    cerrarVentana();
                }
            }

            function cerrarVentana() {
                if (navigator.appName == "Microsoft Internet Explorer") {
                    window.parent.window.opener = null;
                    window.parent.window.close();
                } else if (navigator.appName == "Netscape") {
                    top.window.opener = top;
                    top.window.open('', '_parent', '');
                    top.window.close();
                } else {
                    window.close();
                }
            }

            function validarDatosNumericosVacios() {
                mensajeValidacion = "";
                var correcto = true;
                if (document.getElementById('importe').value == null || document.getElementById('importe').value == '') {
                } else {
                    if (!validarNumericoDecimalPrecision(document.getElementById('importe').value, 8, 2)) {
                        mensajeValidacion = '<%=meLanbide11I18n.getMensaje(idiomaUsuario, "msg.importe.errNumerico")%>';
                        return false;
                    }
                }
                return correcto;
            }

            function validarNumericoDecimalPrecision(numero, longTotal, longParteDecimal) {
                try {
                    var longParteEntera = parseInt(longTotal) - parseInt(longParteDecimal);
                    if (Trim(numero) != '') {
                        var valor = numero;
                        var pattern = '^[0-9]{1,' + longParteEntera + '}(,[0-9]{1,' + longParteDecimal + '})?$';
                        var regex = new RegExp(pattern);
                        var result = regex.test(valor);
                        return result;
                    } else {
                        return true;
                    }
                } catch (err) {
                    alert(err);
                    return false;
                }
            }

            function validarNumerico(numero) {
                try {
                    if (Trim(numero.value) != '') {
                        return /^([0-9])*$/.test(numero.value);
                    }
                } catch (err) {
                    return false;
                }
            }

            function validarDatos() {
                mensajeValidacion = "";
                var correcto = true;
                return correcto;
            }

            function comprobarFechaLanbide(inputFecha) {
                var formato = 'dd/mm/yyyy';
                if (Trim(inputFecha.value) != '') {
                    var D = ValidarFechaConFormatoLanbide(inputFecha.value, formato);
                    if (!D[0]) {
                        jsp_alerta("A", "<%=meLanbide11I18n.getMensaje(idiomaUsuario, "msg.fechaNoVal")%>");
                        document.getElementById(inputFecha.name).focus();
                        document.getElementById(inputFecha.name).select();
                        return false;
                    } else {
                        inputFecha.value = D[1];
                        return true;
                    }//if (!D[0])
                }//if (Trim(inputFecha.value)!='')
                return true;
            }//comprobarFechaLanbide

            //Funcion para el calendario de fecha 
            function mostrarCalFecha(evento) {
                if (window.event)
                    evento = window.event;
                if (document.getElementById("calFecha").src.indexOf("icono.gif") != -1)
                    showCalendar('forms[0]', 'fecha', null, null, null, '', 'calFecha', '', null, null, null, null, null, null, null, null, evento);
            }

            function compruebaTamanoCampo(elemento, maxTex) {
                var texto = elemento.value;
                if (texto.length > maxTex) {
                    jsp_alerta("A", '<%=meLanbide11I18n.getMensaje(idiomaUsuario, "msg.errExcdTexto")%>');
                    elemento.focus();
                    return false;
                }
                return true;
            }

        </script>
    </head>
    <body>
        <div class="contenidoPantalla">
            <form><!--    action="/PeticionModuloIntegracion.do" method="POST" -->
                <div style="width: 100%; padding: 10px; text-align: left;">
                    <div  class="sub3titulo" style="clear: both; text-align: center; font-size: 14px;">
                        <span>
                            <%=meLanbide11I18n.getMensaje(idiomaUsuario,"label.nuevaSubvencion")%>
                        </span>
                    </div> 

                    <br><br>
                    <div class="lineaFormulario">
                        <div class="etiqueta" style="width: 250px; float: left;">
                            <%=meLanbide11I18n.getMensaje(idiomaUsuario,"label.estado")%>
                        </div>
                        <div>
                            <div>
                                <input type="text" name="codListaEstado" id="codListaEstado" size="12" class="inputTexto" value="" onkeyup="xAMayusculas(this);" />
                                <input type="text" name="descListaEstado"  id="descListaEstado" size="60" class="inputTexto" readonly="true" value="" />
                                <a href="" id="anchorListaEstado" name="anchorListaEstado">
                                    <span class="fa fa-chevron-circle-down" aria-hidden="true" id="botonAplicacion"
                                          name="botonAplicacion" style="cursor:pointer;"></span>
                                </a>
                            </div>
                        </div>
                    </div>

                    <div class="lineaFormulario">
                        <div style="width: 250px; float: left;" class="etiqueta">
                            <%=meLanbide11I18n.getMensaje(idiomaUsuario,"label.organismo")%>
                        </div>
                        <div>
                            <div style="float: left;">
                                <input id="organismo" name="organismo" type="text" class="inputTexto" size="100" maxlength="100" 
                                       value="<%=datModif != null && datModif.getOrganismo() != null ? datModif.getOrganismo() : ""%>"/>
                            </div>
                        </div>
                    </div>

                    <br><br>
                    <div class="lineaFormulario">
                        <div style="width: 250px; float: left;" class="etiqueta">
                            <%=meLanbide11I18n.getMensaje(idiomaUsuario,"label.objeto")%>
                        </div>
                        <div>
                            <div style="float: left;">
                                <input id="objeto" name="objeto" type="text" class="inputTexto" size="100" maxlength="50" 
                                       value="<%=datModif != null && datModif.getObjeto() != null ? datModif.getObjeto() : ""%>"/>
                            </div>
                        </div>
                    </div>

                    <br><br>
                    <div class="lineaFormulario">
                        <div style="width: 250px; float: left;" class="etiqueta">
                            <%=meLanbide11I18n.getMensaje(idiomaUsuario,"label.importe")%>
                        </div>
                        <div>
                            <div style="float: left;">
                                <input id="importe" name="importe" type="text" class="inputTexto" size="25" maxlength="10" 
                                       value="<%=datModif != null && datModif.getImporte() != null ? datModif.getImporte().toString().replaceAll("\\.", ","): ""%>" onchange="reemplazarPuntos(this);"/>
                            </div>
                        </div>
                    </div>        

                    <br><br>
                    <div class="lineaFormulario">
                        <div style="width: 250px; float: left;" class="etiqueta">
                            <%=meLanbide11I18n.getMensaje(idiomaUsuario,"label.fecha")%>
                        </div>
                        <div>
                            <div style="float: left;">
                                <input type="text" class="inputTxtFecha" 
                                       id="fecha" name="fecha"
                                       maxlength="10"  size="10"
                                       value="<%=fecha%>"
                                       onkeyup = "return SoloCaracteresFechaLanbide(this);"
                                       onblur = "javascript:return comprobarFechaLanbide(this);"
                                       onfocus="javascript:this.select();"/>
                                <A href="javascript:calClick(event);return false;" onClick="mostrarCalFecha(event);
                                        return false;" style="text-decoration:none;">
                                    <IMG style="border: 0px solid" height="17" id="calFecha" name="calFecha" border="0" 
                                         src="<c:url value='/images/calendario/icono.gif'/>" > <!--width="25"-->
                                </A>
                            </div>
                        </div>
                    </div> 


                    <br><br>
                    <div class="lineaFormulario" style="margin-top: 25px;">
                        <div class="botonera" style="text-align: center;">
                            <input type="button" id="btnAceptar" name="btnAceptar" class="botonGeneral" value="<%=meLanbide11I18n.getMensaje(idiomaUsuario, "btn.aceptar")%>" onclick="guardarDatos();"/>
                            <input type="button" id="btnCancelar" name="btnCancelar" class="botonGeneral" value="<%=meLanbide11I18n.getMensaje(idiomaUsuario, "btn.cancelar")%>" onclick="cancelar();"/>
                        </div>
                    </div>
                </div>
                <div id="reloj" style="font-size:20px;"></div>
            </form>
            <script type="text/javascript">

                /*desplegable estado*/
                listaCodigosEstado[0] = "";
                listaDescripcionesEstado[0] = "";
                contador = 0;
                <logic:iterate id="estado" name="listaEstado" scope="request">
                listaCodigosEstado[contador] = ['<bean:write name="estado" property="des_val_cod" />'];
                listaDescripcionesEstado[contador] = ['<bean:write name="estado" property="des_nom" />'];
                contador++;
                </logic:iterate>
                comboListaEstado = new Combo("ListaEstado");
                comboListaEstado.addItems(listaCodigosEstado, listaDescripcionesEstado);
                comboListaEstado.change = cargarDatosEstado;

                var nuevo = "<%=nuevo%>";
                if (nuevo == 0) {
                    rellenardatModificar();
                }

            </script>
            <div id="popupcalendar" class="text"></div>        
        </div>
    </body>
</html> 
