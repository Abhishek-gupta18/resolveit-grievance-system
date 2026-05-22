export const API_BASE_URL = (process.env.REACT_APP_API_URL || 'http://localhost:8008').replace(/\/$/, '');

export const buildApiUrl = (path = '') => {
  if (!path) {
    return API_BASE_URL;
  }

  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path;
  }

  if (!API_BASE_URL) {
    return path.startsWith('/') ? path : `/${path}`;
  }

  return `${API_BASE_URL}${path.startsWith('/') ? '' : '/'}${path}`;
};

export default API_BASE_URL;
