package es.altia.flexia.integracion.moduloexterno.melanbide11.vo;

import java.math.BigDecimal;

/**
 * Value Object para representar una línea de desglose de retribución salarial
 * 
 * TIPO puede ser:
 * - 1: Complementos salariales (FIJO o VARIABLE según concepto)
 * - 2: Otras percepciones extrasalariales
 * 
 * CONCEPTO puede ser:
 * - 'F': Complemento FIJO (se incluye en retribución computable)
 * - 'V': Complemento VARIABLE (NO se incluye en retribución computable)
 */
public class DesgloseRetribucionVO {
    
    private Integer id;
    private String numExp;
    private String dni;
    private Integer tipo;          // 1=Complementos salariales, 2=Otras percepciones
    private BigDecimal importe;
    private String concepto;       // 'F'=Fijo, 'V'=Variable
    private String observaciones;
    
    public DesgloseRetribucionVO() {
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
    
    public String getDni() {
        return dni;
    }
    
    public void setDni(String dni) {
        this.dni = dni;
    }
    
    public Integer getTipo() {
        return tipo;
    }
    
    public void setTipo(Integer tipo) {
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
    
    @Override
    public String toString() {
        return "DesgloseRetribucionVO{" +
                "id=" + id +
                ", numExp='" + numExp + '\'' +
                ", dni='" + dni + '\'' +
                ", tipo=" + tipo +
                ", importe=" + importe +
                ", concepto='" + concepto + '\'' +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
