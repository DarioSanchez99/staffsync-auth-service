<div align="center">

# StaffSync — Auth Service

**Autenticación, usuarios, roles y permisos con JWT**

![Java](https://img.shields.io/badge/Java%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot%203.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)

</div>

---

Microservicio responsable de la autenticación y la gestión de accesos. Emite tokens JWT con el rol y los permisos del usuario. El API Gateway valida estos tokens en cada petición y propaga las cabeceras `X-User-Id` y `X-User-Role` a los servicios aguas abajo.

---

## Arquitectura hexagonal

```
domain/
  model/          ← User, Role, Permission (objetos de dominio puros)
  port/in/        ← AuthUseCase, UserManagementUseCase, RoleUseCase
  port/out/       ← UserRepository, RoleRepository, TokenPort
  service/        ← Implementaciones de los casos de uso
infrastructure/
  adapter/in/web/         ← Controladores REST (generados con OpenAPI Generator)
  adapter/out/persistence/ ← Entidades JPA, mappers MapStruct, adaptadores JPA
  config/                 ← SecurityConfig, JwtConfig, DataInitializer
```

---

## API endpoints

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| POST | `/auth/register` | No | Registrar nuevo usuario |
| POST | `/auth/login` | No | Login → devuelve JWT |
| GET | `/auth/me` | Sí | Perfil del usuario autenticado |
| GET | `/users` | ADMIN | Listar todos los usuarios |
| POST | `/users/{id}/role` | ADMIN | Asignar rol a usuario |
| GET | `/roles` | Sí | Listar roles con sus permisos |
| POST | `/roles` | ADMIN | Crear rol |
| PUT | `/roles/{id}/permissions` | ADMIN | Actualizar permisos de un rol |
| GET | `/permissions` | Sí | Listar todos los permisos |

---

## Modelo de seguridad

### Roles predefinidos

| Rol | Permisos |
|---|---|
| `ADMIN` | Todos los permisos |
| `MANAGER` | EMPLOYEE_READ, SCHEDULE_READ/WRITE, VACATION_APPROVE, NOTIFICATION_READ |
| `EMPLOYEE` | EMPLOYEE_READ, SCHEDULE_READ, VACATION_REQUEST, NOTIFICATION_READ |

### Permisos disponibles
`EMPLOYEE_READ` · `EMPLOYEE_WRITE` · `EMPLOYEE_DELETE` · `SCHEDULE_READ` · `SCHEDULE_WRITE` · `VACATION_REQUEST` · `VACATION_APPROVE` · `NOTIFICATION_READ`

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Runtime | Java 21 |
| Framework | Spring Boot 3.3.4 |
| ORM | Spring Data JPA + Hibernate |
| Base de datos | PostgreSQL (producción) / H2 (tests) |
| JWT | jjwt 0.12.6 |
| Mapeo | MapStruct 1.5.5 |
| API spec | OpenAPI Generator 7.7.0 (contract-first) |

---

## Variables de entorno

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/staffsync_auth` | Conexión a PostgreSQL |
| `DB_USER` / `DB_PASS` | `staffsync` | Credenciales de la base de datos |
| `JWT_SECRET` | `devSecretKeyAtLeast32CharactersLong!!` | Clave de firma JWT |
| `EUREKA_URL` | `http://admin:admin@localhost:8761/eureka/` | URL de Eureka |

---

## Datos iniciales (seed)

Al arrancar por primera vez se crean automáticamente:
- Los 8 permisos predefinidos
- Los roles ADMIN, MANAGER y EMPLOYEE con sus permisos
- Usuario administrador: `admin@staffsync.com` / `admin123`

---

## Tests

```bash
mvn test
```

5 tests unitarios — AuthService (login exitoso, contraseña incorrecta, usuario no encontrado) y RoleService (añadir/eliminar permisos de un rol).

---

## Ejecución local

```bash
mvn spring-boot:run
```

Servicio disponible en: `http://localhost:8081`

---

## Parte de StaffSync

Ver [staffsync](https://github.com/DarioSanchez99/staffsync) para el índice completo del proyecto.
