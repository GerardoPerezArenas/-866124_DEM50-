# Ejemplos de Uso de Herramientas MELANBIDE

## 1. Crear un nuevo VO para documentos
```bash
gen-vo DocumentoVO example-vo-fields.json
```

## 2. Crear pestaña de documentación
```bash
gen-tab Documentacion DocumentoVO
```

## 3. Crear desplegable de tipos de documento
```bash
gen-dropdown TipoDocumento
```

## 4. Agregar columna de teléfono a contrataciones
```bash
gen-column ContratacionVO melanbide11 telefono VARCHAR(20)
```

## 5. Workflow completo para nueva funcionalidad
```bash
# 1. Crear VO
gen-vo EventoVO

# 2. Crear pestaña
gen-tab Eventos EventoVO

# 3. Crear desplegables necesarios
gen-dropdown TipoEvento
gen-dropdown EstadoEvento

# 4. Agregar columnas adicionales si es necesario
gen-column EventoVO eventos observaciones VARCHAR(500)
```
