const ADMIN_API_BASE_URL = 'http://localhost:8008/api/admin';

const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json',
  };
};

const parseResponse = async (response, fallbackMessage) => {
  if (response.ok) {
    return response.json();
  }

  const message = await response.text();
  throw new Error(message || fallbackMessage);
};

export const getAdminUsers = async () => {
  const response = await fetch(`${ADMIN_API_BASE_URL}/users`, {
    headers: getAuthHeaders(),
  });

  return parseResponse(response, 'Failed to fetch users');
};

export const createAdminUser = async (payload) => {
  const response = await fetch(`${ADMIN_API_BASE_URL}/users`, {
    method: 'POST',
    headers: getAuthHeaders(),
    body: JSON.stringify(payload),
  });

  return parseResponse(response, 'Failed to create user');
};

export const updateAdminUser = async (userId, payload) => {
  const response = await fetch(`${ADMIN_API_BASE_URL}/users/${userId}`, {
    method: 'PUT',
    headers: getAuthHeaders(),
    body: JSON.stringify(payload),
  });

  return parseResponse(response, 'Failed to update user');
};

export const addSampleStaffAndAssignComplaints = async () => {
  const response = await fetch(`${ADMIN_API_BASE_URL}/staff/add-and-assign`, {
    method: 'POST',
    headers: getAuthHeaders(),
  });

  return parseResponse(response, 'Failed to add sample staff and assign complaints');
};

export const markStaffAttendance = async (payload) => {
  const response = await fetch(`${ADMIN_API_BASE_URL}/attendance/mark`, {
    method: 'POST',
    headers: getAuthHeaders(),
    body: JSON.stringify(payload),
  });

  return parseResponse(response, 'Failed to mark staff attendance');
};
