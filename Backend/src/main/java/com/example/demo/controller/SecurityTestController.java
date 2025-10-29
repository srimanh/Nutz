package com.example.demo.controller;

import com.example.demo.dto.PostDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = {"http://localhost:3000", "https://localhost:3000"})
public class SecurityTestController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "UP");
        response.put("message", "Application is running securely");
        response.put("security", Map.of(
            "corsEnabled", true,
            "validationEnabled", true,
            "passwordHashingEnabled", true
        ));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/validate-post")
    public ResponseEntity<Map<String, Object>> validatePost(@Valid @RequestBody PostDto postDto) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Post validation successful");
        response.put("data", Map.of(
            "content", postDto.getContent().substring(0, Math.min(50, postDto.getContent().length())) + "...",
            "isPublic", postDto.getIsPublic(),
            "validated", true
        ));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/security-status")
    public ResponseEntity<Map<String, Object>> getSecurityStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Security configuration status");
        response.put("securityFeatures", Map.of(
            "passwordHashing", "BCrypt with strength 12",
            "inputValidation", "Jakarta Validation with custom rules",
            "corsProtection", "Configured for React app",
            "csrfProtection", "Disabled for stateless API",
            "globalExceptionHandling", "Implemented",
            "passwordHistoryTracking", "Ready for implementation"
        ));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
