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
export MELANBIDE_PROJECT_DIR="/home/runner/work/-866124_DEM50-/-866124_DEM50-"
