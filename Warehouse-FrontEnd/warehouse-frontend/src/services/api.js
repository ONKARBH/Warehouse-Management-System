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
        // Skip adding token for login and register endpoints
        if (token && !config.url.includes('/auth/login') && !config.url.includes('/auth/register')) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);


// Auth APIs
export const authAPI = {
    login: (username, password) => api.post('/auth/login', { username, password }),
    register: (userData) => api.post('/auth/register', userData),
};

// Product APIs
export const productAPI = {
    getAll: () => api.get('/products'),
    getById: (id) => api.get(`/products/${id}`),
    getBySku: (sku) => api.get(`/products/sku/${sku}`),
    create: (data) => api.post('/products', data),
    update: (id, data) => api.put(`/products/${id}`, data),
    delete: (id) => api.delete(`/products/${id}`),
};

// Warehouse APIs
export const warehouseAPI = {
    getAll: () => api.get('/warehouses'),
    create: (data) => api.post('/warehouses', data),
};

// Bin APIs
export const binAPI = {
    getAll: () => api.get('/bins'),
    getAvailable: () => api.get('/bins/available'),
    create: (data) => api.post('/bins', data),
};

// Inventory APIs
export const inventoryAPI = {
    getAll: () => api.get('/inventory'),
    getByProduct: (sku) => api.get(`/inventory/product/${sku}`),
    create: (data) => api.post('/inventory', data),
};

// Receiving APIs
export const receivingAPI = {
    suggestBin: (productSku, quantity) => api.post('/receiving/suggest-bin', { productSku, quantity }),
    receive: (data) => api.post('/receiving/receive', data),
};

// Order APIs
export const orderAPI = {
    getAll: () => api.get('/orders'),
    getByNumber: (orderNumber) => api.get(`/orders/${orderNumber}`),
    create: (data) => api.post('/orders', data),
    updateState: (orderNumber, state) => api.put(`/orders/${orderNumber}/state?state=${state}`),
};

// Barcode APIs
export const barcodeAPI = {
    getProductBarcode: (sku) => `${API_BASE}/barcode/product/${sku}`,
    scan: (barcode, action, quantity) => api.post('/barcode/scan', { barcode, action, quantity }),
};

export default api;