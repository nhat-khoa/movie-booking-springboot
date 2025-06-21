# 🎬 Movie Ticket Booking Backend – Spring Boot

This is the backend service for a full-stack movie ticket booking platform, developed with **Spring Boot**. It provides RESTful APIs for movie listings and booking, real-time seat selection, Google OAuth2 login, QR code-based ticketing, multi-language support, and email confirmation.

> 🔗 Frontend: [Nuxt 3 repository](https://github.com/nhat-khoa/movie-booking-nuxtjs)

---

## 🚀 Key Features

- 🔐 **Google OAuth2 Authentication**  
  Secure login using Google accounts with Spring Security and JWT.

- 🪑 **Real-time Seat Selection**  
  Seamless concurrent seat booking via **WebSocket (SockJS + STOMP)** and **Redis Pub/Sub**.

- 📧 **Automated Email Confirmation**  
  Sends confirmation emails (with QR code) using **Spring Mail**.

- 🌍 **Multi-language Support**  
  Backend designed to support multiple languages via i18n-friendly API responses.

- 🎟️ **QR Code Ticketing**  
  Generates QR codes using **ZXing** for ticket validation at the entrance.

---

## 🛠️ Technology Stack

| Layer        | Tech Stack |
|--------------|------------|
| **Backend**  | Spring Boot, Spring Web, Spring Data JPA |
| **Database** | MySQL, Redis |
| **Security** | Spring Security, OAuth2, JWT |
| **Mapping**  | MapStruct |
| **Utils**    | Lombok, JavaMailSender, ZXing |
| **Realtime** | WebSocket, SockJS, WebStomp, Redis Pub/Sub |

---

---

## ⚙️ Getting Started

### ✅ Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8+
- Redis 6+

### 📦 Installation

```bash
# Clone the project
git clone https://github.com/nhat-khoa/movie-booking-springboot.git
cd movie-booking-springboot

# Build and run
./mvnw spring-boot:run
```

### 🔑 Environment Configuration
```properties
# App info
spring.application.name=moviebooking
spring.jackson.time-zone=Asia/Ho_Chi_Minh

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/booking_movie?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Ho_Chi_Minh

# Redis
spring.redis.host=localhost
spring.redis.port=6379

# OAuth2 Google
GOOGLE_CLIENT_ID=your-google-client-id

# JWT
SIGNER_KEY=your-secret-signing-key
VALID_DURATION=3600
REFRESHABLE_DURATION=36000

# Email (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## 📬 API Highlights

| Method | Endpoint                     | Description                                 |
|--------|------------------------------|---------------------------------------------|
| POST   | `/api/auth/login`           | Authenticate via Google OAuth2              |
| GET    | `/api/movies`               | Fetch all movies                            |
| GET    | `/api/schedule/{movieId}`   | Get showtimes for a movie                   |
| GET    | `/api/seats/{scheduleId}`   | Get available seats                         |
| POST   | `/api/book`                 | Book selected seats                         |
| WS     | `/ws/seats/{scheduleId}`    | WebSocket connection for live seat updates  |


## 👤 Author

@nhat-khoa – Backend Developer
