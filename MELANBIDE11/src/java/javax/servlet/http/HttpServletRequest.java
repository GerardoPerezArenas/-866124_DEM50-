package javax.servlet.http;

import java.util.Enumeration;

/**
 * Stub implementation of HttpServletRequest for compilation compatibility
 */
public interface HttpServletRequest {
    
    String getParameter(String name);
    
    String[] getParameterValues(String name);
    
    Enumeration<String> getParameterNames();
    
    String getMethod();
    
    String getRequestURI();
    
    String getQueryString();
    
    HttpSession getSession();
    
    HttpSession getSession(boolean create);
    
    Object getAttribute(String name);
    
    void setAttribute(String name, Object value);
    
    Enumeration<String> getAttributeNames();
    
    void removeAttribute(String name);
    
    String getContextPath();
    
    String getServletPath();
    
    String getPathInfo();
}