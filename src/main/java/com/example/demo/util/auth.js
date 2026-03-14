 
// Decode JWT
export function decodeJwt(token) {
  const base64Payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
  const payload = decodeURIComponent(
    atob(base64Payload)
      .split('')
      .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
      .join('')
  );
  return JSON.parse(payload);
}
 
// Save token, role, exp
export function setAuthData(token) {
  if (!token) return;
  const payload = decodeJwt(token);
  localStorage.setItem('token', token);
  localStorage.setItem('role', payload.role);
  localStorage.setItem('userId', payload.sub);
  localStorage.setItem('exp', payload.exp);
}
 
// Getters
export function getToken() { return localStorage.getItem('token'); }
export function getRole() { return localStorage.getItem('role'); }
export function getUserId() { return localStorage.getItem('userId'); }
export function isTokenExpired() {
  const exp = localStorage.getItem('exp');
  if (!exp) return true;
  const now = Math.floor(Date.now() / 1000);
  return now > Number(exp);
}
 
// Clear auth
export function clearAuthData() {
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  localStorage.removeItem('userId');
  localStorage.removeItem('exp');
}
// CURRENT USER LOGIN DATA WE CAN GET FROM AUTH FILE  FORM THE LOCALSTORAGE (INSTRUCTORR= RAM)
//TOKEN
// NAME
//INS DTRUCTOR ID(USER RAM HE IS AN INSTARTOR ) =1
//ROLE = INSTRUCTOR
//EMAIL