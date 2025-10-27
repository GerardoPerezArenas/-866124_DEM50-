
package es.altia.flexia.integracion.moduloexterno.melanbide11.bean;

import java.math.BigDecimal;

/**
 * Bean para representar otras percepciones extrasalariales.
 * Compatible con Java 1.6
 */
public class OtraPercepcion {
    
    private String concepto;
    private BigDecimal importe;
    
    public OtraPercepcion() {
        this.importe = BigDecimal.ZERO;
    }
    
    public OtraPercepcion(String concepto, BigDecimal importe) {
        this.concepto = concepto;
        this.importe = (importe != null) ? importe : BigDecimal.ZERO;
    }
    
    public String getConcepto() {
        return concepto;
    }
    
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
    
    public BigDecimal getImporte() {
        return importe;
    }
    
    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }
    
    public String toString() {
        return "OtraPercepcion{" +
               "concepto='" + concepto + '\'' +
               ", importe=" + importe +
               '}';
    }
}
