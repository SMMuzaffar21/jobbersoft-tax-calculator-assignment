# Fuel Tax Calculator Service

A RESTful tax calculation service for fuel shipments across different 
jurisdictions (states), built with Spring Boot 3.2.4 and Java 21.

---

## Tech Stack

- **Java** 21
- **Spring Boot** 3.2.4
- **Spring Data JPA** + Hibernate 6.4.4
- **QueryDSL** 5.1.0 (type-safe queries)
- **Flyway** 9.22.3 (database migrations)
- **PostgreSQL** (production)
- **H2** (testing)
- **Lombok** 1.18.30
- **SpringDoc OpenAPI** 2.3.0 (Swagger UI)
- **JaCoCo** 0.8.11 (code coverage)

---

## Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 12+

---

## Setup Instructions

### 1. Create the database
```sql
CREATE DATABASE fuel_tax;
```

### 2. Configure database credentials

Update `src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fuel_tax
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 3. Build the project
```bash
./mvnw clean install
```

### 4. Run the application
```bash
./mvnw spring-boot:run
```

Flyway will automatically create the schema and seed 5 jurisdictions
on first startup.

### 5. Run tests
```bash
./mvnw clean test
```

### 6. View code coverage report
After running tests, open:

## API Documentation
Swagger UI is available at:
http://localhost:8080/swagger-ui/index.html


### Endpoints

#### Calculate Tax
/v1/tax/fuel/calculate
Request:
```json
{
  "jurisdictionCode": "CA",
  "shipmentId": "SHIP-001",
  "fuelQuantity": 100,
  "pricePerGallon": 3.50
}
```
Response (201 Created):
```json
{
  "success": true,
  "message": "Tax calculated successfully",
  "data": {
    "shipmentId": "SHIP-001",
    "jurisdictionCode": "CA",
    "jurisdictionName": "California",
    "fuelQuantity": 100.0000,
    "pricePerGallon": 3.5000,
    "taxRate": 0.0660,
    "calculatedTax": 23.1000,
    "calculatedOn": "2026-04-03T10:00:00"
  },
  "timestamp": "2026-04-03T10:00:00"
}
```

#### Get Tax History
GET /v1/tax/fuel/history/{shipmentId}
Response (200 OK):
```json
{
  "success": true,
  "message": "Tax history retrieved successfully",
  "data": [...],
  "timestamp": "2026-04-03T10:00:00"
}
```

#### Get All Jurisdictions
GET /v1/tax/fuel/jurisdictions
Response (200 OK):
```json
{
  "success": true,
  "message": "Jurisdictions retrieved successfully",
  "data": [
    { "code": "CA", "name": "California", "baseRate": 0.0660, "effectiveDate": "2024-01-01" },
    { "code": "FL", "name": "Florida",    "baseRate": 0.0363, "effectiveDate": "2024-01-01" },
    { "code": "NY", "name": "New York",   "baseRate": 0.0817, "effectiveDate": "2024-01-01" },
    { "code": "TX", "name": "Texas",      "baseRate": 0.0200, "effectiveDate": "2024-01-01" },
    { "code": "WA", "name": "Washington", "baseRate": 0.0494, "effectiveDate": "2024-01-01" }
  ],
  "timestamp": "2026-04-03T10:00:00"
}
```

---

## Tax Calculation Formula
calculatedTax = fuelQuantity × pricePerGallon × baseRate
Example:
100 gallons × $3.50/gallon × 0.0660 = $23.10 tax
---

## Design Decisions

- **QueryDSL over @Query** — type-safe queries catch field rename errors 
  at compile time rather than runtime
- **BigDecimal for all monetary values** — avoids floating point precision 
  loss (0.1 + 0.2 = 0.30000000000000004 with double)
- **Flyway for schema management** — ensures consistent schema across all 
  environments, no manual SQL needed
- **Service interface + impl** — follows Dependency Inversion principle, 
  makes unit testing easier via mocking
- **Repository sub-packages** — `jurisdiction/querydsl` separates QueryDSL 
  implementation details from the main repository interface
- **Global exception handler** — centralised error handling with consistent 
  JSON error responses across all endpoints
- **`@Transactional(readOnly = true)`** — on read methods for better 
  database performance

---

## Testing
Unit Tests (TaxCalculationServiceImplTest)
→ 8 tests, mocked repositories, no DB required
→ covers: valid calculation, unknown jurisdiction,
blank/null inputs, empty history, all jurisdictions
Repository Tests (TaxJurisdictionRepositoryTest)
→ 6 tests, real H2 in-memory DB
→ covers: findActiveByCode, findByRateRange, ordering
Integration Test (TaxApplicationTests)
→ verifies Spring context loads successfully

Run all tests:
```bash
./mvnw clean test
```

---

## Assumptions

- `effectiveDate` represents the date from which a tax rate becomes active
- `baseRate` is stored as a decimal (e.g. `0.0660` = 6.60%)
- A jurisdiction code is unique — one rate per jurisdiction
- Tax history is per shipment ID and can have multiple entries
- H2 is used for testing only — production always uses PostgreSQL
