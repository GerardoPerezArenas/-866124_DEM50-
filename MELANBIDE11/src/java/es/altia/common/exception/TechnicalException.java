package es.altia.common.exception;

/**
 * Stub implementation of TechnicalException for compilation compatibility
 */
public class TechnicalException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public TechnicalException() {
        super();
    }
    
    public TechnicalException(String message) {
        super(message);
    }
    
    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public TechnicalException(Throwable cause) {
        super(cause);
    }
}