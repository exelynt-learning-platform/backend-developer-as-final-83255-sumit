# Resource Booking System API

A Spring Boot RESTful service for managing resource bookings with JWT authentication and role-based access control (RBAC).

## Features
- User Authentication & Authorization using JWT
- Role-based permissions (User vs Admin)
- Resource Management with filtering and pagination
- Reservation booking and status management
- OpenAPI / Swagger documentation

## Prerequisites
- Java 17 or Java 21
- Maven

## Environment Variables
- `JWT_SECRET`: Secret key for JWT token generation (Minimum 512 bits for HS512)
- `JWT_EXPIRATION_MS`: Token expiration time in milliseconds (default: 86400000)
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database user
- `SPRING_DATASOURCE_PASSWORD`: Database password

## Getting Started

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd backend-developer-as-final-83255-sumit