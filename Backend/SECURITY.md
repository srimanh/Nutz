# Security Implementation

## Overview
This document outlines the security measures implemented in the Nutz application.

## Security Features Implemented

### 1. Password Security
- **BCrypt Hashing**: All passwords are hashed using BCrypt with strength factor 12
- **Password History**: Tracks last 3 passwords to prevent reuse
- **Strong Password Policy**: Enforces complex password requirements
- **Password Service**: Centralized password management with validation

### 2. Input Validation
- **Jakarta Validation**: Comprehensive validation using Bean Validation API
- **DTO Validation**: All input DTOs have validation annotations
- **Global Exception Handler**: Centralized validation error handling
- **Custom Validation Rules**: 
  - Username: 3-20 characters, alphanumeric + underscore only
  - Email: Valid email format, max 100 characters
  - Password: 8-128 characters, must contain uppercase, lowercase, digit, and special character
  - Post Content: 1-2000 characters

### 3. CORS Configuration
- **React App Support**: Configured for `http://localhost:3000` and `https://localhost:3000`
- **Flexible Headers**: Supports common headers including Authorization
- **Credentials Support**: Allows cookies and authorization headers
- **Preflight Caching**: 1-hour cache for preflight requests

### 4. CSRF Protection
- **Stateless API**: CSRF disabled for stateless API design (JWT will be used)
- **Session Management**: Stateless session management

### 5. Authentication & Authorization
- **AuthenticationManager**: Properly configured for user authentication
- **Password Encoding**: BCrypt password encoder with strength 12
- **Security Filter Chain**: Configured with proper endpoint protection
- **Public Endpoints**: `/api/auth/**`, `/api/public/**`, `/actuator/health`

### 6. Global Exception Handling
- **Validation Errors**: Structured error responses for validation failures
- **Security Errors**: Proper HTTP status codes and error messages
- **Generic Exception Handling**: Fallback for unexpected errors

## Security Configuration Files

### SecurityConfig.java
- Main security configuration
- Password encoder bean
- Authentication manager bean
- CORS configuration
- Security filter chain

### DTOs with Validation
- `UserRegistrationDto`: User registration validation
- `UserLoginDto`: Login validation
- `PostDto`: Post creation validation
- `PasswordChangeDto`: Password change validation

### Services
- `PasswordService`: Password hashing, validation, and history management
- `AuthService`: Authentication and authorization logic

### Exception Handling
- `GlobalExceptionHandler`: Centralized error handling

## API Endpoints

### Public Endpoints
- `GET /api/public/health`: Health check
- `GET /api/public/security-status`: Security configuration status
- `POST /api/public/validate-post`: Post validation demo

### Authentication Endpoints
- `POST /api/auth/register`: User registration
- `POST /api/auth/login`: User login
- `GET /api/auth/security-info`: Security information

## Testing Security Features

### Validation Testing
```bash
# Test invalid registration data
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"ab","email":"invalid","password":"weak"}'
```

### Health Check
```bash
# Test application health
curl http://localhost:8080/api/public/health
```

### Security Status
```bash
# Check security configuration
curl http://localhost:8080/api/public/security-status
```

## Production Considerations

1. **HTTPS**: Enable HTTPS in production
2. **JWT Tokens**: Implement JWT for stateless authentication
3. **Rate Limiting**: Add rate limiting for API endpoints
4. **Logging**: Implement proper security logging
5. **Environment Variables**: Use environment variables for sensitive configuration
6. **Dependency Updates**: Keep all dependencies updated
7. **Security Headers**: Add security headers (HSTS, CSP, etc.)

## Database Security
- **Connection Security**: PostgreSQL connection with credentials
- **Password History**: Secure storage of password history
- **Entity Relationships**: Proper foreign key constraints

## Frontend Integration
- **CORS**: Properly configured for React app
- **API Integration**: Ready for React app integration
- **Error Handling**: Structured error responses for frontend consumption
