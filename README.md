# 📒 AddressbookApp

> A Spring Boot REST API for managing personal address books and contacts — built iteratively across feature branches, from in-memory CRUD to a production-ready JWT-secured multi-user backend with MySQL persistence.

**Backend API Base URL:** `http://addressbook.us-east-1.elasticbeanstalk.com`  
**Swagger UI:** `http://addressbook.us-east-1.elasticbeanstalk.com/swagger-ui.html`

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Branch Structure](#-branch-structure)
- [Branch Details](#-branch-details)
  - [dev](#1-dev)
  - [feature/UC1 — Create a Contact](#2-featureuc1--ability-to-create-a-contact-in-addressbook)
  - [feature/UC2 — Add & Read Contacts](#3-featureuc2--ability-to-add-a-new-contact-in-addressbook)
  - [feature/UC3 — Edit a Contact](#4-featureuc3--ability-to-edit-existing-contact-person-using-their-name)
  - [feature/UC4 — Delete a Contact](#5-featureuc4--ability-to-delete-a-person-using-their-name)
  - [feature/UC5 — Spring Backend with Auth & JPA](#6-featureuc5--spring-backend)
  - [main](#7-main)
- [API Reference](#-api-reference)
- [Data Model](#-data-model)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)

---

## 🏗 Project Overview

AddressbookApp is a RESTful Spring Boot application that evolved across 7 branches, each adding a new use case:

| Phase | Branch | What was built |
|---|---|---|
| Setup | `dev` | Repository + README scaffold |
| UC1 | `feature/UC1-...` | In-memory contact creation |
| UC2 | `feature/UC2-...` | Read all contacts + unit tests |
| UC3 | `feature/UC3-...` | Edit contact by name |
| UC4 | `feature/UC4-...` | Delete contact by name |
| UC5 | `feature/UC5-...` | Full Spring backend: JWT auth, JPA/MySQL, multi-user address books |
| Prod | `main` | Production-ready build (UC5 + README) |

---

## 🌿 Branch Structure

```
AddressbookApp
│
├── main                                                               ← Production (8 commits)
├── feature/UC5-spring-backend                                         ← Full backend: auth, JPA, MySQL (4 commits)
├── feature/UC4-ability-to-delete-a-person-using-their-name           ← Delete (8 commits)
├── feature/UC3-ability-to-edit-existing-contact-person-using-their-name  ← Edit (7 commits)
├── feature/UC2-ability-to-add-a-new-contact-in-addressbook           ← Read + tests (6 commits)
├── feature/UC1-ability-to-create-a-contact-in-addressbook            ← Create (4 commits)
└── dev                                                                ← Scaffold/scratch (3 commits)
```

---

## 🔍 Branch Details

---

### 1. `dev`

> **Scaffold branch** — project initialisation and README only.

**Commits:** 3

Contains only a `README.md`. Used as the initial scratch branch before feature development began. No application code.

---

### 2. `feature/UC1` — Ability to Create a Contact in Addressbook

> **First working endpoint** — `POST /addcontact` backed by an in-memory `ArrayList`.

**Commits:** 4

#### Package structure
```
com.addressbook
├── Controller/AddressBookController.java
├── DTO/ContactDTO.java
├── Entity/ContactEntity.java
└── Service/AddressBookService.java
```

#### `ContactEntity` — plain Java object (no JPA)
Fields: `id` (long), `firstName`, `lastName`, `address`, `city`, `state`, `zip`, `phoneNumber`, `email`

#### `ContactDTO` — request/response transfer object
Same 8 fields as `ContactEntity` (no `id`).

#### `AddressBookService` — in-memory, backed by `List<ContactEntity>`
| Method | Description |
|---|---|
| `addContact(ContactDTO)` | Maps DTO → Entity, assigns auto-incremented `id` (`contacts.size() + 1`), adds to list, returns entity |
| `toEntity(ContactDTO)` *(private)* | DTO → Entity mapper |
| `toDTO(ContactEntity)` *(private)* | Entity → DTO mapper |

#### `AddressBookController` — `@RestController`
| Endpoint | Description |
|---|---|
| `POST /addcontact` | Creates a new contact — returns `201 CREATED` with saved entity |

---

### 3. `feature/UC2` — Ability to Add a New Contact in Addressbook

> **Read contacts** — adds `GET /getcontacts` and the first unit + controller tests.

**Commits:** 6

#### New in service
| Method | Description |
|---|---|
| `getAllContacts()` | Iterates list, maps each Entity → DTO, returns `List<ContactDTO>` |

#### New endpoint
| Endpoint | Description |
|---|---|
| `GET /getcontacts` | Returns all stored contacts — `200 OK` with `List<ContactDTO>` |

#### Tests added

**`AddressbookServiceTest`** (pure unit, no Spring context):
- `addContact_setsAllFields` — verifies all 8 fields mapped correctly
- `addContact_firstContactGetsId1` — ID assignment starts at 1
- `getAllContacts_returnsEmptyListWhenNoContacts`
- `getAllContacts_returnsAllAddedContacts` — adds 2, verifies both returned
- `getAllContacts_mappingIsCorrect` — DTO values match entity fields

**`AddressbookControllerTest`** (`@WebMvcTest` with `MockMvc`):
- `addContact_returns201WithBody`
- `getAllContacts_returns200WithList`
- `getAllContacts_returnsEmptyListWhenNoContacts`

---

### 4. `feature/UC3` — Ability to Edit Existing Contact Person Using Their Name

> **Update contact** — adds `POST /updatecontact/{firstName}/{lastName}`.

**Commits:** 7

#### New in service
| Method | Description |
|---|---|
| `editContact(firstName, lastName, ContactDTO)` | Finds match by name (case-insensitive), overwrites all 8 fields, returns updated entity. Returns `null` if not found. |

#### New endpoint
| Endpoint | Description |
|---|---|
| `POST /updatecontact/{firstName}/{lastName}` | Updates contact — `200 OK` with updated DTO, or `404 NOT FOUND` if no match |

#### Tests added
- `editContact_updatesContactCorrectly`
- `editContact_returnsNullWhenNotFound`
- Controller test for the update endpoint

---

### 5. `feature/UC4` — Ability to Delete a Person Using Their Name

> **Delete contact** — adds `DELETE /deletecontact/{firstName}/{lastName}`.

**Commits:** 8

#### New in service
| Method | Description |
|---|---|
| `deleteContact(firstName, lastName)` | Uses `List.removeIf()` with case-insensitive match. Returns `true` if removed, `false` if not found. |

#### New endpoint
| Endpoint | Description |
|---|---|
| `DELETE /deletecontact/{firstName}/{lastName}` | Deletes contact — `200 OK` `"Contact deleted."` or `404 NOT FOUND` `"Contact not found"` |

#### All 4 endpoints in UC4
| Endpoint | HTTP | Description |
|---|---|---|
| `/addcontact` | POST | Create contact |
| `/getcontacts` | GET | Read all contacts |
| `/updatecontact/{firstName}/{lastName}` | POST | Update contact by name |
| `/deletecontact/{firstName}/{lastName}` | DELETE | Delete contact by name |

#### Tests added
- `deleteContact_returnsTrueWhenFound`
- `deleteContact_returnsFalseWhenNotFound`
- `deleteContact_removesContactFromList`
- Controller tests for all 4 endpoints

---

### 6. `feature/UC5` — Spring Backend

> **Complete production-grade rewrite** — JWT auth, Spring Data JPA + MySQL, multi-user address books, ownership enforcement, full exception handling, Swagger UI, and a comprehensive test suite.

**Commits:** 4

#### New package structure
```
com.addressbook
├── config/
│   ├── SecurityConfig.java             ← Spring Security 6 + two-layer CORS
│   └── SwaggerConfig.java              ← OpenAPI 3 / Swagger UI
├── controller/
│   ├── AuthController.java             ← POST /api/auth/register, /login
│   ├── AddressBookController.java      ← Address book CRUD
│   └── ContactController.java          ← Contact CRUD + search + sort + count
├── dto/
│   ├── AuthDTO.java                    ← RegisterRequest, LoginRequest, AuthResponse
│   ├── ContactDTO.java                 ← Validated contact transfer object
│   └── ResponseDTO.java                ← Generic wrapper: { message, data }
├── exception/
│   ├── AddressBookNotFoundException.java
│   ├── ContactNotFoundException.java
│   ├── DuplicateAddressBookException.java
│   ├── DuplicateContactException.java
│   └── GlobalExceptionHandler.java     ← @RestControllerAdvice
├── filter/
│   └── JwtAuthFilter.java              ← OncePerRequestFilter JWT validation
├── model/
│   ├── AddressBook.java                ← @Entity, unique (name + owner)
│   ├── Contact.java                    ← @Entity, unique (firstName + lastName + addressBookId)
│   └── User.java                       ← @Entity, unique username + email
├── repository/
│   ├── AddressBookRepository.java      ← Owner-scoped queries
│   ├── ContactRepository.java          ← JPQL search + sort queries
│   └── UserRepository.java
├── service/
│   ├── AddressBookService.java         ← CRUD with ownership checks
│   ├── ContactService.java             ← CRUD + search + sort + count
│   └── UserDetailsServiceImpl.java     ← Spring Security integration
└── util/
    └── JwtUtil.java                    ← JJWT 0.12.3 token generation + validation
```

---

#### Authentication (`/api/auth` — public)

| Endpoint | Description |
|---|---|
| `POST /api/auth/register` | Creates user, BCrypt-hashes password, returns JWT + expiry |
| `POST /api/auth/login` | Authenticates via `AuthenticationManager`, returns JWT + expiry |

**`JwtUtil`:**
| Method | Description |
|---|---|
| `generateToken(username)` | Creates HS256-signed JWT (`subject`, `issuedAt`, `expiration`) |
| `getUsernameFromToken(token)` | Extracts `subject` from claims |
| `validateToken(token)` | Parses and validates signature + expiry; returns `false` on `JwtException` |
| `getExpirationMs()` | Returns configured expiry for response payloads |

**`JwtAuthFilter`:**
- Reads `Authorization: Bearer <token>`
- Validates token via `JwtUtil`
- Loads `UserDetails`, sets `SecurityContextHolder` authentication

**`SecurityConfig`:**
- Stateless JWT (`SessionCreationPolicy.STATELESS`)
- BCrypt password encoding
- Public: `/api/auth/**`, Swagger paths, `OPTIONS /**`
- All other routes require valid JWT
- Two-layer CORS: `@Order(HIGHEST_PRECEDENCE)` `CorsFilter` bean (handles preflight before Spring Security) + `http.cors()` inside filter chain

---

#### Address Book Management (`/api/addressbooks` — JWT required)

| Endpoint | Description |
|---|---|
| `POST /api/addressbooks?name=` | Create address book (unique per user) |
| `GET /api/addressbooks` | List caller's address books only |
| `GET /api/addressbooks/{id}` | Get by ID — 404 if wrong owner |
| `DELETE /api/addressbooks/{id}` | Delete — 404 if wrong owner |

**`AddressBookService`:**
| Method | Description |
|---|---|
| `createAddressBook(name, owner)` | Checks name uniqueness per user; `DuplicateAddressBookException` (409) if duplicate |
| `getAllAddressBooks(owner)` | `findByOwner(owner)` — caller only |
| `getAddressBookById(id, owner)` | `findByIdAndOwner` — throws `AddressBookNotFoundException` (404) if not found or wrong owner |
| `getAddressBookById(id)` | Internal overload for `ContactService` — no ownership check |
| `deleteAddressBook(id, owner)` | Single DB call: find + delete with ownership enforcement |

**`AddressBookRepository`** (custom queries):
- `findByOwner(owner)`
- `findByIdAndOwner(id, owner)`
- `existsByNameIgnoreCaseAndOwner(name, owner)`

---

#### Contact Management (`/api/addressbooks` — JWT required)

| Endpoint | Description |
|---|---|
| `POST /api/addressbooks/{bookId}/contacts` | Add contact — validates ownership + duplicate check |
| `GET /api/addressbooks/{bookId}/contacts` | All contacts in book |
| `GET /api/addressbooks/contacts/{contactId}` | Contact by ID |
| `PUT /api/addressbooks/{bookId}/contacts/{firstName}/{lastName}` | Partial update — only non-null fields; duplicate-name check on rename |
| `DELETE /api/addressbooks/{bookId}/contacts/{firstName}/{lastName}` | Delete by name |
| `GET /api/addressbooks/{bookId}/contacts/sorted/name` | Sort by firstName ASC, lastName ASC |
| `GET /api/addressbooks/{bookId}/contacts/sorted/location` | Sort by city ASC, state ASC, zip ASC |
| `GET /api/addressbooks/contacts/search?name=` | Full-name LIKE search across all books |
| `GET /api/addressbooks/contacts/city/{city}` | Filter by city (case-insensitive) |
| `GET /api/addressbooks/contacts/state/{state}` | Filter by state (case-insensitive) |
| `GET /api/addressbooks/contacts/count?city=&state=` | Count by city and/or state; `400` if neither param given |

**`ContactService`:**
| Method | Description |
|---|---|
| `addContact(bookId, dto, owner)` | Validates ownership; duplicate check; saves to DB |
| `getAllContacts(bookId, owner)` | Validates ownership; returns all contacts |
| `getContactById(contactId)` | `findById`; throws `ContactNotFoundException` (404) |
| `editContact(bookId, firstName, lastName, updatedDTO, owner)` | Partial update; detects renamed-to-existing-name clash |
| `deleteContact(bookId, firstName, lastName, owner)` | Validates ownership; finds + deletes |
| `searchByCity(city)` | Case-insensitive city search |
| `searchByState(state)` | Case-insensitive state search |
| `countByCity(city)` / `countByState(state)` | Count queries |
| `getSortedByName(bookId, owner)` | Alphabetical sort |
| `getSortedByCityStateZip(bookId, owner)` | Geographic sort |
| `searchByName(name)` | JPQL: `LOWER(CONCAT(firstName, ' ', lastName)) LIKE LOWER('%name%')` |

**`ContactRepository`** (custom queries):
- `findByAddressBookId(bookId)`
- `findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndAddressBookId(...)`
- `findByCityIgnoreCase` / `findByStateIgnoreCase`
- `countByCityIgnoreCase` / `countByStateIgnoreCase`
- `findByAddressBookIdOrderByFirstNameAscLastNameAsc`
- `findByAddressBookIdOrderByCityAscStateAscZipAsc`
- `existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndAddressBookId`
- `findByFullNameContaining(name)` — JPQL LIKE search

#### Exception Handling (`GlobalExceptionHandler`)
| Exception | HTTP Status |
|---|---|
| `AddressBookNotFoundException` | 404 Not Found |
| `ContactNotFoundException` | 404 Not Found |
| `DuplicateAddressBookException` | 409 Conflict |
| `DuplicateContactException` | 409 Conflict |
| `MethodArgumentNotValidException` | 400 Bad Request (field-level error map) |
| `ResponseStatusException` | Passthrough status (e.g. 400) |
| `Exception` (catch-all) | 500 Internal Server Error |

#### Test Suite (UC5)
| File | Type | Coverage |
|---|---|---|
| `AddressBookControllerTest` | `@WebMvcTest` + MockMvc | All address book endpoints |
| `AddressBookServiceTest` | Unit | Create/read/delete with ownership |
| `AddressBookRepositoryTest` | `@DataJpaTest` + H2 | All custom repository queries |
| `ContactControllerTest` | `@WebMvcTest` + MockMvc | All contact endpoints |
| `ContactServiceTest` | Unit | CRUD + search + sort + count |
| `ContactRepositoryTest` | `@DataJpaTest` + H2 | All custom contact queries |
| `AddressbookAppApplicationTests` | Spring context | Context loads successfully |

---

### 7. `main`

> **Production branch** — identical to `feature/UC5-spring-backend` plus an updated `README.md`.

**Commits:** 8

Only difference from `feature/UC5-spring-backend` is the `README.md`. All application code, services, security config, and tests are identical. This is the branch deployed to AWS Elastic Beanstalk.

---

## 📡 API Reference

### Authentication
```
POST /api/auth/register   Body: { username, email, password }
POST /api/auth/login      Body: { username, password }
Both return: { message, data: { token, expiresIn, username } }
```

### Address Books  *(Header: Authorization: Bearer <token>)*
```
POST   /api/addressbooks?name=Friends     Create address book
GET    /api/addressbooks                  List my address books
GET    /api/addressbooks/{id}             Get by ID
DELETE /api/addressbooks/{id}             Delete
```

### Contacts  *(Header: Authorization: Bearer <token>)*
```
POST   /api/addressbooks/{bookId}/contacts                              Add contact
GET    /api/addressbooks/{bookId}/contacts                              Get all
GET    /api/addressbooks/contacts/{contactId}                           Get by ID
PUT    /api/addressbooks/{bookId}/contacts/{firstName}/{lastName}        Edit
DELETE /api/addressbooks/{bookId}/contacts/{firstName}/{lastName}        Delete

GET    /api/addressbooks/{bookId}/contacts/sorted/name                  Sort by name
GET    /api/addressbooks/{bookId}/contacts/sorted/location               Sort by city/state/zip

GET    /api/addressbooks/contacts/search?name=John                      Full-name search
GET    /api/addressbooks/contacts/city/{city}                           By city
GET    /api/addressbooks/contacts/state/{state}                         By state
GET    /api/addressbooks/contacts/count?city=NYC&state=NY               Count
```

### Response envelope
```json
{
  "message": "Contact added successfully",
  "data": { ... }
}
```

---

## 📊 Data Model

```
User
 └── id, username (unique), email (unique), password (BCrypt)

AddressBook
 └── id, name, owner (username)
     Unique: (name, owner) — same name allowed across users, not within same user
     One user → many address books

Contact
 └── id, firstName, lastName, address, city, state, zip, phoneNumber, email
     addressBook_id (FK → AddressBook)
     Unique: (firstName, lastName, addressBook_id)
```

---

## 🛠 Tech Stack

### UC1–UC4 (in-memory)
| Technology | Details |
|---|---|
| Java | 17+ |
| Spring Boot | 3.x |
| Spring Web | REST controllers |
| JUnit 5 + Mockito | Unit + MockMvc tests |

### UC5 / main (production)
| Technology | Version |
|---|---|
| Java | 17+ |
| Spring Boot | 3.2.5 |
| Spring Security | 6.x |
| Spring Data JPA | Hibernate ORM |
| MySQL | AWS RDS |
| H2 | Test database |
| JJWT | 0.12.3 |
| SpringDoc OpenAPI | 2.5.0 |
| Bean Validation | jakarta.validation |
| JUnit 5 + Mockito | Unit, MockMvc, `@DataJpaTest` |

---

## 🚀 Getting Started

### Run locally — UC1–UC4 (in-memory, no DB)
```bash
git clone https://github.com/ANUBHAV-03042004/AddressbookApp
cd AddressbookApp
git checkout feature/UC4-ability-to-delete-a-person-using-their-name
./mvnw spring-boot:run
# → http://localhost:8080
```

### Run locally — UC5 / main (MySQL required)
```bash
git checkout main

export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/addressbook
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=yourpassword

./mvnw spring-boot:run
# → http://localhost:5000
# → Swagger: http://localhost:5000/swagger-ui.html
```

### Run tests
```bash
./mvnw test
```

---

## 👤 Author

**Anubhav Kumar Srivastava** — [@ANUBHAV-03042004](https://github.com/ANUBHAV-03042004)
