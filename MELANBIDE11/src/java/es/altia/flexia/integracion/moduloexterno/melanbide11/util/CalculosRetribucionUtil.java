
package es.altia.flexia.integracion.moduloexterno.melanbide11.util;

import es.altia.flexia.integracion.moduloexterno.melanbide11.bean.ComplementoSalarial;
import es.altia.flexia.integracion.moduloexterno.melanbide11.bean.OtraPercepcion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidades para cálculos de retribución.
 * Implementa los cálculos de RSBCOMPCONV, CSTCONT e IMPSUBVCONT.
 * Compatible con Java 1.6
 */
public class CalculosRetribucionUtil {
    
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    /**
     * Calcula la Retribución Salarial Bruta Computable del Convenio (RSBCOMPCONV).
     * RSBCOMPCONV = salario base + pagas extra + complementos FIJOS
     * 
     * @param salarioBase Salario base anual
     * @param pagasExtra Pagas extraordinarias anuales
     * @param complementos Lista de complementos salariales
     * @return RSBCOMPCONV calculado
     */
    public static BigDecimal calcularRetribucionComputable(
            BigDecimal salarioBase, 
            BigDecimal pagasExtra, 
            List<ComplementoSalarial> complementos) {
        
        BigDecimal resultado = BigDecimal.ZERO;
        
        // Sumar salario base
        if (salarioBase != null) {
            resultado = resultado.add(salarioBase);
        }
        
        // Sumar pagas extra
        if (pagasExtra != null) {
            resultado = resultado.add(pagasExtra);
        }
        
        // Sumar solo complementos FIJOS
        if (complementos != null) {
            for (int i = 0; i < complementos.size(); i++) {
                ComplementoSalarial comp = complementos.get(i);
                if (comp.esFijo() && comp.getImporte() != null) {
                    resultado = resultado.add(comp.getImporte());
                }
            }
        }
        
        return resultado.setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * Calcula el Coste del Contrato Total (CSTCONT).
     * CSTCONT = salario base + pagas extra + TODOS los complementos salariales + percepciones extrasalariales
     * 
     * @param salarioBase Salario base anual
     * @param pagasExtra Pagas extraordinarias anuales
     * @param complementos Lista de complementos salariales (fijos + variables)
     * @param otrasPercepciones Lista de percepciones extrasalariales
     * @return CSTCONT calculado
     */
    public static BigDecimal calcularRetribucionBrutaTotal(
            BigDecimal salarioBase,
            BigDecimal pagasExtra,
            List<ComplementoSalarial> complementos,
            List<OtraPercepcion> otrasPercepciones) {
        
        BigDecimal resultado = BigDecimal.ZERO;
        
        // Sumar salario base
        if (salarioBase != null) {
            resultado = resultado.add(salarioBase);
        }
        
        // Sumar pagas extra
        if (pagasExtra != null) {
            resultado = resultado.add(pagasExtra);
        }
        
        // Sumar TODOS los complementos salariales (fijos y variables)
        if (complementos != null) {
            for (int i = 0; i < complementos.size(); i++) {
                ComplementoSalarial comp = complementos.get(i);
                if (comp.getImporte() != null) {
                    resultado = resultado.add(comp.getImporte());
                }
            }
        }
        
        // Sumar percepciones extrasalariales
        if (otrasPercepciones != null) {
            for (int i = 0; i < otrasPercepciones.size(); i++) {
                OtraPercepcion percep = otrasPercepciones.get(i);
                if (percep.getImporte() != null) {
                    resultado = resultado.add(percep.getImporte());
                }
            }
        }
        
        return resultado.setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * Clase interna para retornar sumas de complementos.
     * Compatible con Java 1.6
     */
    public static class SumasComplementos {
        private BigDecimal sumaFijos;
        private BigDecimal sumaVariables;
        private BigDecimal sumaExtrasalariales;
        
        public SumasComplementos() {
            this.sumaFijos = BigDecimal.ZERO;
            this.sumaVariables = BigDecimal.ZERO;
            this.sumaExtrasalariales = BigDecimal.ZERO;
        }
        
        public BigDecimal getSumaFijos() {
            return sumaFijos;
        }
        
        public void setSumaFijos(BigDecimal sumaFijos) {
            this.sumaFijos = sumaFijos;
        }
        
        public BigDecimal getSumaVariables() {
            return sumaVariables;
        }
        
        public void setSumaVariables(BigDecimal sumaVariables) {
            this.sumaVariables = sumaVariables;
        }
        
        public BigDecimal getSumaExtrasalariales() {
            return sumaExtrasalariales;
        }
        
        public void setSumaExtrasalariales(BigDecimal sumaExtrasalariales) {
            this.sumaExtrasalariales = sumaExtrasalariales;
        }
    }
    
    /**
     * Calcula las sumas de complementos salariales por tipo.
     * 
     * @param complementos Lista de complementos salariales
     * @param otrasPercepciones Lista de percepciones extrasalariales
     * @return Objeto con las sumas por tipo
     */
    public static SumasComplementos calcularComplementoSalarial(
            List<ComplementoSalarial> complementos,
            List<OtraPercepcion> otrasPercepciones) {
        
        SumasComplementos sumas = new SumasComplementos();
        
        BigDecimal sumaFijos = BigDecimal.ZERO;
        BigDecimal sumaVariables = BigDecimal.ZERO;
        BigDecimal sumaExtrasalariales = BigDecimal.ZERO;
        
        // Procesar complementos salariales
        if (complementos != null) {
            for (int i = 0; i < complementos.size(); i++) {
                ComplementoSalarial comp = complementos.get(i);
                if (comp.getImporte() != null) {
                    if (comp.esFijo()) {
                        sumaFijos = sumaFijos.add(comp.getImporte());
                    } else if (comp.esVariable()) {
                        sumaVariables = sumaVariables.add(comp.getImporte());
                    }
                }
            }
        }
        
        // Procesar percepciones extrasalariales
        if (otrasPercepciones != null) {
            for (int i = 0; i < otrasPercepciones.size(); i++) {
                OtraPercepcion percep = otrasPercepciones.get(i);
                if (percep.getImporte() != null) {
                    sumaExtrasalariales = sumaExtrasalariales.add(percep.getImporte());
                }
            }
        }
        
        sumas.setSumaFijos(sumaFijos.setScale(SCALE, ROUNDING_MODE));
        sumas.setSumaVariables(sumaVariables.setScale(SCALE, ROUNDING_MODE));
        sumas.setSumaExtrasalariales(sumaExtrasalariales.setScale(SCALE, ROUNDING_MODE));
        
        return sumas;
    }
    
    /**
     * Calcula el Importe de Subvención (IMPSUBVCONT) según las reglas de negocio.
     * 
     * Reglas:
     * - Base: según tipo de contrato (temporal ≥6m vs indefinido)
     * - Incremento +15% si mujer o ≥55 años
     * - Incremento +10% si formación finalizada en 2022/2023/2024 con compromiso
     * - Incremento +20% si ambas condiciones se cumplen
     * 
     * @param importeBase Importe base de la subvención según tipo de contrato
     * @param esMujer true si es mujer
     * @param esMayor55 true si tiene 55 años o más
     * @param tieneFormacionReciente true si finalizó formación en 2022/2023/2024 con compromiso
     * @return IMPSUBVCONT calculado
     */
    public static BigDecimal calcularImporteSubvencion(
            BigDecimal importeBase,
            boolean esMujer,
            boolean esMayor55,
            boolean tieneFormacionReciente) {
        
        if (importeBase == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal resultado = importeBase;
        
        // Verificar condiciones de incremento
        boolean condicion1 = esMujer || esMayor55;
        boolean condicion2 = tieneFormacionReciente;
        
        if (condicion1 && condicion2) {
            // +20% si ambas condiciones
            BigDecimal incremento = importeBase.multiply(new BigDecimal("0.20"));
            resultado = resultado.add(incremento);
        } else if (condicion1) {
            // +15% si solo condición 1
            BigDecimal incremento = importeBase.multiply(new BigDecimal("0.15"));
            resultado = resultado.add(incremento);
        } else if (condicion2) {
            // +10% si solo condición 2
            BigDecimal incremento = importeBase.multiply(new BigDecimal("0.10"));
            resultado = resultado.add(incremento);
        }
        
        return resultado.setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * Obtiene el importe base de subvención según el tipo de contrato.
     * Placeholder para futura implementación con DAO.
     * 
     * @param tipoContrato Tipo de contrato
     * @param duracionMeses Duración del contrato en meses
     * @return Importe base según tabla de referencia
     */
    public static BigDecimal obtenerImporteBaseSubvencion(String tipoContrato, int duracionMeses) {
        // TODO: Implementar lectura desde MELANBIDE11_SUBVENCION_REF cuando esté disponible el DAO
        // Por ahora, valores de ejemplo hardcodeados
        
        if (tipoContrato == null) {
            return BigDecimal.ZERO;
        }
        
        // Valores temporales de ejemplo (deberían venir de BD)
        if (tipoContrato.toUpperCase().contains("INDEFINIDO")) {
            return new BigDecimal("5000.00");
        } else if (duracionMeses >= 6) {
            return new BigDecimal("3000.00");
        }
        
        return BigDecimal.ZERO;
    }
}
