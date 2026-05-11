import axios from 'axios';

const API_BASE = 'http://localhost:8081/api';

const api = axios.create({
    baseURL: API_BASE,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add token to requests
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// ==================== AUTH APIs ====================
export const authAPI = {
    login: (username, password) => api.post('/auth/login', { username, password }),
    register: (userData) => api.post('/auth/register', userData),
};

// ==================== PRODUCT APIs ====================
export const productAPI = {
    getAll: () => api.get('/products'),
    getById: (id) => api.get(`/products/${id}`),
    getBySku: (sku) => api.get(`/products/sku/${sku}`),
    create: (data) => api.post('/products', data),
    update: (id, data) => api.put(`/products/${id}`, data),
    delete: (id) => api.delete(`/products/${id}`),
};

// ==================== WAREHOUSE APIs ====================
export const warehouseAPI = {
    getAll: () => api.get('/warehouses'),
    create: (data) => api.post('/warehouses', data),
    delete: (id) => api.delete(`/warehouses/${id}`),
};

// ==================== AISLE APIs ====================
export const aisleAPI = {
    getAll: () => api.get('/aisles'),
    getById: (id) => api.get(`/aisles/${id}`),
    create: (data) => api.post('/aisles', data),
    delete: (id) => api.delete(`/aisles/${id}`),
};
// ==================== BIN APIs ====================
export const binAPI = {
    getAll: () => api.get('/bins'),
    getAvailable: () => api.get('/bins/available'),
    create: (data) => api.post('/bins', data),
    delete: (id) => api.delete(`/bins/${id}`),
};

// ==================== INVENTORY APIs ====================
export const inventoryAPI = {
    getAll: () => api.get('/inventory'),
    getByProduct: (sku) => api.get(`/inventory/product/${sku}`),
    create: (data) => api.post('/inventory', data),
    delete: (id) => api.delete(`/inventory/${id}`),
};

// ==================== RECEIVING APIs ====================
export const receivingAPI = {
    suggestBin: (productSku, quantity) => api.post('/receiving/suggest-bin', { productSku, quantity }),
    receive: (data) => api.post('/receiving/receive', data),
};

// ==================== ORDER APIs ====================
export const orderAPI = {
    getAll: () => api.get('/orders'),
    getByNumber: (orderNumber) => api.get(`/orders/${orderNumber}`),
    create: (data) => api.post('/orders', data),
    updateState: (orderNumber, state) => api.put(`/orders/${orderNumber}/state?state=${state}`),
};

// ==================== USER APIs ====================
// FIXED: Using the new UserController endpoints
export const userAPI = {
    getAll: () => api.get('/users'),  // This will now work with UserController
    getById: (id) => api.get(`/users/${id}`),
    create: (data) => api.post('/users', data),  // Use /users endpoint instead of /auth/register
    updateRole: (id, role) => api.put(`/users/${id}/role?role=${role}`),
    delete: (id) => api.delete(`/users/${id}`),
    // Keep register for backward compatibility
    register: (data) => api.post('/users', data),
};

export default api;