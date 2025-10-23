package es.altia.util.conexion;

import java.sql.SQLException;

/**
 * Stub implementation of BDException for compilation compatibility
 */
public class BDException extends SQLException {
    
    private static final long serialVersionUID = 1L;
    
    public BDException() {
        super();
    }
    
    public BDException(String message) {
        super(message);
    }
    
    public BDException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public BDException(Throwable cause) {
        super(cause);
    }
}