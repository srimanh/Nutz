# Nutz Social Media API Documentation

## Overview
The Nutz Social Media API is a comprehensive REST API built with Spring Boot that provides secure authentication, password management, and social media functionality.

## Base URL
- Development: `http://localhost:8080`
- Production: `https://api.nutz.com`

## Authentication
The API uses JWT (JSON Web Tokens) for authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

### Authentication Endpoints

#### 1. User Registration
**POST** `/api/auth/signup`

Creates a new user account.

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully!"
}
```

**Error Responses:**
- `400 Bad Request`: Invalid input data
- `409 Conflict`: Username or email already exists

#### 2. User Login
**POST** `/api/auth/login`

Authenticates a user and returns a JWT token.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful!",
  "token": "jwt-token-string",
  "username": "string"
}
```

**Error Responses:**
- `401 Unauthorized`: Invalid credentials

#### 3. Change Password
**POST** `/api/auth/change-password`

Changes user password with security validation.

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
  "currentPassword": "string",
  "newPassword": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Password changed successfully!"
}
```

**Error Responses:**
- `400 Bad Request`: Invalid password requirements
- `401 Unauthorized`: Invalid current password
- `409 Conflict`: New password matches recent password history

### Post Endpoints

#### 1. Create Post
**POST** `/api/posts`

Creates a new post (public or private).

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
  "content": "string",
  "isPublic": boolean
}
```

**Response:**
```json
{
  "success": true,
  "message": "Post created successfully!",
  "postId": "long"
}
```

#### 2. Get Public Posts
**GET** `/api/posts/public`

Retrieves all public posts.

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response:**
```json
[
  {
    "id": "long",
    "content": "string",
    "isPublic": true,
    "createdAt": "datetime",
    "updatedAt": "datetime",
    "username": "string"
  }
]
```

#### 3. Get User's Posts
**GET** `/api/posts/my-posts`

Retrieves all posts by the authenticated user.

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response:**
```json
[
  {
    "id": "long",
    "content": "string",
    "isPublic": boolean,
    "createdAt": "datetime",
    "updatedAt": "datetime"
  }
]
```

#### 4. Update Post
**PUT** `/api/posts/{postId}`

Updates an existing post.

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
  "content": "string",
  "isPublic": boolean
}
```

**Response:**
```json
{
  "success": true,
  "message": "Post updated successfully!"
}
```

#### 5. Delete Post
**DELETE** `/api/posts/{postId}`

Deletes a post.

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response:**
```json
{
  "success": true,
  "message": "Post deleted successfully!"
}
```

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Posts Table
```sql
CREATE TABLE posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_public BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Password History Table
```sql
CREATE TABLE password_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## Security Measures

### 1. Password Security
- **Hashing**: Passwords are hashed using BCrypt with cost factor 12
- **History Validation**: Users cannot reuse their last 3 passwords
- **Strong Requirements**: Minimum complexity requirements (implemented in frontend)

### 2. JWT Security
- **Secret Key**: Strong 256-bit secret key for token signing
- **Expiration**: Tokens expire after 24 hours
- **Stateless**: No server-side session storage

### 3. CORS Configuration
- **Allowed Origins**: Configured for frontend domain
- **Methods**: GET, POST, PUT, DELETE, OPTIONS, PATCH
- **Headers**: Authorization, Content-Type, etc.

### 4. Input Validation
- **Bean Validation**: Using Jakarta Validation annotations
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries
- **XSS Prevention**: Input sanitization and output encoding

## Error Handling

The API uses consistent error response format:

```json
{
  "success": false,
  "message": "Error description",
  "timestamp": "2024-01-01T00:00:00Z",
  "path": "/api/endpoint"
}
```

## Rate Limiting
- **Authentication**: 5 attempts per minute per IP
- **API Calls**: 100 requests per minute per user
- **Password Changes**: 3 attempts per hour per user

## Testing

### Running Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AuthIntegrationTest

# Run with coverage
./mvnw test jacoco:report
```

### Test Coverage
- **Unit Tests**: Service layer and utility classes
- **Integration Tests**: Full API endpoint testing
- **Security Tests**: Authentication and authorization
- **Database Tests**: Repository layer testing

## Deployment

### Environment Variables
```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/nutz_db
DATABASE_USERNAME=nutz_user
DATABASE_PASSWORD=secure_password

# JWT
JWT_SECRET=your-super-secret-key-256-bits
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
```

### Docker Deployment
```bash
# Build image
docker build -t nutz-api .

# Run container
docker run -p 8080:8080 nutz-api
```

## Monitoring and Logging

### Health Checks
- **Endpoint**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Info**: `/actuator/info`

### Logging Levels
- **DEBUG**: Development environment
- **INFO**: Production environment
- **ERROR**: Error tracking and monitoring

## API Versioning
- **Current Version**: v1
- **Version Header**: `Accept: application/vnd.nutz.v1+json`
- **URL Versioning**: `/api/v1/endpoint` (future)

## Support
- **Documentation**: Available at `/swagger-ui.html`
- **OpenAPI Spec**: Available at `/v3/api-docs`
- **Contact**: dev@nutz.com
