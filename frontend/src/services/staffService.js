import API_URL from './apiConfig';

const STAFF_API_BASE_URL = `${API_URL}/api/staff`;

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

export const getStaffDashboard = async () => {
  const response = await fetch(`${STAFF_API_BASE_URL}/dashboard`, {
    headers: getAuthHeaders(),
  });
  return parseResponse(response, 'Failed to fetch staff dashboard');
};

export const getStaffAttendance = async (from, to) => {
  const params = new URLSearchParams();
  if (from) params.append('from', from);
  if (to) params.append('to', to);
  const response = await fetch(`${STAFF_API_BASE_URL}/attendance?${params.toString()}`, {
    headers: getAuthHeaders(),
  });
  return parseResponse(response, 'Failed to fetch staff attendance');
};

export const resolveStaffComplaint = async (complaintId, comment) => {
  const response = await fetch(`${STAFF_API_BASE_URL}/complaints/${complaintId}/resolve`, {
    method: 'PUT',
    headers: getAuthHeaders(),
    body: JSON.stringify({ comment }),
  });

  return parseResponse(response, 'Failed to mark complaint as resolved');
};

export const escalateStaffComplaint = async (complaintId, comment) => {
  const response = await fetch(`${STAFF_API_BASE_URL}/complaints/${complaintId}/escalate`, {
    method: 'PUT',
    headers: getAuthHeaders(),
    body: JSON.stringify({ comment }),
  });

  return parseResponse(response, 'Failed to escalate complaint');
};
