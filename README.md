# ShopEase — E-commerce Platform (Spring Boot Microservices + React)

A full-stack e-commerce demo with a microservice backend and a React frontend.

## Architecture

```
                     ┌─────────────────┐
   React (5173)  ──► │  API Gateway     │  :8080
                     │ (Spring Cloud    │
                     │  Gateway)        │
                     └───────┬──────────┘
             ┌───────────────┼───────────────┬───────────────┐
             ▼               ▼               ▼               ▼
      auth-service    product-service   cart-service    order-service
        :8081             :8082            :8083            :8084
      (H2, JWT)         (H2)          (H2, calls          (H2, calls
                                       product-service)     cart-service)
```

- **api-gateway** — single entry point, routes `/api/auth/**`, `/api/products/**`, `/api/cart/**`, `/api/orders/**` to the right service.
- **auth-service** — registration/login, issues JWT tokens, BCrypt password hashing.
- **product-service** — product catalog (CRUD), seeded with sample data.
- **cart-service** — per-user cart, enriches items by calling product-service.
- **order-service** — checkout: reads the live cart from cart-service, creates an order, clears the cart.
- **frontend** — React + Vite SPA: browsing, auth, cart, checkout, order history.

Each service has its own in-memory H2 database (simplest to run locally; swap for PostgreSQL/MySQL in production — just change the `datasource` block in each `application.yml`).

## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+ and npm

## Running it locally

Start each backend service in its own terminal, **in this order** (each blocks the terminal, so open 5 terminals):

```bash
cd auth-service && mvn spring-boot:run       # :8081
cd product-service && mvn spring-boot:run    # :8082
cd cart-service && mvn spring-boot:run       # :8083
cd order-service && mvn spring-boot:run      # :8084
cd api-gateway && mvn spring-boot:run        # :8080
```

Then start the frontend:

```bash
cd frontend
npm install
npm run dev                                   # http://localhost:5173
```

Open **http://localhost:5173** — register an account, browse products, add to cart, and check out.

## API summary (via gateway, http://localhost:8080)

| Method | Path                              | Description                  |
|--------|-----------------------------------|-------------------------------|
| POST   | /api/auth/register                | Create account, returns JWT   |
| POST   | /api/auth/login                   | Login, returns JWT            |
| GET    | /api/products                     | List products (`?search=`, `?category=`) |
| GET    | /api/products/{id}                | Product detail                |
| POST   | /api/products                     | Create product (admin)        |
| GET    | /api/cart/{userId}                | Get cart (enriched with product data) |
| POST   | /api/cart/{userId}/add            | Add item `{productId, quantity}` |
| PUT    | /api/cart/{userId}/item/{itemId}  | Update quantity `{quantity}`  |
| DELETE | /api/cart/{userId}/item/{itemId}  | Remove item                   |
| POST   | /api/orders/{userId}/checkout     | Place order from cart `{shippingAddress}` |
| GET    | /api/orders/{userId}              | Order history                 |

## Notes / what's simplified for a starter project

- H2 in-memory DB per service (data resets on restart). Swap to Postgres/MySQL for real use.
- JWT is issued by auth-service but **not yet verified inside** product/cart/order services (no shared auth filter) — the frontend attaches it, but for production add a JWT validation filter (or check it at the gateway) to protect writes and scope cart/orders to the authenticated user rather than a client-supplied `userId`.
- No service discovery (Eureka) — the gateway and services call each other via hardcoded `localhost` ports, which is fine for local dev but should become Eureka/Consul + config server for real deployments.
- No payment integration — checkout just records the order.
- Frontend uses `localStorage` for the JWT/user (fine for a demo; consider httpOnly cookies for production).

## Suggested next steps

1. Add a shared JWT filter to product/cart/order services so gateway-forwarded requests are authenticated, and derive `userId` from the token instead of the URL.
2. Add Docker Compose to spin up all 5 services + Postgres with one command.
3. Add Eureka (service discovery) so the gateway and services don't hardcode ports.
4. Add an admin product-management UI.
