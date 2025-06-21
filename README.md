# 🎬 Movie Ticket Booking Backend – Spring Boot

This is the backend service for a full-stack movie ticket booking platform, built with **Spring Boot**. It provides RESTful APIs, real-time seat selection, QR code ticketing, Google OAuth2 login, and email notifications.

> 🔗 Frontend: [Nuxt 3 repository](https://github.com/nhat-khoa/movie-booking-nuxtjs)

---

## 🚀 Key Features

- 🔐 **Google OAuth2 Authentication**  
  Secure login with Google accounts using Spring Security.

- 🪑 **Real-time Seat Selection**  
  Synchronized seat selection using **Redis Pub/Sub** + **WebSocket (SockJS + STOMP)**.

- 📧 **Email Confirmation**  
  Sends automated confirmation emails (with QR ticket) via **JavaMailSender**.

- 🌍 **Multi-language Support**  
  Designed with i18n-compatible API responses for multilingual frontend integration.

- 📱 **QR Code Ticketing**  
  Generates QR codes with **ZXing** for digital ticket verification.

- 📦 **Robust Tech Stack**
  - Spring Boot
  - JPA / Hibernate
  - MySQL
  - Redis
  - WebSocket (SockJS + STOMP)
  - OAuth2 (Google Login)
  - MapStruct
  - Lombok

---

## 🔧 Getting Started

### ✅ Prerequisites

- Java 17+
- Maven 3.8+
- MySQL
- Redis

### 📥 Clone & Run

```bash
# Clone the repo
git clone https://github.com/nhat-khoa/movie-booking-springboot.git
cd movie-booking-backend

# Update application.yml with your DB, Redis, OAuth, and mail configs

# Build and run
./mvnw spring-boot:run
```
### 🔑 Environment Configuration
```env
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_db
    username: your_user
    password: your_pass
  redis:
    host: localhost
    port: 6379
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your-client-id
            client-secret: your-client-secret

mail:
  host: smtp.gmail.com
  username: your-email@gmail.com
  password: your-app-password
```
### 📬 API Highlights
- POST /api/auth/login – Google login
- GET /api/movies – Get list of movies
- GET /api/schedule/{movieId} – Showtimes
- GET /api/seats/{scheduleId} – Seat layout
- POST /api/book – Book seats
- WS /ws/seats/{scheduleId} – Real-time seat updates

