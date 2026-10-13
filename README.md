# Product API (Quarkus + PostgreSQL)

Proyecto educativo que implementa un **CRUD de productos** con Java 21 y [Quarkus](https://quarkus.io/).
Expone el mismo dominio por dos vías —**REST** y **GraphQL**— y sirve una pequeña interfaz web (AngularJS) para probarlo.

Sirve como ejemplo práctico de una arquitectura en capas, persistencia con Panache, pruebas unitarias con Mockito y documentación OpenAPI.

## Tabla de contenidos

1. [Stack tecnológico](#stack-tecnológico)
2. [Arquitectura](#arquitectura)
3. [Estructura del proyecto](#estructura-del-proyecto)
4. [Requisitos y puesta en marcha](#requisitos-y-puesta-en-marcha)
5. [API REST](#api-rest)
6. [API GraphQL](#api-graphql)
7. [Configuración](#configuración)
8. [Pruebas](#pruebas)
9. [Conceptos que ilustra el proyecto](#conceptos-que-ilustra-el-proyecto)
10. [Problemas conocidos y ejercicios](#problemas-conocidos-y-ejercicios)
11. [Empaquetado y despliegue](#empaquetado-y-despliegue)

## Stack tecnológico

| Área | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Quarkus 3.39.5 |
| REST | `quarkus-rest` + `quarkus-rest-jackson` (Jakarta REST / RESTEasy Reactive) |
| GraphQL | `quarkus-smallrye-graphql` (MicroProfile GraphQL) |
| Persistencia | Hibernate ORM + Panache (`quarkus-hibernate-orm-panache`) |
| Base de datos | PostgreSQL (`quarkus-jdbc-postgresql`) |
| Documentación | `quarkus-smallrye-openapi` + Swagger UI |
| Utilidades | Lombok |
| Pruebas | JUnit 5, Mockito (`quarkus-junit5-mockito`), REST Assured |
| Frontend | HTML + AngularJS 1.4.8 (estático) |

## Arquitectura

```
 Navegador / cliente
        │
        ├── HTTP  /product ───────► ProductController   (REST)
        │                                   │
        └── HTTP  /graphql ───────► ProductResource     (GraphQL)
                                            │
                                            ▼
                                     ProductService     (reglas de negocio, transacciones)
                                            │
                                            ▼
                                    ProductRepository   (Panache)
                                            │
                                            ▼
                                       PostgreSQL
```

Ambas APIs son **adaptadores delgados**: no contienen lógica, solo traducen HTTP/GraphQL a llamadas
al servicio. Toda la lógica y el manejo de errores viven en `ProductService`, por lo que se reutilizan entre protocolos.

### Responsabilidad de cada capa

| Clase | Capa | Responsabilidad |
|---|---|---|
| `entites/Product` | Dominio | Entidad JPA (`id`, `code`, `name`, `description`). Lombok genera getters, setters y constructores. |
| `repositories/ProductRepository` | Acceso a datos | Implementa `PanacheRepository<Product>`; hereda `listAll()`, `findByIdOptional()`, `persist()`, `deleteById()`… |
| `services/ProductService` | Negocio | Valida, lanza `WebApplicationException` (404/400) y delimita transacciones con `@Transactional`. |
| `controllers/ProductController` | Presentación REST | Mapea verbos HTTP a operaciones del servicio y construye las `Response`. |
| `graphql/ProductResource` | Presentación GraphQL | Expone queries y mutaciones con `@GraphQLApi`. |
| `config/MyObjectMapperCustomizer` | Configuración | Omite propiedades `null` en el JSON de salida. |

### Reglas de negocio (en `ProductService`)

- **Buscar** un id inexistente → `404 Not Found`.
- **Crear** un producto con `id` ya informado → `400 Bad Request` (el id lo genera la base de datos).
- **Actualizar** copia `code`, `name` y `description` sobre la entidad ya cargada; Hibernate detecta el cambio y lo persiste al cerrar la transacción (*dirty checking*, no hay `persist` explícito).
- **Eliminar** devuelve el producto eliminado; `404` si no existe.

## Estructura del proyecto

```
.
├── pom.xml
├── src/main/java/org/digitalthinking/
│   ├── config/         MyObjectMapperCustomizer
│   ├── controllers/    ProductController        (REST)
│   ├── entites/        Product                  (entidad JPA)
│   ├── graphql/        ProductResource          (GraphQL)
│   ├── repositories/   ProductRepository        (Panache)
│   └── services/       ProductService
├── src/main/resources/
│   ├── application.properties
│   ├── import.sql                               (datos de ejemplo)
│   └── META-INF/resources/
│       ├── Product.html                         (UI AngularJS)
│       └── index.html                           (página por defecto de Quarkus)
├── src/main/docker/                             (Dockerfiles JVM / native)
└── src/test/java/org/digitalthinking/
    ├── controllers/ProductControllerTest
    ├── graphql/ProductResourceTest
    └── services/ProductServiceTest
```

> El paquete se llama `entites` (sin la "i"). Es un error ortográfico del paquete original; se mantiene por compatibilidad.

## Requisitos y puesta en marcha

- JDK 21
- Docker (para PostgreSQL) o una instancia local de PostgreSQL
- No hace falta instalar Maven: se usa el wrapper `./mvnw`

### 1. Levantar PostgreSQL

Los valores coinciden con `application.properties`:

```shell
docker run --name product-db -d \
  -e POSTGRES_USER=sa \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=testdb \
  -p 5432:5432 postgres:16
```

### 2. Ejecutar en modo desarrollo

```shell
./mvnw compile quarkus:dev
```

El modo dev recarga el código al guardar. La aplicación queda en **http://localhost:8081**.

| URL | Descripción |
|---|---|
| http://localhost:8081/Product.html | Interfaz web de productos |
| http://localhost:8081/swaggerui | Swagger UI (REST) |
| http://localhost:8081/q/openapi | Especificación OpenAPI |
| http://localhost:8081/graphql | Endpoint GraphQL |
| http://localhost:8081/q/graphql-ui | GraphQL UI (solo en dev) |
| http://localhost:8081/q/dev/ | Dev UI de Quarkus (solo en dev) |

### Datos de ejemplo

Con `drop-and-create`, en cada arranque Hibernate recrea el esquema y ejecuta `import.sql`, que inserta
10 productos (`PRD-001` … `PRD-010`) y reinicia la secuencia de ids en 11 para que los nuevos registros no colisionen.

## API REST

Base: `/product` · Formato: `application/json`

| Método | Ruta | Cuerpo | Respuesta |
|---|---|---|---|
| `GET` | `/product` | — | `200` + lista de productos |
| `GET` | `/product/{id}` | — | `200` + producto · `404` si no existe |
| `POST` | `/product` | producto sin `id` | `201` + cabecera `Location` · `400` si trae `id` |
| `PUT` | `/product/{id}` | producto | `202` + producto actualizado · `404` |
| `DELETE` | `/product/{id}` | — | `202` + producto eliminado · `404` |

Ejemplos:

```shell
# Listar
curl http://localhost:8081/product

# Obtener uno
curl http://localhost:8081/product/1

# Crear
curl -i -X POST http://localhost:8081/product \
  -H 'Content-Type: application/json' \
  -d '{"code":"PRD-011","name":"Hub USB-C","description":"Hub de 7 puertos"}'

# Actualizar
curl -X PUT http://localhost:8081/product/1 \
  -H 'Content-Type: application/json' \
  -d '{"code":"PRD-001","name":"Laptop Pro 16","description":"Nueva versión"}'

# Eliminar
curl -X DELETE http://localhost:8081/product/1
```

## API GraphQL

Endpoint: `POST /graphql`. El esquema se genera a partir de las anotaciones de `ProductResource`.

| Operación | Tipo | Argumentos |
|---|---|---|
| `allProducts` | Query | — |
| `productById` | Query | `productId: BigInteger` |
| `createProduct` | Mutation | `product: ProductInput` |
| `updateProduct` | Mutation | `product: ProductInput` (debe incluir `id`) |
| `deletProduct` | Mutation | `productId: BigInteger` |

> Los nombres de operación los deriva SmallRye de los métodos Java. Revisa el esquema real en `/graphql/schema.graphql`
> o en GraphQL UI antes de escribir clientes. `deletProduct` conserva una errata (falta la "e").

Ejemplo:

```graphql
query {
  allProducts { id code name description }
}

mutation {
  createProduct(product: { code: "PRD-012", name: "Soporte", description: "Soporte de monitor" }) {
    id
    name
  }
}
```

## Configuración

Archivo: `src/main/resources/application.properties`

| Propiedad | Valor | Significado |
|---|---|---|
| `quarkus.http.port` | `8081` | Puerto HTTP |
| `quarkus.datasource.*` | `sa` / `admin` @ `localhost:5432/testdb` | Conexión a PostgreSQL |
| `quarkus.hibernate-orm.database.generation` | `drop-and-create` | Recrea el esquema en cada arranque (**borra los datos**) |
| `quarkus.hibernate-orm.log.sql` | `true` | Imprime el SQL generado |
| `quarkus.swagger-ui.path` / `always-include` | `swaggerui` / `true` | Swagger UI disponible también en producción |
| `mp.openapi.extensions.smallrye.info.*` | — | Metadatos de OpenAPI |

Cualquier propiedad puede sobrescribirse con variables de entorno, por ejemplo
`QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://db:5432/prod`. Para producción conviene usar un perfil (`%prod.`) y
secretos externos en lugar de credenciales en el archivo.

## Pruebas

```shell
./mvnw test
```

Las pruebas son **unitarias puras** (sin levantar Quarkus ni base de datos): usan `@ExtendWith(MockitoExtension.class)`,
`@Mock` para la dependencia y `@InjectMocks` para la clase bajo prueba.

| Clase | Qué verifica |
|---|---|
| `ProductServiceTest` | Reglas de negocio con `ProductRepository` simulado |
| `ProductControllerTest` | Códigos HTTP y entidades de las `Response` con `ProductService` simulado |
| `ProductResourceTest` | Delegación de las operaciones GraphQL al servicio |

Para ejecutar una sola clase: `./mvnw test -Dtest=ProductControllerTest`.

## Conceptos que ilustra el proyecto

- **Inyección de dependencias (CDI)**: `@Inject`, `@ApplicationScoped`, inyección por constructor en controller y resource.
- **Patrón Repository con Panache**: CRUD sin escribir SQL ni DAOs.
- **Transacciones declarativas**: `@Transactional` solo en operaciones de escritura.
- **Manejo de errores HTTP** con `WebApplicationException` en lugar de códigos manuales en el controlador.
- **Semántica REST**: `201 Created` + `Location`, `202 Accepted`, `404`, `400`.
- **Una capa de negocio, varias APIs**: REST y GraphQL comparten el mismo servicio.
- **Pruebas unitarias aisladas** de cada capa con dobles de prueba.
- **Documentación autogenerada** (OpenAPI/Swagger y esquema GraphQL).
- **Perfiles de configuración** (`%dev.`, `%test.`) en `application.properties`.

## Problemas conocidos y ejercicios

El repositorio contiene fallos reales que sirven como ejercicio de depuración:

1. **`POST /product` falla en tiempo de ejecución.** `ProductController.createdProduct` usa
   `UriBuilder.fromResource(Product.class)`, pero `Product` no tiene `@Path`; lanza
   `IllegalArgumentException: class must be annotated with @Path`.
   *Solución:* usar `ProductController.class`. El test `createdProductReturns201WithLocation` falla por esta causa.
2. **`ProductRepository.findByNombre` y `findByPrecioMayorA`** consultan campos (`nombre`, `precio`) que
   no existen en `Product` (`name`, sin precio). Fallarán al invocarse. *Ejercicio:* corregirlos o eliminarlos.
3. **`Product.html` no envía `code`**, solo `name` y `description`, por lo que los productos creados desde la UI quedan sin código.
4. **`import.sql` con ids explícitos** y `ALTER TABLE ... RESTART WITH` es específico de la base de datos;
   revisa su compatibilidad si cambias de motor.
5. **`drop-and-create`** no es apto para producción; *ejercicio:* migrar a `update`/`validate` o a Flyway.
6. **Propuestas de mejora:** validación con Bean Validation (`@NotBlank`), paginación en el listado,
   DTOs en lugar de exponer la entidad, `@QuarkusTest` + Testcontainers para pruebas de integración,
   y un manejador global de excepciones.

## Empaquetado y despliegue

```shell
# JAR ejecutable
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar

# Über-jar
./mvnw package -Dquarkus.package.jar.type=uber-jar

# Ejecutable nativo (requiere GraalVM, o contenedor)
./mvnw package -Dnative
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

En `src/main/docker/` hay Dockerfiles para JVM, JAR legacy, nativo y nativo-micro. Ejemplo:

```shell
./mvnw package
docker build -f src/main/docker/Dockerfile.jvm -t product-api .
docker run -i --rm -p 8081:8081 product-api
```

La aplicación necesita acceso a PostgreSQL; en contenedor, apunta `QUARKUS_DATASOURCE_JDBC_URL` al host de la base de datos.

## Más información

- [Quarkus](https://quarkus.io/guides/)
- [Hibernate ORM con Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [REST en Quarkus](https://quarkus.io/guides/rest)
- [SmallRye GraphQL](https://quarkus.io/guides/smallrye-graphql)
- [OpenAPI y Swagger UI](https://quarkus.io/guides/openapi-swaggerui)
