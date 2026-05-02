# Entregables

## Repositorios publicos en GitHub

Backend:

```text
https://github.com/Christian9005/cash-service
```

Frontend:

```text
https://github.com/Christian9005/bank-frontend
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
