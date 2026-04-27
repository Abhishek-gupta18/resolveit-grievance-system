import React, { useEffect, useState } from 'react';
import Landing from './pages/Landing';
import Login from './pages/Login';

import UDashboard from './pages/Udashboard';
import ADashboard from './pages/Adashboard';
import SDashboard from './pages/SDashboard';
import './App.css';

function App() {
  const [currentPage, setCurrentPage] = useState('landing');

  useEffect(() => {
    const CLICK_FEEDBACK_CLASS = 'btn-click-feedback';

    const handleButtonClickFeedback = (event) => {
      const clickedButton = event.target instanceof Element
        ? event.target.closest('button')
        : null;

      if (!clickedButton || clickedButton.disabled) {
        return;
      }

      clickedButton.classList.remove(CLICK_FEEDBACK_CLASS);
      // Force reflow so rapid repeated clicks replay the animation every time.
      void clickedButton.offsetWidth;
      clickedButton.classList.add(CLICK_FEEDBACK_CLASS);

      window.setTimeout(() => {
        clickedButton.classList.remove(CLICK_FEEDBACK_CLASS);
      }, 520);
    };

    document.addEventListener('click', handleButtonClickFeedback);

    return () => {
      document.removeEventListener('click', handleButtonClickFeedback);
    };
  }, []);

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
