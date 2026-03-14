import React, { useState } from 'react';
import Landing from './pages/Landing';
import Login from './pages/Login';

import UDashboard from './pages/Udashboard';
import ADashboard from './pages/Adashboard';
import SDashboard from './pages/SDashboard';
import './App.css';

function App() {
  const [currentPage, setCurrentPage] = useState('landing');

  const handleNavigateLogin = () => {
    setCurrentPage('login');
  };

  const handleNavigateLanding = () => {
    setCurrentPage('landing');
  };

  const handleNavigateDashboard = () => {
    const userRole = (localStorage.getItem('userRole') || '').toUpperCase();
    const userEmail = (localStorage.getItem('userEmail') || '').toLowerCase();

    if (userRole === 'ADMIN') {
      setCurrentPage('admin-dashboard');
      return;
    }

    if (userRole === 'STAFF') {
      setCurrentPage('staff-dashboard');
      return;
    }

    if (userRole === 'USER') {
      setCurrentPage('dashboard');
      return;
    }

    if (userEmail.endsWith('@admin.com')) {
      setCurrentPage('admin-dashboard');
      return;
    }

    if (userEmail.endsWith('@staff.com')) {
      setCurrentPage('staff-dashboard');
      return;
    }

    setCurrentPage('dashboard');
  };
  

  return (
    <div className="App">
      {currentPage === 'landing' && (
        <Landing onNavigateLogin={handleNavigateLogin} />
      )}
      {currentPage === 'login' && (
        <Login onNavigateLanding={handleNavigateLanding} onNavigateDashboard={handleNavigateDashboard} />
      )}
      {currentPage === 'dashboard' && (
        <UDashboard onNavigateLanding={handleNavigateLanding} />
      )}
      {currentPage === 'admin-dashboard' && (
        <ADashboard onNavigateLanding={handleNavigateLanding} />
      )}
      {currentPage === 'staff-dashboard' && (
        <SDashboard onNavigateLanding={handleNavigateLanding} />
      )}
    </div>
  );
}

export default App;
