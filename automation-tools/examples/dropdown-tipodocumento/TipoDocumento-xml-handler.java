
        } else if (operacion.equals("cargarTipoDocumento")) {
            try {
                List<DesplegableTipoDocumentoVO> lista = manager.cargarTipoDocumento(numExp);
                
                // Generar XML de respuesta
                escritor.println("<RESPUESTA>");
                escritor.println("<CODIGO_OPERACION>0</CODIGO_OPERACION>");
                
                for (DesplegableTipoDocumentoVO vo : lista) {
                    escritor.println("<FILA>");
                    escritor.println("<CODIGO>" + (vo.getCodigo() != null ? vo.getCodigo() : "") + "</CODIGO>");
                    escritor.println("<DESCRIPCION>" + (vo.getDescripcion() != null ? vo.getDescripcion() : "") + "</DESCRIPCION>");
                    escritor.println("</FILA>");
                }
                
                escritor.println("</RESPUESTA>");
                
            } catch (Exception e) {
                log.error("Error cargando TipoDocumento: " + e.getMessage(), e);
                escritor.println("<RESPUESTA>");
                escritor.println("<CODIGO_OPERACION>1</CODIGO_OPERACION>");
                escritor.println("</RESPUESTA>");
            }
