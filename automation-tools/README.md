# Herramientas de Automatización para Módulos MELANBIDE

## Descripción
Este conjunto de herramientas automatiza las tareas comunes de desarrollo de módulos en el sistema MELANBIDE, incluyendo:

1. **Generación de nuevas pestañas (tabs)** - Automatiza la creación de nuevas pestañas como "contrataciones"
2. **Generación de desplegables** - Crea componentes dropdown con sus respectivos VOs y lógica
3. **Adición de columnas a tablas** - Facilita agregar nuevas columnas a tablas existentes
4. **Generación de VOs** - Crea nuevas clases Value Object siguiendo los patrones del proyecto
5. **Entorno de desarrollo** - Configuración y scripts para facilitar el desarrollo

## Estructura
```
automation-tools/
├── generators/          # Scripts generadores
│   ├── tab-generator.py      # Generador de pestañas
│   ├── dropdown-generator.py # Generador de desplegables  
│   ├── column-generator.py   # Generador de columnas
│   └── vo-generator.py       # Generador de VOs
├── templates/           # Plantillas de código
│   ├── vo-template.java     # Plantilla para VOs
│   ├── jsp-template.jsp     # Plantilla para JSPs
│   └── js-template.js       # Plantilla para JavaScript
├── config/             # Configuraciones
│   └── project-config.json # Configuración del proyecto
├── dev-environment/    # Entorno de desarrollo
│   └── setup.sh           # Script de configuración
└── examples/           # Ejemplos de uso
```

## Uso
Cada herramienta incluye documentación específica y ejemplos de uso.