# Cambios Implementados: Cálculo de Retribución Computable MELANBIDE11

## Objetivo
Implementar el cálculo de "Retribución salarial bruta computable para la convocatoria" en el módulo MELANBIDE11, aplicando la fórmula:

**RETRIBUCION_COMPUTABLE = Salario base + Pagas extraordinarias + SUMA(complementos salariales tipo FIJO)**

Los complementos marcados como VARIABLE no deben sumarse para este campo.

## Resumen de Cambios

### 1. Nueva Infraestructura de Código

#### 1.1 Clase de Utilidad para Cálculos
**Archivo:** `src/java/es/altia/flexia/integracion/moduloexterno/melanbide11/util/CalculosRetribucionUtil.java`

Métodos implementados:
- `calcularRetribucionComputable()`: Calcula la retribución computable sumando solo base + pagas extra + complementos FIJOS
- `calcularCosteContratoTotal()`: Calcula el coste total del contrato (CSTCONT) sumando TODO (fijos + variables + otras percepciones)
- `esTipoFijo()`: Método privado para determinar si un complemento es FIJO (concepto='F')
- `toBigDecimal()` / `toDouble()`: Métodos de conversión segura entre tipos

Características:
- Usa BigDecimal para precisión en cálculos monetarios
- Redondeo a 2 decimales con RoundingMode.HALF_UP
- Manejo robusto de valores nulos
- Logging de errores

#### 1.2 Value Objects (VO)

**Archivo:** `src/java/es/altia/flexia/integracion/moduloexterno/melanbide11/vo/DesgloseRetribucionVO.java`

Nuevo VO para representar líneas de desglose de retribución:
- `id`: Identificador único (secuencial)
- `numExp`: Número de expediente
- `dni`: DNI/NIE de la persona contratada
- `tipo`: 1=Complementos salariales, 2=Otras percepciones extrasalariales
- `importe`: Importe del concepto (BigDecimal)
- `concepto`: 'F'=Fijo, 'V'=Variable
- `observaciones`: Texto libre hasta 500 caracteres

**Modificaciones en:** `src/java/es/altia/flexia/integracion/moduloexterno/melanbide11/vo/ContratacionVO.java`

Campos añadidos:
- `salarioBase`: Salario base anual (Double)
- `pagasExtraordinarias`: Pagas extraordinarias anuales (Double)
- `retribucionComputable`: Retribución computable calculada (Double)

Con sus respectivos getters y setters.

### 2. Capa de Acceso a Datos (DAO)

**Archivo:** `src/java/es/altia/flexia/integracion/moduloexterno/melanbide11/dao/MeLanbide11DAO.java`

Métodos añadidos:
- `getLineasDesgloseRSB()`: Recupera todas las líneas de desglose para un expediente y DNI
- `eliminarLineasDesgloseRSB()`: Elimina todas las líneas existentes (para reemplazo transaccional)
- `insertarLineaDesgloseRSB()`: Inserta una nueva línea de desglose

**Archivo:** `src/java/es/altia/flexia/integracion/moduloexterno/melanbide11/util/MeLanbide11MappingUtils.java`

Métodos añadidos:
- `mapDesgloseRetribucion()`: Mapea ResultSet a DesgloseRetribucionVO
- `mapearDesgloseRetribucionVO()`: Implementación del mapeo con manejo de tipos y nulos

### 3. Capa de Lógica de Negocio (Manager)

**Archivo:** `src/java/es/altia/flexia/integracion/moduloexterno/melanbide11/manager/MeLanbide11Manager.java`

Métodos añadidos:
- `getLineasDesgloseRSB()`: Obtiene líneas con gestión de conexión
- `guardarLineasDesgloseRSB()`: Guarda líneas con manejo transaccional (elimina e inserta)
- `calcularRetribucionComputable()`: Calcula retribución computable filtrando solo complementos FIJOS
- `calcularCosteContratoTotal()`: Calcula coste total sumando TODOS los conceptos

Características:
- Gestión automática de conexiones BBDD
- Transacciones con commit/rollback
- Logging exhaustivo de errores
- Devolución correcta de conexiones al pool

### 4. Capa de Presentación (Controller)

**Archivo:** `src/java/es/altia/flexia/integracion/moduloexterno/melanbide11/MELANBIDE11.java`

Endpoints AJAX añadidos:

#### `listarLineasDesgloseRSB()`
- Recupera líneas de desglose para el expediente y DNI de la contratación
- Devuelve JSON: `{"dni": "...", "lineas": [...]}`
- Cada línea incluye: tipo, importe, concepto, observaciones

#### `guardarLineasDesgloseRSB()`
- Recibe líneas en formato custom: `tipo|importe|concepto|observ;;tipo|importe|concepto|observ...`
- Parsea y guarda en base de datos
- Devuelve JSON: `{"resultado": {"codigoOperacion": "0"}}` (0=éxito, 1=error, 3=parámetros insuficientes)

#### `escapeJson()`
- Método auxiliar para escapar caracteres especiales en JSON
- Manejo de comillas, saltos de línea, tabuladores, etc.

**Nota:** JSON construido manualmente sin dependencias externas para evitar conflictos de versiones.

### 5. Base de Datos

**Archivo:** `scriptBBDD/melanbide11_Desglose_RSB.sql`

Nueva tabla: `MELANBIDE11_DESGLOSE_RSB`
```sql
CREATE TABLE MELANBIDE11_DESGLOSE_RSB (
    ID NUMBER(10,0) NOT NULL,
    NUM_EXP VARCHAR2(30 BYTE) NOT NULL,
    DNI VARCHAR2(15 BYTE) NOT NULL,
    TIPO NUMBER(1,0) NOT NULL,
    IMPORTE NUMBER(10,2) NOT NULL,
    CONCEPTO VARCHAR2(1 BYTE),
    OBSERVACIONES VARCHAR2(500 BYTE)
);
```

Secuencia: `SEQ_MELANBIDE11_DESGLOSE_RSB`

Scripts comentados para ampliar MELANBIDE11_CONTRATACION:
```sql
-- ALTER TABLE MELANBIDE11_CONTRATACION ADD SALARIO_BASE NUMBER(10,2);
-- ALTER TABLE MELANBIDE11_CONTRATACION ADD PAGAS_EXTRAORD NUMBER(10,2);
-- ALTER TABLE MELANBIDE11_CONTRATACION ADD RETRIB_COMPUTABLE NUMBER(10,2);
```

### 6. Internacionalización (i18n)

#### Español (`MELANBIDE11_TEXT_1.properties`)
```properties
tab2.desglose.titulo1=Complementos salariales
tab2.desglose.titulo2=Otras percepciones extrasalariales
tab2.desglose.col.importe=Importe
tab2.desglose.col.concepto=Concepto
tab2.desglose.col.observ=Observaciones
tab2.desglose.cargando=Cargando...
tab2.desglose.sinLineas=No hay líneas registradas
tab2.desglose.alert.seleccione=Debe seleccionar una contratación primero
tab2.desglose.concepto.fijo=Fijo
tab2.desglose.concepto.variable=Variable
```

#### Euskera (`MELANBIDE11_TEXT_4.properties`)
```properties
tab2.desglose.titulo1=Soldata-osagarriak
tab2.desglose.titulo2=Beste pertzepzio batzuk lanez kanpokoak
tab2.desglose.col.importe=Zenbatekoa
tab2.desglose.col.concepto=Kontzeptua
tab2.desglose.col.observ=Oharrak
tab2.desglose.cargando=Kargatzen...
tab2.desglose.sinLineas=Ez dago erregistratutako lerrorik
tab2.desglose.alert.seleccione=Lehenik kontratazio bat hautatu behar duzu
tab2.desglose.concepto.fijo=Finkoa
tab2.desglose.concepto.variable=Aldakorra
```

### 7. Interfaz de Usuario

**Archivo:** `src/web/jsp/extension/melanbide11/desglose/m11Desglose_Tab2.jsp`

La interfaz ya está completamente implementada con:

#### Características visuales:
- Dos tablas independientes: Complementos salariales y Otras percepciones
- Botones CRUD: Nuevo, Modificar, Eliminar
- Diálogo modal para edición de líneas
- Dropdown personalizado para seleccionar Fijo/Variable
- Estilos CSS responsive
- Eliminación de scrolls horizontales
- Estados de carga y mensajes vacíos

#### Funcionalidad JavaScript:
- `cargarDesgloseTabla()`: Carga inicial de datos vía AJAX
- `guardarLineasDesgloseRSB()`: Guarda con callback
- `refrescarTabla()`: Actualiza ambas tablas
- `construirFilaSimple()`: Formatea datos para tabla
- `abrirDialogoLinea()`: Muestra modal de edición
- `mostrarOpcionesConcepto()`: Dropdown personalizado
- `recalcularAnchoTabla()`: Ajusta dimensiones dinámicamente
- Funciones CRUD: `agregarLinea()`, `modificarLinea()`, `eliminarLinea()`

#### Cálculos en tiempo real:
- Los importes se formatean automáticamente (separador de miles, 2 decimales)
- Validación de importes antes de guardar
- Mensajes de error claros

## Flujo de Funcionamiento

### Carga de Datos
1. Usuario abre la pestaña "Desglose" en una contratación
2. JavaScript llama a `listarLineasDesgloseRSB` vía AJAX
3. Backend recupera DNI de la contratación
4. Backend consulta líneas de desglose desde BBDD
5. Frontend renderiza dos tablas (Complementos y Otras percepciones)

### Edición de Líneas
1. Usuario hace clic en "Nuevo" o "Modificar"
2. Se abre diálogo modal con campos: Importe, Concepto, Observaciones
3. Usuario selecciona concepto (Fijo/Variable) del dropdown
4. Usuario ingresa datos y hace clic en "Aceptar"
5. JavaScript actualiza cache local
6. Llama a `guardarLineasDesgloseRSB` vía AJAX
7. Backend parsea datos y ejecuta transacción (DELETE + INSERTs)
8. Frontend recarga tabla con datos actualizados

### Cálculo de Retribuciones
Backend (cuando sea necesario):
```java
// Obtener líneas de desglose
List<DesgloseRetribucionVO> lineas = 
    manager.getLineasDesgloseRSB(numExp, dni, adapt);

// Calcular retribución computable (solo FIJOS)
BigDecimal retribComputable = 
    manager.calcularRetribucionComputable(salarioBase, pagasExtra, lineas);

// Calcular coste total (TODO)
BigDecimal costeTotal = 
    manager.calcularCosteContratoTotal(salarioBase, pagasExtra, lineas);
```

## Integración con Sistema Existente

### Puntos de Integración
1. **Tab2 en nueva/modificar contratación**: La interfaz de desglose se carga cuando el usuario accede a la pestaña 2
2. **Guardado de contratación**: Se debe llamar a los métodos de cálculo antes de persistir ContratacionVO
3. **Exportación/Informes**: Los campos calculados están disponibles en ContratacionVO

### Ejemplo de Uso en Guardado
```java
// En crearNuevaContratacion() o modificarContratacion()

// 1. Obtener líneas de desglose
List<DesgloseRetribucionVO> lineas = 
    MeLanbide11Manager.getInstance().getLineasDesgloseRSB(
        numExpediente, dni, adapt);

// 2. Calcular retribución computable
BigDecimal retribComputable = 
    MeLanbide11Manager.getInstance().calcularRetribucionComputable(
        contratacion.getSalarioBase(),
        contratacion.getPagasExtraordinarias(),
        lineas);

// 3. Actualizar VO
contratacion.setRetribucionComputable(
    CalculosRetribucionUtil.toDouble(retribComputable));

// 4. Calcular coste total si es necesario
BigDecimal costeTotal = 
    MeLanbide11Manager.getInstance().calcularCosteContratoTotal(
        contratacion.getSalarioBase(),
        contratacion.getPagasExtraordinarias(),
        lineas);
        
contratacion.setCosteContrato(
    CalculosRetribucionUtil.toDouble(costeTotal));

// 5. Guardar contratación con campos calculados
```

## Validaciones Implementadas

### Backend
- DNI obligatorio para operaciones de desglose
- Validación de formato de líneas (tipo|importe|concepto|observ)
- Conversión segura de tipos numéricos
- Manejo de transacciones con rollback en caso de error

### Frontend
- Selección obligatoria de concepto (Fijo/Variable)
- Validación de formato de importe (numérico, >= 0)
- Mensajes de error descriptivos en español y euskera
- Confirmación antes de eliminar líneas

## Consideraciones Técnicas

### Precisión Monetaria
- Uso de BigDecimal en capa de negocio para evitar errores de redondeo
- Double en VO por compatibilidad con sistema existente
- Redondeo HALF_UP a 2 decimales en todos los cálculos

### Rendimiento
- Carga lazy de líneas de desglose (solo cuando se accede a Tab2)
- Cache local en JavaScript para ediciones múltiples
- Guardado por lotes (elimina todo e inserta nuevo)
- Índices en NUM_EXP y DNI para búsquedas rápidas

### Mantenibilidad
- Código documentado con JavaDoc
- Separación clara de responsabilidades (DAO/Manager/Controller)
- Métodos reutilizables para cálculos
- JSON manual para evitar dependencias

### Seguridad
- Escape de caracteres especiales en JSON
- Validación de parámetros en backend
- Uso de prepared statements (vía secuencias) para IDs
- Transacciones para integridad de datos

## Pruebas Sugeridas

### Unitarias
```java
// Probar cálculo de retribución computable
@Test
public void testCalcularRetribucionComputable() {
    BigDecimal base = new BigDecimal("30000.00");
    BigDecimal pagas = new BigDecimal("5000.00");
    
    List<DesgloseRetribucionVO> complementos = new ArrayList<>();
    
    DesgloseRetribucionVO fijo1 = new DesgloseRetribucionVO();
    fijo1.setTipo(1);
    fijo1.setConcepto("F");
    fijo1.setImporte(new BigDecimal("2000.00"));
    complementos.add(fijo1);
    
    DesgloseRetribucionVO variable1 = new DesgloseRetribucionVO();
    variable1.setTipo(1);
    variable1.setConcepto("V");
    variable1.setImporte(new BigDecimal("3000.00"));
    complementos.add(variable1);
    
    BigDecimal resultado = CalculosRetribucionUtil.calcularRetribucionComputable(
        base, pagas, complementos);
    
    // Esperado: 30000 + 5000 + 2000 = 37000 (NO incluye variable)
    assertEquals(new BigDecimal("37000.00"), resultado);
}
```

### Integración
1. Crear una contratación con salario base y pagas extras
2. Añadir varios complementos (algunos fijos, algunos variables)
3. Añadir otras percepciones extrasalariales
4. Verificar que RETRIBUCION_COMPUTABLE solo suma fijos
5. Verificar que CSTCONT suma todo

### Interfaz
1. Navegación a Tab2 con y sin datos existentes
2. CRUD completo de líneas
3. Cambio entre conceptos Fijo/Variable
4. Validación de importes negativos
5. Guardado con errores de red
6. Multiidioma (español/euskera)

## Migración de Datos

Si existen contrataciones previas, ejecutar:

```sql
-- 1. Crear tabla de desglose
@melanbide11_Desglose_RSB.sql

-- 2. Añadir campos a tabla existente (si se desea)
ALTER TABLE MELANBIDE11_CONTRATACION ADD SALARIO_BASE NUMBER(10,2);
ALTER TABLE MELANBIDE11_CONTRATACION ADD PAGAS_EXTRAORD NUMBER(10,2);
ALTER TABLE MELANBIDE11_CONTRATACION ADD RETRIB_COMPUTABLE NUMBER(10,2);

-- 3. Migrar datos existentes si es necesario
-- Ejemplo: Si CSTCONT actual era salario base + pagas
UPDATE MELANBIDE11_CONTRATACION
SET SALARIO_BASE = CSTCONT * 0.7,
    PAGAS_EXTRAORD = CSTCONT * 0.3,
    RETRIB_COMPUTABLE = CSTCONT
WHERE SALARIO_BASE IS NULL;
```

## Contacto y Soporte

Para dudas o problemas relacionados con esta implementación:
- Revisar logs en servidor de aplicaciones
- Verificar secuencias en Oracle
- Comprobar permisos de usuario BBDD en nueva tabla
- Validar configuración de desplegables (MELANBIDE11.properties)

## Historial de Cambios

- **2025-10-14**: Implementación inicial completa
  - Estructura de clases Java
  - Endpoints AJAX
  - Scripts SQL
  - Traducciones
  - Interfaz funcional

---
*Documento generado automáticamente como parte de la implementación del cálculo de Retribución Computable en MELANBIDE11*
