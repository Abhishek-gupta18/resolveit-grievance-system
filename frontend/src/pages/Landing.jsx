import React from 'react';
import './Landing.css';

const Landing = ({ onNavigateLogin }) => {
  return (
    <div className="landing-container">
      <header className="landing-header">
        <div className="brand">Smart Grievance Management System😎😎</div>
        <button className="btn btn-outline"  onClick={onNavigateLogin}>Login / Register</button>
      </header>

      <section className="hero">
        <div className="hero-left">
          <h1>Resolve complaints faster with a transparent workflow</h1>
          <p>
            A unified platform for citizens and administrators to submit, track,
            manage, escalate, and analyze grievances.
          </p>
          <div className="hero-actions">
            <button className="btn btn-primary" onClick={onNavigateLogin}>Get Started</button>
            <button className="btn btn-light" onClick={onNavigateLogin}>Submit as Guest</button>
          </div>
        </div>
      </section>

      <section className="feature-grid">
        <article className="feature-card">
          <h3>Complaint Submission</h3>
          <p>Submit public or anonymous complaints with category, urgency, and attachments.</p>
        </article>
        <article className="feature-card">
          <h3>Real-time Tracking</h3>
          <p>Track complaint ID, status, timeline updates, and admin comments in one place.</p>
        </article>
        <article className="feature-card">
          <h3>Admin Operations</h3>
          <p>Assign cases, update statuses, and escalate long-pending complaints efficiently.</p>
        </article>
        <article className="feature-card">
          <h3>Analytics & Reports</h3>
          <p>Use trend insights, status distribution, and exports to improve service delivery.</p>
        </article>
      </section>
    </div>
  );
};

export default Landing;
