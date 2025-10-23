package javax.servlet.http;

import java.io.IOException;

/**
 * Stub implementation of HttpServletResponse for compilation compatibility
 */
public interface HttpServletResponse {
    
    void setContentType(String type);
    
    void setCharacterEncoding(String charset);
    
    void setStatus(int sc);
    
    void sendRedirect(String location) throws IOException;
    
    void sendError(int sc) throws IOException;
    
    void sendError(int sc, String msg) throws IOException;
    
    void setHeader(String name, String value);
    
    void addHeader(String name, String value);
    
    void setIntHeader(String name, int value);
    
    void addIntHeader(String name, int value);
    
    void setDateHeader(String name, long date);
    
    void addDateHeader(String name, long date);
    
    String encodeURL(String url);
    
    String encodeRedirectURL(String url);
    
    // Common status codes
    int SC_OK = 200;
    int SC_MOVED_PERMANENTLY = 301;
    int SC_FOUND = 302;
    int SC_BAD_REQUEST = 400;
    int SC_UNAUTHORIZED = 401;
    int SC_FORBIDDEN = 403;
    int SC_NOT_FOUND = 404;
    int SC_INTERNAL_SERVER_ERROR = 500;
}