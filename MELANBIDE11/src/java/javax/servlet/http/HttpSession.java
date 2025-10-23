package javax.servlet.http;

import java.util.Enumeration;

/**
 * Stub implementation of HttpSession for compilation compatibility
 */
public interface HttpSession {
    
    Object getAttribute(String name);
    
    void setAttribute(String name, Object value);
    
    Enumeration<String> getAttributeNames();
    
    void removeAttribute(String name);
    
    String getId();
    
    long getCreationTime();
    
    long getLastAccessedTime();
    
    int getMaxInactiveInterval();
    
    void setMaxInactiveInterval(int interval);
    
    void invalidate();
    
    boolean isNew();
}