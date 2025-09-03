# Guía de Uso - Herramientas de Automatización MELANBIDE

## 🎯 Objetivo

Este conjunto de herramientas automatiza las tareas más comunes en el desarrollo de módulos MELANBIDE:

1. **Agregar nuevas pestañas** (ej: pestaña "contrataciones")
2. **Crear desplegables** (dropdowns)
3. **Agregar columnas a tablas existentes**
4. **Generar Value Objects (VOs)**

## 🚀 Configuración Inicial

```bash
# 1. Ejecutar setup del entorno
cd automation-tools
./dev-environment/setup.sh

# 2. Cargar aliases (recomendado)
source dev-environment/melanbide-aliases.sh

# 3. Verificar que todo funciona
./dev-environment/test-generators.sh
```

## 📖 Ejemplos Prácticos

### Ejemplo 1: Agregar pestaña "Contrataciones"

```bash
# 1. Crear VO para contrataciones (si no existe)
gen-vo ContratacionVO examples/contratacion-fields.json

# 2. Generar pestaña completa
gen-tab Contrataciones ContratacionVO

# Resultado: Se crea contrataciones.jsp con tabla y funcionalidad CRUD básica
```

### Ejemplo 2: Crear desplegable "Tipo de Contrato"

```bash
# Generar desplegable completo
gen-dropdown TipoContrato

# Resultado: Se crean todos los archivos necesarios:
# - VO: DesplegableTipoContratoVO.java
# - JavaScript para cargar datos
# - HTML para el select
# - Métodos para Manager y DAO
# - SQL para crear tabla
```

### Ejemplo 3: Agregar columna "Teléfono" a Contrataciones

```bash
# Agregar campo teléfono a la tabla de contrataciones
gen-column ContratacionVO melanbide11 telefono VARCHAR(20)

# Resultado: 
# - Campo agregado al VO
# - Columna agregada a la tabla JSP
# - Guía de integración generada
```

## 🔧 Herramientas Disponibles

### 1. Generador de Value Objects (VO)
```bash
gen-vo <NombreVO> [archivo_campos.json]

# Ejemplos:
gen-vo DocumentoVO
gen-vo EventoVO examples/evento-fields.json
```

### 2. Generador de Pestañas
```bash
gen-tab <NombrePestana> <NombreVO>

# Ejemplos:
gen-tab Documentacion DocumentoVO
gen-tab Eventos EventoVO
```

### 3. Generador de Desplegables
```bash
gen-dropdown <NombreDesplegable>

# Ejemplos:
gen-dropdown TipoDocumento
gen-dropdown EstadoEvento
gen-dropdown Prioridad
```

### 4. Generador de Columnas
```bash
gen-column <VO> <JSP> <NombreColumna> <TipoColumna> [NombreTabla]

# Ejemplos:
gen-column ContratacionVO melanbide11 telefono VARCHAR(20)
gen-column DocumentoVO documentacion tamaño NUMBER(10,2)
```

## 📁 Estructura de Archivos Generados

```
MELANBIDE11/
├── src/java/.../vo/
│   ├── NuevoVO.java          # ← Generado por gen-vo
│   └── DesplegableXXXVO.java # ← Generado por gen-dropdown
├── src/web/jsp/extension/melanbide11/
│   ├── nueva-pestana.jsp     # ← Generado por gen-tab
│   └── melanbide11.jsp       # ← Modificado por gen-column
└── ...

automation-tools/examples/
├── dropdown-xxx/             # ← Archivos del desplegable
│   ├── VO, JavaScript, SQL, etc.
└── column-xxx/               # ← Guías de columna
    └── integration-guide.md
```

## 🔄 Workflow Típico para Nueva Funcionalidad

### Escenario: Agregar gestión de "Documentos"

```bash
# Paso 1: Crear VO
gen-vo DocumentoVO examples/documento-fields.json

# Paso 2: Crear pestaña
gen-tab Documentos DocumentoVO

# Paso 3: Crear desplegables necesarios
gen-dropdown TipoDocumento
gen-dropdown EstadoDocumento

# Paso 4: Agregar columnas adicionales si es necesario
gen-column DocumentoVO documentos observaciones VARCHAR(500)
gen-column DocumentoVO documentos fechaVencimiento DATE
```

### Después de generar:

1. **Base de datos**: Ejecutar los SQLs generados
2. **Backend**: Copiar métodos a Manager y DAO
3. **Frontend**: Integrar JSPs y JavaScript
4. **i18n**: Agregar claves de internacionalización
5. **Pruebas**: Verificar funcionalidad

## 🧪 Testing y Validación

```bash
# Probar generadores
./dev-environment/test-generators.sh

# Ver estado del proyecto
mel-status

# Ver ayuda rápida
mel-help
```

## 📝 Configuración de Campos

Para definir campos específicos, crea un archivo JSON:

```json
[
    {
        "name": "codigo",
        "type": "VARCHAR(20)",
        "description": "Código único",
        "width": "100"
    },
    {
        "name": "fechaCreacion",
        "type": "DATE",
        "description": "Fecha de creación",
        "width": "120"
    },
    {
        "name": "importe",
        "type": "NUMBER(10,2)",
        "description": "Importe en euros",
        "width": "80"
    }
]
```

## 🔍 Tipos de Datos Soportados

| Tipo Base    | Tipo Java | Descripción |
|-------------|-----------|-------------|
| VARCHAR     | String    | Texto |
| NUMBER      | Double    | Números decimales |
| INTEGER     | Integer   | Números enteros |
| DATE        | Date      | Fechas |
| BOOLEAN     | Boolean   | Verdadero/Falso |

## 🚨 Consideraciones Importantes

1. **Backup**: Siempre haz backup antes de usar los generadores
2. **Revisión**: Revisa el código generado antes de integrarlo
3. **Testing**: Prueba la funcionalidad después de integrar
4. **i18n**: No olvides agregar las claves de internacionalización
5. **Base de datos**: Ejecuta los SQLs en el orden correcto

## 🆘 Solución de Problemas

### Error: "Archivo no encontrado"
- Verifica que estés en el directorio correcto
- Asegúrate que los aliases estén cargados

### Error: "Directorio no existe"
- Los generadores crean los directorios automáticamente
- Verifica permisos de escritura

### Error: "Python no encontrado"
- Instala Python 3.7 o superior
- Ejecuta el setup nuevamente

## 📞 Soporte

Para más información, consulta:
- `automation-tools/README.md`
- `automation-tools/examples/`
- Archivos de configuración en `automation-tools/config/`