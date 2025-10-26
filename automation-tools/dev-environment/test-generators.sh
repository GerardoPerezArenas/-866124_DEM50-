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
