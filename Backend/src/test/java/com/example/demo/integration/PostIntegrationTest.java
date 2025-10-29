package com.example.demo.integration;

import com.example.demo.dto.PostDto;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class PostIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private String testToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        
        // Clean up
        postRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("password123"));
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        testUser = userRepository.save(testUser);

        testToken = generateTestToken("testuser");
    }

    @Test
    void testCreatePublicPost() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setContent("This is a public post");
        postDto.setIsPublic(true);

        mockMvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Post created successfully!"));

        // Verify post was created
        assert postRepository.count() == 1;
        Post savedPost = postRepository.findAll().get(0);
        assert savedPost.getContent().equals("This is a public post");
        assert savedPost.getIsPublic() == true;
        assert savedPost.getUserId().equals(testUser.getId());
    }

    @Test
    void testCreatePrivatePost() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setContent("This is a private post");
        postDto.setIsPublic(false);

        mockMvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Post created successfully!"));

        // Verify post was created
        assert postRepository.count() == 1;
        Post savedPost = postRepository.findAll().get(0);
        assert savedPost.getContent().equals("This is a private post");
        assert savedPost.getIsPublic() == false;
        assert savedPost.getUserId().equals(testUser.getId());
    }

    @Test
    void testCreatePostWithoutAuthentication() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setContent("This should fail");
        postDto.setIsPublic(true);

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetPublicPosts() throws Exception {
        // Create some test posts
        Post publicPost = new Post();
        publicPost.setUserId(testUser.getId());
        publicPost.setContent("Public post 1");
        publicPost.setIsPublic(true);
        publicPost.setCreatedAt(LocalDateTime.now());
        postRepository.save(publicPost);

        Post privatePost = new Post();
        privatePost.setUserId(testUser.getId());
        privatePost.setContent("Private post 1");
        privatePost.setIsPublic(false);
        privatePost.setCreatedAt(LocalDateTime.now());
        postRepository.save(privatePost);

        mockMvc.perform(get("/api/posts/public")
                .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("Public post 1"))
                .andExpect(jsonPath("$[0].isPublic").value(true));
    }

    @Test
    void testGetUserPosts() throws Exception {
        // Create some test posts
        Post post1 = new Post();
        post1.setUserId(testUser.getId());
        post1.setContent("My post 1");
        post1.setIsPublic(true);
        post1.setCreatedAt(LocalDateTime.now());
        postRepository.save(post1);

        Post post2 = new Post();
        post2.setUserId(testUser.getId());
        post2.setContent("My post 2");
        post2.setIsPublic(false);
        post2.setCreatedAt(LocalDateTime.now());
        postRepository.save(post2);

        mockMvc.perform(get("/api/posts/my-posts")
                .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testUpdatePost() throws Exception {
        // Create a post first
        Post post = new Post();
        post.setUserId(testUser.getId());
        post.setContent("Original content");
        post.setIsPublic(true);
        post.setCreatedAt(LocalDateTime.now());
        post = postRepository.save(post);

        PostDto updateDto = new PostDto();
        updateDto.setContent("Updated content");
        updateDto.setIsPublic(false);

        mockMvc.perform(put("/api/posts/" + post.getId())
                .header("Authorization", "Bearer " + testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Post updated successfully!"));

        // Verify post was updated
        Post updatedPost = postRepository.findById(post.getId()).orElse(null);
        assert updatedPost != null;
        assert updatedPost.getContent().equals("Updated content");
        assert updatedPost.getIsPublic() == false;
    }

    @Test
    void testDeletePost() throws Exception {
        // Create a post first
        Post post = new Post();
        post.setUserId(testUser.getId());
        post.setContent("Post to delete");
        post.setIsPublic(true);
        post.setCreatedAt(LocalDateTime.now());
        post = postRepository.save(post);

        mockMvc.perform(delete("/api/posts/" + post.getId())
                .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Post deleted successfully!"));

        // Verify post was deleted
        assert !postRepository.existsById(post.getId());
    }

    private String generateTestToken(String username) {
        // In a real test, you'd use a test JWT utility
        return "test-jwt-token";
    }
}
