package com.lanbide.lan6.errores.bean;

import java.io.Serializable;

/**
 * Stub implementation of ErrorBean for compilation compatibility.
 * This is a minimal implementation for Java 6 compatibility.
 */
public class ErrorBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String codigo;
    private String mensaje;
    private String tipo;
    private String origen;
    
    public ErrorBean() {
    }
    
    public ErrorBean(String codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getOrigen() {
        return origen;
    }
    
    public void setOrigen(String origen) {
        this.origen = origen;
    }
    
    @Override
    public String toString() {
        return "ErrorBean{" +
                "codigo='" + codigo + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", tipo='" + tipo + '\'' +
                ", origen='" + origen + '\'' +
                '}';
    }
}