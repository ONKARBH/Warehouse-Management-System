import React, { createContext, useState, useContext, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');
        const userData = localStorage.getItem('user');
        if (token && userData) {
            setUser(JSON.parse(userData));
        }
        setLoading(false);
    }, []);

    const login = async (username, password) => {
        const response = await authAPI.login(username, password);
        const { token, username: userName, role, fullName } = response.data;
        
        localStorage.setItem('token', token);
        const userObj = { username: userName, role, fullName };
        localStorage.setItem('user', JSON.stringify(userObj));
        setUser(userObj);
        
        return response;
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setUser(null);
    };

    const isAdmin = () => user?.role === 'ADMIN';
    const isOperator = () => user?.role === 'OPERATOR';

    return (
        <AuthContext.Provider value={{ user, login, logout, isAdmin, isOperator, loading }}>
            {children}
        </AuthContext.Provider>
    );
};