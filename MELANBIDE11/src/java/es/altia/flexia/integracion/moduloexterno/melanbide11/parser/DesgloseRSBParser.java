
package es.altia.flexia.integracion.moduloexterno.melanbide11.parser;

import es.altia.flexia.integracion.moduloexterno.melanbide11.bean.ComplementoSalarial;
import es.altia.flexia.integracion.moduloexterno.melanbide11.bean.OtraPercepcion;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidades de parsing para desglose RSB.
 * Compatible con Java 1.6
 */
public class DesgloseRSBParser {
    
    /**
     * Parsea un String a BigDecimal, manejando comas y puntos.
     * @param valor String con el valor a parsear
     * @return BigDecimal parseado o BigDecimal.ZERO si hay error
     */
    public static BigDecimal parseImporte(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        try {
            // Reemplazar coma por punto para parsear
            String valorNormalizado = valor.trim().replace(',', '.');
            // Eliminar espacios en blanco
            valorNormalizado = valorNormalizado.replaceAll("\\s", "");
            return new BigDecimal(valorNormalizado);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Formatea un BigDecimal como String con coma decimal.
     * @param importe BigDecimal a formatear
     * @return String formateado con dos decimales y coma
     */
    public static String formatearImporte(BigDecimal importe) {
        if (importe == null) {
            return "0,00";
        }
        String valor = importe.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        return valor.replace('.', ',');
    }
    
    /**
     * Valida que un importe sea no negativo.
     * @param importe BigDecimal a validar
     * @return true si es válido (>= 0), false en caso contrario
     */
    public static boolean esImporteValido(BigDecimal importe) {
        return importe != null && importe.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    /**
     * Valida que un concepto sea válido (FIJO o VARIABLE).
     * @param concepto String con el concepto
     * @return true si es válido, false en caso contrario
     */
    public static boolean esConceptoValido(String concepto) {
        if (concepto == null || concepto.trim().isEmpty()) {
            return false;
        }
        String conceptoUpper = concepto.trim().toUpperCase();
        return conceptoUpper.equals("FIJO") || 
               conceptoUpper.equals("VARIABLE") ||
               conceptoUpper.equals("FINKOA") ||
               conceptoUpper.equals("F") ||
               conceptoUpper.equals("V");
    }
    
    /**
     * Parsea una lista de complementos salariales desde un String raw.
     * Formato esperado: "tipo|importe|concepto|observ;;tipo|importe|concepto|observ;;..."
     * Compatible con Java 1.6 (sin try-with-resources, sin diamond operator)
     * @param raw String con datos raw
     * @return Lista de ComplementoSalarial
     */
    public static List<ComplementoSalarial> parseComplementos(String raw) {
        List<ComplementoSalarial> complementos = new ArrayList<ComplementoSalarial>();
        
        if (raw == null || raw.trim().isEmpty()) {
            return complementos;
        }
        
        String[] lineas = raw.split(";;");
        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i];
            if (linea == null || linea.trim().isEmpty()) {
                continue;
            }
            
            String[] partes = linea.split("\\|");
            if (partes.length >= 3) {
                String tipo = (partes.length > 0) ? partes[0] : "";
                BigDecimal importe = (partes.length > 1) ? parseImporte(partes[1]) : BigDecimal.ZERO;
                String concepto = (partes.length > 2) ? partes[2] : "";
                String observ = (partes.length > 3) ? partes[3] : "";
                
                ComplementoSalarial comp = new ComplementoSalarial(tipo, importe, concepto, observ);
                complementos.add(comp);
            }
        }
        
        return complementos;
    }
    
    /**
     * Parsea una lista de otras percepciones desde un String raw.
     * Formato esperado: "concepto|importe;;concepto|importe;;..."
     * Compatible con Java 1.6
     * @param raw String con datos raw
     * @return Lista de OtraPercepcion
     */
    public static List<OtraPercepcion> parseOtrasPercepciones(String raw) {
        List<OtraPercepcion> percepciones = new ArrayList<OtraPercepcion>();
        
        if (raw == null || raw.trim().isEmpty()) {
            return percepciones;
        }
        
        String[] lineas = raw.split(";;");
        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i];
            if (linea == null || linea.trim().isEmpty()) {
                continue;
            }
            
            String[] partes = linea.split("\\|");
            if (partes.length >= 2) {
                String concepto = partes[0];
                BigDecimal importe = parseImporte(partes[1]);
                
                OtraPercepcion percepcion = new OtraPercepcion(concepto, importe);
                percepciones.add(percepcion);
            }
        }
        
        return percepciones;
    }
    
    /**
     * Serializa una lista de complementos salariales a String raw.
     * @param complementos Lista de ComplementoSalarial
     * @return String en formato raw
     */
    public static String serializarComplementos(List<ComplementoSalarial> complementos) {
        if (complementos == null || complementos.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < complementos.size(); i++) {
            ComplementoSalarial comp = complementos.get(i);
            if (i > 0) {
                sb.append(";;");
            }
            sb.append(sanitizarTexto(comp.getTipo()));
            sb.append("|");
            sb.append(comp.getImporte() != null ? comp.getImporte().toString() : "0");
            sb.append("|");
            sb.append(sanitizarTexto(comp.getConcepto()));
            sb.append("|");
            sb.append(sanitizarTexto(comp.getObservaciones()));
        }
        
        return sb.toString();
    }
    
    /**
     * Sanitiza un texto eliminando caracteres problemáticos.
     * @param texto Texto a sanitizar
     * @return Texto sanitizado
     */
    private static String sanitizarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        // Eliminar | y ;; que son delimitadores
        return texto.replaceAll("[|;]", " ").trim();
    }
}
