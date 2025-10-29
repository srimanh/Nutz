package com.example.demo.service;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<Post> getPublicPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findPublicPosts(pageable);
    }

    public Page<Post> getPostsVisibleToUser(String username, int page, int size) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            // If user not found, return only public posts
            return getPublicPosts(page, size);
        }
        
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findVisibleToUser(userOpt.get().getId(), pageable);
    }

    public Page<Post> getUserPosts(String username, int page, int size) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Page.empty();
        }
        
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByUserId(userOpt.get().getId(), pageable);
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Post createPost(String content, boolean isPublic, String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found!");
        }
        
        Post post = new Post();
        post.setUserId(userOpt.get().getId());
        post.setContent(content);
        post.setIsPublic(isPublic);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        
        return postRepository.save(post);
    }

    public Post updatePost(Long postId, String content, Boolean isPublic, String username) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Post not found!");
        }
        
        Post post = postOpt.get();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty() || !post.getUserId().equals(userOpt.get().getId())) {
            throw new RuntimeException("You don't have permission to update this post!");
        }
        
        if (content != null) {
            post.setContent(content);
        }
        if (isPublic != null) {
            post.setIsPublic(isPublic);
        }
        post.setUpdatedAt(LocalDateTime.now());
        
        return postRepository.save(post);
    }

    public boolean deletePost(Long postId, String username) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return false;
        }
        
        Post post = postOpt.get();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty() || !post.getUserId().equals(userOpt.get().getId())) {
            return false;
        }
        
        postRepository.delete(post);
        return true;
    }

    public boolean canUserAccessPost(Long postId, String username) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return false;
        }
        
        Post post = postOpt.get();
        // Public posts can be accessed by anyone
        if (post.getIsPublic()) {
            return true;
        }
        
        // Private posts can only be accessed by the owner
        if (username == null) {
            return false;
        }
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        return post.getUserId().equals(userOpt.get().getId());
    }
}