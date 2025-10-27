
package es.altia.flexia.integracion.moduloexterno.melanbide11.vo;

import java.math.BigDecimal;

/**
 * Value Object para MELANBIDE11_DESGRSB
 * Representa una línea de desglose de retribución salarial bruta.
 * Compatible con Java 1.6
 */
public class DesgloseRSBVO {
    
    private Integer id;
    private String numExp;
    private String dniContrsb;
    private String rsbTipo;        // "1" = complementos salariales, "2" = extrasalariales
    private BigDecimal rsbImporte;
    private String rsbConcepto;    // "FIJO", "VARIABLE", etc.
    private String rsbObserv;
    
    public DesgloseRSBVO() {
        this.rsbImporte = BigDecimal.ZERO;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNumExp() {
        return numExp;
    }
    
    public void setNumExp(String numExp) {
        this.numExp = numExp;
    }
    
    public String getDniContrsb() {
        return dniContrsb;
    }
    
    public void setDniContrsb(String dniContrsb) {
        this.dniContrsb = dniContrsb;
    }
    
    public String getRsbTipo() {
        return rsbTipo;
    }
    
    public void setRsbTipo(String rsbTipo) {
        this.rsbTipo = rsbTipo;
    }
    
    public BigDecimal getRsbImporte() {
        return rsbImporte;
    }
    
    public void setRsbImporte(BigDecimal rsbImporte) {
        this.rsbImporte = rsbImporte;
    }
    
    public String getRsbConcepto() {
        return rsbConcepto;
    }
    
    public void setRsbConcepto(String rsbConcepto) {
        this.rsbConcepto = rsbConcepto;
    }
    
    public String getRsbObserv() {
        return rsbObserv;
    }
    
    public void setRsbObserv(String rsbObserv) {
        this.rsbObserv = rsbObserv;
    }
    
    public String toString() {
        return "DesgloseRSBVO{" +
               "id=" + id +
               ", numExp='" + numExp + '\'' +
               ", dniContrsb='" + dniContrsb + '\'' +
               ", rsbTipo='" + rsbTipo + '\'' +
               ", rsbImporte=" + rsbImporte +
               ", rsbConcepto='" + rsbConcepto + '\'' +
               ", rsbObserv='" + rsbObserv + '\'' +
               '}';
    }
}
