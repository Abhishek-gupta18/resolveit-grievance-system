import React, { useState } from 'react';
import { login, register } from '../services/authService';
import './Login.css';

const Login = ({ onNavigateLanding, onNavigateDashboard }) => {
  const [isRegister, setIsRegister] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'user'
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const validateEmail = (email) => /\S+@\S+\.\S+/.test(email);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSuccess('');

    // Client-side validation
    if (!validateEmail(formData.email)) {
      setError('Please enter a valid email address.');
      return;
    }

    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters long.');
      return;
    }

    if (isRegister) {
      // Registration validation
      if (!formData.name.trim()) {
        setError('Name is required.');
        return;
      }

      if (formData.password !== formData.confirmPassword) {
        setError('Password and confirm password must match.');
        return;
      }

      // Call register API
      setLoading(true);
      const response = await register({
        name: formData.name,
        email: formData.email,
        password: formData.password,
      });
      setLoading(false);

      if (response.success) {
        setSuccess('Registration successful! Please login to continue.');
        setError('');
        // Switch to login mode after successful registration
        setTimeout(() => {
          setIsRegister(false);
          setFormData({ name: '', email: '', password: '', confirmPassword: '', role: 'user' });
        }, 2000);
      } else {
        setError(response.message);
      }
    } else {
      // Login
      setLoading(true);
      const response = await login({
        email: formData.email,
        password: formData.password,
        // Note: role is no longer needed for login - it's determined by server
      });
      setLoading(false);

      if (response.success) {
        setSuccess(`Welcome back, ${response.data.name}! Redirecting to Dashboard...`);
        setError('');
        // Redirect to dashboard
        setTimeout(() => {
          onNavigateDashboard();
        }, 1500);
      } else {
        setError(response.message);
      }
    }
  };

  return (
    <div className="auth-page">
      <header className="auth-header">
        <button className="back-link" onClick={onNavigateLanding}>← Back to Home</button>
      </header>

      <div className="auth-card">
        <h1>{isRegister ? 'Create Account' : 'Welcome Back'}</h1>
        <p>{isRegister ? 'Register to submit and track grievances.' : 'Login to access your dashboard.'}</p>

        <form onSubmit={handleSubmit} className="auth-form">
          {isRegister && (
            <div className="form-field">
              <label>Name</label>
              <input
                name="name"
                type="text"
                value={formData.name}
                onChange={handleChange}
                placeholder="Enter your full name"
                required
              />
            </div>
          )}

          <div className="form-field">
            <label>Email</label>
            <input
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Enter your email"
              required
            />
          </div>

          <div className="form-field">
            <label>Password</label>
            <input
              name="password"
              type="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Enter your password"
              required
            />
          </div>

          {isRegister && (
            <div className="form-field">
              <label>Confirm Password</label>
              <input
                name="confirmPassword"
                type="password"
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="Confirm your password"
                required
              />
            </div>
          )}

          {/* {!isRegister && (
            <div className="form-field">
              <label>Role</label>
              <select
                name="role"
                value={formData.role}
                onChange={handleChange}
                className="role-select"
              >
                <option value="user">User / Client</option>
                <option value="admin">Admin</option>
                <option value="staff">Working Staff</option>
              </select>
            </div>
          )} */}

          {error && <div className="message error">{error}</div>}
          {success && <div className="message success">{success}</div>}

          <button className="submit-btn" type="submit" disabled={loading}>
            {loading ? 'Processing...' : (isRegister ? 'Register' : 'Login')}
          </button>
        </form>

        <div className="switch-text">
          {isRegister ? 'Already have an account?' : 'New user?'}
          <button type="button" onClick={() => setIsRegister((prev) => !prev)}>
            {isRegister ? ' Login' : ' Register'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default Login;
