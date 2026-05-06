import React, { useState, useEffect } from 'react';
import { inventoryAPI, productAPI } from '../services/api';

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
            setInventory(response.data);
            setError(null);
        } catch (err) {
            setError('Failed to load inventory');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const loadProducts = async () => {
        try {
            const response = await productAPI.getAll();
            setProducts(response.data);
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
            setInventory(response.data);
        } catch (err) {
            setError('Product not found');
        } finally {
            setLoading(false);
        }
    };

    const getTotalStock = () => {
        return inventory.reduce((sum, item) => sum + item.quantity, 0);
    };

    const getLowStockItems = () => {
        return inventory.filter(item => item.quantity < 10);
    };

    if (loading) return <div className="loading">Loading inventory...</div>;

    return (
        <div className="inventory">
            <h2>Inventory Management</h2>
            
            <div className="stats-cards">
                <div className="stat-card">
                    <h3>Total Products</h3>
                    <p>{products.length}</p>
                </div>
                <div className="stat-card">
                    <h3>Total Units</h3>
                    <p>{getTotalStock()}</p>
                </div>
                <div className="stat-card warning">
                    <h3>Low Stock Items</h3>
                    <p>{getLowStockItems().length}</p>
                </div>
            </div>

            <div className="search-bar">
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
                <button onClick={searchByProduct}>Search</button>
                <button onClick={loadInventory}>Reset</button>
            </div>

            {error && <div className="error">{error}</div>}

            <table className="inventory-table">
                <thead>
                    <tr>
                        <th>Product SKU</th>
                        <th>Product Name</th>
                        <th>Bin Location</th>
                        <th>Quantity</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    {inventory.length === 0 ? (
                        <tr>
                            <td colSpan="5" className="no-data">No inventory found</td>
                        </tr>
                    ) : (
                        inventory.map((item, index) => (
                            <tr key={index} className={item.quantity < 10 ? 'low-stock' : ''}>
                                <td>{item.product?.sku}</td>
                                <td>{item.product?.name}</td>
                                <td>{item.storageBin?.binCode}</td>
                                <td>{item.quantity}</td>
                                <td>
                                    {item.quantity <= 0 ? (
                                        <span className="badge out">Out of Stock</span>
                                    ) : item.quantity < 10 ? (
                                        <span className="badge low">Low Stock</span>
                                    ) : (
                                        <span className="badge ok">In Stock</span>
                                    )}
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default Inventory;