import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { inventoryAPI, orderAPI, productAPI, receivingAPI } from '../services/api';
import '../style/Dashboard.css';

const Dashboard = () => {
    const { user } = useAuth();
    const [stats, setStats] = useState({
        totalProducts: 0,
        totalInventory: 0,
        pendingOrders: 0,
        lowStockItems: 0,
        totalBins: 0,
        recentActivities: []
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadDashboardData();
    }, []);

    const loadDashboardData = async () => {
        try {
            const [products, inventory, orders, bins] = await Promise.all([
                productAPI.getAll(),
                inventoryAPI.getAll(),
                orderAPI.getAll(),
                receivingAPI.getBins?.() || Promise.resolve({ data: [] })
            ]);

            const inventoryData = inventory.data || [];
            const ordersData = orders.data || [];

            setStats({
                totalProducts: products.data?.length || 0,
                totalInventory: inventoryData.reduce((sum, item) => sum + (item.quantity || 0), 0),
                pendingOrders: ordersData.filter(o => o.state === 'PENDING').length,
                lowStockItems: inventoryData.filter(item => (item.quantity || 0) < 10).length,
                totalBins: bins.data?.length || 0,
                recentActivities: ordersData.slice(0, 5)
            });
        } catch (error) {
            console.error('Error loading dashboard:', error);
        } finally {
            setLoading(false);
        }
    };

    const statCards = [
        { title: 'Total Products', value: stats.totalProducts, icon: '📦', color: '#667eea', bg: 'rgba(102,126,234,0.1)' },
        { title: 'Inventory Units', value: stats.totalInventory, icon: '📊', color: '#4facfe', bg: 'rgba(79,172,254,0.1)' },
        { title: 'Pending Orders', value: stats.pendingOrders, icon: '🛒', color: '#f093fb', bg: 'rgba(240,147,251,0.1)' },
        { title: 'Low Stock', value: stats.lowStockItems, icon: '⚠️', color: '#f5576c', bg: 'rgba(245,87,108,0.1)' },
    ];

    if (loading) return <div className="loading">Loading dashboard...</div>;

    return (
        <div className="dashboard">
            <div className="welcome-section">
                <h1 className="welcome-title">
                    Welcome back, <span className="user-name">{user?.fullName || user?.username}</span>
                </h1>
                <p className="welcome-subtitle">Here's what's happening in your warehouse today</p>
            </div>

            <div className="stats-grid">
                {statCards.map((card, index) => (
                    <div key={index} className="stat-card" style={{ animationDelay: `${index * 0.1}s` }}>
                        <div className="stat-icon" style={{ background: card.bg }}>
                            {card.icon}
                        </div>
                        <div className="stat-info">
                            <h3>{card.title}</h3>
                            <p className="stat-value" style={{ color: card.color }}>
                                {card.value.toLocaleString()}
                            </p>
                        </div>
                    </div>
                ))}
            </div>

            {user?.role === 'ADMIN' && (
                <div className="admin-panel">
                    <h2>⚙️ Admin Controls</h2>
                    <div className="admin-buttons">
                        <button className="admin-btn">➕ Add Product</button>
                        <button className="admin-btn">🏭 Create Warehouse</button>
                        <button className="admin-btn">📦 Add Bin</button>
                        <button className="admin-btn">👥 Manage Users</button>
                    </div>
                </div>
            )}

            <div className="recent-section">
                <div className="recent-header">
                    <h2>🕐 Recent Orders</h2>
                    <a href="/orders" className="view-all">View All →</a>
                </div>
                <div className="recent-table">
                    <table>
                        <thead>
                            <tr>
                                <th>Order ID</th>
                                <th>Customer</th>
                                <th>Status</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            {stats.recentActivities.length === 0 ? (
                                <tr>
                                    <td colSpan="4" className="no-data">No recent orders</td>
                                </tr>
                            ) : (
                                stats.recentActivities.map((order, idx) => (
                                    <tr key={idx}>
                                        <td>{order.orderNumber}</td>
                                        <td>{order.customerName}</td>
                                        <td>
                                            <span className={`status-badge status-${order.state?.toLowerCase()}`}>
                                                {order.state}
                                            </span>
                                        </td>
                                        <td>{new Date(order.createdAt).toLocaleDateString()}</td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;