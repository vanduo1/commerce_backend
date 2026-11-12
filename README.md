# Commerce Platform

A RESTful e-commerce backend built with Spring Boot, featuring JWT authentication, cart management, order processing, and MoMo payment integration.

## Tech Stack

- **Java 21** · **Spring Boot 4.0.6**
- **Spring Security** – JWT-based authentication & authorization
- **Spring Data JPA** – ORM with PostgreSQL
- **Spring Data Redis** – Token blacklisting / session store
- **MapStruct** – DTO mapping
- **Lombok** – Boilerplate reduction
- **Docker Compose** – Local infrastructure

## Features

- User registration & login with JWT (access token + logout/token invalidation via Redis)
- Product CRUD
- Shopping cart (add, update, remove items)
- Order placement and status tracking
- Payment via **MoMo** (IPN webhook support)
- Global exception handling

## Project Structure

```
src/main/java/com/sara/
├── auth/          # Auth controller, service, JWT filter, token invalidation
├── cart/          # Cart & cart item management
├── order/         # Order creation and status
├── payment/       # MoMo integration (request, response, webhook)
├── product/       # Product CRUD
├── user/          # User entity, details service
├── config/        # Security config, MoMo config
└── exception/     # Global exception handler
```

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose

### 1. Configure environment

```bash
cp .env.example .env
```

Fill in your values in `.env`:

```env
DB_URL=jdbc:postgresql://localhost:5432/your_db
DB_NAME=your_db
DB_USER=your_user
DB_PASSWORD=your_password

JWT_SECRET=your_jwt_secret

MOMO_PARTNER_CODE=...
MOMO_ACCESS_KEY=...
MOMO_SECRET_KEY=...
MOMO_ENDPOINT=https://test-payment.momo.vn/v2/gateway/api/create
MOMO_RETURN_URL=https://yourdomain.com/payment/momo/return
MOMO_NOTIFY_URL=https://yourdomain.com/api/payment/momo/webhook
```

### 2. Start infrastructure

```bash
docker-compose up -d
```

This starts a PostgreSQL instance. Redis must also be running on `localhost:6379`.

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`.

## API Overview

| Module   | Base Path         |
|----------|-------------------|
| Auth     | `/api/auth`       |
| Products | `/api/products`   |
| Cart     | `/api/cart`       |
| Orders   | `/api/orders`     |
| Payments | `/api/payment`    |

## License

MIT

