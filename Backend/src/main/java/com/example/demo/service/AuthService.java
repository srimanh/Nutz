package com.example.demo.service;

import com.example.demo.model.PasswordHistory;
import com.example.demo.model.User;
import com.example.demo.repository.PasswordHistoryRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String registerUser(String username, String email, String password) {
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        // Add password to history
        PasswordHistory passwordHistory = new PasswordHistory();
        passwordHistory.setUserId(user.getId());
        passwordHistory.setPasswordHash(user.getPasswordHash());
        passwordHistory.setCreatedAt(LocalDateTime.now());
        passwordHistoryRepository.save(passwordHistory);

        return username;
    }

    public boolean authenticateUser(String usernameOrEmail, String password) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElse(null);
        
        if (user == null) {
            return false;
        }
        
        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            throw new RuntimeException("User not found!");
        }

        // Verify current password
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect!");
        }

        // Check if new password matches any of the last 3 passwords
        List<PasswordHistory> lastThreePasswords = passwordHistoryRepository
                .findRecentPasswordsByUserIdWithLimit(user.getId(), 3);

        String newPasswordHash = passwordEncoder.encode(newPassword);
        
        for (PasswordHistory history : lastThreePasswords) {
            if (passwordEncoder.matches(newPassword, history.getPasswordHash())) {
                throw new RuntimeException("New password cannot be the same as any of your last 3 passwords!");
            }
        }

        // Update user's password
        user.setPasswordHash(newPasswordHash);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Add new password to history
        PasswordHistory newPasswordHistory = new PasswordHistory();
        newPasswordHistory.setUserId(user.getId());
        newPasswordHistory.setPasswordHash(newPasswordHash);
        newPasswordHistory.setCreatedAt(LocalDateTime.now());
        passwordHistoryRepository.save(newPasswordHistory);

        // Keep only the last 3 passwords in history
        List<PasswordHistory> allPasswords = passwordHistoryRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());
        
        if (allPasswords.size() > 3) {
            List<PasswordHistory> passwordsToDelete = allPasswords.subList(3, allPasswords.size());
            passwordHistoryRepository.deleteAll(passwordsToDelete);
        }

        return true;
    }
}