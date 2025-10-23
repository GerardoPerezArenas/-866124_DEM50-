package es.altia.technical;

import java.util.HashMap;
import java.util.Map;

/**
 * Stub implementation of PortableContext for compilation compatibility.
 * This is a minimal implementation for Java 6 compatibility.
 */
public class PortableContext {
    
    private Map<String, Object> attributes;
    
    public PortableContext() {
        this.attributes = new HashMap<String, Object>();
    }
    
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
    
    public Object getAttribute(String name) {
        return attributes.get(name);
    }
    
    public void removeAttribute(String name) {
        attributes.remove(name);
    }
    
    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }
    
    public void clear() {
        attributes.clear();
    }
    
    public Map<String, Object> getAttributes() {
        return new HashMap<String, Object>(attributes);
    }
}