package com.lanbide.lan6.registro.error;

import com.lanbide.lan6.errores.bean.ErrorBean;

/**
 * Stub implementation of RegistroErrores for compilation compatibility.
 * This is a minimal implementation for Java 6 compatibility.
 */
public class RegistroErrores {
    
    public static void grabarError(ErrorBean error, String excepError, String traza, String numExp) {
        System.out.println("[REGISTRO_ERRORES] Grabando error: " + error.getCodigo() + " - " + error.getMensaje());
        System.out.println("[REGISTRO_ERRORES] Excepcion: " + excepError);
        System.out.println("[REGISTRO_ERRORES] Traza: " + traza);
        System.out.println("[REGISTRO_ERRORES] Numero expediente: " + numExp);
    }
    
    public static void grabarError(ErrorBean error) {
        grabarError(error, null, null, null);
    }
    
    public static void grabarError(String codigo, String mensaje) {
        ErrorBean error = new ErrorBean(codigo, mensaje);
        grabarError(error);
    }
}