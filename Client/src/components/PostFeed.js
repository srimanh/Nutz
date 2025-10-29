import React, { useState, useEffect } from 'react';
import Post from './Post';
import CreatePostForm from './CreatePostForm';
import EditPostForm from './EditPostForm';

const PostFeed = ({ isAuthenticated, username }) => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingPost, setEditingPost] = useState(null);

  const API_BASE = 'http://localhost:8080/api';

  const fetchPosts = async () => {
    try {
      setLoading(true);
      setError('');
      
      const endpoint = isAuthenticated ? '/posts/feed?page=0&size=20' : '/posts/public?page=0&size=20';
      const token = localStorage.getItem('token');
      const headers = { 'Content-Type': 'application/json' };
      if (token && isAuthenticated) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      const response = await fetch(`${API_BASE}${endpoint}`, {
        headers,
        credentials: 'include'
      });

      if (response.ok) {
        const data = await response.json();
        setPosts(data.content || []);
      } else {
        setError('Failed to load posts');
      }
    } catch (err) {
      setError('Error loading posts');
      console.error('Error fetching posts:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPosts();
  }, [isAuthenticated, username]); // eslint-disable-line react-hooks/exhaustive-deps

  const handleCreatePost = async (postData) => {
    try {
      const token = localStorage.getItem('token');
      const headers = {};
      if (token) headers['Authorization'] = `Bearer ${token}`;

      const form = new FormData();
      form.append('content', postData.content);
      form.append('isPublic', postData.isPublic);
      if (postData.description) form.append('description', postData.description);
      if (postData.mediaType && postData.mediaType !== 'none') form.append('mediaType', postData.mediaType);
      if (postData.mediaFile) form.append('media', postData.mediaFile);

      const response = await fetch(`${API_BASE}/posts`, {
        method: 'POST',
        headers,
        credentials: 'include',
        body: form
      });

      if (response.ok) {
        setShowCreateForm(false);
        fetchPosts(); // Refresh the feed
      } else {
        const errorData = await response.json();
        setError(errorData.error || 'Failed to create post');
      }
    } catch (err) {
      setError('Error creating post');
      console.error('Error creating post:', err);
    }
  };

  const handleEditPost = async (postData) => {
    try {
      const token = localStorage.getItem('token');
      const headers = {};
      if (token) headers['Authorization'] = `Bearer ${token}`;

      const form = new FormData();
      if (postData.content != null) form.append('content', postData.content);
      if (postData.isPublic != null) form.append('isPublic', postData.isPublic);
      if (postData.description != null) form.append('description', postData.description);
      if (postData.mediaType && postData.mediaType !== 'none') form.append('mediaType', postData.mediaType);
      if (postData.mediaFile) form.append('media', postData.mediaFile);

      const response = await fetch(`${API_BASE}/posts/${postData.id}`, {
        method: 'PUT',
        headers,
        credentials: 'include',
        body: form
      });

      if (response.ok) {
        setEditingPost(null);
        fetchPosts(); // Refresh the feed
      } else {
        const errorData = await response.json();
        setError(errorData.error || 'Failed to update post');
      }
    } catch (err) {
      setError('Error updating post');
      console.error('Error updating post:', err);
    }
  };

  const handleDeletePost = async (postId) => {
    if (!window.confirm('Are you sure you want to delete this post?')) {
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const headers = {};
      if (token) headers['Authorization'] = `Bearer ${token}`;

      const response = await fetch(`${API_BASE}/posts/${postId}`, {
        method: 'DELETE',
        headers,
        credentials: 'include'
      });

      if (response.ok) {
        fetchPosts(); // Refresh the feed
      } else {
        const errorData = await response.json();
        setError(errorData.error || 'Failed to delete post');
      }
    } catch (err) {
      setError('Error deleting post');
      console.error('Error deleting post:', err);
    }
  };

  if (loading) {
    return <div className="loading">Loading posts...</div>;
  }

  return (
    <div className="post-feed">
      <div className="feed-header">
        <h2>Posts Feed</h2>
        {isAuthenticated && (
          <button 
            className="create-post-btn"
            onClick={() => setShowCreateForm(true)}
          >
            Create Post
          </button>
        )}
      </div>

      {error && <div className="error">{error}</div>}

      {showCreateForm && (
        <div className="modal-overlay" onClick={() => setShowCreateForm(false)}>
          <div className="modal-container" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">Create Post</h3>
              <button className="modal-close" onClick={() => setShowCreateForm(false)}>×</button>
            </div>
            <div className="modal-body">
              <CreatePostForm
                onSubmit={handleCreatePost}
                onCancel={() => setShowCreateForm(false)}
              />
            </div>
          </div>
        </div>
      )}

      {editingPost && (
        <div className="modal-overlay" onClick={() => setEditingPost(null)}>
          <div className="modal-container" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">Edit Post</h3>
              <button className="modal-close" onClick={() => setEditingPost(null)}>×</button>
            </div>
            <div className="modal-body">
              <EditPostForm
                post={editingPost}
                onSubmit={handleEditPost}
                onCancel={() => setEditingPost(null)}
              />
            </div>
          </div>
        </div>
      )}

      <div className="posts-list">
        {posts.length === 0 ? (
          <div className="no-posts">No posts available</div>
        ) : (
          posts.map((post, idx) => (
            <div key={post.id} className="reveal" style={{ transitionDelay: `${Math.min(idx, 8) * 60}ms` }}>
              <Post
                post={post}
                currentUser={isAuthenticated ? { id: post.userId } : null}
                onEdit={setEditingPost}
                onDelete={handleDeletePost}
              />
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default PostFeed;
