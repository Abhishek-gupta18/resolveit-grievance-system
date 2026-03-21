const API_URL = (process.env.REACT_APP_API_URL || '').replace(/\/$/, '');

export const buildApiUrl = (path = '') => {
  if (!path) {
    return API_URL;
  }

  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path;
  }

  if (!API_URL) {
    return path.startsWith('/') ? path : `/${path}`;
  }

  return `${API_URL}${path.startsWith('/') ? '' : '/'}${path}`;
};

export default API_URL;
