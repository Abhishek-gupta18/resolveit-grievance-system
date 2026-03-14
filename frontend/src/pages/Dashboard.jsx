import React, { useState } from 'react';
import './Dashboard.css';

const Dashboard = ({ onLogout }) => {
  const [copied, setCopied] = useState(false);
  
  const token = localStorage.getItem('token');
  const userName = localStorage.getItem('userName');
  const userId = localStorage.getItem('userId');

  const copyToClipboard = () => {
    navigator.clipboard.writeText(token);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    onLogout();
  };

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="brand">Smart Grievance Management System😎😎</div>
        <button className="btn btn-outline" onClick={handleLogout}>Logout</button>
      </header>

      <section className="dashboard-content">
        <div className="success-message">
          <h1>✅ Login Successful!</h1>
          <p>Welcome to the Smart Grievance Management System</p>
        </div>

        <div className="user-info-card">
          <h2>User Information</h2>
          <div className="info-row">
            <span className="label">Name:</span>
            <span className="value">{userName}</span>
          </div>
          <div className="info-row">
            <span className="label">User ID:</span>
            <span className="value">{userId}</span>
          </div>
        </div>

        <div className="token-card">
          <div className="token-header">
            <h2>JWT Token for this Session(Just for demonstartion purspose.... will not be visible on the actual product)</h2>
            <button 
              className={`copy-btn ${copied ? 'copied' : ''}`}
              onClick={copyToClipboard}
            >
              {copied ? '✓ Copied!' : 'Copy Token'}
            </button>
          </div>
          <div className="token-display">
            <code className="token-text">{token}</code>
          </div>
          <div className="token-info">
            <p>This token is used to authenticate your requests to protected endpoints.</p>
            <p className="expiry">Token expires in 1 hour from login.</p>
          </div>
        </div>

        <div className="features-grid">
          <div className="feature-box">
            <h3>Submit Grievance</h3>
            <p>Submit your complaint with detailed information and attachments</p>
          </div>
          <div className="feature-box">
            <h3>Track Status</h3>
            <p>Monitor your complaint in real-time and get instant updates</p>
          </div>
          <div className="feature-box">
            <h3>View Analytics</h3>
            <p>See trends, insights, and resolution metrics</p>
          </div>
          <div className="feature-box">
            <h3>Manage Cases</h3>
            <p>(Admin) Assign and escalate cases efficiently</p>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Dashboard;
