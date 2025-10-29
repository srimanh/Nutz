import React from 'react';
import { useTheme } from '../contexts/ThemeContext';
import { useAuth } from '../contexts/AuthContext';

const Navbar = ({ onLoginClick, onLogoutClick, onChangePasswordClick, onOpenAuth }) => {
  const { theme, toggleTheme } = useTheme();
  const { isAuthenticated, username } = useAuth();

  return (
    <nav className="navbar">
      <div className="nav-left">
        <span className="brand">Nutz</span>
      </div>
      <div className="nav-right">
        <button className="theme-toggle" onClick={toggleTheme} aria-label="Toggle theme">
          {theme === 'light' ? '🌙' : '☀️'}
        </button>
        {isAuthenticated ? (
          <div className="profile-menu" style={{ position: 'relative' }}>
            <details>
              <summary className="nav-auth" style={{ listStyle: 'none' }}>Profile ({username}) ▾</summary>
              <div style={{ position: 'absolute', right: 0, marginTop: 8, background: 'var(--bg)', border: '1px solid var(--border)', borderRadius: 8, padding: 8, minWidth: 160 }}>
                <button className="nav-auth" onClick={onChangePasswordClick} style={{ width: '100%' }}>Change Password</button>
                <button className="nav-auth" onClick={onLogoutClick} style={{ width: '100%', marginTop: 6 }}>Logout</button>
              </div>
            </details>
          </div>
        ) : (
          <button className="nav-auth" onClick={onOpenAuth}>Login / Sign Up</button>
        )}
      </div>
    </nav>
  );
};

export default Navbar;


