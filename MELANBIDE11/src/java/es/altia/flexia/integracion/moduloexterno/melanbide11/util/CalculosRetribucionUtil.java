package es.altia.flexia.integracion.moduloexterno.melanbide11.util;

import es.altia.flexia.integracion.moduloexterno.melanbide11.vo.DesgloseRetribucionVO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Utilidad para cálculos de retribución en el módulo MELANBIDE11
 * 
 * Calcula la retribución computable para la convocatoria según la fórmula:
 * RETRIBUCION_COMPUTABLE = Salario base + Pagas extraordinarias + SUMA(complementos salariales tipo FIJO)
 * 
 * También calcula CSTCONT (retribución bruta total) que suma TODO (fijos + variables + otras percepciones)
 */
public class CalculosRetribucionUtil {
    
    private static Logger log = Logger.getLogger(CalculosRetribucionUtil.class);
    
    /**
     * Calcula la retribución computable para la convocatoria
     * Incluye solo salario base, pagas extraordinarias y complementos salariales FIJOS
     * 
     * @param salarioBase Salario base anual
     * @param pagasExtraordinarias Importe de pagas extraordinarias anuales
     * @param complementos Lista de complementos salariales (solo se suman los FIJOS)
     * @return Retribución computable calculada
     */
    public static BigDecimal calcularRetribucionComputable(
            BigDecimal salarioBase,
            BigDecimal pagasExtraordinarias,
            List<DesgloseRetribucionVO> complementos) {
        
        try {
            BigDecimal resultado = BigDecimal.ZERO;
            
            // Añadir salario base si no es nulo
            if (salarioBase != null) {
                resultado = resultado.add(salarioBase);
            }
            
            // Añadir pagas extraordinarias si no es nulo
            if (pagasExtraordinarias != null) {
                resultado = resultado.add(pagasExtraordinarias);
            }
            
            // Sumar solo complementos de tipo FIJO (tipo=1 o concepto='F')
            if (complementos != null) {
                for (DesgloseRetribucionVO complemento : complementos) {
                    if (complemento != null && esTipoFijo(complemento)) {
                        BigDecimal importe = complemento.getImporte();
                        if (importe != null) {
                            resultado = resultado.add(importe);
                        }
                    }
                }
            }
            
            // Redondear a 2 decimales
            return resultado.setScale(2, RoundingMode.HALF_UP);
            
        } catch (Exception e) {
            log.error("Error calculando retribución computable", e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Calcula el coste total del contrato (CSTCONT)
     * Incluye TODO: salario base, pagas extraordinarias, complementos fijos, complementos variables y otras percepciones
     * 
     * @param salarioBase Salario base anual
     * @param pagasExtraordinarias Importe de pagas extraordinarias anuales
     * @param complementosSalariales Lista de complementos salariales (tipo 1: fijos y variables)
     * @param otrasPercepciones Lista de otras percepciones extrasalariales (tipo 2)
     * @return Coste total del contrato
     */
    public static BigDecimal calcularCosteContratoTotal(
            BigDecimal salarioBase,
            BigDecimal pagasExtraordinarias,
            List<DesgloseRetribucionVO> complementosSalariales,
            List<DesgloseRetribucionVO> otrasPercepciones) {
        
        try {
            BigDecimal resultado = BigDecimal.ZERO;
            
            // Añadir salario base
            if (salarioBase != null) {
                resultado = resultado.add(salarioBase);
            }
            
            // Añadir pagas extraordinarias
            if (pagasExtraordinarias != null) {
                resultado = resultado.add(pagasExtraordinarias);
            }
            
            // Sumar TODOS los complementos salariales (fijos y variables)
            if (complementosSalariales != null) {
                for (DesgloseRetribucionVO complemento : complementosSalariales) {
                    if (complemento != null && complemento.getImporte() != null) {
                        resultado = resultado.add(complemento.getImporte());
                    }
                }
            }
            
            // Sumar TODAS las otras percepciones
            if (otrasPercepciones != null) {
                for (DesgloseRetribucionVO percepcion : otrasPercepciones) {
                    if (percepcion != null && percepcion.getImporte() != null) {
                        resultado = resultado.add(percepcion.getImporte());
                    }
                }
            }
            
            // Redondear a 2 decimales
            return resultado.setScale(2, RoundingMode.HALF_UP);
            
        } catch (Exception e) {
            log.error("Error calculando coste total del contrato", e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Determina si un complemento es de tipo FIJO
     * Un complemento es FIJO si su concepto es 'F' o 'f'
     * 
     * @param complemento El complemento a evaluar
     * @return true si es de tipo FIJO, false en caso contrario
     */
    private static boolean esTipoFijo(DesgloseRetribucionVO complemento) {
        if (complemento == null) {
            return false;
        }
        
        String concepto = complemento.getConcepto();
        if (concepto == null) {
            return false;
        }
        
        // Un complemento es FIJO si concepto es 'F' o 'f'
        return "F".equalsIgnoreCase(concepto.trim());
    }
    
    /**
     * Convierte un Double a BigDecimal de forma segura
     * 
     * @param valor El valor Double a convertir
     * @return BigDecimal equivalente, o ZERO si el valor es nulo
     */
    public static BigDecimal toBigDecimal(Double valor) {
        if (valor == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(valor);
    }
    
    /**
     * Convierte un BigDecimal a Double de forma segura
     * 
     * @param valor El valor BigDecimal a convertir
     * @return Double equivalente, o null si el valor es nulo
     */
    public static Double toDouble(BigDecimal valor) {
        if (valor == null) {
            return null;
        }
        return valor.doubleValue();
    }
}
