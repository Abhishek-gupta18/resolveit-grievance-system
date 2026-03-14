import axios from "axios";
import { getToken, clearAuthData, isTokenExpired } from "../utils/auth";
 
const api = axios.create({
  baseURL: "http://localhost:8080/api"
});
 
api.interceptors.request.use((config) => {
  if (isTokenExpired()) {
    clearAuthData();
    window.location.href = "/";
    return config;
  }
 
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
 
export default api;
 
 