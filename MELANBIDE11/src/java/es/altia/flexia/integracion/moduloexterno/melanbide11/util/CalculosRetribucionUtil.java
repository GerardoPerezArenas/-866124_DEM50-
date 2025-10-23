package es.altia.flexia.integracion.moduloexterno.melanbide11.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import es.altia.flexia.integracion.moduloexterno.melanbide11.bean.ComplementoSalarial;
import es.altia.flexia.integracion.moduloexterno.melanbide11.bean.OtraPercepcion;

/**
 * Utilidad para cálculos de retribuciones en MELANBIDE11
 * CRÍTICO: La retribución computable solo suma complementos FIJOS
 */
public final class CalculosRetribucionUtil {

    private static final String TIPO_FIJO = "FIJO";
    private static final String TIPO_FINKOA = "FINKOA"; // euskera

    private CalculosRetribucionUtil() {}

    public static BigDecimal calcularRetribucionComputable(
            BigDecimal salarioBase,
            BigDecimal pagasExtraordinarias,
            List<ComplementoSalarial> complementos) {

        if (salarioBase == null) salarioBase = BigDecimal.ZERO;
        if (pagasExtraordinarias == null) pagasExtraordinarias = BigDecimal.ZERO;

        BigDecimal total = salarioBase.add(pagasExtraordinarias);

        if (complementos != null) {
            for (ComplementoSalarial c : complementos) {
                if (c == null) continue;
                String tipo = c.getTipo();
                if (esComplementoFijo(tipo)) {
                    BigDecimal imp = c.getImporte();
                    if (imp != null) total = total.add(imp);
                }
            }
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularRetribucionBrutaTotal(
            BigDecimal salarioBase,
            BigDecimal pagasExtraordinarias,
            List<ComplementoSalarial> complementos,
            List<OtraPercepcion> otrasPercepciones) {

        if (salarioBase == null) salarioBase = BigDecimal.ZERO;
        if (pagasExtraordinarias == null) pagasExtraordinarias = BigDecimal.ZERO;

        BigDecimal total = salarioBase.add(pagasExtraordinarias);

        if (complementos != null) {
            for (ComplementoSalarial c : complementos) {
                if (c == null) continue;
                BigDecimal imp = c.getImporte();
                if (imp != null) total = total.add(imp);
            }
        }

        if (otrasPercepciones != null) {
            for (OtraPercepcion p : otrasPercepciones) {
                if (p == null) continue;
                BigDecimal imp = p.getImporte();
                if (imp != null) total = total.add(imp);
            }
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private static boolean esComplementoFijo(String tipo) {
        if (tipo == null) return false;
        tipo = tipo.trim().toUpperCase();
        return TIPO_FIJO.equals(tipo) || TIPO_FINKOA.equals(tipo);
    }
}
