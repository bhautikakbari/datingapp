# Dating App Backend

A robust, scalable dating application backend built with Java Spring Boot, following Clean Architecture principles. This backend powers a modern dating application with features like user profiles, matching, messaging, and subscription plans.

## Features

### User Management
- User registration and authentication with JWT
- Detailed user profiles with photos
- Location-based user discovery
- Profile completion tracking

### Matching System
- Swipe functionality (left/right)
- Match creation when both users express mutual interest
- Customizable user preferences (age range, distance)
- View users who liked you (premium feature)

### Messaging
- Real-time messaging between matched users
- Message read status tracking
- Conversation history

### Subscription System
- Multiple subscription tiers (Free, Premium, Platinum)
- Feature-based access control
- Subscription management

### Photos
- Multiple photo uploads per user
- Primary photo selection
- Photo management

### Preferences & Filtering
- Age range preferences
- Distance-based filtering
- Gender preferences

## Architecture

The application follows **Clean Architecture** principles with a clear separation of concerns:

### Layers
1. **Domain Layer** - Core business entities and repository interfaces
2. **Application Layer** - Business logic, DTOs, and service implementations
3. **Infrastructure Layer** - External concerns like security, persistence, and exception handling
4. **Presentation Layer** - REST controllers and request/response handling

### Design Patterns
- Repository Pattern for data access
- DTO Pattern for data transfer
- Mapper Pattern for object transformations
- Dependency Injection for loose coupling

## Project Structure

\`\`\`
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── datingapp/
│   │           ├── DatingAppApplication.java
│   │           ├── domain/
│   │           │   ├── entity/         # Domain entities
│   │           │   └── repository/     # Repository interfaces
│   │           ├── application/
│   │           │   ├── dto/            # Data Transfer Objects
│   │           │   │   ├── request/    # Request DTOs
│   │           │   │   └── response/   # Response DTOs
│   │           │   ├── mapper/         # Object mappers
│   │           │   ├── service/        # Business logic
│   │           │   └── exception/      # Application exceptions
│   │           ├── infrastructure/
│   │           │   ├── config/         # Configuration classes
│   │           │   ├── security/       # Security implementation
│   │           │   ├── exception/      # Exception handlers
│   │           │   └── aop/            # Aspect-oriented programming
│   │           └── presentation/
│   │               └── controller/     # REST controllers
│   └── resources/
│       ├── application.yml             # Application configuration
│       └── db/
│           └── migration/              # Flyway database migrations
└── test/                               # Test classes
\`\`\`

## Technologies Used

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security** with JWT authentication
- **Spring Data JPA** with Hibernate
- **MySQL** for data persistence
- **Flyway** for database migrations
- **Lombok** for reducing boilerplate code
- **AOP** for cross-cutting concerns
- **Maven** for dependency management

## API Endpoints

The application exposes RESTful APIs for:
- Authentication (register, login)
- User profile management
- Matching and swiping
- Messaging
- Subscription management
- Photo management
