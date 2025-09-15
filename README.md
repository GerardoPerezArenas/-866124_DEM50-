## Ejecución local

Para preparar el entorno y compilar el proyecto ejecuta:

```bash
chmod +x run/setup.sh run/maintenance_setup.sh
./run/setup.sh
./run/maintenance_setup.sh
```

> **Nota:** No uses CMD para ejecutar archivos `.sh`. Utiliza Git Bash o WSL.

Si solo tienes CMD o PowerShell, ejecuta Maven directamente:

```bash
mvnw -q -DskipTests package
mvnw -q -DskipTests verify
```

## CI/CD

El pipeline de GitHub Actions se ejecutará en cada push o pull request usando `.github/workflows/ci.yml`.