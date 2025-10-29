import React, { useState, useEffect } from 'react';

const EditPostForm = ({ post, onSubmit, onCancel }) => {
  const [content, setContent] = useState('');
  const [isPublic, setIsPublic] = useState(true);
  const [mediaFile, setMediaFile] = useState(null);
  const [mediaType, setMediaType] = useState('none');
  const [description, setDescription] = useState('');

  useEffect(() => {
    if (post) {
      setContent(post.content);
      setIsPublic(post.isPublic);
      // keep file empty by default when editing
      setMediaType(post.mediaType || 'none');
      setDescription(post.description || '');
    }
  }, [post]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (content.trim()) {
      onSubmit({
        id: post.id,
        content: content.trim(),
        isPublic,
        mediaFile,
        mediaType,
        description
      });
    }
  };

  if (!post) return null;

  return (
    <div className="edit-post-form">
      <h3>Edit Post</h3>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="content">Content</label>
          <textarea
            id="content"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows="4"
            required
          />
        </div>

        <div className="form-group" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
          <div>
            <label htmlFor="mediaType">Media Type</label>
            <select id="mediaType" value={mediaType} onChange={(e) => setMediaType(e.target.value)} style={{ width: '100%', padding: 10, borderRadius: 8, border: '2px solid #ddd', background: 'var(--bg)', color: 'var(--text)' }}>
              <option value="none">None</option>
              <option value="image">Image</option>
              <option value="video">Video</option>
            </select>
          </div>
          <div>
            <label htmlFor="mediaFile">Upload</label>
            <input id="mediaFile" type="file" accept={mediaType==='image' ? 'image/*' : (mediaType==='video' ? 'video/*' : '*/*')} onChange={(e) => setMediaFile(e.target.files[0])} style={{ width: '100%', padding: 10, borderRadius: 8, border: '2px solid #ddd', background: 'var(--bg)', color: 'var(--text)' }} />
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="description">Description</label>
          <input id="description" type="text" value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Short description" style={{ width: '100%', padding: 10, borderRadius: 8, border: '2px solid #ddd', background: 'var(--bg)', color: 'var(--text)' }} />
        </div>
        
        <div className="form-group">
          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={isPublic}
              onChange={(e) => setIsPublic(e.target.checked)}
            />
            Make this post public
          </label>
        </div>
        
        <div className="form-actions">
          <button type="submit" className="submit-btn">
            Update
          </button>
          <button type="button" onClick={onCancel} className="cancel-btn">
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};

export default EditPostForm;
