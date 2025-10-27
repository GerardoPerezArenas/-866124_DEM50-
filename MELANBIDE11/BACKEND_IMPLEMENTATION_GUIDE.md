# Backend Implementation Guide for Desglose RSB

## Overview
This document describes the backend operations that need to be implemented to support the Desglose RSB (retribution breakdown) functionality.

## Required Operations in MELANBIDE11.java

The JSP files make AJAX calls to the following operations via `PeticionModuloIntegracion.do`:
- `modulo=MELANBIDE11`
- `operacion=<operation_name>`

### 1. guardarDesgloseRSB (Tab1 - Summary)

**Purpose**: Save summary data from m11Desglose_Tab1.jsp

**Parameters**:
- `idRegistro`: ID of the contratación record
- `rsbSalBase`: Salario base
- `rsbPagasExtra`: Pagas extraordinarias  
- `rsbCompImporte`: Complementos salariales (fijos/variables)
- `rsbCompExtra`: Percepciones extrasalariales

**Response Format** (JSON):
```json
{
  "resultado": {
    "codigoOperacion": "0",  // "0"=success, "1"=DB error, "3"=params error, "4"=generic error
    "mensajeOperacion": "mensaje"
  }
}
```

**Database Operations**:
- Update MELANBIDE11_CONTRATACION table
- Calculate RSBCOMPCONV using CalculosRetribucionUtil.calcularRetribucionComputable()
- Calculate CSTCONT using CalculosRetribucionUtil.calcularRetribucionBrutaTotal()
- Store calculated values in the contratación record

### 2. listarLineasDesgloseRSB (Tab2 - Detail)

**Purpose**: List detail lines for complementos and percepciones from m11Desglose_Tab2.jsp

**Parameters**:
- `numExp`: Número de expediente
- `id`: ID of the contratación record (optional)

**Response Format** (JSON):
```json
{
  "dni": "12345678A",
  "lineas": [
    {
      "tipo": "1",  // "1"=complemento salarial, "2"=extrasalarial
      "importe": 1000.50,
      "concepto": "F",  // "F"=Fijo, "V"=Variable
      "observ": "Observaciones"
    }
  ]
}
```

**Database Operations**:
- Query MELANBIDE11_DESGRSB table
- Join with MELANBIDE11_CONTRATACION to get DNI
- Return lines grouped by tipo

### 3. guardarLineasDesgloseRSB (Tab2 - Detail)

**Purpose**: Save detail lines from m11Desglose_Tab2.jsp

**Parameters**:
- `numExp`: Número de expediente
- `dni`: DNI of the contracted person
- `lineas`: Raw string with format: "tipo|importe|concepto|observ;;tipo|importe|concepto|observ;;..."

**Response Format** (JSON):
```json
{
  "resultado": {
    "codigoOperacion": "0",
    "mensajeOperacion": "mensaje"
  }
}
```

**Database Operations**:
- Parse lineas using DesgloseRSBParser.parseComplementos()
- Delete existing lines for this DNI in MELANBIDE11_DESGRSB
- Insert new lines
- Recalculate RSBCOMPCONV and CSTCONT for the contratación

### 4. eliminarContratacionAJAX (Optional Enhancement)

**Purpose**: Delete a contratación record via AJAX (called from Tab1)

**Parameters**:
- `id`: ID of the contratación record
- `numExp`: Número de expediente

**Response Format** (JSON):
```json
{
  "resultado": {
    "codigoOperacion": "0",
    "mensajeOperacion": "mensaje"
  }
}
```

## Database Schema

### MELANBIDE11_CONTRATACION
```sql
-- Existing fields (partial list)
ID              NUMBER
NUMEXP          VARCHAR2(50)
DNICONT         VARCHAR2(15)
CSTCONT         NUMBER(8,2)  -- Coste contrato (calculated)
IMPSUBVCONT     NUMBER(8,2)  -- Importe subvención (calculated)

-- New calculated fields (may need to be added)
RSBCOMPCONV     NUMBER(8,2)  -- Retribución computable (FIJOS only)
RSBSALBASE      NUMBER(8,2)  -- Salario base
RSBPAGASEXTRA   NUMBER(8,2)  -- Pagas extra
RSBCOMPIMPORTE  NUMBER(8,2)  -- Complementos salariales
RSBCOMPEXTRA    NUMBER(8,2)  -- Percepciones extrasalariales
```

### MELANBIDE11_DESGRSB (New table - needs to be created)
```sql
CREATE TABLE MELANBIDE11_DESGRSB (
  ID            NUMBER PRIMARY KEY,
  NUMEXP        VARCHAR2(50) NOT NULL,
  DNICONTRSB    VARCHAR2(15) NOT NULL,
  RSBTIPO       VARCHAR2(1) NOT NULL,     -- "1"=salarial, "2"=extrasalarial
  RSBIMPORTE    NUMBER(8,2) NOT NULL,
  RSBCONCEPTO   VARCHAR2(10),             -- "F"=Fijo, "V"=Variable
  RSBOBSERV     VARCHAR2(500),
  CONSTRAINT FK_DESGRSB_NUMEXP FOREIGN KEY (NUMEXP) REFERENCES MELANBIDE11_CONTRATACION(NUMEXP)
);

CREATE SEQUENCE SEQ_MELANBIDE11_DESGRSB START WITH 1 INCREMENT BY 1;
```

## Java 6 Compatibility Notes

All backend code must maintain Java 6 compatibility:
- No diamond operator `<>`
- No try-with-resources
- No `@Override` on interface method implementations  
- Use traditional for loops with List
- Use `BigDecimal.setScale(2, RoundingMode.HALF_UP)`

## Calculation Logic

### RSBCOMPCONV (Retribución Computable)
```java
RSBCOMPCONV = salarioBase + pagasExtra + complementosFijos
```
Use: `CalculosRetribucionUtil.calcularRetribucionComputable()`

### CSTCONT (Coste Total del Contrato)
```java
CSTCONT = salarioBase + pagasExtra + complementosSalariales + percepcionesExtrasalariales
```
Use: `CalculosRetribucionUtil.calcularRetribucionBrutaTotal()`

### IMPSUBVCONT (Importe Subvención)
```java
// Base según tipo de contrato
base = obtenerImporteBaseSubvencion(tipoContrato, duracionMeses)

// Aplicar incrementos
if (esMujer || esMayor55) && tieneFormacionReciente:
    IMPSUBVCONT = base * 1.20
else if (esMujer || esMayor55):
    IMPSUBVCONT = base * 1.15
else if tieneFormacionReciente:
    IMPSUBVCONT = base * 1.10
else:
    IMPSUBVCONT = base
```
Use: `CalculosRetribucionUtil.calcularImporteSubvencion()`

## Implementation Pattern

Follow the existing pattern in MELANBIDE11.java:

```java
public void operationName(int codOrganizacion, int codTramite, 
                         int ocurrenciaTramite, String numExpediente,
                         HttpServletRequest request, HttpServletResponse response) {
    AdaptadorSQLBD adapt = null;
    PrintWriter out = null;
    
    try {
        adapt = this.getAdaptSQLBD(String.valueOf(codOrganizacion));
        out = response.getWriter();
        
        // Get parameters
        String param1 = request.getParameter("param1");
        
        // Validate parameters
        if (param1 == null || param1.isEmpty()) {
            out.print("{\"resultado\":{\"codigoOperacion\":\"3\"}}");
            return;
        }
        
        // Perform database operations
        Connection con = adapt.getConnection();
        MeLanbide11DAO dao = MeLanbide11DAO.getInstance();
        
        // ... business logic ...
        
        // Return success
        out.print("{\"resultado\":{\"codigoOperacion\":\"0\"}}");
        
    } catch (Exception ex) {
        log.error("Error in operationName", ex);
        if (out != null) {
            out.print("{\"resultado\":{\"codigoOperacion\":\"1\"}}");
        }
    }
}
```

## Next Steps

1. Create MELANBIDE11_DESGRSB table in database
2. Add DAO methods to MeLanbide11DAO.java for CRUD operations on MELANBIDE11_DESGRSB
3. Implement the three main operations in MELANBIDE11.java
4. Test the complete flow from JSP to database and back
5. Verify calculations are correct
6. Add any missing validation logic

## Testing Checklist

- [ ] Tab1 loads with empty form
- [ ] Tab1 saves data correctly
- [ ] Tab1 calculates RSBCOMPCONV correctly
- [ ] Tab1 calculates CSTCONT correctly
- [ ] Tab2 loads empty initially
- [ ] Tab2 allows adding complemento salarial (tipo 1)
- [ ] Tab2 allows adding percepción extrasalarial (tipo 2)
- [ ] Tab2 allows modifying existing lines
- [ ] Tab2 allows deleting lines
- [ ] Tab2 saves all lines correctly
- [ ] Closing modal passes result back to parent
- [ ] IMPSUBVCONT calculates correctly based on rules
- [ ] All validations work (numeric, >= 0, concept types)
- [ ] i18n works in both Spanish and Euskera
- [ ] CSS styles display correctly
