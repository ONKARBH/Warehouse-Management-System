import React, { useState, useEffect } from 'react';
import { inventoryAPI, productAPI } from '../services/api';
import '../styles/Inventory.css';

const Inventory = () => {
    const [inventory, setInventory] = useState([]);
    const [products, setProducts] = useState([]);
    const [selectedProduct, setSelectedProduct] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        loadInventory();
        loadProducts();
    }, []);

    const loadInventory = async () => {
        try {
            setLoading(true);
            const response = await inventoryAPI.getAll();
            console.log('Inventory data:', response.data);
            setInventory(response.data || []);
            setError(null);
        } catch (err) {
            console.error('Failed to load inventory:', err);
            setError('Failed to load inventory');
        } finally {
            setLoading(false);
        }
    };

    const loadProducts = async () => {
        try {
            const response = await productAPI.getAll();
            setProducts(response.data || []);
        } catch (err) {
            console.error('Failed to load products', err);
        }
    };

    const searchByProduct = async () => {
        if (!selectedProduct) {
            loadInventory();
            return;
        }
        try {
            setLoading(true);
            const response = await inventoryAPI.getByProduct(selectedProduct);
            setInventory(response.data || []);
            setError(null);
        } catch (err) {
            setError('Product not found');
            setInventory([]);
        } finally {
            setLoading(false);
        }
    };

    const resetSearch = () => {
        setSelectedProduct('');
        loadInventory();
    };

    const getTotalStock = () => {
        return inventory.reduce((sum, item) => sum + (item.quantity || 0), 0);
    };

    const getLowStockItems = () => {
        return inventory.filter(item => (item.quantity || 0) < 10).length;
    };

    if (loading) return <div className="loading">Loading inventory...</div>;

    return (
        <div className="inventory">
            <h2>📋 Inventory Management</h2>
            
            {/* Stats Cards */}
            <div className="stats-cards">
                <div className="stat-card">
                    <div className="stat-info">
                        <h3>Total Products</h3>
                        <p>{products.length}</p>
                    </div>
                    <div className="stat-icon">📦</div>
                </div>
                <div className="stat-card">
                    <div className="stat-info">
                        <h3>Total Units</h3>
                        <p>{getTotalStock()}</p>
                    </div>
                    <div className="stat-icon">📊</div>
                </div>
                <div className="stat-card warning">
                    <div className="stat-info">
                        <h3>Low Stock Items</h3>
                        <p>{getLowStockItems()}</p>
                    </div>
                    <div className="stat-icon">⚠️</div>
                </div>
            </div>

            {/* Search Bar */}
            <div className="search-bar">
                <div className="search-group">
                    <label>Search by Product</label>
                    <select 
                        value={selectedProduct} 
                        onChange={(e) => setSelectedProduct(e.target.value)}
                    >
                        <option value="">All Products</option>
                        {products.map(product => (
                            <option key={product.id} value={product.sku}>
                                {product.name} ({product.sku})
                            </option>
                        ))}
                    </select>
                </div>
                <div className="search-buttons">
                    <button className="search-btn" onClick={searchByProduct}>🔍 Search</button>
                    <button className="reset-btn" onClick={resetSearch}>⟳ Reset</button>
                </div>
            </div>

            {error && <div className="error">{error}</div>}

            {/* Inventory Table */}
            <div className="inventory-table-container">
                {inventory.length === 0 ? (
                    <div className="no-data">
                        <p>📭 No inventory found</p>
                        <p style={{ fontSize: '12px', marginTop: '10px' }}>
                            Go to <strong>Receiving</strong> page to add stock
                        </p>
                    </div>
                ) : (
                    <table className="inventory-table">
                        <thead>
                            <tr>
                                <th>SKU</th>
                                <th>Product Name</th>
                                <th>Bin Location</th>
                                <th>Quantity</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            {inventory.map((item, index) => {
                                // SAFELY ACCESS NESTED PROPERTIES
                                const sku = item.product?.sku || '-';
                                const productName = item.product?.name || '-';
                                const binCode = item.storageBin?.binCode || '-';
                                const quantity = item.quantity || 0;
                                
                                return (
                                    <tr key={item.id || index} className={quantity < 10 ? 'low-stock' : ''}>
                                        <td className="sku-cell">{sku}</td>
                                        <td className="product-cell">{productName}</td>
                                        <td className="bin-cell">{binCode}</td>
                                        <td className="quantity-cell">{quantity}</td>
                                        <td className="status-cell">
                                            {quantity <= 0 ? (
                                                <span className="badge out">Out of Stock</span>
                                            ) : quantity < 10 ? (
                                                <span className="badge low">Low Stock</span>
                                            ) : (
                                                <span className="badge ok">In Stock</span>
                                            )}
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
};

export default Inventory;