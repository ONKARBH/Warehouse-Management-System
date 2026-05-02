import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { inventoryAPI, orderAPI, productAPI } from '../services/api';

function Dashboard() {
    const { user, isAdmin } = useAuth();
    const [stats, setStats] = useState({
        totalProducts: 0,
        totalInventory: 0,
        pendingOrders: 0,
        lowStockItems: 0
    });

    useEffect(() => {
        loadDashboardData();
    }, []);

    const loadDashboardData = async () => {
        try {
            const products = await productAPI.getAll();
            const inventory = await inventoryAPI.getAll();
            const orders = await orderAPI.getAll();

            setStats({
                totalProducts: products.data.length,
                totalInventory: inventory.data.reduce((sum, item) => sum + item.quantity, 0),
                pendingOrders: orders.data.filter(o => o.state === 'PENDING').length,
                lowStockItems: inventory.data.filter(item => item.quantity < 10).length
            });
        } catch (error) {
            console.error('Error loading dashboard:', error);
        }
    };

    return (
        <div className="dashboard">
            <h1>Welcome, {user?.fullName}!</h1>
            <div className="stats-grid">
                <div className="stat-card">
                    <h3>Total Products</h3>
                    <p>{stats.totalProducts}</p>
                </div>
                <div className="stat-card">
                    <h3>Total Inventory</h3>
                    <p>{stats.totalInventory} units</p>
                </div>
                <div className="stat-card">
                    <h3>Pending Orders</h3>
                    <p>{stats.pendingOrders}</p>
                </div>
                <div className="stat-card">
                    <h3>Low Stock Items</h3>
                    <p>{stats.lowStockItems}</p>
                </div>
            </div>
            {isAdmin() && (
                <div className="admin-section">
                    <h2>Admin Controls</h2>
                    <button>Add Product</button>
                    <button>Create Warehouse</button>
                    <button>Add Bin</button>
                </div>
            )}
        </div>
    );
}

export default Dashboard;