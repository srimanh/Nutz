package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/public")
    public ResponseEntity<Page<Post>> getPublicPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Post> posts = postService.getPublicPosts(page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<Post>> getFeed(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        String username = authentication != null ? authentication.getName() : null;
        Page<Post> posts = postService.getPostsVisibleToUser(username, page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/my-posts")
    public ResponseEntity<Page<Post>> getMyPosts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        String username = authentication.getName();
        Page<Post> posts = postService.getUserPosts(username, page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        
        if (!postService.canUserAccessPost(id, username)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        
        Optional<Post> post = postService.getPostById(id);
        if (post.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(post.get());
    }

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody CreatePostRequest request,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            Post post = postService.createPost(request.getContent(), request.getIsPublic(), username);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestBody UpdatePostRequest request,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            Post post = postService.updatePost(id, request.getContent(), request.getIsPublic(), username);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        boolean deleted = postService.deletePost(id, username);
        
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
        } else {
            return ResponseEntity.status(403).body(Map.of("error", "Post not found or you don't have permission to delete it"));
        }
    }

    // DTO classes for request bodies
    public static class CreatePostRequest {
        private String content;
        private boolean isPublic = true;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public boolean getIsPublic() { return isPublic; }
        public void setIsPublic(boolean isPublic) { this.isPublic = isPublic; }
    }

    public static class UpdatePostRequest {
        private String content;
        private Boolean isPublic;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Boolean getIsPublic() { return isPublic; }
        public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    }
}
