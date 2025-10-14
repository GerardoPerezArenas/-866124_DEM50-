
package es.altia.flexia.integracion.moduloexterno.melanbide11.manager;

import com.lanbide.lan6.errores.bean.ErrorBean;
import com.lanbide.lan6.registro.error.RegistroErrores;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.ContratacionVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.MinimisVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.dao.MeLanbide11DAO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.util.CalculosRetribucionUtil;
import es.altia.flexia.integracion.moduloexterno.melanbide11.util.ConfigurationParameter;
import es.altia.flexia.integracion.moduloexterno.melanbide11.util.ConstantesMeLanbide11;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.DatosTablaDesplegableExtVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.DesplegableAdmonLocalVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.DesplegableExternoVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.DesgloseRetribucionVO;
import java.math.BigDecimal;
import es.altia.util.conexion.AdaptadorSQLBD;
import es.altia.util.conexion.BDException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class MeLanbide11Manager {

    //Logger
    private static Logger log = Logger.getLogger(MeLanbide11Manager.class);

    //Instancia
    private static MeLanbide11Manager instance = null;

    public static MeLanbide11Manager getInstance() {
        if (instance == null) {
            synchronized (MeLanbide11Manager.class) {
                instance = new MeLanbide11Manager();
            }
        }
        return instance;
    }

    public static void grabarError(ErrorBean error, String excepError, String traza, String numExp) {
        try {
            log.error("grabando el error");
            error.setMensajeExcepError(excepError);
            error.setTraza(excepError);
            error.setCausa(traza);
            log.error("causa: " + traza);
            log.error("numExp: " + numExp);
            if ("".equals(numExp)) {
                numExp = "0000/ERRMISGEST/000000";
            }
           
            String idProcedimiento = "DEM50";
            log.error("procedimiento: " + idProcedimiento);
            error.setIdProcedimiento(idProcedimiento);
            error.setIdClave("");
            error.setSistemaOrigen("REGEXLAN");
            error.setErrorLog("flexia_debug");
            error.setIdFlexia(numExp);
            log.error("Vamos a registrar el error");

            RegistroErrores.registroError(error);
        } catch (Exception ex) {
            log.error("Error al grabarError" + ex);
        }
    }

    public List<ContratacionVO> getDatosContratacion(String numExp, int codOrganizacion, AdaptadorSQLBD adapt) throws Exception {
        List<ContratacionVO> lista = new ArrayList<ContratacionVO>();
        Connection con = null;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            lista = meLanbide11DAO.getDatosContratacion(numExp, codOrganizacion, con);
            //recuperamos los cod y desc de desplegables para traducir en la tabla principal
            List<DesplegableAdmonLocalVO> listaSexo = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_SEXO, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            List<DesplegableAdmonLocalVO> listaMayor55 = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_BOOL, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            List<DesplegableAdmonLocalVO> listaFinFormativa = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_BOOL, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            List<DesplegableAdmonLocalVO> listaJornada = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_JORN, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            List<DesplegableAdmonLocalVO> listaGrupoCotizacion = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_GCOT, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            List<DesplegableAdmonLocalVO> listaTipRetribucion = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_DTRT, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
           
           
            //desplegables externos
            DatosTablaDesplegableExtVO datosTablaDesplegableOcupaciones = MeLanbide11Manager.getInstance().getDatosMapeoDesplegableExterno(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_EXT_OCIN, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            String tablaOcupaciones = datosTablaDesplegableOcupaciones.getTabla();
            String campoCodigoOcupaciones = datosTablaDesplegableOcupaciones.getCampoCodigo();
            String campoValorOcupaciones = datosTablaDesplegableOcupaciones.getCampoValor();
            List<DesplegableExternoVO> listaOcupacion = MeLanbide11Manager.getInstance().getValoresDesplegablesExternos(tablaOcupaciones, campoCodigoOcupaciones, campoValorOcupaciones, adapt);
        
            DatosTablaDesplegableExtVO datosTablaDesplegableTitulaciones = MeLanbide11Manager.getInstance().getDatosMapeoDesplegableExterno(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_EXT_TIIN, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            String tablaTitulaciones = datosTablaDesplegableTitulaciones.getTabla();
            String campoCodigoTitulaciones = datosTablaDesplegableTitulaciones.getCampoCodigo();
            String campoValorTitulaciones = datosTablaDesplegableTitulaciones.getCampoValor();
            List<DesplegableExternoVO> listaTitulacion = MeLanbide11Manager.getInstance().getValoresDesplegablesExternos(tablaTitulaciones, campoCodigoTitulaciones, campoValorTitulaciones, adapt);
       
            DatosTablaDesplegableExtVO datosTablaDesplegableCProfesionales = MeLanbide11Manager.getInstance().getDatosMapeoDesplegableExterno(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_EXT_CPIN, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            String tablaCProfesionales = datosTablaDesplegableCProfesionales.getTabla();
            String campoCodigoCProfesionales = datosTablaDesplegableCProfesionales.getCampoCodigo();
            String campoValorCProfesionales = datosTablaDesplegableCProfesionales.getCampoValor();
            List<DesplegableExternoVO> listaCProfesionalidad = MeLanbide11Manager.getInstance().getValoresDesplegablesExternos(tablaCProfesionales, campoCodigoCProfesionales, campoValorCProfesionales, adapt);
            
            for (ContratacionVO cont : lista) {
                for (DesplegableAdmonLocalVO valordesp : listaSexo) {
                    if (valordesp.getDes_val_cod().equals(cont.getSexo())) {
                        cont.setDesSexo(valordesp.getDes_nom());
                        break;
                    }
                }
                for (DesplegableAdmonLocalVO valordesp : listaMayor55) {
                    if (valordesp.getDes_val_cod().equals(cont.getMayor55())) {
                        cont.setMayor55(valordesp.getDes_nom());
                        break;
                    }
                }
                for (DesplegableAdmonLocalVO valordesp : listaFinFormativa) {
                    if (valordesp.getDes_val_cod().equals(cont.getFinFormativa())) {
                        cont.setFinFormativa(valordesp.getDes_nom());
                        break;
                    }
                }
                for (DesplegableAdmonLocalVO valordesp : listaJornada) {
                    if (valordesp.getDes_val_cod().equals(cont.getJornada())) {
                        cont.setDesJornada(valordesp.getDes_nom());
                        break;
                    }
                }
                for (DesplegableAdmonLocalVO valordesp : listaGrupoCotizacion) {
                    if (valordesp.getDes_val_cod().equals(cont.getGrupoCotizacion())) {
                        cont.setDesGrupoCotizacion(valordesp.getDes_nom());
                        break;
                    }
                }
                for (DesplegableAdmonLocalVO valordesp : listaTipRetribucion) {
                    if (valordesp.getDes_val_cod().equals(cont.getTipRetribucion())) {
                        cont.setDesTipRetribucion(valordesp.getDes_nom());
                        break;
                    }
                }
                
                //desplegables externos
                for (DesplegableExternoVO valordesp : listaOcupacion) {
                    if (valordesp.getCampoCodigo().equals(cont.getOcupacion())) {
                        cont.setDesOcupacion(valordesp.getCampoValor());
                        break;
                    }
                }
                for (DesplegableExternoVO valordesp : listaTitulacion) {
                    if (valordesp.getCampoCodigo().equals(cont.getTitulacion())) {
                        cont.setDesTitulacion(valordesp.getCampoValor());
                        break;
                    }
                }
                for (DesplegableExternoVO valordesp : listaCProfesionalidad) {
                    if (valordesp.getCampoCodigo().equals(cont.getcProfesionalidad())) {
                        cont.setDesCProfesionalidad(valordesp.getCampoValor());
                        break;
                    }
                }
            }
            
            return lista;
        } catch (BDException e) {
            log.error("Se ha producido una excepci�n en la BBDD recuperando datos sobre las contrataciones ", e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepci�n en la BBDD recuperando datos sobre las contrataciones ", ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexi�n a la BBDD: " + e.getMessage());
            }
        }
    }

    public ContratacionVO getContratacionPorID(String id, AdaptadorSQLBD adapt) throws Exception {
        Connection con = null;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            return meLanbide11DAO.getContratacionPorID(id, con);
        } catch (BDException e) {
            log.error("Se ha producido una excepcion en la BBDD recuperando datos sobre una contrataci�n:  " + id, e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepcion en la BBDD recuperando datos sobre una contrataci�n:  " + id, ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexion a la BBDD: " + e.getMessage());
            }
        }
    }

    public int eliminarContratacion(String id, AdaptadorSQLBD adapt) throws Exception {
        Connection con = null;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            return meLanbide11DAO.eliminarContratacion(id, con);
        } catch (BDException e) {
            log.error("Se ha producido una excepci�n en la BBDD al eliminar una contrataci�n:  " + id, e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepci�n en la BBDD al eliminar una contrataci�n:   " + id, ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexi�n a la BBDD: " + e.getMessage());
            }
        }
    }

    public List<ContratacionVO> getContrataciones(String numExp, AdaptadorSQLBD adapt) throws Exception {
        List<ContratacionVO> lista = new ArrayList<ContratacionVO>();
        Connection con = null;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            lista = meLanbide11DAO.getContratacion(numExp, con);

            //recuperamos los cod y desc de desplegables para traducir en la tabla principal
            List<DesplegableAdmonLocalVO> listaSexo = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_SEXO, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            List<DesplegableAdmonLocalVO> listaMayor55 = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_BOOL, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            List<DesplegableAdmonLocalVO> listaFinFormativa = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_BOOL, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            List<DesplegableAdmonLocalVO> listaJornada = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_JORN, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            List<DesplegableAdmonLocalVO> listaGrupoCotizacion = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_GCOT, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            List<DesplegableAdmonLocalVO> listaTipRetribucion = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_DTRT, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
           
            
            //desplegables externos
            DatosTablaDesplegableExtVO datosTablaDesplegableOcupaciones = MeLanbide11Manager.getInstance().getDatosMapeoDesplegableExterno(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_EXT_OCIN, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            String tablaOcupaciones = datosTablaDesplegableOcupaciones.getTabla();
            String campoCodigoOcupaciones = datosTablaDesplegableOcupaciones.getCampoCodigo();
            String campoValorOcupaciones = datosTablaDesplegableOcupaciones.getCampoValor();
            List<DesplegableExternoVO> listaOcupacion = MeLanbide11Manager.getInstance().getValoresDesplegablesExternos(tablaOcupaciones, campoCodigoOcupaciones, campoValorOcupaciones, adapt);
        
            DatosTablaDesplegableExtVO datosTablaDesplegableTitulaciones = MeLanbide11Manager.getInstance().getDatosMapeoDesplegableExterno(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_EXT_TIIN, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            String tablaTitulaciones = datosTablaDesplegableTitulaciones.getTabla();
            String campoCodigoTitulaciones = datosTablaDesplegableTitulaciones.getCampoCodigo();
            String campoValorTitulaciones = datosTablaDesplegableTitulaciones.getCampoValor();
            List<DesplegableExternoVO> listaTitulacion = MeLanbide11Manager.getInstance().getValoresDesplegablesExternos(tablaTitulaciones, campoCodigoTitulaciones, campoValorTitulaciones, adapt);
            
            DatosTablaDesplegableExtVO datosTablaDesplegableCProfesionales = MeLanbide11Manager.getInstance().getDatosMapeoDesplegableExterno(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_EXT_CPIN, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
            String tablaCProfesionales = datosTablaDesplegableCProfesionales.getTabla();
            String campoCodigoCProfesionales = datosTablaDesplegableCProfesionales.getCampoCodigo();
            String campoValorCProfesionales = datosTablaDesplegableCProfesionales.getCampoValor();
            List<DesplegableExternoVO> listaCProfesionalidad = MeLanbide11Manager.getInstance().getValoresDesplegablesExternos(tablaCProfesionales, campoCodigoCProfesionales, campoValorCProfesionales, adapt);
            
            for (ContratacionVO cont : lista) {
                for (DesplegableAdmonLocalVO valordesp : listaSexo) {
                    if (valordesp.getDes_val_cod().equals(cont.getSexo())) {
                        cont.setDesSexo(valordesp.getDes_nom());
                        break;
                    }
                }
                for (DesplegableAdmonLocalVO valordesp : listaMayor55) {
                    if (valordesp.getDes_val_cod().equals(cont.getMayor55())) {
                        cont.setMayor55(valordesp.getDes_nom());
                        break;
                    }
                }
                for (DesplegableAdmonLocalVO valordesp : listaFinFormativa) {
                    if (valordesp.getDes_val_cod().equals(cont.getFinFormativa())) {
                        cont.setFinFormativa(valordesp.getDes_nom());
                        break;
                    }
                }
                for (DesplegableAdmonLocalVO valordesp : listaJornada) {
                    if (valordesp.getDes_val_cod().equals(cont.getJornada())) {
                        cont.setDesJornada(valordesp.getDes_nom());
                        break;
                    }
                }
                for (DesplegableAdmonLocalVO valordesp : listaGrupoCotizacion) {
                    if (valordesp.getDes_val_cod().equals(cont.getGrupoCotizacion())) {
                        cont.setDesGrupoCotizacion(valordesp.getDes_nom());
                        break;
                    }
                }
                for (DesplegableAdmonLocalVO valordesp : listaTipRetribucion) {
                    if (valordesp.getDes_val_cod().equals(cont.getTipRetribucion())) {
                        cont.setDesTipRetribucion(valordesp.getDes_nom());
                        break;
                    }
                }
                
                //desplegables externos
                for (DesplegableExternoVO valordesp : listaOcupacion) {
                    if (valordesp.getCampoCodigo().equals(cont.getOcupacion())) {
                        cont.setDesOcupacion(valordesp.getCampoValor());
                        break;
                    }
                }
                for (DesplegableExternoVO valordesp : listaTitulacion) {
                    if (valordesp.getCampoCodigo().equals(cont.getTitulacion())) {
                        cont.setDesTitulacion(valordesp.getCampoValor());
                        break;
                    }
                }
                for (DesplegableExternoVO valordesp : listaCProfesionalidad) {
                    if (valordesp.getCampoCodigo().equals(cont.getcProfesionalidad())) {
                        cont.setDesCProfesionalidad(valordesp.getCampoValor());
                        break;
                    }
                }
            }
            
            return lista;
        } catch (BDException e) {
            log.error("Se ha producido una excepci�n en la BBDD recuperando las contrataciones:  " + e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepci�n general en la BBDD recuperando las contrataciones:   " + ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexi�n a la BBDD: " + e.getMessage());
            }
        }
    }

    public boolean crearNuevaContratacion(ContratacionVO nuevaContratacion, AdaptadorSQLBD adapt) throws Exception {
        Connection con = null;
        boolean insertOK;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            insertOK = meLanbide11DAO.crearNuevaContratacion(nuevaContratacion, con);
        } catch (BDException e) {
            log.error("Se ha producido una excepci�n en la BBDD creando una contrataci�n : " + e.getMessage(), e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepci�n en la BBDD creando una contrataci�n : " + ex.getMessage(), ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexi�n a la BBDD: " + e.getMessage());
            }
        }
        return insertOK;
    }

    public boolean modificarContratacion(ContratacionVO datModif, AdaptadorSQLBD adapt) throws Exception {
        Connection con = null;
        boolean insertOK;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            insertOK = meLanbide11DAO.modificarContratacion(datModif, con);
        } catch (BDException e) {
            log.error("Se ha producido una excepci�n en la BBDD actualizando una contrataci�n : " + e.getMessage(), e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepci�n en la BBDD actualizando una contrataci�n : " + ex.getMessage(), ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexi�n a la BBDD: " + e.getMessage());
            }
        }
        return insertOK;
    }
    
    public List<MinimisVO> getDatosMinimis(String numExp, int codOrganizacion, AdaptadorSQLBD adapt) throws Exception {
        List<MinimisVO> lista = new ArrayList<MinimisVO>();
        Connection con = null;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            lista = meLanbide11DAO.getDatosMinimis(numExp, codOrganizacion, con);
            //recuperamos los cod y desc de desplegables para traducir en la tabla principal
            List<DesplegableAdmonLocalVO> listaEstado = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_DTSV, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
       
            for (MinimisVO cont : lista) {
                for (DesplegableAdmonLocalVO valordesp : listaEstado) {
                    if (valordesp.getDes_val_cod().equals(cont.getEstado())) {
                        cont.setDesEstado(valordesp.getDes_nom());
                        break;
                    }
                }
            }
            
            return lista;
        } catch (BDException e) {
            log.error("Se ha producido una excepci�n en la BBDD recuperando datos sobre las minimis ", e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepci�n en la BBDD recuperando datos sobre las minimis ", ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexi�n a la BBDD: " + e.getMessage());
            }
        }
    }

    public MinimisVO getMinimisPorID(String id, AdaptadorSQLBD adapt) throws Exception {
        Connection con = null;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            return meLanbide11DAO.getMinimisPorID(id, con);
        } catch (BDException e) {
            log.error("Se ha producido una excepcion en la BBDD recuperando datos sobre una minimis:  " + id, e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepcion en la BBDD recuperando datos sobre una minimis:  " + id, ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexion a la BBDD: " + e.getMessage());
            }
        }
    }

    public int eliminarMinimis(String id, AdaptadorSQLBD adapt) throws Exception {
        Connection con = null;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            return meLanbide11DAO.eliminarMinimis(id, con);
        } catch (BDException e) {
            log.error("Se ha producido una excepci�n en la BBDD al eliminar una minimis:  " + id, e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepci�n en la BBDD al eliminar una minimis:   " + id, ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexi�n a la BBDD: " + e.getMessage());
            }
        }
    }

    public List<MinimisVO> getMinimis(String numExp, AdaptadorSQLBD adapt) throws Exception {
        List<MinimisVO> lista = new ArrayList<MinimisVO>();
        Connection con = null;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            lista = meLanbide11DAO.getMinimis(numExp, con);

            //recuperamos los cod y desc de desplegables para traducir en la tabla principal
            List<DesplegableAdmonLocalVO> listaEstado = MeLanbide11Manager.getInstance().getValoresDesplegablesAdmonLocalxdes_cod(ConfigurationParameter.getParameter(ConstantesMeLanbide11.COD_DES_DTSV, ConstantesMeLanbide11.FICHERO_PROPIEDADES), adapt);
       
            for (MinimisVO cont : lista) {
                for (DesplegableAdmonLocalVO valordesp : listaEstado) {
                    if (valordesp.getDes_val_cod().equals(cont.getEstado())) {
                        cont.setDesEstado(valordesp.getDes_nom());
                        break;
                    }
                }
            }
            
            return lista;
        } catch (BDException e) {
            log.error("Se ha producido una excepci�n en la BBDD recuperando las minimis:  " + e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepci�n general en la BBDD recuperando las minimis:   " + ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexi�n a la BBDD: " + e.getMessage());
            }
        }
    }

    public boolean crearNuevaMinimis(MinimisVO nuevaMinimis, AdaptadorSQLBD adapt) throws Exception {
        Connection con = null;
        boolean insertOK;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            insertOK = meLanbide11DAO.crearNuevaMinimis(nuevaMinimis, con);
        } catch (BDException e) {
            log.error("Se ha producido una excepci�n en la BBDD creando una minimis : " + e.getMessage(), e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepci�n en la BBDD creando una minimis : " + ex.getMessage(), ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexi�n a la BBDD: " + e.getMessage());
            }
        }
        return insertOK;
    }

    public boolean modificarMinimis(MinimisVO datModif, AdaptadorSQLBD adapt) throws Exception {
        Connection con = null;
        boolean insertOK;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            insertOK = meLanbide11DAO.modificarMinimis(datModif, con);
        } catch (BDException e) {
            log.error("Se ha producido una excepci�n en la BBDD actualizando una minimis : " + e.getMessage(), e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepci�n en la BBDD actualizando una minimis : " + ex.getMessage(), ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexi�n a la BBDD: " + e.getMessage());
            }
        }
        return insertOK;
    }

    public List<DesplegableAdmonLocalVO> getValoresDesplegablesAdmonLocalxdes_cod(String des_cod, AdaptadorSQLBD adapt) throws Exception {
        Connection con = null;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            return meLanbide11DAO.getValoresDesplegablesAdmonLocalxdes_cod(des_cod, con);
        } catch (BDException e) {
            log.error("Se ha producido una excepci�n en la BBDD recuperando valores de desplegable : " + des_cod, e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepci�n en la BBDD recuperando valores de desplegable :  " + des_cod, ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexi�n a la BBDD: " + e.getMessage());
            }
        }
    }
    
    public DatosTablaDesplegableExtVO getDatosMapeoDesplegableExterno(String des_cod, AdaptadorSQLBD adapt) throws Exception {
        Connection con = null;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            return meLanbide11DAO.getDatosMapeoDesplegableExterno(des_cod, con);
        } catch (BDException e) {
            log.error("Se ha producido una excepci�n en la BBDD recuperando valores de datos de tabla de desplegable externo : " + des_cod, e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepci�n en la BBDD recuperando valores de datos de tabla de desplegable externo :  " + des_cod, ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexi�n a la BBDD: " + e.getMessage());
            }
        }
    }

    public List<DesplegableExternoVO> getValoresDesplegablesExternos(String tablaDesplegable, String campoCodigo, String campoValor, AdaptadorSQLBD adapt) throws Exception {
        Connection con = null;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            return meLanbide11DAO.getValoresDesplegablesExternos(tablaDesplegable, campoCodigo, campoValor, con);
        } catch (BDException e) {
            log.error("Se ha producido una excepci�n en la BBDD recuperando valores de desplegable externo de tabla : " + tablaDesplegable, e);
            throw new Exception(e);
        } catch (Exception ex) {
            log.error("Se ha producido una excepci�n en la BBDD recuperando valores de desplegable externo de tabla :  " + tablaDesplegable, ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexi�n a la BBDD: " + e.getMessage());
            }
        }
    }
    
    /**
     * Obtiene las líneas de desglose de retribución para un expediente y DNI
     */
    public List<DesgloseRetribucionVO> getLineasDesgloseRSB(String numExp, String dni, AdaptadorSQLBD adapt) throws Exception {
        Connection con = null;
        try {
            con = adapt.getConnection();
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            return meLanbide11DAO.getLineasDesgloseRSB(numExp, dni, con);
        } catch (Exception ex) {
            log.error("Excepción recuperando líneas desglose RSB para numExp=" + numExp + ", dni=" + dni, ex);
            throw new Exception(ex);
        } finally {
            try {
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexión a la BBDD: " + e.getMessage());
            }
        }
    }
    
    /**
     * Guarda las líneas de desglose de retribución para un expediente y DNI
     * Primero elimina todas las líneas existentes y luego inserta las nuevas
     */
    public boolean guardarLineasDesgloseRSB(String numExp, String dni, List<DesgloseRetribucionVO> lineas, AdaptadorSQLBD adapt) throws Exception {
        Connection con = null;
        try {
            con = adapt.getConnection();
            con.setAutoCommit(false); // Iniciar transacción
            
            MeLanbide11DAO meLanbide11DAO = MeLanbide11DAO.getInstance();
            
            // Eliminar líneas existentes
            meLanbide11DAO.eliminarLineasDesgloseRSB(numExp, dni, con);
            
            // Insertar nuevas líneas
            if (lineas != null && !lineas.isEmpty()) {
                for (DesgloseRetribucionVO linea : lineas) {
                    linea.setNumExp(numExp);
                    linea.setDni(dni);
                    meLanbide11DAO.insertarLineaDesgloseRSB(linea, con);
                }
            }
            
            con.commit(); // Confirmar transacción
            return true;
        } catch (Exception ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception rollbackEx) {
                    log.error("Error haciendo rollback", rollbackEx);
                }
            }
            log.error("Excepción guardando líneas desglose RSB para numExp=" + numExp + ", dni=" + dni, ex);
            throw new Exception(ex);
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                }
                adapt.devolverConexion(con);
            } catch (Exception e) {
                log.error("Error al cerrar conexión a la BBDD: " + e.getMessage());
            }
        }
    }
    
    /**
     * Calcula la retribución computable basándose en las líneas de desglose
     * Solo incluye salario base, pagas extraordinarias y complementos FIJOS
     */
    public BigDecimal calcularRetribucionComputable(
            Double salarioBase, 
            Double pagasExtraordinarias, 
            List<DesgloseRetribucionVO> lineasDesglose) {
        
        try {
            // Filtrar solo complementos salariales (tipo 1)
            List<DesgloseRetribucionVO> complementos = new ArrayList<DesgloseRetribucionVO>();
            if (lineasDesglose != null) {
                for (DesgloseRetribucionVO linea : lineasDesglose) {
                    if (linea != null && linea.getTipo() != null && linea.getTipo() == 1) {
                        complementos.add(linea);
                    }
                }
            }
            
            return CalculosRetribucionUtil.calcularRetribucionComputable(
                CalculosRetribucionUtil.toBigDecimal(salarioBase),
                CalculosRetribucionUtil.toBigDecimal(pagasExtraordinarias),
                complementos
            );
        } catch (Exception ex) {
            log.error("Error calculando retribución computable", ex);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Calcula el coste total del contrato (CSTCONT)
     * Incluye TODO: salario base, pagas extraordinarias, complementos fijos, variables y otras percepciones
     */
    public BigDecimal calcularCosteContratoTotal(
            Double salarioBase,
            Double pagasExtraordinarias,
            List<DesgloseRetribucionVO> lineasDesglose) {
        
        try {
            // Separar complementos salariales (tipo 1) y otras percepciones (tipo 2)
            List<DesgloseRetribucionVO> complementosSalariales = new ArrayList<DesgloseRetribucionVO>();
            List<DesgloseRetribucionVO> otrasPercepciones = new ArrayList<DesgloseRetribucionVO>();
            
            if (lineasDesglose != null) {
                for (DesgloseRetribucionVO linea : lineasDesglose) {
                    if (linea != null && linea.getTipo() != null) {
                        if (linea.getTipo() == 1) {
                            complementosSalariales.add(linea);
                        } else if (linea.getTipo() == 2) {
                            otrasPercepciones.add(linea);
                        }
                    }
                }
            }
            
            return CalculosRetribucionUtil.calcularCosteContratoTotal(
                CalculosRetribucionUtil.toBigDecimal(salarioBase),
                CalculosRetribucionUtil.toBigDecimal(pagasExtraordinarias),
                complementosSalariales,
                otrasPercepciones
            );
        } catch (Exception ex) {
            log.error("Error calculando coste total del contrato", ex);
            return BigDecimal.ZERO;
        }
    }

    
}
