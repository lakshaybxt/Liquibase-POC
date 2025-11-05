# API Documentation

This document lists backend API endpoints, request/response shapes and examples so the frontend team can integrate with this service.

Base URL (example):
- http://localhost:8080

Auth:
- Most endpoints require a JWT Bearer token in the `Authorization` header: `Authorization: Bearer <token>`.
- Some endpoints read the authenticated user's id from a request attribute `userId` (populated by the JWT security filter). For local/testing, use the `/api/users/mock` endpoint to receive a token for a given `userId`.

Contents
- Users
  - POST /api/users/register
  - POST /api/users/login
  - POST /api/users/verify
  - POST /api/users/mock
- Products (all mounted under `/api/products`) - require authenticated user (JWT)
  - GET /api/products
  - POST /api/products
  - PUT /api/products/{id}
  - DELETE /api/products/{id}
  - GET /api/products/{id}  (returns encrypted JSON string)
  - POST /api/products/decrypt

---

Users

1) Register: POST /api/users/register
- Description: Create a new user account.
- Request headers: Content-Type: application/json
- Request body:
  {
    "username": "string",
    "email": "user@example.com",
    "password": "string"
  }
- Response: 200 OK
  Returns the saved `User` entity (for now it returns the entity object). Example:
  {
    "id": "<generated-id>",
    "username": "alice",
    "email": "alice@example.com",
    "password": "{noop}secret", // may appear in returned entity in current implementation
    "enabled": false
  }

2) Login: POST /api/users/login
- Description: Authenticate and receive a JWT token.
- Request headers: Content-Type: application/json
- Request body:
  {
    "email": "user@example.com",
    "password": "yourpassword"
  }
- Response: 200 OK
  Content-Type: application/json
  {
    "token": "<jwt-token>",
    "expiration": 169XXX0000
  }
- Use: Set header `Authorization: Bearer <jwt-token>` on subsequent requests.

3) Verify account: POST /api/users/verify
- Description: Verify account using code provided during registration.
- Request headers: Content-Type: application/json
- Request body:
  {
    "email": "user@example.com",
    "verificationCode": "123456"
  }
- Response:
  - 200 OK: "Account verified successfully"
  - 400 Bad Request: error message string

4) Mock login: POST /api/users/mock
- Description: Helper to generate a JWT for a given `userId` (used in local/testing). Returns `LoginResponse` identical to `/login`.
- Request headers: Content-Type: application/json
- Query params: `userId` (string) - the tenant/user id you want the token to represent
- Request body same as login (email/password) but only email is used to build the mock user; password is echoed into the mock user.
- Example request: POST /api/users/mock?userId=test-123
  Body:
  {
    "email": "test@example.com",
    "password": "password"
  }
- Response: 200 OK
  {
    "token": "<jwt-token-for-userId>",
    "expiration": 169XXX0000
  }

---
Products

All product endpoints are tenant-scoped: the server uses the authenticated user's id (populated by the JWT filter) as the tenant id.

Common headers:
- Authorization: Bearer <token> (required for all except decrypt endpoint which accepts raw body)
- Content-Type: application/json (for POST/PUT bodies)

1) List products (paginated): GET /api/products
- Query parameters:
  - page (default 0)
  - size (default 20)
  - sortBy (default "createdAt")
  - sortDir (ASC|DESC) (default "DESC")
- Example request: GET /api/products?page=0&size=10&sortBy=createdAt&sortDir=DESC
- Response: 200 OK (Spring Data Page<ProductResponse> JSON)
  {
    "content": [
      {
        "id": 1,
        "name": "Phone X",
        "sku": "PX-001",
        "category": "phones",
        "price": 699.99,
        "description": "Flagship phone",
        "features": { "ram": "8GB", "storage": "128GB" }
      }
    ],
    "pageable": { /* pageable metadata */ },
    "totalPages": 3,
    "totalElements": 25,
    "last": false,
    "size": 10,
    "number": 0,
    "sort": { /* sort metadata */ },
    "first": true,
    "numberOfElements": 10
  }

2) Create product: POST /api/products
- Description: Create a new product for the authenticated user.
- Request headers: Authorization: Bearer <token>, Content-Type: application/json
- Request body (CreateProductRequest):
  {
    "name": "Phone X",
    "sku": "PX-001",
    "category": "phones",
    "price": 699.99,
    "description": "Flagship phone",
    "features": { "ram": "8GB", "storage": "128GB" }
  }
- Response: 200 OK
  ProductResponse JSON:
  {
    "id": 12,
    "name": "Phone X",
    "sku": "PX-001",
    "category": "phones",
    "price": 699.99,
    "description": "Flagship phone",
    "features": { "ram": "8GB", "storage": "128GB" }
  }

3) Update product: PUT /api/products/{id}
- Description: Update fields of an existing product owned by the user.
- Request headers: Authorization: Bearer <token>, Content-Type: application/json
- Path param: id (numeric)
- Request body (UpdateProductRequest) - any fields may be null to indicate no change:
  {
    "name": "Phone X 2025",
    "sku": "PX-001",
    "category": "phones",
    "price": 749.99,
    "description": "Updated",
    "features": { "ram": "12GB" }
  }
- Response: 200 OK ProductResponse (updated)

4) Delete product: DELETE /api/products/{id}
- Description: Remove a product owned by the authenticated user.
- Request headers: Authorization: Bearer <token>
- Path param: id (numeric)
- Response: 204 No Content on success

5) Get product by id (encrypted): GET /api/products/{id}
- Description: Returns the product as an encrypted JSON string. The server encrypts the ProductResponse JSON before returning.
- Request headers: Authorization: Bearer <token>
- Path param: id (numeric)
- Response: 200 OK with encrypted string body (not application/json). Example:
  "U2FsdGVkX1+..."  // encrypted base64/text
- To decrypt on the client use the `/api/products/decrypt` endpoint or the client's decryption mechanism if shared.

6) Decrypt product JSON: POST /api/products/decrypt
- Description: Accepts an encrypted JSON string (produced by GET /{id}) and returns the decrypted ProductResponse object.
- Request headers: Content-Type: text/plain or application/json (plain string body)
- Request body: raw encrypted string
- Response: 200 OK
  {
    "id": 12,
    "name": "Phone X",
    "sku": "PX-001",
    "category": "phones",
    "price": 699.99,
    "description": "Flagship phone",
    "features": { "ram": "8GB", "storage": "128GB" }
  }

---

Errors
- Validation errors return 400 Bad Request with a JSON error description or string message.
- Global exception handler is present in `GlobalExceptionHandler.java` which may return structured `ErrorDto` objects: { "status": 400, "message": "..." }

Notes / Implementation details
- Tenant scoping: Product endpoints use the authenticated user's id (in `userId` request attribute) as tenant id.
- The `GET /api/products/{id}` response is encrypted with `EncryptionUtils` and returns a string; use `/decrypt` to get the JSON.
