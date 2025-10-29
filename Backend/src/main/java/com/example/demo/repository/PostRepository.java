package com.example.demo.repository;

import com.example.demo.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Find public posts
    @Query("SELECT p FROM Post p WHERE p.isPublic = true ORDER BY p.createdAt DESC")
    Page<Post> findPublicPosts(Pageable pageable);
    
    // Find posts by user (both public and private)
    @Query("SELECT p FROM Post p WHERE p.userId = :userId ORDER BY p.createdAt DESC")
    Page<Post> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // Find posts visible to a user (public posts + user's own private posts)
    @Query("SELECT p FROM Post p WHERE p.isPublic = true OR p.userId = :userId ORDER BY p.createdAt DESC")
    Page<Post> findVisibleToUser(@Param("userId") Long userId, Pageable pageable);
    
    // Find a specific post by ID and user ID (for authorization)
    Optional<Post> findByIdAndUserId(Long id, Long userId);
    
    // Count posts by user
    long countByUserId(Long userId);
}
