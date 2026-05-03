import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import Receiving from './components/Receiving';
import './App.css';

const PrivateRoute = ({ children }) => {
    const { user, loading } = useAuth();
    if (loading) return <div>Loading...</div>;
    return user ? children : <Navigate to="/login" />;
};

const AdminRoute = ({ children }) => {
    const { user, isAdmin } = useAuth();
    return isAdmin() ? children : <Navigate to="/dashboard" />;
};

function AppContent() {
    const { user, logout } = useAuth();

    return (
        <div className="app">
            {user && (
                <nav className="navbar">
                    <h1>WMS</h1>
                    <ul>
                        <li><a href="/dashboard">Dashboard</a></li>
                        <li><a href="/receiving">Receiving</a></li>
                        <li><a href="/inventory">Inventory</a></li>
                        <li><a href="/orders">Orders</a></li>
                        {user.role === 'ADMIN' && <li><a href="/admin">Admin</a></li>}
                        <li><button onClick={logout}>Logout ({user.username})</button></li>
                    </ul>
                </nav>
            )}
            <div className="container">
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
                    <Route path="/receiving" element={<PrivateRoute><Receiving /></PrivateRoute>} />
                    <Route path="/" element={<Navigate to="/dashboard" />} />
                </Routes>
            </div>
        </div>
    );
}

function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <AppContent />
            </AuthProvider>
        </BrowserRouter>
    );
}

export default App;