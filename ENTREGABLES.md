# Entregables

## Repositorio publico en GitHub

El entregable pide publicar todos los archivos generados en un repositorio publico y pegar la URL en los comentarios del ejercicio.

En esta maquina `gh` esta instalado, pero la sesion actual de GitHub tiene el token vencido. Primero inicia sesion:

```bash
gh auth login -h github.com
```

Despues puedes crear el repositorio publico desde la carpeta que decidas subir:

```bash
git init
git add .
git commit -m "Entrega prueba tecnica"
gh repo create cash-service-entrega --public --source=. --remote=origin --push
```

## Archivo comprimido

Se genero un ZIP limpio en:

```text
entregables/cash-service-entrega.zip
```

El ZIP excluye carpetas pesadas o locales como `.git`, `.idea`, `.vscode`, `target`, `node_modules`, `.angular`, `dist` y archivos `.env`.

## Coleccion de Postman

La coleccion para validar los endpoints esta en:

```text
postman/Cash-Service.postman_collection.json
```

Importala en Postman y ejecuta primero `Auth/Login`. El script de esa peticion guarda el token en la variable `token` para usarlo en los demas endpoints.
