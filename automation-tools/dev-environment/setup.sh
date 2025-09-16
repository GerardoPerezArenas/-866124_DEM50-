#!/bin/bash
"""
Script de configuración del entorno de desarrollo para MELANBIDE
Automatiza la configuración de herramientas y dependencias
"""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para logging
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Directorio del proyecto
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
AUTOMATION_DIR="$PROJECT_DIR/automation-tools"

log_info "Configurando entorno de desarrollo para MELANBIDE"
log_info "Directorio del proyecto: $PROJECT_DIR"

# Verificar Python 3
check_python() {
    log_info "Verificando Python 3..."
    if command -v python3 &> /dev/null; then
        PYTHON_VERSION=$(python3 --version | cut -d' ' -f2)
        log_success "Python 3 encontrado: $PYTHON_VERSION"
    else
        log_error "Python 3 no está instalado"
        exit 1
    fi
}

# Verificar Java
check_java() {
    log_info "Verificando Java..."
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
        log_success "Java encontrado: $JAVA_VERSION"
    else
        log_warning "Java no encontrado, necesario para compilar el proyecto"
    fi
}

# Verificar Ant
check_ant() {
    log_info "Verificando Apache Ant..."
    if command -v ant &> /dev/null; then
        ANT_VERSION=$(ant -version | head -n1)
        log_success "Ant encontrado: $ANT_VERSION"
    else
        log_warning "Apache Ant no encontrado, necesario para build del proyecto"
    fi
}

# Hacer scripts ejecutables
setup_permissions() {
    log_info "Configurando permisos de scripts..."
    chmod +x "$AUTOMATION_DIR/generators"/*.py
    chmod +x "$AUTOMATION_DIR/dev-environment"/*.sh
    log_success "Permisos configurados"
}

# Crear aliases útiles
create_aliases() {
    log_info "Creando aliases..."
    
    ALIAS_FILE="$AUTOMATION_DIR/dev-environment/melanbide-aliases.sh"
    
    cat > "$ALIAS_FILE" << 'EOF'
#!/bin/bash
# Aliases para desarrollo MELANBIDE

# Navegación rápida
alias cdmel="cd $MELANBIDE_PROJECT_DIR"
alias cdauto="cd $MELANBIDE_PROJECT_DIR/automation-tools"
alias cdsrc="cd $MELANBIDE_PROJECT_DIR/MELANBIDE11/src"
alias cdjava="cd $MELANBIDE_PROJECT_DIR/MELANBIDE11/src/java"
alias cdjsp="cd $MELANBIDE_PROJECT_DIR/MELANBIDE11/src/web/jsp/extension/melanbide11"

# Generadores
alias gen-vo="python3 $MELANBIDE_PROJECT_DIR/automation-tools/generators/vo-generator.py"
alias gen-tab="python3 $MELANBIDE_PROJECT_DIR/automation-tools/generators/tab-generator.py"
alias gen-dropdown="python3 $MELANBIDE_PROJECT_DIR/automation-tools/generators/dropdown-generator.py"
alias gen-column="python3 $MELANBIDE_PROJECT_DIR/automation-tools/generators/column-generator.py"

# Build
alias mel-build="cd $MELANBIDE_PROJECT_DIR/MELANBIDE11 && ant clean compile"
alias mel-deploy="cd $MELANBIDE_PROJECT_DIR/MELANBIDE11 && ant clean compile jar"

# Utilidades
alias mel-help="cat $MELANBIDE_PROJECT_DIR/automation-tools/dev-environment/help.txt"
alias mel-status="echo 'Proyecto MELANBIDE en: $MELANBIDE_PROJECT_DIR'"

echo "✓ Aliases de MELANBIDE cargados"
EOF

    # Exportar variable de entorno
    echo "export MELANBIDE_PROJECT_DIR=\"$PROJECT_DIR\"" >> "$ALIAS_FILE"
    
    log_success "Aliases creados en: $ALIAS_FILE"
    log_info "Para cargar los aliases, ejecuta: source $ALIAS_FILE"
}

# Crear archivo de ayuda
create_help() {
    log_info "Creando archivo de ayuda..."
    
    HELP_FILE="$AUTOMATION_DIR/dev-environment/help.txt"
    
    cat > "$HELP_FILE" << 'EOF'
=== HERRAMIENTAS DE DESARROLLO MELANBIDE ===

🔧 GENERADORES DISPONIBLES:
  gen-vo <nombre>              - Generar Value Object
  gen-tab <nombre> <vo>        - Generar nueva pestaña  
  gen-dropdown <nombre>        - Generar desplegable completo
  gen-column <vo> <jsp> <col>  - Agregar columna a tabla

📁 NAVEGACIÓN RÁPIDA:
  cdmel     - Ir al directorio del proyecto
  cdauto    - Ir a automation-tools
  cdsrc     - Ir a src/
  cdjava    - Ir a src/java/
  cdjsp     - Ir a JSPs de melanbide11

🏗️ BUILD Y DEPLOY:
  mel-build   - Compilar proyecto
  mel-deploy  - Compilar y generar JAR

📋 UTILIDADES:
  mel-help    - Mostrar esta ayuda
  mel-status  - Estado del proyecto

💡 EJEMPLOS DE USO:
  gen-vo Documento
  gen-tab Documentacion DocumentoVO
  gen-dropdown TipoDocumento
  gen-column ContratacionVO melanbide11 telefono VARCHAR(20)

📖 Para más información, consulta:
  automation-tools/README.md
  automation-tools/examples/
EOF

    log_success "Archivo de ayuda creado: $HELP_FILE"
}

# Crear estructura de ejemplos
create_examples() {
    log_info "Creando estructura de ejemplos..."
    
    EXAMPLES_DIR="$AUTOMATION_DIR/examples"
    
    # Ejemplo de campos para VO
    cat > "$EXAMPLES_DIR/example-vo-fields.json" << 'EOF'
[
    {
        "name": "codigo",
        "type": "VARCHAR(20)",
        "description": "Código único del documento"
    },
    {
        "name": "titulo",
        "type": "VARCHAR(255)",
        "description": "Título del documento"
    },
    {
        "name": "descripcion",
        "type": "VARCHAR(500)",
        "description": "Descripción detallada"
    },
    {
        "name": "fechaCreacion",
        "type": "DATE",
        "description": "Fecha de creación"
    },
    {
        "name": "activo",
        "type": "VARCHAR(1)",
        "description": "Indica si está activo (S/N)"
    },
    {
        "name": "tamaño",
        "type": "NUMBER(10,2)",
        "description": "Tamaño en MB"
    }
]
EOF

    # Ejemplo de uso
    cat > "$EXAMPLES_DIR/usage-examples.md" << 'EOF'
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
EOF

    log_success "Ejemplos creados en: $EXAMPLES_DIR"
}

# Crear script de testing
create_test_script() {
    log_info "Creando script de testing..."
    
    TEST_SCRIPT="$AUTOMATION_DIR/dev-environment/test-generators.sh"
    
    cat > "$TEST_SCRIPT" << 'EOF'
#!/bin/bash
# Script para probar los generadores

echo "🧪 Probando generadores de MELANBIDE..."

AUTOMATION_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TEST_DIR="$AUTOMATION_DIR/test-output"

# Limpiar y crear directorio de pruebas
rm -rf "$TEST_DIR"
mkdir -p "$TEST_DIR"

cd "$TEST_DIR"

echo "1. Probando generador de VO..."
python3 "$AUTOMATION_DIR/generators/vo-generator.py" TestVO

echo "2. Probando generador de desplegable..."
python3 "$AUTOMATION_DIR/generators/dropdown-generator.py" TestDropdown

echo "✅ Tests completados. Revisa el directorio: $TEST_DIR"
EOF

    chmod +x "$TEST_SCRIPT"
    log_success "Script de testing creado: $TEST_SCRIPT"
}

# Verificar configuración del proyecto
verify_project_config() {
    log_info "Verificando configuración del proyecto..."
    
    CONFIG_FILE="$AUTOMATION_DIR/config/project-config.json"
    
    if [ -f "$CONFIG_FILE" ]; then
        log_success "Configuración encontrada: $CONFIG_FILE"
        
        # Verificar que las rutas existen
        JAVA_SRC="$PROJECT_DIR/MELANBIDE11/src/java"
        JSP_SRC="$PROJECT_DIR/MELANBIDE11/src/web/jsp/extension/melanbide11"
        
        if [ -d "$JAVA_SRC" ]; then
            log_success "Directorio Java src encontrado: $JAVA_SRC"
        else
            log_warning "Directorio Java src no encontrado: $JAVA_SRC"
        fi
        
        if [ -d "$JSP_SRC" ]; then
            log_success "Directorio JSP encontrado: $JSP_SRC"
        else
            log_warning "Directorio JSP no encontrado: $JSP_SRC"
        fi
        
    else
        log_error "Configuración no encontrada: $CONFIG_FILE"
    fi
}

# Función principal
main() {
    echo "======================================"
    echo "   SETUP ENTORNO DESARROLLO MELANBIDE"
    echo "======================================"
    echo
    
    check_python
    check_java
    check_ant
    echo
    
    setup_permissions
    create_aliases
    create_help
    create_examples
    create_test_script
    verify_project_config
    
    echo
    log_success "¡Entorno de desarrollo configurado exitosamente!"
    echo
    echo "📋 PRÓXIMOS PASOS:"
    echo "   1. Cargar aliases: source $AUTOMATION_DIR/dev-environment/melanbide-aliases.sh"
    echo "   2. Ver ayuda: mel-help"
    echo "   3. Probar generadores: $AUTOMATION_DIR/dev-environment/test-generators.sh"
    echo "   4. Ver ejemplos: cat $AUTOMATION_DIR/examples/usage-examples.md"
    echo
    echo "🎉 ¡Ya puedes empezar a usar las herramientas de automatización!"
}

# Ejecutar función principal
main "$@"