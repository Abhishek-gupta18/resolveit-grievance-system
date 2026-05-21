import React, { useEffect, useState } from 'react';
import App from './App';

const Startup = () => {
  const [backendAvailable, setBackendAvailable] = useState(null); // null = checking

  useEffect(() => {
    const API_URL = (process.env.REACT_APP_API_URL || '').replace(/\/$/, '');

    if (!API_URL) {
      setBackendAvailable(false);
      return;
    }

    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 3000);

    fetch(`${API_URL}/health`, { signal: controller.signal, method: 'GET' })
      .then((res) => {
        setBackendAvailable(res.ok);
      })
      .catch(() => setBackendAvailable(false))
      .finally(() => clearTimeout(timeout));

    return () => controller.abort();
  }, []);

  if (backendAvailable === null) {
    return <div style={{padding:20}}>Checking backend availability…</div>;
  }

  return <App backendAvailable={backendAvailable} />;
};

export default Startup;
