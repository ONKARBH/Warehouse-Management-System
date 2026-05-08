import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import '../styles/Navbar.css';

const Navbar = () => {
    const { user, logout } = useAuth();
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

    const navLinks = [
        { path: '/dashboard', name: 'Dashboard', icon: '📊' },
        { path: '/receiving', name: 'Receiving', icon: '📦' },
        { path: '/inventory', name: 'Inventory', icon: '📋' },
        { path: '/orders', name: 'Orders', icon: '🛒' },
        { path: '/picking', name: 'Picking', icon: '🎯' },
    ];

    if (user?.role === 'ADMIN') {
        navLinks.push({ path: '/admin', name: 'Admin', icon: '⚙️' });
    }

    return (
        <nav className="navbar">
            <div className="nav-container">
                <div className="nav-logo">
                    <div className="logo-icon">🏭</div>
                    <div className="logo-text">
                        <span className="logo-title">WMS</span>
                        <span className="logo-subtitle">Warehouse Management</span>
                    </div>
                </div>

                <div className={`nav-links ${mobileMenuOpen ? 'active' : ''}`}>
                    {navLinks.map((link, index) => (
                        <a 
                            key={index} 
                            href={link.path} 
                            className="nav-link"
                            style={{ animationDelay: `${index * 0.05}s` }}
                        >
                            <span className="nav-icon">{link.icon}</span>
                            <span className="nav-name">{link.name}</span>
                        </a>
                    ))}
                </div>

                <div className="nav-user">
                    <div className="user-info">
                        <div className="user-avatar">
                            {user?.fullName?.charAt(0) || user?.username?.charAt(0)}
                        </div>
                        <div className="user-details">
                            <span className="user-name">{user?.fullName || user?.username}</span>
                            <span className={`user-role role-${user?.role?.toLowerCase()}`}>
                                {user?.role}
                            </span>
                        </div>
                    </div>
                    <button onClick={logout} className="logout-btn">
                        <span>🚪</span>
                        <span>Logout</span>
                    </button>
                </div>

                <button 
                    className="mobile-menu-btn" 
                    onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                >
                    ☰
                </button>
            </div>
        </nav>
    );
};

export default Navbar;