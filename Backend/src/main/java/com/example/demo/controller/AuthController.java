package com.example.demo.controller;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.AuthService;
import com.example.demo.service.PasswordService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PasswordService passwordService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<HashMap<String, Object>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        HashMap<String, Object> response = new HashMap<>();
        
        try {
            // Validate password strength
            if (!passwordService.isPasswordStrong(registrationDto.getPassword())) {
                response.put("success", false);
                response.put("message", "Password must be at least 8 characters with uppercase, lowercase, digit, and special character");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Register user through AuthService
            String username = authService.registerUser(
                registrationDto.getUsername(), 
                registrationDto.getEmail(), 
                registrationDto.getPassword()
            );
            
            response.put("success", true);
            response.put("message", "User registered successfully!");
            response.put("username", username);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<HashMap<String, Object>> login(@Valid @RequestBody UserLoginDto loginDto) {
        HashMap<String, Object> response = new HashMap<>();
        
        try {
            // Authenticate user through AuthService
            boolean authenticated = authService.authenticateUser(
                loginDto.getUsernameOrEmail(), 
                loginDto.getPassword()
            );
            
            if (authenticated) {
                // Generate JWT token for the user so frontend can send Authorization header
                String token = jwtUtil.generateToken(loginDto.getUsernameOrEmail());

                response.put("success", true);
                response.put("message", "Login successful!");
                response.put("username", loginDto.getUsernameOrEmail());
                response.put("token", token);

                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid credentials");
                
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/changepassword")
    public ResponseEntity<HashMap<String, Object>> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        HashMap<String, Object> response = new HashMap<>();
        
        try {
            // Validate new password strength
            if (!passwordService.isPasswordStrong(changePasswordDto.getNewPassword())) {
                response.put("success", false);
                response.put("message", "New password must be at least 8 characters with uppercase, lowercase, digit, and special character");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Change password through AuthService
            boolean changed = authService.changePassword(
                changePasswordDto.getUsername(),
                changePasswordDto.getCurrentPassword(),
                changePasswordDto.getNewPassword()
            );
            
            if (changed) {
                response.put("success", true);
                response.put("message", "Password changed successfully!");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to change password. Check your current password.");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // DTO for change password request
    public static class ChangePasswordDto {
        @NotBlank(message = "Username is required")
        private String username;
        
        @NotBlank(message = "Current password is required")
        private String currentPassword;
        
        @NotBlank(message = "New password is required")
        private String newPassword;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}