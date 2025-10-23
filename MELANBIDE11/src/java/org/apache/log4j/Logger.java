package org.apache.log4j;

/**
 * Stub implementation of Log4j Logger for compilation compatibility
 * This is a simplified version for Java 6 compilation only.
 */
public class Logger {
    
    private String name;
    
    private Logger(String name) {
        this.name = name;
    }
    
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz.getName());
    }
    
    public static Logger getLogger(String name) {
        return new Logger(name);
    }
    
    public void debug(Object message) {
        System.out.println("[DEBUG] " + name + ": " + message);
    }
    
    public void debug(Object message, Throwable t) {
        System.out.println("[DEBUG] " + name + ": " + message);
        if (t != null) {
            t.printStackTrace();
        }
    }
    
    public void info(Object message) {
        System.out.println("[INFO] " + name + ": " + message);
    }
    
    public void info(Object message, Throwable t) {
        System.out.println("[INFO] " + name + ": " + message);
        if (t != null) {
            t.printStackTrace();
        }
    }
    
    public void warn(Object message) {
        System.out.println("[WARN] " + name + ": " + message);
    }
    
    public void warn(Object message, Throwable t) {
        System.out.println("[WARN] " + name + ": " + message);
        if (t != null) {
            t.printStackTrace();
        }
    }
    
    public void error(Object message) {
        System.err.println("[ERROR] " + name + ": " + message);
    }
    
    public void error(Object message, Throwable t) {
        System.err.println("[ERROR] " + name + ": " + message);
        if (t != null) {
            t.printStackTrace();
        }
    }
    
    public boolean isDebugEnabled() {
        return true;
    }
    
    public boolean isInfoEnabled() {
        return true;
    }
}