import React, { useState } from 'react';
import LoginForm from './LoginForm';
import SignupForm from './SignupForm';

const AuthModal = ({ open, onClose, onLoginSuccess, onSignupSuccess }) => {
  const [tab, setTab] = useState('login');
  if (!open) return null;

  return (
    <div className="modal-overlay blur-bg" onClick={onClose}>
      <div className="modal-container" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div style={{ display: 'flex', gap: 10 }}>
            <button className="nav-auth" onClick={() => setTab('login')} aria-pressed={tab==='login'}>Login</button>
            <button className="nav-auth" onClick={() => setTab('signup')} aria-pressed={tab==='signup'}>Sign Up</button>
          </div>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <div className="modal-body">
          {tab === 'login' ? (
            <LoginForm onLoginSuccess={onLoginSuccess} />
          ) : (
            <SignupForm onSignupSuccess={onSignupSuccess} />
          )}
        </div>
      </div>
    </div>
  );
};

export default AuthModal;


