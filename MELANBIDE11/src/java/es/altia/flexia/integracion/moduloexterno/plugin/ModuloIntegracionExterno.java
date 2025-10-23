package es.altia.flexia.integracion.moduloexterno.plugin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Stub implementation of ModuloIntegracionExterno for compilation compatibility
 */
public abstract class ModuloIntegracionExterno {
    
    public abstract void procesar(String organizacion, String usuario, String usuarioId, 
            String numExpediente, HttpServletRequest request, HttpServletResponse response);
    
    protected void log(String message) {
        System.out.println("[ModuloIntegracion] " + message);
    }
}