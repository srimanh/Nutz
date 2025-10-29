import React from 'react';

const Post = ({ post, currentUser, onEdit, onDelete }) => {
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  };

  const isOwner = currentUser && post.userId === currentUser.id;

  return (
    <div className="post" style={{ transition: 'transform .2s ease, box-shadow .2s ease' }} onMouseEnter={(e)=>{e.currentTarget.style.transform='translateY(-2px)'; e.currentTarget.style.boxShadow='0 8px 24px rgba(0,0,0,.12)';}} onMouseLeave={(e)=>{e.currentTarget.style.transform='none'; e.currentTarget.style.boxShadow='';}}>
      <div className="post-header">
        <div className="post-meta">
          <span className="post-author">User ID: {post.userId}</span>
          <span className="post-date">{formatDate(post.createdAt)}</span>
          {post.updatedAt !== post.createdAt && (
            <span className="post-updated">(edited)</span>
          )}
        </div>
        <div className="post-visibility">
          {post.isPublic ? (
            <span className="public-badge">Public</span>
          ) : (
            <span className="private-badge">Private</span>
          )}
        </div>
      </div>
      
      <div className="post-content">
        {post.content}
      </div>

      {post.mediaType === 'image' && post.mediaUrl && (
        <div style={{ marginTop: 8 }}>
          <img src={post.mediaUrl} alt={post.description || 'post image'} style={{ width: '100%', borderRadius: 10, border: '1px solid var(--border)' }} />
        </div>
      )}
      {post.mediaType === 'video' && post.mediaUrl && (
        <div style={{ marginTop: 8 }}>
          <video controls style={{ width: '100%', borderRadius: 10, border: '1px solid var(--border)' }}>
            <source src={post.mediaUrl} />
          </video>
        </div>
      )}
      {post.description && (
        <div style={{ color: 'var(--muted)', marginTop: 6 }}>{post.description}</div>
      )}
      
      {isOwner && (
        <div className="post-actions">
          <button 
            className="edit-btn"
            onClick={() => onEdit(post)}
          >
            Edit
          </button>
          <button 
            className="delete-btn"
            onClick={() => onDelete(post.id)}
          >
            Delete
          </button>
        </div>
      )}
    </div>
  );
};

export default Post;
