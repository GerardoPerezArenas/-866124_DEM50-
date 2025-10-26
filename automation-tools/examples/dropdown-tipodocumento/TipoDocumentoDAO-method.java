
    /**
     * Cargar lista de TipoDocumento desde base de datos
     * @param numExp Número de expediente
     * @return Lista de DesplegableTipoDocumentoVO
     * @throws BDException
     */
    public List<DesplegableTipoDocumentoVO> cargarTipoDocumento(String numExp) throws BDException {
        List<DesplegableTipoDocumentoVO> lista = new ArrayList<DesplegableTipoDocumentoVO>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConexion();
            stmt = conn.createStatement();
            
            String sql = "SELECT CODIGO, DESCRIPCION, ACTIVO, ORDEN " +
                        "FROM MELANBIDE11_DESP_TIPODOCUMENTO " +
                        "WHERE ACTIVO = 'S' " +
                        "ORDER BY ORDEN, DESCRIPCION";
            
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                DesplegableTipoDocumentoVO vo = new DesplegableTipoDocumentoVO();
                vo.setCodigo(rs.getString("CODIGO"));
                vo.setDescripcion(rs.getString("DESCRIPCION"));
                vo.setActivo(rs.getString("ACTIVO"));
                vo.setOrden(rs.getInt("ORDEN"));
                
                lista.add(vo);
            }
            
        } catch (SQLException e) {
            log.error("Error ejecutando consulta para TipoDocumento: " + e.getMessage(), e);
            throw new BDException("Error cargando TipoDocumento", e);
        } finally {
            cerrarRecursos(rs, stmt, conn);
        }
        
        return lista;
    }
