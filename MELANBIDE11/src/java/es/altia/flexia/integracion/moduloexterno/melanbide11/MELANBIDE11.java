
package es.altia.flexia.integracion.moduloexterno.melanbide11;

import es.altia.agora.business.escritorio.UsuarioValueObject;

import es.altia.agora.technical.ConstantesDatos;
import es.altia.common.exception.TechnicalException;

import es.altia.flexia.integracion.moduloexterno.melanbide11.manager.MeLanbide11Manager;
import es.altia.flexia.integracion.moduloexterno.melanbide11.dao.MeLanbide11DAO.ComplementosPorTipo;
import es.altia.flexia.integracion.moduloexterno.melanbide11.util.ConfigurationParameter;
import es.altia.flexia.integracion.moduloexterno.melanbide11.util.ConstantesMeLanbide11;
import es.altia.flexia.integracion.moduloexterno.melanbide11.util.DesgloseRSBParser;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.ContratacionVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.DesgloseRSBVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.MinimisVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.DatosTablaDesplegableExtVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.DesplegableAdmonLocalVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.DesplegableExternoVO;
import es.altia.flexia.integracion.moduloexterno.plugin.ModuloIntegracionExterno;
import es.altia.technical.PortableContext;
import es.altia.util.conexion.AdaptadorSQLBD;
import es.altia.util.conexion.BDException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

public class MELANBIDE11 extends ModuloIntegracionExterno {

    private static Logger log = Logger.getLogger(MELANBIDE11.class);
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    ResourceBundle m_Conf = ResourceBundle.getBundle("common");

    public void cargarExpedienteExtension(int codigoOrganizacion, String numeroExpediente, String xml)
            throws Exception {
        final Class cls = Class.forName("es.altia.flexia.integracion.moduloexterno.melanbide42.MELANBIDE42");
        final Object me42Class = cls.newInstance();
        final Class[] types = { int.class, String.class, String.class };
        final Method method = cls.getMethod("cargarExpedienteExtension", types);

        method.invoke(me42Class, codigoOrganizacion, numeroExpediente, xml);
    }

    public String cargarPantallaPrincipal(int codOrganizacion, int codTramite, int ocurrenciaTramite,
            String numExpediente, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entramos en cargarPantallaPrincipal de " + this.getClass().getName());
        AdaptadorSQLBD adapt = null;
        try {
            adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));
        } catch (Exception ex) {
            log.error(this.getClass().getName() + " Error al recuperar el adaptador getAdaptSQLBD ", ex);
        }
        String url = "/jsp/extension/melanbide11/melanbide11.jsp";
        request.setAttribute("numExp", numExpediente);
        if (adapt != null) {
            try {
                List<ContratacionVO> listaAccesos = MeLanbide11Manager.getInstance().getDatosContratacion(numExpediente,
                        codOrganizacion, adapt);
                if (listaAccesos.size() > 0) {
                    request.setAttribute("listaAccesos", listaAccesos);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("Error al recuperar los datos de contrataciones - MELANBIDE11 - cargarPantallaPrincipal", ex);
            }
        }

        return url;
    }

    public String cargarPantallaMinimis(int codOrganizacion, int codTramite, int ocurrenciaTramite,
            String numExpediente, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entramos en cargarPantallaMinimis de " + this.getClass().getName());
        AdaptadorSQLBD adapt = null;
        try {
            adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));
        } catch (Exception ex) {
            log.error(this.getClass().getName() + " Error al recuperar el adaptador getAdaptSQLBD ", ex);
        }
        String url = "/jsp/extension/melanbide11/minimis.jsp";
        request.setAttribute("numExp", numExpediente);
        if (adapt != null) {
            try {
                List<MinimisVO> listaMinimis = MeLanbide11Manager.getInstance().getDatosMinimis(numExpediente,
                        codOrganizacion, adapt);
                if (listaMinimis.size() > 0) {
                    request.setAttribute("listaMinimis", listaMinimis);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("Error al recuperar los datos de minimis - MELANBIDE11 - cargarPantallaMinimis", ex);
            }
        }

        return url;
    }

    private List<DesplegableAdmonLocalVO> traducirDesplegable(HttpServletRequest request,
            List<DesplegableAdmonLocalVO> desplegable) {

        for (DesplegableAdmonLocalVO d : desplegable) {
            if (d.getDes_nom() != null && !d.getDes_nom().equals("")) {
                d.setDes_nom(getDescripcionDesplegable(request, d.getDes_nom()));
            }
        }

        return desplegable;
    }

    public String cargarNuevaContratacion(int codOrganizacion, int codTramite, int ocurrenciaTramite,
            String numExpediente, HttpServletRequest request, HttpServletResponse response) {
        String nuevo = "1";
        String numExp = "";
        String urlnuevaContratacion = "/jsp/extension/melanbide11/nuevaContratacion.jsp?codOrganizacion="
                + codOrganizacion;
        try {
            if (request.getAttribute("nuevo") != null) {
                if (request.getAttribute("nuevo").toString().equals("0")) {
                    request.setAttribute("nuevo", nuevo);
                }
            } else {
                request.setAttribute("nuevo", nuevo);
            }
            if (request.getParameter("numExp") != null) {
                numExp = request.getParameter("numExp").toString();
                request.setAttribute("numExp", numExp);
            }
            // Cargamos en el request los valores de los desplegables
            List<DesplegableAdmonLocalVO> listaSexo = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_SEXO,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaSexo.size() > 0) {
                listaSexo = traducirDesplegable(request, listaSexo);
                request.setAttribute("listaSexo", listaSexo);
            }
            List<DesplegableAdmonLocalVO> listaMayor55 = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_BOOL,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaMayor55.size() > 0) {
                listaMayor55 = traducirDesplegable(request, listaMayor55);
                request.setAttribute("listaMayor55", listaMayor55);
            }
            List<DesplegableAdmonLocalVO> listaFinFormativa = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_BOOL,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaFinFormativa.size() > 0) {
                listaFinFormativa = traducirDesplegable(request, listaFinFormativa);
                request.setAttribute("listaFinFormativa", listaFinFormativa);
            }
            List<DesplegableAdmonLocalVO> listaJornada = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_JORN,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaJornada.size() > 0) {
                listaJornada = traducirDesplegable(request, listaJornada);
                request.setAttribute("listaJornada", listaJornada);
            }
            // TITREQPUESTO
            List<DesplegableAdmonLocalVO> listaTitReqPuesto = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_TITREQPUESTO,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaTitReqPuesto.size() > 0) {
                listaTitReqPuesto = traducirDesplegable(request, listaTitReqPuesto);
                request.setAttribute("listaTitReqPuesto", listaTitReqPuesto);
            }
            List<DesplegableAdmonLocalVO> listaGrupoCotizacion = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_GCOT,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaGrupoCotizacion.size() > 0) {
                listaGrupoCotizacion = traducirDesplegable(request, listaGrupoCotizacion);
                request.setAttribute("listaGrupoCotizacion", listaGrupoCotizacion);
            }
            List<DesplegableAdmonLocalVO> listaTipRetribucion = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_DTRT,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaTipRetribucion.size() > 0) {
                listaTipRetribucion = traducirDesplegable(request, listaTipRetribucion);
                request.setAttribute("listaTipRetribucion", listaTipRetribucion);
            }

            // Desplegables externos
            DatosTablaDesplegableExtVO datosTablaDesplegableOcupaciones = MeLanbide11Manager.getInstance()
                    .getDatosMapeoDesplegableExterno(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_EXT_OCIN,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            String tablaOcupaciones = datosTablaDesplegableOcupaciones.getTabla();
            String campoCodigoOcupaciones = datosTablaDesplegableOcupaciones.getCampoCodigo();
            String campoValorOcupaciones = datosTablaDesplegableOcupaciones.getCampoValor();
            List<DesplegableExternoVO> listaOcupacion = MeLanbide11Manager.getInstance().getValoresDesplegablesExternos(
                    tablaOcupaciones, campoCodigoOcupaciones, campoValorOcupaciones,
                    this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaOcupacion.size() > 0) {
                request.setAttribute("listaOcupacion", listaOcupacion);
            }
            DatosTablaDesplegableExtVO datosTablaDesplegableTitulaciones = MeLanbide11Manager.getInstance()
                    .getDatosMapeoDesplegableExterno(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_EXT_TIIN,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            String tablaTitulaciones = datosTablaDesplegableTitulaciones.getTabla();
            String campoCodigoTitulaciones = datosTablaDesplegableTitulaciones.getCampoCodigo();
            String campoValorTitulaciones = datosTablaDesplegableTitulaciones.getCampoValor();
            List<DesplegableExternoVO> listaTitulacion = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesExternos(tablaTitulaciones, campoCodigoTitulaciones, campoValorTitulaciones,
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaTitulacion.size() > 0) {
                request.setAttribute("listaTitulacion", listaTitulacion);
            }
            DatosTablaDesplegableExtVO datosTablaDesplegableCProfesionales = MeLanbide11Manager.getInstance()
                    .getDatosMapeoDesplegableExterno(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_EXT_CPIN,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            String tablaCProfesionales = datosTablaDesplegableCProfesionales.getTabla();
            String campoCodigoCProfesionales = datosTablaDesplegableCProfesionales.getCampoCodigo();
            String campoValorCProfesionales = datosTablaDesplegableCProfesionales.getCampoValor();
            List<DesplegableExternoVO> listaCProfesionalidad = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesExternos(tablaCProfesionales, campoCodigoCProfesionales,
                            campoValorCProfesionales, this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaCProfesionalidad.size() > 0) {
                request.setAttribute("listaCProfesionalidad", listaCProfesionalidad);
            }
        } catch (Exception ex) {
            log.debug("Se ha presentado un error al intentar preparar la jsp de una nueva contrataci�n : "
                    + ex.getMessage());
        }
        return urlnuevaContratacion;
    }

    public String cargarModificarContratacion(int codOrganizacion, int codTramite, int ocurrenciaTramite,
            String numExpediente, HttpServletRequest request, HttpServletResponse response) {
        String nuevo = "0";
        String urlnuevaContratacion = "/jsp/extension/melanbide11/nuevaContratacion.jsp?codOrganizacion="
                + codOrganizacion;
        try {
            if (request.getAttribute("nuevo") != null) {
                if (!request.getAttribute("nuevo").toString().equals("0")) {
                    request.setAttribute("nuevo", nuevo);
                }
            } else {
                request.setAttribute("nuevo", nuevo);
            }
            String id = request.getParameter("id");
            // Recuperramos datos e Acceso a modificar y cargamos en el request
            if (id != null && !id.equals("")) {
                ContratacionVO datModif = MeLanbide11Manager.getInstance().getContratacionPorID(id,
                        this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
                if (datModif != null) {
                    request.setAttribute("datModif", datModif);
                }
            }
            // Cargamos el el request los valores de los desplegables
            List<DesplegableAdmonLocalVO> listaSexo = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_SEXO,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaSexo.size() > 0) {
                listaSexo = traducirDesplegable(request, listaSexo);
                request.setAttribute("listaSexo", listaSexo);
            }
            List<DesplegableAdmonLocalVO> listaMayor55 = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_BOOL,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaMayor55.size() > 0) {
                listaMayor55 = traducirDesplegable(request, listaMayor55);
                request.setAttribute("listaMayor55", listaMayor55);
            }
            List<DesplegableAdmonLocalVO> listaFinFormativa = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_BOOL,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaFinFormativa.size() > 0) {
                listaFinFormativa = traducirDesplegable(request, listaFinFormativa);
                request.setAttribute("listaFinFormativa", listaFinFormativa);
            }
            List<DesplegableAdmonLocalVO> listaJornada = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_JORN,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaJornada.size() > 0) {
                listaJornada = traducirDesplegable(request, listaJornada);
                request.setAttribute("listaJornada", listaJornada);
            }
            // TITREQPUESTO
            List<DesplegableAdmonLocalVO> listaTitReqPuesto = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_TITREQPUESTO,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaTitReqPuesto.size() > 0) {
                listaTitReqPuesto = traducirDesplegable(request, listaTitReqPuesto);
                request.setAttribute("listaTitReqPuesto", listaTitReqPuesto);
            }
            List<DesplegableAdmonLocalVO> listaGrupoCotizacion = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_GCOT,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaGrupoCotizacion.size() > 0) {
                listaGrupoCotizacion = traducirDesplegable(request, listaGrupoCotizacion);
                request.setAttribute("listaGrupoCotizacion", listaGrupoCotizacion);
            }
            List<DesplegableAdmonLocalVO> listaTipRetribucion = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_DTRT,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaTipRetribucion.size() > 0) {
                listaTipRetribucion = traducirDesplegable(request, listaTipRetribucion);
                request.setAttribute("listaTipRetribucion", listaTipRetribucion);
            }

            // Desplegables externos
            DatosTablaDesplegableExtVO datosTablaDesplegableOcupaciones = MeLanbide11Manager.getInstance()
                    .getDatosMapeoDesplegableExterno(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_EXT_OCIN,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            String tablaOcupaciones = datosTablaDesplegableOcupaciones.getTabla();
            String campoCodigoOcupaciones = datosTablaDesplegableOcupaciones.getCampoCodigo();
            String campoValorOcupaciones = datosTablaDesplegableOcupaciones.getCampoValor();
            List<DesplegableExternoVO> listaOcupacion = MeLanbide11Manager.getInstance().getValoresDesplegablesExternos(
                    tablaOcupaciones, campoCodigoOcupaciones, campoValorOcupaciones,
                    this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaOcupacion.size() > 0) {
                request.setAttribute("listaOcupacion", listaOcupacion);
            }
            DatosTablaDesplegableExtVO datosTablaDesplegableTitulaciones = MeLanbide11Manager.getInstance()
                    .getDatosMapeoDesplegableExterno(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_EXT_TIIN,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            String tablaTitulaciones = datosTablaDesplegableTitulaciones.getTabla();
            String campoCodigoTitulaciones = datosTablaDesplegableTitulaciones.getCampoCodigo();
            String campoValorTitulaciones = datosTablaDesplegableTitulaciones.getCampoValor();
            List<DesplegableExternoVO> listaTitulacion = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesExternos(tablaTitulaciones, campoCodigoTitulaciones, campoValorTitulaciones,
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaTitulacion.size() > 0) {
                request.setAttribute("listaTitulacion", listaTitulacion);
            }
            DatosTablaDesplegableExtVO datosTablaDesplegableCProfesionales = MeLanbide11Manager.getInstance()
                    .getDatosMapeoDesplegableExterno(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_EXT_CPIN,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            String tablaCProfesionales = datosTablaDesplegableCProfesionales.getTabla();
            String campoCodigoCProfesionales = datosTablaDesplegableCProfesionales.getCampoCodigo();
            String campoValorCProfesionales = datosTablaDesplegableCProfesionales.getCampoValor();
            List<DesplegableExternoVO> listaCProfesionalidad = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesExternos(tablaCProfesionales, campoCodigoCProfesionales,
                            campoValorCProfesionales, this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaCProfesionalidad.size() > 0) {
                request.setAttribute("listaCProfesionalidad", listaCProfesionalidad);
            }
        } catch (Exception ex) {
            log.debug("Error al tratar de preparar los datos para modificar y llamar la jsp de modificaci�n : "
                    + ex.getMessage());
        }
        return urlnuevaContratacion;

    }

    public void eliminarContratacion(int codOrganizacion, int codTramite, int ocurrenciaTramite, String numExpediente,
            HttpServletRequest request, HttpServletResponse response) {
        String codigoOperacion = "0";
        List<ContratacionVO> lista = new ArrayList<ContratacionVO>();
        String numExp = "";
        try {
            String id = (String) request.getParameter("id");
            if (id == null || id.equals("")) {
                log.debug("No se ha recibido desde la JSP el id de la contrataci�n a elimnar ");
                codigoOperacion = "3";
            } else {
                numExp = request.getParameter("numExp").toString();
                AdaptadorSQLBD adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));
                int result = MeLanbide11Manager.getInstance().eliminarContratacion(id, adapt);
                if (result <= 0) {
                    codigoOperacion = "1";
                } else {
                    codigoOperacion = "0";
                    try {
                        lista = MeLanbide11Manager.getInstance().getDatosContratacion(numExp, codOrganizacion, adapt);
                    } catch (Exception ex) {
                        log.debug(
                                "Error al recuperar la lista de contrataci�n despu�s de eliminar una contrataci�n");
                    }
                }
            }
        } catch (Exception ex) {
            log.debug("Error eliminando una contrataci�n: " + ex);
            codigoOperacion = "2";
        }
        String xmlSalida = null;
        xmlSalida = obtenerXmlSalidaContratacion(request, codigoOperacion, lista);
        retornarXML(xmlSalida, response);
    }

    public void crearNuevaContratacion(int codOrganizacion, int codTramite, int ocurrenciaTramite, String numExpediente,
            HttpServletRequest request, HttpServletResponse response) {
        String codigoOperacion = "0";
        List<ContratacionVO> lista = new ArrayList<ContratacionVO>();
        ContratacionVO nuevaContratacion = new ContratacionVO();
        try {
            AdaptadorSQLBD adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));

            String numExp = (String) request.getParameter("expediente");

            String oferta = (String) request.getParameter("oferta");
            String idContrato1 = (String) request.getParameter("idContrato1");
            String idContrato2 = (String) request.getParameter("idContrato2");

            String dni = (String) request.getParameter("dni");
            String nombre = (String) request.getParameter("nombre");
            String apellido1 = (String) request.getParameter("apellido1");
            String apellido2 = (String) request.getParameter("apellido2");
            String fechaNacimiento = (String) request.getParameter("fechaNacimiento");
            String edad = (String) request.getParameter("edad");
            String sexo = (String) request.getParameter("sexo");
            String mayor55 = (String) request.getParameter("mayor55");
            String finFormativa = (String) request.getParameter("finFormativa");
            String codFormativa = (String) request.getParameter("codFormativa");
            String denFormativa = (String) request.getParameter("denFormativa");

            String puesto = (String) request.getParameter("puesto");
            String ocupacion = (String) request.getParameter("ocupacion");
            String desOcupacion = (String) request.getParameter("desOcupacion");
            String desOcupacionLibre = (String) request.getParameter("desOcupacionLibre");
            String desTitulacionLibre = (String) request.getParameter("desTitulacionLibre");
            String titulacion = (String) request.getParameter("titulacion");
            String cProfesionalidad = (String) request.getParameter("cProfesionalidad");
            String modalidadContrato = (String) request.getParameter("modalidadContrato");
            String jornada = (String) request.getParameter("jornada");
            String porcJornadaParam = (String) request.getParameter("porcJornada");
            String porcJornada = (porcJornadaParam != null) ? porcJornadaParam.replace(",", ".") : null;
            String horasConv = (String) request.getParameter("horasConv");
            String fechaInicio = (String) request.getParameter("fechaInicio");
            String fechaFin = (String) request.getParameter("fechaFin");
            String mesesContrato = (String) request.getParameter("mesesContrato");

            // --- Normalizaci�n y validaci�n de par�metros num�ricos y texto ---
            String grupoCotizacion = request.getParameter("grupoCotizacion");
            grupoCotizacion = (grupoCotizacion != null && !grupoCotizacion.trim().isEmpty()) ? grupoCotizacion.trim()
                    : null;

            String direccionCT = request.getParameter("direccionCT");
            direccionCT = (direccionCT != null && !direccionCT.trim().isEmpty()) ? direccionCT.trim() : null;

            String numSS = request.getParameter("numSS");
            numSS = (numSS != null && !numSS.trim().isEmpty()) ? numSS.trim() : null;

            String costeContratoParam = request.getParameter("costeContrato");
            String costeContrato = (costeContratoParam != null && !costeContratoParam.trim().isEmpty())
                    ? costeContratoParam.trim().replace(",", ".")
                    : null;

            String tipRetribucion = request.getParameter("tipRetribucion");
            tipRetribucion = (tipRetribucion != null && !tipRetribucion.trim().isEmpty()) ? tipRetribucion.trim()
                    : null;

            String importeSubParam = request.getParameter("importeSub");
            String importeSub = (importeSubParam != null && !importeSubParam.trim().isEmpty())
                    ? importeSubParam.trim().replace(",", ".")
                    : null;

            // --- Nuevos campos TITREQPUESTO y FUNCIONES ---
            String titReqPuesto = request.getParameter("titReqPuesto"); // c�digo del combo
            titReqPuesto = (titReqPuesto != null && !titReqPuesto.trim().isEmpty()) ? titReqPuesto.trim() : null;

            String funciones = request.getParameter("funciones");
            if (funciones != null) {
                funciones = funciones.trim();
                if (funciones.length() > 200)
                    funciones = funciones.substring(0, 200);
                if (funciones.isEmpty())
                    funciones = null;
            }

            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

            nuevaContratacion.setNumExp(numExp);

            nuevaContratacion.setOferta(oferta);
            nuevaContratacion.setIdContrato1(idContrato1);
            nuevaContratacion.setIdContrato2(idContrato2);

            nuevaContratacion.setDni(dni);
            nuevaContratacion.setNombre(nombre);
            nuevaContratacion.setApellido1(apellido1);
            nuevaContratacion.setApellido2(apellido2);
            if (fechaNacimiento != null && !"".equals(fechaNacimiento)) {
                nuevaContratacion.setFechaNacimiento(new java.sql.Date(formatoFecha.parse(fechaNacimiento).getTime()));
            }
            if (edad != null && !"".equals(edad)) {
                nuevaContratacion.setEdad(Integer.parseInt(edad));
            }
            nuevaContratacion.setSexo(sexo);
            nuevaContratacion.setMayor55(mayor55);
            nuevaContratacion.setFinFormativa(finFormativa);
            nuevaContratacion.setCodFormativa(codFormativa);
            nuevaContratacion.setDenFormativa(denFormativa);

            nuevaContratacion.setPuesto(puesto);
            nuevaContratacion.setOcupacion(ocupacion);
            nuevaContratacion.setDesOcupacion(desOcupacion);
            nuevaContratacion.setDesOcupacionLibre(desOcupacionLibre);
            nuevaContratacion.setDesTitulacionLibre(desTitulacionLibre);
            nuevaContratacion.setTitulacion(titulacion);
            nuevaContratacion.setcProfesionalidad(cProfesionalidad);
            nuevaContratacion.setModalidadContrato(modalidadContrato);
            nuevaContratacion.setJornada(jornada);
            nuevaContratacion.setTitReqPuesto(titReqPuesto);
            nuevaContratacion.setFunciones(funciones);
            if (porcJornada != null && !"".equals(porcJornada)) {
                nuevaContratacion.setPorcJornada(Double.parseDouble(porcJornada));
            }
            if (horasConv != null && !"".equals(horasConv)) {
                nuevaContratacion.setHorasConv(Integer.parseInt(horasConv));
            }
            if (fechaInicio != null && !"".equals(fechaInicio)) {
                nuevaContratacion.setFechaInicio(new java.sql.Date(formatoFecha.parse(fechaInicio).getTime()));
            }
            if (fechaFin != null && !"".equals(fechaFin)) {
                nuevaContratacion.setFechaFin(new java.sql.Date(formatoFecha.parse(fechaFin).getTime()));
            }
            nuevaContratacion.setMesesContrato(mesesContrato);
            nuevaContratacion.setGrupoCotizacion(grupoCotizacion);
            nuevaContratacion.setDireccionCT(direccionCT);
            nuevaContratacion.setNumSS(numSS);
            if (costeContrato != null && !"".equals(costeContrato)) {
                nuevaContratacion.setCosteContrato(Double.parseDouble(costeContrato));
            }
            nuevaContratacion.setTipRetribucion(tipRetribucion);

            if (importeSub != null && !"".equals(importeSub)) {
                nuevaContratacion.setImporteSub(Double.parseDouble(importeSub));
            }

            MeLanbide11Manager meLanbide11Manager = MeLanbide11Manager.getInstance();
            boolean insertOK = meLanbide11Manager.crearNuevaContratacion(nuevaContratacion, adapt);
            if (insertOK) {
                log.debug("Contrataci�n insertada correctamente");
                lista = meLanbide11Manager.getDatosContratacion(numExp, codOrganizacion, adapt);

            } else {
                log.debug("No se ha insertado correctamente la nueva contrataci�n");
                codigoOperacion = "1";
            }
        } catch (Exception ex) {
            log.debug("Error al parsear los parametros recibidos del jsp al objeto ContratacionVO" + ex.getMessage());
            codigoOperacion = "2";
        }

        String xmlSalida = null;
        xmlSalida = obtenerXmlSalidaContratacion(request, codigoOperacion, lista);
        retornarXML(xmlSalida, response);
    }

    public void modificarContratacion(int codOrganizacion, int codTramite, int ocurrenciaTramite, String numExpediente,
            HttpServletRequest request, HttpServletResponse response) {
        String codigoOperacion = "0";
        List<ContratacionVO> lista = new ArrayList<ContratacionVO>();

        try {
            AdaptadorSQLBD adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));
            // Recojo los parametros
            String id = (String) request.getParameter("id");

            String numExp = (String) request.getParameter("expediente");

            String oferta = (String) request.getParameter("oferta");
            String idContrato1 = (String) request.getParameter("idContrato1");
            String idContrato2 = (String) request.getParameter("idContrato2");

            String dni = (String) request.getParameter("dni");
            String nombre = (String) request.getParameter("nombre");
            String apellido1 = (String) request.getParameter("apellido1");
            String apellido2 = (String) request.getParameter("apellido2");
            String fechaNacimiento = (String) request.getParameter("fechaNacimiento");
            String edad = (String) request.getParameter("edad");
            String sexo = (String) request.getParameter("sexo");
            String mayor55 = (String) request.getParameter("mayor55");
            String finFormativa = (String) request.getParameter("finFormativa");
            String codFormativa = (String) request.getParameter("codFormativa");
            String denFormativa = (String) request.getParameter("denFormativa");

            String puesto = (String) request.getParameter("puesto");
            String ocupacion = (String) request.getParameter("ocupacion");
            String desOcupacion = (String) request.getParameter("desOcupacion");
            String desOcupacionLibre = (String) request.getParameter("desOcupacionLibre");
            String desTitulacionLibre = (String) request.getParameter("desTitulacionLibre");
            String titulacion = (String) request.getParameter("titulacion");
            String cProfesionalidad = (String) request.getParameter("cProfesionalidad");
            String modalidadContrato = (String) request.getParameter("modalidadContrato");
            String jornada = (String) request.getParameter("jornada");
            String porcJornadaParam = (String) request.getParameter("porcJornada");
            String porcJornada = (porcJornadaParam != null) ? porcJornadaParam.replace(",", ".") : null;
            String horasConv = (String) request.getParameter("horasConv");
            String fechaInicio = (String) request.getParameter("fechaInicio");
            // log.debug("++++++++fechaInicio: " + fechaInicio);
            String fechaFin = (String) request.getParameter("fechaFin");
            // log.debug("++++++++fechaFin: " + fechaFin);
            String mesesContrato = (String) request.getParameter("mesesContrato");
            String grupoCotizacion = (String) request.getParameter("grupoCotizacion");
            String direccionCT = (String) request.getParameter("direccionCT");
            String numSS = (String) request.getParameter("numSS");
            String costeContratoParam = (String) request.getParameter("costeContrato");
            String costeContrato = (costeContratoParam != null) ? costeContratoParam.replace(",", ".") : null;
            String tipRetribucion = (String) request.getParameter("tipRetribucion");

            String importeSubParam = (String) request.getParameter("importeSub");
            String importeSub = (importeSubParam != null) ? importeSubParam.replace(",", ".") : null;

            // Nuevos campos TITREQPUESTO y FUNCIONES (modificar)
            String titReqPuesto = (String) request.getParameter("titReqPuesto");
            String funciones = request.getParameter("funciones");
            if (funciones != null && funciones.length() > 200) {
                funciones = funciones.substring(0, 200);
            }

            if (id == null || id.equals("")) {
                log.debug("No se ha recibido desde la JSP el id de la contrataci�n a modificar ");
                codigoOperacion = "3";
            } else {
                ContratacionVO datModif = MeLanbide11Manager.getInstance().getContratacionPorID(id, adapt);
                numExp = datModif.getNumExp();
                datModif.setId(Integer.parseInt(id));

                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

                datModif.setNumExp(numExp);

                datModif.setOferta(oferta);
                datModif.setIdContrato1(idContrato1);
                datModif.setIdContrato2(idContrato2);

                datModif.setDni(dni);
                datModif.setNombre(nombre);
                datModif.setApellido1(apellido1);
                datModif.setApellido2(apellido2);
                datModif.setFechaNacimiento(null);
                if (fechaNacimiento != null && !"".equals(fechaNacimiento)) {
                    datModif.setFechaNacimiento(new java.sql.Date(formatoFecha.parse(fechaNacimiento).getTime()));
                }
                if (edad != null && !"".equals(edad)) {
                    datModif.setEdad(Integer.parseInt(edad));
                }
                datModif.setSexo(sexo);
                datModif.setMayor55(mayor55);
                datModif.setFinFormativa(finFormativa);
                datModif.setCodFormativa(codFormativa);
                datModif.setDenFormativa(denFormativa);

                datModif.setPuesto(puesto);
                datModif.setOcupacion(ocupacion);
                datModif.setDesOcupacion(desOcupacion);
                datModif.setDesOcupacionLibre(desOcupacionLibre);
                datModif.setDesTitulacionLibre(desTitulacionLibre);
                datModif.setTitulacion(titulacion);
                datModif.setcProfesionalidad(cProfesionalidad);
                datModif.setModalidadContrato(modalidadContrato);
                datModif.setJornada(jornada);
                datModif.setTitReqPuesto(titReqPuesto);
                datModif.setFunciones(funciones);
                datModif.setPorcJornada(null);
                if (porcJornada != null && !"".equals(porcJornada)) {
                    datModif.setPorcJornada(Double.parseDouble(porcJornada));
                }
                if (horasConv != null && !"".equals(horasConv)) {
                    datModif.setHorasConv(Integer.parseInt(horasConv));
                }
                datModif.setFechaInicio(null);
                if (fechaInicio != null && !"".equals(fechaInicio)) {
                    datModif.setFechaInicio(new java.sql.Date(formatoFecha.parse(fechaInicio).getTime()));
                }
                datModif.setFechaFin(null);
                if (fechaFin != null && !"".equals(fechaFin)) {
                    datModif.setFechaFin(new java.sql.Date(formatoFecha.parse(fechaFin).getTime()));
                }
                datModif.setMesesContrato(mesesContrato);
                datModif.setGrupoCotizacion(grupoCotizacion);
                datModif.setDireccionCT(direccionCT);
                datModif.setNumSS(numSS);
                datModif.setCosteContrato(null);
                if (costeContrato != null && !"".equals(costeContrato)) {
                    datModif.setCosteContrato(Double.parseDouble(costeContrato));
                }
                datModif.setTipRetribucion(tipRetribucion);

                datModif.setImporteSub(null);
                if (importeSub != null && !"".equals(importeSub)) {
                    datModif.setImporteSub(Double.parseDouble(importeSub));
                }

                MeLanbide11Manager meLanbide11Manager = MeLanbide11Manager.getInstance();
                boolean modOK = meLanbide11Manager.modificarContratacion(datModif, adapt);
                if (modOK) {
                    try {
                        lista = meLanbide11Manager.getDatosContratacion(numExp, codOrganizacion, adapt);
                    } catch (BDException bde) {
                        codigoOperacion = "1";
                        log.debug(
                                "Error de tipo BD al recuperar la lista de contrataciones despu�s de modificar una contrataci�n : "
                                        + bde.getMensaje());
                    } catch (Exception ex) {
                        codigoOperacion = "2";
                        log.debug(
                                "Error al recuperar la lista de contrataciones despu�s de modificar una contrataci�n : "
                                        + ex.getMessage());
                    }
                } else {
                    codigoOperacion = "2";
                }
            }

        } catch (Exception ex) {
            log.debug("Error modificar --- ", ex);
            codigoOperacion = "2";
        }

        String xmlSalida = null;
        xmlSalida = obtenerXmlSalidaContratacion(request, codigoOperacion, lista);
        retornarXML(xmlSalida, response);

    }

    public String cargarNuevaMinimis(int codOrganizacion, int codTramite, int ocurrenciaTramite, String numExpediente,
            HttpServletRequest request, HttpServletResponse response) {
        String nuevo = "1";
        String numExp = "";
        String urlnuevaMinimis = "/jsp/extension/melanbide11/nuevoMinimis.jsp?codOrganizacion=" + codOrganizacion;
        try {
            if (request.getAttribute("nuevo") != null) {
                if (request.getAttribute("nuevo").toString().equals("0")) {
                    request.setAttribute("nuevo", nuevo);
                }
            } else {
                request.setAttribute("nuevo", nuevo);
            }
            if (request.getParameter("numExp") != null) {
                numExp = request.getParameter("numExp").toString();
                request.setAttribute("numExp", numExp);
            }
            // Cargamos en el request los valores de los desplegables
            List<DesplegableAdmonLocalVO> listaEstado = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_DTSV,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaEstado.size() > 0) {
                listaEstado = traducirDesplegable(request, listaEstado);
                request.setAttribute("listaEstado", listaEstado);
            }

        } catch (Exception ex) {
            log.debug(
                    "Se ha presentado un error al intentar preparar la jsp de una nueva minimis : " + ex.getMessage());
        }
        return urlnuevaMinimis;
    }

    public String cargarModificarMinimis(int codOrganizacion, int codTramite, int ocurrenciaTramite,
            String numExpediente, HttpServletRequest request, HttpServletResponse response) {
        String nuevo = "0";
        String urlnuevaMinimis = "/jsp/extension/melanbide11/nuevoMinimis.jsp?codOrganizacion=" + codOrganizacion;
        try {
            if (request.getAttribute("nuevo") != null) {
                if (!request.getAttribute("nuevo").toString().equals("0")) {
                    request.setAttribute("nuevo", nuevo);
                }
            } else {
                request.setAttribute("nuevo", nuevo);
            }
            String id = request.getParameter("id");
            // Recuperramos datos e Acceso a modificar y cargamos en el request
            if (id != null && !id.equals("")) {
                MinimisVO datModif = MeLanbide11Manager.getInstance().getMinimisPorID(id,
                        this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
                if (datModif != null) {
                    request.setAttribute("datModif", datModif);
                }
            }
            // Cargamos el el request los valores de los desplegables
            List<DesplegableAdmonLocalVO> listaEstado = MeLanbide11Manager.getInstance()
                    .getValoresDesplegablesAdmonLocalxdes_cod(
                            ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_DTSV,
                                    ConstantesMeLanbide11.FICHERO_PROPIEDADES),
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
            if (listaEstado.size() > 0) {
                listaEstado = traducirDesplegable(request, listaEstado);
                request.setAttribute("listaEstado", listaEstado);
            }
        } catch (Exception ex) {
            log.debug("Error al tratar de preparar los datos para modificar y llamar la jsp de modificaci�n : "
                    + ex.getMessage());
        }
        return urlnuevaMinimis;

    }

    public void eliminarMinimis(int codOrganizacion, int codTramite, int ocurrenciaTramite, String numExpediente,
            HttpServletRequest request, HttpServletResponse response) {
        String codigoOperacion = "0";
        List<MinimisVO> lista = new ArrayList<MinimisVO>();
        String numExp = "";
        try {
            String id = (String) request.getParameter("id");
            if (id == null || id.equals("")) {
                log.debug("No se ha recibido desde la JSP el id de la minimis a elimnar ");
                codigoOperacion = "3";
            } else {
                numExp = request.getParameter("numExp").toString();
                AdaptadorSQLBD adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));
                int result = MeLanbide11Manager.getInstance().eliminarMinimis(id, adapt);
                if (result <= 0) {
                    codigoOperacion = "1";
                } else {
                    codigoOperacion = "0";
                    try {
                        lista = MeLanbide11Manager.getInstance().getDatosMinimis(numExp, codOrganizacion, adapt);
                    } catch (Exception ex) {
                        log.debug("Error al recuperar la lista de minimis despu�s de eliminar una minimis");
                    }
                }
            }
        } catch (Exception ex) {
            log.debug("Error eliminando una minimis: " + ex);
            codigoOperacion = "2";
        }
        String xmlSalida = null;
        xmlSalida = obtenerXmlSalidaMinimis(request, codigoOperacion, lista);
        retornarXML(xmlSalida, response);
    }

    public void crearNuevaMinimis(int codOrganizacion, int codTramite, int ocurrenciaTramite, String numExpediente,
            HttpServletRequest request, HttpServletResponse response) {
        String codigoOperacion = "0";
        List<MinimisVO> lista = new ArrayList<MinimisVO>();
        MinimisVO nuevaMinimis = new MinimisVO();
        try {
            AdaptadorSQLBD adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));

            String numExp = (String) request.getParameter("expediente");

            String estado = (String) request.getParameter("estado");
            String organismo = (String) request.getParameter("organismo");
            String objeto = (String) request.getParameter("objeto");
            String importe = (String) request.getParameter("importe").replace(",", ".");
            String fecha = (String) request.getParameter("fecha");

            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

            nuevaMinimis.setNumExp(numExp);

            nuevaMinimis.setEstado(estado);
            nuevaMinimis.setOrganismo(organismo);
            nuevaMinimis.setObjeto(objeto);
            if (importe != null && !"".equals(importe)) {
                nuevaMinimis.setImporte(Double.parseDouble(importe));
            }
            if (fecha != null && !"".equals(fecha)) {
                nuevaMinimis.setFecha(new java.sql.Date(formatoFecha.parse(fecha).getTime()));
            }

            MeLanbide11Manager meLanbide11Manager = MeLanbide11Manager.getInstance();
            boolean insertOK = meLanbide11Manager.crearNuevaMinimis(nuevaMinimis, adapt);
            if (insertOK) {
                log.debug("minimis insertada correctamente");
                lista = meLanbide11Manager.getDatosMinimis(numExp, codOrganizacion, adapt);

            } else {
                log.debug("No se ha insertado correctamente la nueva minimis");
                codigoOperacion = "1";
            }
        } catch (Exception ex) {
            log.debug("Error al parsear los parametros recibidos del jsp al objeto MinimisVO" + ex.getMessage());
            codigoOperacion = "2";
        }

        String xmlSalida = null;
        xmlSalida = obtenerXmlSalidaMinimis(request, codigoOperacion, lista);
        retornarXML(xmlSalida, response);
    }

    public void modificarMinimis(int codOrganizacion, int codTramite, int ocurrenciaTramite, String numExpediente,
            HttpServletRequest request, HttpServletResponse response) {
        String codigoOperacion = "0";
        List<MinimisVO> lista = new ArrayList<MinimisVO>();

        try {
            AdaptadorSQLBD adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));
            // Recojo los parametros
            String id = (String) request.getParameter("id");

            String numExp = (String) request.getParameter("expediente");

            String estado = (String) request.getParameter("estado");
            String organismo = (String) request.getParameter("organismo");
            String objeto = (String) request.getParameter("objeto");
            String importe = (String) request.getParameter("importe").replace(",", ".");
            String fecha = (String) request.getParameter("fecha");

            if (id == null || id.equals("")) {
                log.debug("No se ha recibido desde la JSP el id de la minimis a modificar ");
                codigoOperacion = "3";
            } else {
                MinimisVO datModif = MeLanbide11Manager.getInstance().getMinimisPorID(id, adapt);
                numExp = datModif.getNumExp();
                datModif.setId(Integer.parseInt(id));

                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

                datModif.setNumExp(numExp);

                datModif.setEstado(estado);
                datModif.setOrganismo(organismo);
                datModif.setObjeto(objeto);
                if (importe != null && !"".equals(importe)) {
                    datModif.setImporte(Double.parseDouble(importe));
                }
                if (fecha != null && !"".equals(fecha)) {
                    datModif.setFecha(new java.sql.Date(formatoFecha.parse(fecha).getTime()));
                }

                MeLanbide11Manager meLanbide11Manager = MeLanbide11Manager.getInstance();
                boolean modOK = meLanbide11Manager.modificarMinimis(datModif, adapt);
                if (modOK) {
                    try {
                        lista = meLanbide11Manager.getDatosMinimis(numExp, codOrganizacion, adapt);
                    } catch (BDException bde) {
                        codigoOperacion = "1";
                        log.debug(
                                "Error de tipo BD al recuperar la lista de minimis despu�s de modificar una minimis : "
                                        + bde.getMensaje());
                    } catch (Exception ex) {
                        codigoOperacion = "2";
                        log.debug("Error al recuperar la lista de minimis despu�s de modificar una minimis : "
                                + ex.getMessage());
                    }
                } else {
                    codigoOperacion = "2";
                }
            }

        } catch (Exception ex) {
            log.debug("Error modificar --- ", ex);
            codigoOperacion = "2";
        }

        String xmlSalida = null;
        xmlSalida = obtenerXmlSalidaMinimis(request, codigoOperacion, lista);
        retornarXML(xmlSalida, response);

    }

    // Funciones Privadas
    private AdaptadorSQLBD getAdaptSQLBD(String codOrganizacion) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("getConnection ( codOrganizacion = " + codOrganizacion + " ) : BEGIN");
        }
        ResourceBundle config = ResourceBundle.getBundle("techserver");
        String gestor = config.getString("CON.gestor");
        String jndiGenerico = config.getString("CON.jndi");
        Connection conGenerico = null;
        Statement st = null;
        ResultSet rs = null;
        String[] salida = null;
        Connection con = null;

        if (log.isDebugEnabled()) {
            log.debug("getJndi =========> ");
            log.debug("parametro codOrganizacion: " + codOrganizacion);
            log.debug("gestor: " + gestor);
            log.debug("jndi: " + jndiGenerico);
        } // if(log.isDebugEnabled())

        DataSource ds = null;
        AdaptadorSQLBD adapt = null;
        synchronized (this) {
            try {
                PortableContext pc = PortableContext.getInstance();
                if (log.isDebugEnabled()) {
                    log.debug("He cogido el jndi: " + jndiGenerico);
                }
                ds = (DataSource) pc.lookup(jndiGenerico, DataSource.class);
                // Conexi�n al esquema gen�rico
                conGenerico = ds.getConnection();

                String sql = "SELECT EEA_BDE FROM A_EEA WHERE EEA_APL=" + ConstantesDatos.APP_GESTION_EXPEDIENTES
                        + " AND AAE_ORG=" + codOrganizacion;
                st = conGenerico.createStatement();
                rs = st.executeQuery(sql);
                String jndi = null;
                while (rs.next()) {
                    jndi = rs.getString("EEA_BDE");
                } // while(rs.next())

                st.close();
                rs.close();
                conGenerico.close();

                if (jndi != null && gestor != null && !"".equals(jndi) && !"".equals(gestor)) {
                    salida = new String[7];
                    salida[0] = gestor;
                    salida[1] = "";
                    salida[2] = "";
                    salida[3] = "";
                    salida[4] = "";
                    salida[5] = "";
                    salida[6] = jndi;
                    adapt = new AdaptadorSQLBD(salida);
                } // if(jndi!=null && gestor!=null && !"".equals(jndi) && !"".equals(gestor))
            } catch (TechnicalException te) {
                te.printStackTrace();
                log.error("*** AdaptadorSQLBD: " + te.toString());
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (st != null) {
                    st.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conGenerico != null && !conGenerico.isClosed()) {
                    conGenerico.close();
                }
            } // finally
            if (log.isDebugEnabled()) {
                log.debug("getConnection() : END");
            }
        } // synchronized
        return adapt;
    }// getConnection

    private String getDescripcionDesplegable(HttpServletRequest request, String descripcionCompleta) {
        String descripcion = descripcionCompleta;

        String barraSeparadoraDobleIdiomaDesple = ConfigurationParameter.getParameter(
                ConstantesMeLanbide11.BARRA_SEPARADORA_IDIOMA_DESPLEGABLES, ConstantesMeLanbide11.FICHERO_PROPIEDADES);

        try {
            if (!descripcion.isEmpty()) {

                String[] descripcionDobleIdioma = (descripcion != null
                        ? descripcion.split(barraSeparadoraDobleIdiomaDesple)
                        : null);
                if (descripcionDobleIdioma != null && descripcionDobleIdioma.length > 1) {
                    if (getIdioma(request) == ConstantesMeLanbide11.CODIGO_IDIOMA_EUSKERA) {
                        descripcion = descripcionDobleIdioma[1];
                    } else {
                        // Cogemos la primera posicion que deberia ser castellano
                        descripcion = descripcionDobleIdioma[0];
                    }
                }

            } else {
                descripcion = "-";
            }
            return descripcion;
        } catch (Exception e) {
            return descripcion;
        }

    }

    private int getIdioma(HttpServletRequest request) {
        // Recuperamos el Idioma de la request para la gestion de Desplegables
        UsuarioValueObject usuario = new UsuarioValueObject();
        int idioma = ConstantesMeLanbide11.CODIGO_IDIOMA_CASTELLANO; // Por Defecto 1 Castellano
        try {

            if (request != null && request.getSession() != null) {
                usuario = (UsuarioValueObject) request.getSession().getAttribute("usuario");
                if (usuario != null) {
                    idioma = usuario.getIdioma();
                }
            }
        } catch (Exception ex) {
            log.error("Error al recuperar el idioma del usuario de la request, asignamos por defecto 1 Castellano", ex);
            idioma = ConstantesMeLanbide11.CODIGO_IDIOMA_CASTELLANO;
        }

        return idioma;
    }

    private String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // ----------------------------------------------------------------------------------------------------------
    // --------------- XML
    // --------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------
    private String escapeXml(String s) {
        if (s == null)
            return null;
        String out = s;
        out = out.replace("&", "&amp;");
        out = out.replace("<", "&lt;");
        out = out.replace(">", "&gt;");
        out = out.replace("\"", "&quot;");
        out = out.replace("'", "&apos;");
        return out;
    }

    private void retornarXML(String salida, HttpServletResponse response) {
        try {
            if (salida != null) {
                response.setContentType("text/xml");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print(salida);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String obtenerXmlSalidaContratacion(HttpServletRequest request, String codigoOperacion,
            List<ContratacionVO> lista) {
        StringBuffer xmlSalida = new StringBuffer();
        xmlSalida.append("<RESPUESTA>");
        xmlSalida.append("<CODIGO_OPERACION>");
        xmlSalida.append(codigoOperacion);
        xmlSalida.append("</CODIGO_OPERACION>");
        for (ContratacionVO fila : lista) {
            xmlSalida.append("<FILA>");
            xmlSalida.append("<ID>");
            xmlSalida.append(fila.getId() != null ? fila.getId().toString() : "");
            xmlSalida.append("</ID>");

            xmlSalida.append("<NOFECONT>");
            xmlSalida.append(fila.getOferta());
            xmlSalida.append("</NOFECONT>");
            xmlSalida.append("<IDCONT1>");
            xmlSalida.append(fila.getIdContrato1());
            xmlSalida.append("</IDCONT1>");
            xmlSalida.append("<IDCONT2>");
            xmlSalida.append(fila.getIdContrato2());
            xmlSalida.append("</IDCONT2>");

            xmlSalida.append("<DNICONT>");
            xmlSalida.append(fila.getDni());
            xmlSalida.append("</DNICONT>");
            xmlSalida.append("<NOMCONT>");
            xmlSalida.append(fila.getNombre());
            xmlSalida.append("</NOMCONT>");
            xmlSalida.append("<APE1CONT>");
            xmlSalida.append(fila.getApellido1());
            xmlSalida.append("</APE1CONT>");
            xmlSalida.append("<APE2CONT>");
            xmlSalida.append(fila.getApellido2());
            xmlSalida.append("</APE2CONT>");
            xmlSalida.append("<FECHNACCONT>");
            if (fila.getFechaNacimiento() != null) {
                xmlSalida.append(dateFormat.format(fila.getFechaNacimiento()));
            } else {
                xmlSalida.append("");
            }
            xmlSalida.append("</FECHNACCONT>");
            xmlSalida.append("<EDADCONT>");
            if (fila.getEdad() != null && !"".equals(fila.getEdad())) {
                xmlSalida.append(fila.getEdad());
            } else {
                xmlSalida.append("null");
            }
            xmlSalida.append("</EDADCONT>");
            xmlSalida.append("<SEXOCONT>");
            xmlSalida.append(getDescripcionDesplegable(request, fila.getDesSexo()));
            xmlSalida.append("</SEXOCONT>");
            xmlSalida.append("<MAY55CONT>");
            xmlSalida.append(fila.getMayor55());
            xmlSalida.append("</MAY55CONT>");
            xmlSalida.append("<ACCFORCONT>");
            xmlSalida.append(fila.getFinFormativa());
            xmlSalida.append("</ACCFORCONT>");
            xmlSalida.append("<CODFORCONT>");
            xmlSalida.append(fila.getCodFormativa());
            xmlSalida.append("</CODFORCONT>");
            xmlSalida.append("<DENFORCONT>");
            xmlSalida.append(fila.getDenFormativa());
            xmlSalida.append("</DENFORCONT>");

            xmlSalida.append("<PUESTOCONT>");
            xmlSalida.append(fila.getPuesto());
            xmlSalida.append("</PUESTOCONT>");
            xmlSalida.append("<CODOCUCONT>");
            xmlSalida.append(fila.getOcupacion());
            xmlSalida.append("</CODOCUCONT>");
            xmlSalida.append("<OCUCONT>");
            if (fila.getDesOcupacionLibre() != null && !"".equals(fila.getDesOcupacionLibre())) {
                xmlSalida.append(fila.getDesOcupacionLibre());
            } else {
                xmlSalida.append(fila.getDesOcupacion());
            }
            xmlSalida.append("</OCUCONT>");
            xmlSalida.append("<DESTITULACION>");
            xmlSalida.append(fila.getDesTitulacionLibre());
            xmlSalida.append("</DESTITULACION>");
            xmlSalida.append("<TITULACION>");
            xmlSalida.append(fila.getDesTitulacion());
            xmlSalida.append("</TITULACION>");
            xmlSalida.append("<CPROFESIONALIDAD>");
            xmlSalida.append(fila.getDesCProfesionalidad());
            xmlSalida.append("</CPROFESIONALIDAD>");
            xmlSalida.append("<MODCONT>");
            xmlSalida.append(fila.getModalidadContrato());
            xmlSalida.append("</MODCONT>");
            xmlSalida.append("<JORCONT>");
            String descJornada = getDescripcionDesplegable(request, fila.getDesJornada());
            if (descJornada == null || descJornada.trim().isEmpty() || "-".equals(descJornada.trim())) {
                xmlSalida.append("-");
            } else {
                xmlSalida.append(descJornada);
            }
            xmlSalida.append("</JORCONT>");
            xmlSalida.append("<PORCJOR>");
            if (fila.getPorcJornada() != null && !"".equals(fila.getPorcJornada())) {
                xmlSalida.append(fila.getPorcJornada());
            } else {
                xmlSalida.append("null");
            }
            xmlSalida.append("</PORCJOR>");
            xmlSalida.append("<HORASCONV>");
            if (fila.getHorasConv() != null && !"".equals(fila.getHorasConv())) {
                xmlSalida.append(fila.getHorasConv());
            } else {
                xmlSalida.append("null");
            }
            xmlSalida.append("</HORASCONV>");
            xmlSalida.append("<FECHINICONT>");
            if (fila.getFechaInicio() != null) {
                xmlSalida.append(dateFormat.format(fila.getFechaInicio()));
            } else {
                xmlSalida.append("");
            }
            xmlSalida.append("</FECHINICONT>");
            xmlSalida.append("<FECHFINCONT>");
            if (fila.getFechaFin() != null) {
                xmlSalida.append(dateFormat.format(fila.getFechaFin()));
            } else {
                xmlSalida.append("");
            }
            xmlSalida.append("</FECHFINCONT>");
            xmlSalida.append("<DURCONT>");
            if (fila.getMesesContrato() != null && !"".equals(fila.getMesesContrato())) {
                xmlSalida.append(fila.getMesesContrato());
            } else {
                xmlSalida.append("-");
            }
            xmlSalida.append("</DURCONT>");
            xmlSalida.append("<GRSS>");
            xmlSalida.append(getDescripcionDesplegable(request, fila.getDesGrupoCotizacion()));
            xmlSalida.append("</GRSS>");
            xmlSalida.append("<DIRCENTRCONT>");
            xmlSalida.append(fila.getDireccionCT());
            xmlSalida.append("</DIRCENTRCONT>");
            xmlSalida.append("<NSSCONT>");
            xmlSalida.append(fila.getNumSS());
            xmlSalida.append("</NSSCONT>");
            xmlSalida.append("<CSTCONT>");
            if (fila.getCosteContrato() != null && !"".equals(fila.getCosteContrato())) {
                xmlSalida.append(fila.getCosteContrato());
            } else {
                xmlSalida.append("null");
            }
            xmlSalida.append("</CSTCONT>");
            xmlSalida.append("<TIPRSB>");
            xmlSalida.append(getDescripcionDesplegable(request, fila.getDesTipRetribucion()));
            xmlSalida.append("</TIPRSB>");

            xmlSalida.append("<IMPSUBVCONT>");
            if (fila.getImporteSub() != null && !"".equals(fila.getImporteSub())) {
                xmlSalida.append(fila.getImporteSub());
            } else {
                xmlSalida.append("null");
            }
            xmlSalida.append("</IMPSUBVCONT>");
            xmlSalida.append("<TITREQPUESTO>");
            xmlSalida.append(getDescripcionDesplegable(request, fila.getDesTitReqPuesto()));
            xmlSalida.append("</TITREQPUESTO>");
            xmlSalida.append("<FUNCIONES>");
            xmlSalida.append(escapeXml(fila.getFunciones()));
            xmlSalida.append("</FUNCIONES>");

            xmlSalida.append("</FILA>");
        }
        xmlSalida.append("</RESPUESTA>");
        log.debug("xml: " + xmlSalida);
        return xmlSalida.toString();
    }

    private String obtenerXmlSalidaMinimis(HttpServletRequest request, String codigoOperacion, List<MinimisVO> lista) {
        StringBuffer xmlSalida = new StringBuffer();
        xmlSalida.append("<RESPUESTA>");
        xmlSalida.append("<CODIGO_OPERACION>");
        xmlSalida.append(codigoOperacion);
        xmlSalida.append("</CODIGO_OPERACION>");
        for (MinimisVO fila : lista) {
            xmlSalida.append("<FILA>");
            xmlSalida.append("<ID>");
            xmlSalida.append(fila.getId() != null ? fila.getId().toString() : "");
            xmlSalida.append("</ID>");

            xmlSalida.append("<ESTADO>");
            xmlSalida.append(fila.getEstado());
            xmlSalida.append("</ESTADO>");
            xmlSalida.append("<ORGANISMO>");
            xmlSalida.append(fila.getOrganismo());
            xmlSalida.append("</ORGANISMO>");
            xmlSalida.append("<OBJETO>");
            xmlSalida.append(fila.getObjeto());
            xmlSalida.append("</OBJETO>");
            xmlSalida.append("<IMPORTE>");
            if (fila.getImporte() != null && !"".equals(fila.getImporte())) {
                xmlSalida.append(fila.getImporte());
            } else {
                xmlSalida.append("null");
            }
            xmlSalida.append("</IMPORTE>");

            xmlSalida.append("<FECHA>");
            if (fila.getFecha() != null) {
                xmlSalida.append(dateFormat.format(fila.getFecha()));
            } else {
                xmlSalida.append("");
            }
            xmlSalida.append("</FECHA>");

            xmlSalida.append("</FILA>");
        }
        xmlSalida.append("</RESPUESTA>");
        log.debug("xml: " + xmlSalida);
        return xmlSalida.toString();
    }

    /**
     * Nueva acci�n para la pantalla de Desglose RSB (modal con pesta�as). Alineada
     * con la llamada usando parametro operacion=cargarDesgloseRSB. Coloca en
     * request los atributos necesarios y define las URLs de las pesta�as.
     */
    public String cargarDesgloseRSB(int codOrganizacion, int codTramite, int ocurrenciaTramite, String numExpediente,
            HttpServletRequest request, HttpServletResponse response) {
        String numExp = null;
        String idProyecto = null;
        String idContrato = null;
        try {
            numExp = request.getParameter("numExp");
            if (numExp == null || numExp.trim().isEmpty()) {
                numExp = numExpediente;
            }
            idProyecto = request.getParameter("idProyecto");
            idContrato = request.getParameter("id");

            request.setAttribute("numExp", numExp);
            request.setAttribute("idProyecto", idProyecto);
            if (idContrato != null && idContrato.trim().length() > 0) {
                request.setAttribute("id", idContrato); // Necesario para Tab1
            }

            try {
                if (idContrato != null && idContrato.trim().length() > 0) {
                    ContratacionVO vo = MeLanbide11Manager.getInstance().getContratacionPorID(idContrato,
                            this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
                    if (vo != null) {
                        if (vo.getRsbSalBase() != null) {
                            request.setAttribute("salarioBase", String.valueOf(vo.getRsbSalBase()).replace('.', ','));
                        }
                        if (vo.getRsbPagExtra() != null) {
                            request.setAttribute("pagasExtra", String.valueOf(vo.getRsbPagExtra()).replace('.', ','));
                        }
                        // El campo RSBIMPORTE en tabla contratacion lo usamos como "Complementos
                        // salariales" persistidos
                        if (vo.getRsbImporte() != null) {
                            request.setAttribute("compImporte", String.valueOf(vo.getRsbImporte()).replace('.', ','));
                        }
                        // Complementos extrasalariales: SIEMPRE se calculan desde la tabla de desglose
                        // por RSBTIPO=2
                        try {
                            String dni = vo.getDni();
                            if (dni != null && !dni.trim().isEmpty()) {
                                ComplementosPorTipo comp = MeLanbide11Manager.getInstance().getSumaComplementosPorTipo(
                                        numExp, dni, this.getAdaptSQLBD(String.valueOf(codOrganizacion)));
                                if (comp != null) {
                                    // Salariales (tipo 1) se usan solo como referencia para comparar con RSBIMPORTE
                                    // si se quisiera.
                                    double extras = comp.getExtrasalariales();
                                    request.setAttribute("compExtra", String.valueOf(extras).replace('.', ','));
                                    if (log.isDebugEnabled()) {
                                        // Debug de complementos
                                    }
                                } else {
                                    request.setAttribute("compExtra", "0");
                                }
                            }
                        } catch (Exception calcEx) {
                            log.warn("[cargarDesgloseRSB] No se pudieron calcular complementos extrasalariales",
                                    calcEx);
                        }
                    }
                }
            } catch (Exception inner) {
                log.warn("[cargarDesgloseRSB] Error cargando datos iniciales de contrataci�n para pesta�a 1", inner);
            }

            request.setAttribute("urlPestanaResumen", "/jsp/extension/melanbide11/desglose/m11Desglose_Tab1.jsp");
            request.setAttribute("urlPestanaComplementos", "/jsp/extension/melanbide11/desglose/m11Desglose_Tab2.jsp");
        } catch (Exception e) {
            log.error("Error en cargarDesgloseRSB", e);
        }
        return "/jsp/extension/melanbide11/desglose/m11Desglose.jsp";
    }

    /**
     * M�todo AJAX para guardar l�neas de desglose RSB. Recibe las l�neas como
     * par�metro "lineas" con formato de filas separado con ';;' y columnas con '|'
     * => tipo|importe|concepto|observ Ej: 1|1234.56|F|Observaci�n l�nea
     * 1;;2|100|V|Obs 2. Devuelve JSON:
     * {"resultado":{"codigoOperacion":X,"salariales":n,"extrasalariales":m,"totalComputable":t}}
     */
    public String guardarLineasDesgloseRSB(int codOrganizacion, int codTramite, int ocurrenciaTramite,
            String numExpediente, HttpServletRequest request, HttpServletResponse response) {
        String numExp = request.getParameter("numExp");
        if (numExp == null || numExp.trim().isEmpty()) {
            numExp = numExpediente; // fallback
        }
        String dni = request.getParameter("dni");
        String raw = request.getParameter("lineas");

        int codigoOperacion = 0; // 0 OK,1 BD,2 Sin filas (todo borrado ok),3 Par�metros,4 Gen�rico
        double salariales = 0d;
        double extrasalariales = 0d;
        double totalComputable = 0d; // RSBCOMPCONV derivado (no lo recalculamos aqu� salvo sumar)

        AdaptadorSQLBD adapt = null;
        try {
            adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));
        } catch (Exception e) {
            log.error("[guardarLineasDesgloseRSB] Error obteniendo adaptador", e);
        }

        if (adapt == null || numExp == null || dni == null || dni.trim().isEmpty()) {
            codigoOperacion = 3; // par�metros/adaptador
        } else {
            try {
                List<DesgloseRSBVO> lista = parseLineasDesglose(raw);
                boolean ok = MeLanbide11Manager.getInstance().reemplazarDesgloseRSB(numExp, dni, lista, adapt);
                if (!ok) {
                    codigoOperacion = 2; // no se insert� nada (pudo ser solo borrado o fallo silencioso)
                }
                // Recuperar sumas por tipo para devolver
                try {
                    ComplementosPorTipo comp = MeLanbide11Manager.getInstance().getSumaComplementosPorTipo(numExp, dni,
                            adapt);
                    if (comp != null) {
                        salariales = comp.getSalariales();
                        extrasalariales = comp.getExtrasalariales();
                    }
                    totalComputable = salariales; // se sumar� con salario base y pagas en otra llamada si aplica
                } catch (Exception sumEx) {
                    log.warn("[guardarLineasDesgloseRSB] No se pudieron recuperar sumas por tipo", sumEx);
                }
            } catch (Exception e) {
                log.error("[guardarLineasDesgloseRSB] Error BD reemplazando l�neas", e);
                codigoOperacion = 1; // BD
            }
        }

        try {
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            String json = new StringBuilder().append("{\"resultado\":{\"codigoOperacion\":").append(codigoOperacion)
                    .append(",\"salariales\":").append(salariales).append(",\"extrasalariales\":")
                    .append(extrasalariales).append(",\"totalComputable\":").append(totalComputable).append("}}")
                    .toString();
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
            return null;
        } catch (Exception ex) {
            log.error("[guardarLineasDesgloseRSB] Error enviando JSON", ex);
        }
        return null;
    }

    private List<DesgloseRSBVO> parseLineasDesglose(String raw) {
        // Delegamos en la utilidad para permitir pruebas unitarias independientes.
        return es.altia.flexia.integracion.moduloexterno.melanbide11.util.DesgloseRSBParser.parse(raw);
    }

    /**
     * M�todo AJAX para obtener las l�neas de desglose RSB en formato JSON.
     * Respuesta JSON: {"dni":"...",
     * "lineas":[{"tipo":"1","importe":123.45,"concepto":"X","observ":"Y"},...]}
     */
    public String listarLineasDesgloseRSB(int codOrganizacion, int codTramite, int ocurrenciaTramite,
            String numExpediente, HttpServletRequest request, HttpServletResponse response) {
        AdaptadorSQLBD adapt = null;
        List<DesgloseRSBVO> lista = new ArrayList<DesgloseRSBVO>();
        String numExp = request.getParameter("numExp");
        if (numExp == null || numExp.trim().isEmpty()) {
            numExp = numExpediente; // fallback
        }
        // Par�metro id de la contrataci�n seleccionada (opcional)
        String idSeleccion = request.getParameter("id");
        String dniSeleccion = null; // Se resolver� si llega id
        try {
            adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));
            if (adapt != null && numExp != null && numExp.trim().length() > 0) {
                boolean usarFiltro = (idSeleccion != null && idSeleccion.trim().length() > 0);
                if (usarFiltro) {
                    try {
                        // Optimizaci�n: resolver s�lo el DNI de la contrataci�n sin cargar toda la
                        // lista
                        java.sql.Connection con = null;
                        try {
                            con = adapt.getConnection();
                            dniSeleccion = es.altia.flexia.integracion.moduloexterno.melanbide11.dao.MeLanbide11DAO
                                    .getInstance().getDniContratacionById(numExp, idSeleccion, con);
                        } finally {
                            try {
                                if (con != null)
                                    adapt.devolverConexion(con);
                            } catch (Exception ignore) {
                            }
                        }
                        if (dniSeleccion != null && dniSeleccion.trim().length() > 0) {
                            lista = MeLanbide11Manager.getInstance().getDatosDesgloseRSBPorDni(numExp, dniSeleccion,
                                    codOrganizacion, adapt);
                            if (log.isDebugEnabled()) {
                                // Debug optimizado
                            }
                        } else {
                            lista = MeLanbide11Manager.getInstance().getDatosDesgloseRSB(numExp, codOrganizacion,
                                    adapt);
                            if (log.isDebugEnabled()) {
                                // Debug sin DNI asociado
                            }
                        }
                    } catch (Exception exId) {
                        log.warn("[listarLineasDesgloseRSB] Error optimizado resolviendo DNI por ID=" + idSeleccion
                                + ": " + exId.getMessage(), exId);
                        lista = MeLanbide11Manager.getInstance().getDatosDesgloseRSB(numExp, codOrganizacion, adapt);
                    }
                } else {
                    lista = MeLanbide11Manager.getInstance().getDatosDesgloseRSB(numExp, codOrganizacion, adapt);
                }
            }
        } catch (Exception e) {
            log.error("[listarLineasDesgloseRSB] Error recuperando datos", e);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\"dni\":\"").append(escapeJson(dniSeleccion != null ? dniSeleccion : "")).append("\",\"lineas\":[");
        for (int i = 0; i < lista.size(); i++) {
            DesgloseRSBVO vo = lista.get(i);
            if (i > 0)
                sb.append(',');
            sb.append('{');
            sb.append("\"tipo\":\"").append(escapeJson(nvlStr(vo.getRsbTipo()))).append("\",");
            Double imp = vo.getRsbImporte();
            sb.append("\"importe\":").append(imp == null ? 0 : imp.doubleValue()).append(',');
            sb.append("\"concepto\":\"").append(escapeJson(nvlStr(vo.getRsbConcepto()))).append("\",");
            sb.append("\"observ\":\"").append(escapeJson(nvlStr(vo.getRsbObserv()))).append("\"}");
        }
        sb.append("]}");

        try {
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            PrintWriter out = response.getWriter();
            out.print(sb.toString());
            out.flush();
        } catch (Exception ioe) {
            log.error("[listarLineasDesgloseRSB] Error enviando respuesta", ioe);
        }
        return null; // respuesta directa
    }

    private static String nvlStr(String v) {
        return v == null ? "" : v;
    }

    private static String escapeJson(String s) {
        if (s == null)
            return "";
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
            case '"':
                out.append("\\\"");
                break;
            case '\\':
                out.append("\\\\");
                break;
            case '\n':
                out.append("\\n");
                break;
            case '\r':
                out.append("\\r");
                break;
            case '\t':
                out.append("\\t");
                break;
            default:
                if (c < 32) {
                    out.append(String.format("\\u%04x", (int) c));
                } else {
                    out.append(c);
                }
            }
        }
        return out.toString();
    }

    /**
     * Endpoint AJAX para obtener la suma de complementos salariales y extrasalariales
     * por DNI y expediente.
     * Responde directamente con JSON: {"salariales":123.45,"extrasalariales":67.89}
     */
    public String getComplementosPorTipo(int codOrganizacion, int codTramite, int ocurrenciaTramite,
            String numExpediente, HttpServletRequest request, HttpServletResponse response) {
        try {
            String dni = request.getParameter("dni");
            String numExp = request.getParameter("numExp");

            if (numExp == null) {
                numExp = numExpediente;
            }

            AdaptadorSQLBD adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));
            ComplementosPorTipo complementos = null;
            try {
                complementos = MeLanbide11Manager.getInstance().getSumaComplementosPorTipo(numExp, dni, adapt);
            } catch (Exception daoEx) {
                log.error("[getComplementosPorTipo] Error DAO obteniendo complementos", daoEx);
            }
            if (complementos == null) {
                log.warn("[getComplementosPorTipo] complementos es null -> se devuelven 0,0");
                // Crear dummy para evitar NPE
                complementos = new ComplementosPorTipo(0d, 0d);
            }

            // Crear respuesta JSON simple
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"salariales\":").append(complementos.getSalariales()).append(",");
            json.append("\"extrasalariales\":").append(complementos.getExtrasalariales());
            json.append("}");

            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            PrintWriter out = response.getWriter();
            out.print(json.toString());
            out.flush();
            // No navegamos a JSP: respuesta directa JSON
            return null;
        } catch (Exception ex) {
            log.error("Error al obtener complementos por tipo", ex);
            try {
                response.setContentType("application/json; charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print("{\"salariales\":0,\"extrasalariales\":0}");
                out.flush();
            } catch (Exception ioEx) {
                log.error("Error enviando respuesta de error", ioEx);
            }
            return null;
        }
    }

}
