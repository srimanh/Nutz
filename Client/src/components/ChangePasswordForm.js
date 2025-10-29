import React, { useState } from 'react';
import axios from 'axios';

const ChangePasswordForm = ({ onPasswordChange }) => {
  const [formData, setFormData] = useState({
    currentPassword: '',
    newPassword: ''
  });
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    try {
      const token = localStorage.getItem('token');
      const headers = { 'Content-Type': 'application/json' };
      if (token) headers['Authorization'] = `Bearer ${token}`;

      const response = await axios.post(
        'http://localhost:8080/api/auth/changepassword',
        {
          username: localStorage.getItem('username'),
          currentPassword: formData.currentPassword,
          newPassword: formData.newPassword
        },
        { headers }
      );
      
      if (response.data.success) {
        setMessage('Password changed successfully!');
        setFormData({ currentPassword: '', newPassword: '' });
        onPasswordChange();
      } else {
        setMessage(response.data.message || 'Password change failed');
      }
    } catch (error) {
      const serverMessage = error.response?.data?.message || 'Password change failed. Please try again.';
      setMessage(serverMessage);
      console.error('Password change error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-form">
      <h2>Change Password</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="currentPassword">Current Password:</label>
          <input
            type="password"
            id="currentPassword"
            name="currentPassword"
            value={formData.currentPassword}
            onChange={handleChange}
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="newPassword">New Password:</label>
          <input
            type="password"
            id="newPassword"
            name="newPassword"
            value={formData.newPassword}
            onChange={handleChange}
            required
            minLength={6}
            maxLength={40}
          />
        </div>
        
        <button type="submit" disabled={loading}>
          {loading ? 'Changing...' : 'Change Password'}
        </button>
      </form>
      
      {message && (
        <div className={`message ${message.includes('successful') ? 'success' : 'error'}`}>
          {message}
        </div>
      )}
    </div>
  );
};

export default ChangePasswordForm;
