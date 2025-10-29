package com.example.demo.service;

import com.example.demo.model.PasswordHistory;
import com.example.demo.model.User;
import com.example.demo.repository.PasswordHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryRepository passwordHistoryRepository;

    /**
     * Hash a plain text password
     */
    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * Verify if a plain password matches a hashed password
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    /**
     * Check if the new password is different from the last 3 passwords
     */
    public boolean isPasswordReused(User user, String newPlainPassword) {
        List<PasswordHistory> recentPasswords = passwordHistoryRepository
                .findTop3ByUserIdOrderByCreatedAtDesc(user.getId());
        
        return recentPasswords.stream()
                .anyMatch(ph -> verifyPassword(newPlainPassword, ph.getPasswordHash()));
    }

    /**
     * Save password to history and maintain only last 3 passwords
     */
    public void savePasswordToHistory(User user, String newHashedPassword) {
        // Create new password history entry
        PasswordHistory newPasswordHistory = new PasswordHistory();
        newPasswordHistory.setUserId(user.getId());
        newPasswordHistory.setPasswordHash(newHashedPassword);
        passwordHistoryRepository.save(newPasswordHistory);

        // Keep only last 3 passwords
        List<PasswordHistory> allPasswords = passwordHistoryRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());
        
        if (allPasswords.size() > 3) {
            List<PasswordHistory> passwordsToDelete = allPasswords.subList(3, allPasswords.size());
            passwordHistoryRepository.deleteAll(passwordsToDelete);
        }
    }

    /**
     * Validate password strength
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        return hasLower && hasUpper && hasDigit && hasSpecial;
    }
}
