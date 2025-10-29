import React, { useState } from 'react';

const CreatePostForm = ({ onSubmit, onCancel }) => {
  const [content, setContent] = useState('');
  const [isPublic, setIsPublic] = useState(true);
  const [mediaFile, setMediaFile] = useState(null);
  const [mediaType, setMediaType] = useState('none');
  const [description, setDescription] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (content.trim()) {
      onSubmit({ content: content.trim(), isPublic, mediaFile, mediaType, description });
      setContent('');
      setIsPublic(true);
      setMediaFile(null);
      setMediaType('none');
      setDescription('');
    }
  };

  return (
    <div className="create-post-form">
      <h3>Create New Post</h3>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="content">What's on your mind?</label>
          <textarea
            id="content"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="Share your thoughts..."
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
            Post
          </button>
          <button type="button" onClick={onCancel} className="cancel-btn">
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreatePostForm;
