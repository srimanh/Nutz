import React, { useState } from 'react';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { ThemeProvider } from './contexts/ThemeContext';
import Navbar from './components/Navbar';
import Hero from './components/Hero';
import Footer from './components/Footer';
import AuthModal from './components/AuthModal';
import LoginForm from './components/LoginForm';
import SignupForm from './components/SignupForm';
import ChangePasswordForm from './components/ChangePasswordForm';
import PostFeed from './components/PostFeed';
import './App.css';

const AppContent = () => {
  const { isAuthenticated, username, login, logout } = useAuth();
  const [showSignup, setShowSignup] = useState(false);
  const [showChangePassword, setShowChangePassword] = useState(false);
  const [authOpen, setAuthOpen] = useState(false);

  const handleLoginSuccess = (userData) => {
    login(userData);
  };

  const handleSignupSuccess = () => {
    setShowSignup(false);
  };

  const handlePasswordChange = () => {
    // Password changed successfully
    setShowChangePassword(false);
  };

  if (isAuthenticated) {
    return (
      <div className="app">
        <Navbar onLogoutClick={logout} onChangePasswordClick={() => setShowChangePassword(true)} />
        
        <main className="app-main">
          {showChangePassword ? (
            <ChangePasswordForm onPasswordChange={handlePasswordChange} />
          ) : (
            <PostFeed isAuthenticated={isAuthenticated} username={username} />
          )}
        </main>
        <Footer />
      </div>
    );
  }

  return (
    <div className="app">
      <Navbar onOpenAuth={() => setAuthOpen(true)} />
      <Hero />

      <main className="app-main">
        <AuthModal
          open={authOpen}
          onClose={() => setAuthOpen(false)}
          onLoginSuccess={(u)=>{handleLoginSuccess(u); setAuthOpen(false);}}
          onSignupSuccess={()=>{handleSignupSuccess(); setAuthOpen(false);}}
        />
        
        <div className="public-posts">
          <h2>Public Posts</h2>
          <PostFeed isAuthenticated={false} username="" />
        </div>
      </main>
      <Footer />
    </div>
  );
};

function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
