
    /**
     * Cargar lista de TipoDocumento
     * @param numExp Número de expediente
     * @return Lista de DesplegableTipoDocumentoVO
     * @throws TechnicalException
     */
    public List<DesplegableTipoDocumentoVO> cargarTipoDocumento(String numExp) throws TechnicalException {
        List<DesplegableTipoDocumentoVO> lista = new ArrayList<DesplegableTipoDocumentoVO>();
        
        try {
            MeLanbide11DAO dao = new MeLanbide11DAO();
            lista = dao.cargarTipoDocumento(numExp);
            
        } catch (Exception e) {
            log.error("Error cargando TipoDocumento: " + e.getMessage(), e);
            throw new TechnicalException("Error cargando TipoDocumento", e);
        }
        
        return lista;
    }
