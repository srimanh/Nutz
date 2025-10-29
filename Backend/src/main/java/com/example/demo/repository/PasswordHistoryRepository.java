package com.example.demo.repository;

import com.example.demo.model.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    
    /**
     * Find the last 3 passwords for a user ordered by creation date (newest first)
     */
    List<PasswordHistory> findTop3ByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find all passwords for a user ordered by creation date (newest first)
     */
    List<PasswordHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Count total password history entries for a user
     */
    long countByUserId(Long userId);
    
    /**
     * Delete all password history for a user (useful for account cleanup)
     */
    void deleteByUserId(Long userId);
    
    /**
     * Find recent passwords by user ID with limit
     */
    @Query("SELECT ph FROM PasswordHistory ph WHERE ph.userId = :userId ORDER BY ph.createdAt DESC")
    List<PasswordHistory> findRecentPasswordsByUserIdWithLimit(@Param("userId") Long userId, int limit);
}