package es.altia.flexia.integracion.moduloexterno.melanbide11.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.altia.flexia.integracion.moduloexterno.melanbide11.bean.ComplementoSalarial;
import es.altia.flexia.integracion.moduloexterno.melanbide11.bean.OtraPercepcion;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.ContratacionVO;
import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.MinimisVO;
import es.altia.util.conexion.AdaptadorSQLBD;
import es.altia.util.conexion.BDException;

/**
 * Data Access Object para MELANBIDE11 - Version Java 6 compatible
 * Esta es una version simplificada compatible con Java 6
 */
public class MeLanbide11DAO {
    
    private AdaptadorSQLBD dataSource;
    
    public MeLanbide11DAO() {
        // Constructor por defecto
    }
    
    public MeLanbide11DAO(AdaptadorSQLBD dataSource) {
        this.dataSource = dataSource;
    }
    
    // Metodos basicos de contratacion
    public void insertarContratacion(ContratacionVO contratacion) throws BDException {
        // Stub implementation
        System.out.println("DAO: Insertando contratacion para " + contratacion.getDniCont());
    }
    
    public void actualizarContratacion(ContratacionVO contratacion) throws BDException {
        // Stub implementation  
        System.out.println("DAO: Actualizando contratacion " + contratacion.getIdContratacion());
    }
    
    public ContratacionVO obtenerContratacion(Long idContratacion) throws BDException {
        // Stub implementation
        ContratacionVO vo = new ContratacionVO();
        vo.setIdContratacion(idContratacion);
        return vo;
    }
    
    public void eliminarContratacion(Long idContratacion) throws BDException {
        // Stub implementation
        System.out.println("DAO: Eliminando contratacion " + idContratacion);
    }
    
    // Metodos de complementos salariales - Java 6 compatible
    public List<ComplementoSalarial> obtenerComplementos(Long idContratacion) throws SQLException {
        List<ComplementoSalarial> lista = new ArrayList<ComplementoSalarial>();
        String sql = "SELECT ID_COMPLEMENTO, IMPORTE, TIPO, OBSERVACIONES FROM M11_COMPLEMENTOS_SALARIALES WHERE ID_CONTRATACION = ?";
        
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement(sql);
            ps.setLong(1, idContratacion);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                ComplementoSalarial comp = new ComplementoSalarial();
                comp.setId(rs.getLong("ID_COMPLEMENTO"));
                comp.setImporte(rs.getBigDecimal("IMPORTE"));
                comp.setTipo(rs.getString("TIPO"));
                comp.setObservaciones(rs.getString("OBSERVACIONES"));
                comp.setIdContratacion(idContratacion);
                lista.add(comp);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (ps != null) try { ps.close(); } catch (SQLException e) {}
            if (c != null) try { c.close(); } catch (SQLException e) {}
        }
        
        return lista;
    }
    
    // Metodos de otras percepciones - Java 6 compatible  
    public List<OtraPercepcion> obtenerOtrasPercepciones(Long idContratacion) throws SQLException {
        List<OtraPercepcion> lista = new ArrayList<OtraPercepcion>();
        String sql = "SELECT ID, IMPORTE, TIPO, OBSERVACIONES FROM M11_OTRAS_PERCEPCIONES WHERE ID_CONTRATACION = ?";
        
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement(sql);
            ps.setLong(1, idContratacion);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                OtraPercepcion p = new OtraPercepcion();
                p.setId(rs.getLong("ID"));
                p.setImporte(rs.getBigDecimal("IMPORTE"));
                p.setTipo(rs.getString("TIPO"));
                p.setObservaciones(rs.getString("OBSERVACIONES"));
                p.setIdContratacion(idContratacion);
                lista.add(p);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (ps != null) try { ps.close(); } catch (SQLException e) {}
            if (c != null) try { c.close(); } catch (SQLException e) {}
        }
        
        return lista;
    }
    
    // Calculos de retribucion - Java 6 compatible
    public BigDecimal calcularYActualizarRetribucionComputable(Long idContratacion) throws SQLException {
        BigDecimal salarioBase = BigDecimal.ZERO;
        BigDecimal pagasExtra = BigDecimal.ZERO;
        String sqlBase = "SELECT NVL(SALARIO_BASE,0) as SALARIO_BASE, NVL(PAGAS_EXTRAORDINARIAS,0) as PAGAS_EXTRA FROM M11_CONTRATACIONES WHERE ID_CONTRATACION = ?";
        
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement(sqlBase);
            ps.setLong(1, idContratacion);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                salarioBase = rs.getBigDecimal("SALARIO_BASE");
                pagasExtra = rs.getBigDecimal("PAGAS_EXTRA");
            }
            
            // Calculo simple: retribucion computable = salario base + pagas extra
            BigDecimal computable = salarioBase.add(pagasExtra);
            
            // Actualizar el registro
            String upd = "UPDATE M11_CONTRATACIONES SET RETRIBUCION_COMPUTABLE = ? WHERE ID_CONTRATACION = ?";
            PreparedStatement ups = null;
            try {
                ups = c.prepareStatement(upd);
                ups.setBigDecimal(1, computable);
                ups.setLong(2, idContratacion);
                ups.executeUpdate();
            } finally {
                if (ups != null) try { ups.close(); } catch (SQLException e) {}
            }
            
            return computable;
            
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (ps != null) try { ps.close(); } catch (SQLException e) {}
            if (c != null) try { c.close(); } catch (SQLException e) {}
        }
    }
    
    // Metodos de minimis
    public void insertarMinimis(MinimisVO minimis) throws BDException {
        // Stub implementation
        System.out.println("DAO: Insertando minimis " + minimis.getImporte());
    }
    
    public void actualizarMinimis(MinimisVO minimis) throws BDException {
        // Stub implementation
        System.out.println("DAO: Actualizando minimis " + minimis.getIdMinimis());
    }
    
    public List<MinimisVO> obtenerMinimisPorContratacion(Long idContratacion) throws BDException {
        // Stub implementation
        return new ArrayList<MinimisVO>();
    }
    
    // Metodos de configuracion
    public Map<String, String> obtenerConfiguracion() throws BDException {
        // Stub implementation
        Map<String, String> config = new HashMap<String, String>();
        config.put("version", "1.0");
        return config;
    }
    
    // Recalculo completo - Java 6 compatible
    public Map<String, BigDecimal> recalcularTodo(Long idContratacion) throws SQLException {
        Map<String, BigDecimal> resultados = new HashMap<String, BigDecimal>();
        
        BigDecimal retribucionComputable = calcularYActualizarRetribucionComputable(idContratacion);
        resultados.put("retribucionComputable", retribucionComputable);
        
        // Agregar mas calculos segun sea necesario
        resultados.put("totalComplementos", BigDecimal.ZERO);
        resultados.put("totalOtrasPercepciones", BigDecimal.ZERO);
        
        return resultados;
    }
}