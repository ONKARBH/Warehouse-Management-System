import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import Receiving from './components/Receiving';
import Inventory from './components/Inventory';
import Orders from './components/Orders';
import Picking from './components/Picking';
import Navbar from './components/Navbar';
import Admin from './components/Admin';
import './styles/global.css';
import './styles/global.css';
import './styles/Inventory.css';
import './styles/Orders.css';
import './styles/Receiving.css';
import './styles/Picking.css';
import './styles/Navbar.css';
import './styles/Dashboard.css';
import './styles/Login.css';
const PrivateRoute = ({ children }) => {
    const { user, loading } = useAuth();
    if (loading) return <div className="loading">Loading...</div>;
    return user ? children : <Navigate to="/login" />;
};

function AppContent() {
    const { user } = useAuth();

    return (
        <div className="app">
            {user && <Navbar />}
            <div className="container">
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/admin" element={
                        <PrivateRoute>
                                 <Admin />
                              
                        </PrivateRoute>
                    } />
                    <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
                    <Route path="/receiving" element={<PrivateRoute><Receiving /></PrivateRoute>} />
                    <Route path="/inventory" element={<PrivateRoute><Inventory /></PrivateRoute>} />
                    <Route path="/orders" element={<PrivateRoute><Orders /></PrivateRoute>} />
                    <Route path="/picking" element={<PrivateRoute><Picking /></PrivateRoute>} />
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