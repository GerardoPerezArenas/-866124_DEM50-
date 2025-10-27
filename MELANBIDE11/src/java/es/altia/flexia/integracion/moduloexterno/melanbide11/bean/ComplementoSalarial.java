
package es.altia.flexia.integracion.moduloexterno.melanbide11.bean;

import java.math.BigDecimal;

/**
 * Bean para representar un complemento salarial en el desglose de retribución.
 * Compatible con Java 1.6
 */
public class ComplementoSalarial {
    
    private String tipo;           // "FIJO", "VARIABLE", "FINKOA"
    private BigDecimal importe;
    private String concepto;
    private String observaciones;
    
    public ComplementoSalarial() {
        this.importe = BigDecimal.ZERO;
    }
    
    public ComplementoSalarial(String tipo, BigDecimal importe, String concepto, String observaciones) {
        this.tipo = tipo;
        this.importe = (importe != null) ? importe : BigDecimal.ZERO;
        this.concepto = concepto;
        this.observaciones = observaciones;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public BigDecimal getImporte() {
        return importe;
    }
    
    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }
    
    public String getConcepto() {
        return concepto;
    }
    
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    /**
     * Verifica si el complemento es de tipo fijo.
     * @return true si es fijo, false en caso contrario
     */
    public boolean esFijo() {
        return tipo != null && 
               (tipo.equalsIgnoreCase("FIJO") || tipo.equalsIgnoreCase("FINKOA"));
    }
    
    /**
     * Verifica si el complemento es de tipo variable.
     * @return true si es variable, false en caso contrario
     */
    public boolean esVariable() {
        return tipo != null && tipo.equalsIgnoreCase("VARIABLE");
    }
    
    public String toString() {
        return "ComplementoSalarial{" +
               "tipo='" + tipo + '\'' +
               ", importe=" + importe +
               ", concepto='" + concepto + '\'' +
               ", observaciones='" + observaciones + '\'' +
               '}';
    }
}
