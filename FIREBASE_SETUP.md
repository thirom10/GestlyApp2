# Configuración de Firebase para GestlyApp

## Pasos para configurar Firebase

### 1. Crear proyecto en Firebase Console
1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Haz clic en "Crear un proyecto"
3. Nombra tu proyecto (ej: "GestlyApp")
4. Sigue los pasos de configuración

### 2. Agregar app Android
1. En la consola de Firebase, haz clic en "Agregar app" y selecciona Android
2. Ingresa el nombre del paquete: `com.example.gestlyapp`
3. Descarga el archivo `google-services.json`
4. Reemplaza el archivo `app/google-services.json` actual con el descargado

### 3. Configurar Authentication
1. En Firebase Console, ve a "Authentication"
2. Haz clic en "Comenzar"
3. Ve a la pestaña "Sign-in method"
4. Habilita "Correo electrónico/contraseña"

### 4. Configurar Firestore Database
1. En Firebase Console, ve a "Firestore Database"
2. Haz clic en "Crear base de datos"
3. Selecciona "Comenzar en modo de prueba" (para desarrollo)
4. Elige una ubicación para tu base de datos

### 5. Reglas de seguridad de Firestore (para desarrollo)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Permitir lectura/escritura solo a usuarios autenticados
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## Estructura de datos en Firestore

### Colección: users
```json
{
  "uid": "string",
  "fullName": "string", 
  "email": "string",
  "createdAt": "timestamp",
  "isActive": "boolean"
}
```

## Funcionalidades implementadas

### Autenticación
- ✅ Registro de usuarios con email y contraseña
- ✅ Inicio de sesión
- ✅ Validación de formularios
- ✅ Manejo de errores
- ✅ Estados de carga

### Base de datos
- ✅ Creación automática de documento de usuario en Firestore
- ✅ Almacenamiento de datos del perfil
- ✅ Observación del estado de autenticación

### UI/UX
- ✅ Pantalla de login responsive
- ✅ Pantalla de registro con diseño atractivo
- ✅ Navegación entre pantallas
- ✅ Indicadores de carga
- ✅ Manejo de errores en UI

## Próximos pasos

1. Reemplazar el archivo `google-services.json` con el real
2. Configurar las reglas de seguridad de Firestore para producción
3. Implementar pantalla principal de la aplicación
4. Agregar funcionalidad de cerrar sesión
5. Implementar recuperación de contraseña

## Notas importantes

- El archivo `google-services.json` actual es un placeholder
- Las credenciales de Firebase deben mantenerse seguras
- Para producción, configurar reglas de seguridad más estrictas
- Considerar implementar verificación de email