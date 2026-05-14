import React, { useState, useEffect } from 'react';
import { productAPI, warehouseAPI, binAPI, userAPI, aisleAPI } from '../services/api';
import '../styles/Admin.css';

const Admin = () => {
    const [activeTab, setActiveTab] = useState('products');
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' });

    // Product States
    const [products, setProducts] = useState([]);
    const [newProduct, setNewProduct] = useState({
        sku: '',
        name: '',
        description: '',
        category: '',
        price: '',
        weight: ''
    });
    const [editingProduct, setEditingProduct] = useState(null);

    // Warehouse States
    const [warehouses, setWarehouses] = useState([]);
    const [newWarehouse, setNewWarehouse] = useState({
        name: '',
        code: '',
        address: '',
        city: '',
        country: ''
    });

    // Bin States
    const [bins, setBins] = useState([]);
    const [aisles, setAisles] = useState([]);
    const [newBin, setNewBin] = useState({
        binCode: '',
        maxCapacity: '',
        shelfLevel: '',
        aisleId: ''
    });

    // User States
    const [users, setUsers] = useState([]);
    const [newUser, setNewUser] = useState({
        username: '',
        password: '',
        email: '',
        fullName: '',
        role: 'OPERATOR'
    });

    // Fetch data on tab change
    useEffect(() => {
        if (activeTab === 'products') {
            fetchProducts();
        }
        if (activeTab === 'warehouses') {
            fetchWarehouses();
        }
        if (activeTab === 'bins') {
            fetchBins();
            fetchAisles();  // IMPORTANT: Fetch aisles when bin tab opens
        }
        if (activeTab === 'users') {
            fetchUsers();
        }
    }, [activeTab]);

    // ==================== PRODUCT FUNCTIONS ====================
    const fetchProducts = async () => {
        setLoading(true);
        try {
            const response = await productAPI.getAll();
            setProducts(response.data || []);
        } catch (error) {
            showMessage('error', 'Failed to load products');
        } finally {
            setLoading(false);
        }
    };

    const handleCreateProduct = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            await productAPI.create(newProduct);
            showMessage('success', 'Product created successfully!');
            setNewProduct({ sku: '', name: '', description: '', category: '', price: '', weight: '' });
            await fetchProducts();
        } catch (error) {
            showMessage('error', error.response?.data?.message || 'Failed to create product');
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateProduct = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            await productAPI.update(editingProduct.id, editingProduct);
            showMessage('success', 'Product updated successfully!');
            setEditingProduct(null);
            await fetchProducts();
        } catch (error) {
            showMessage('error', 'Failed to update product');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteProduct = async (id) => {
        if (!window.confirm('Are you sure you want to delete this product?')) return;
        setLoading(true);
        try {
            await productAPI.delete(id);
            showMessage('success', 'Product deleted successfully!');
            await fetchProducts();
        } catch (error) {
            showMessage('error', error.response?.data?.message || 'Failed to delete product');
        } finally {
            setLoading(false);
        }
    };

    // ==================== WAREHOUSE FUNCTIONS ====================
    const fetchWarehouses = async () => {
        setLoading(true);
        try {
            const response = await warehouseAPI.getAll();
            console.log("✅ Warehouses loaded:", response.data);
            setWarehouses(response.data || []);
        } catch (error) {
            console.error("Failed to load warehouses:", error);
            showMessage('error', 'Failed to load warehouses');
        } finally {
            setLoading(false);
        }
    };

    const handleCreateWarehouse = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await warehouseAPI.create(newWarehouse);
            console.log("✅ Warehouse created:", response.data);
            showMessage('success', 'Warehouse created successfully!');
            setNewWarehouse({ name: '', code: '', address: '', city: '', country: '' });
            await fetchWarehouses();
        } catch (error) {
            console.error("Create warehouse error:", error.response?.data);
            showMessage('error', error.response?.data?.message || 'Failed to create warehouse');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteWarehouse = async (id) => {
        if (!window.confirm('Delete this warehouse?')) return;
        setLoading(true);
        try {
            await warehouseAPI.delete(id);
            showMessage('success', 'Warehouse deleted!');
            await fetchWarehouses();
        } catch (error) {
            showMessage('error', 'Failed to delete warehouse');
        } finally {
            setLoading(false);
        }
    };

   // ==================== BIN FUNCTIONS (FIXED) ====================
const fetchBins = async () => {
    setLoading(true);
    try {
        const response = await binAPI.getAll();
        console.log("✅ Bins loaded:", response.data);
        setBins(response.data || []);
    } catch (error) {
        console.error("Failed to load bins:", error);
        showMessage('error', 'Failed to load bins');
    } finally {
        setLoading(false);
    }
};

    const fetchAisles = async () => {
        try {
            const response = await aisleAPI.getAll();
            console.log("✅ Aisles loaded:", response.data);
            setAisles(response.data || []);
        } catch (error) {
            console.error("Failed to load aisles:", error);
        }
    };

    const handleCreateBin = async (e) => {
        e.preventDefault();
        
        if (!newBin.binCode) {
            showMessage('error', 'Bin Code is required');
            return;
        }
        if (!newBin.maxCapacity) {
            showMessage('error', 'Max Capacity is required');
            return;
        }
        if (!newBin.aisleId) {
            showMessage('error', 'Please select an Aisle');
            return;
        }
        
        setLoading(true);
        try {
            const binData = {
                binCode: newBin.binCode.toUpperCase(),
                maxCapacity: parseInt(newBin.maxCapacity),
                currentOccupancy: 0,
                shelfLevel: parseInt(newBin.shelfLevel) || 1,
                aisle: { id: parseInt(newBin.aisleId) }
            };
            
            console.log("Creating bin:", binData);
            await binAPI.create(binData);
            showMessage('success', `Bin "${newBin.binCode}" created!`);
            setNewBin({ binCode: '', maxCapacity: '', shelfLevel: '', aisleId: '' });
            await fetchBins();
            await fetchAisles();
        } catch (error) {
            console.error("Create bin error:", error.response?.data);
            if (error.response?.status === 409) {
                showMessage('error', `Bin code "${newBin.binCode}" already exists!`);
            } else {
                showMessage('error', error.response?.data?.message || 'Failed to create bin');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteBin = async (id) => {
        if (!window.confirm('Delete this bin?')) return;
        setLoading(true);
        try {
            await binAPI.delete(id);
            showMessage('success', 'Bin deleted!');
            await fetchBins();
        } catch (error) {
            showMessage('error', 'Failed to delete bin');
        } finally {
            setLoading(false);
        }
    };

    // ==================== USER FUNCTIONS ====================
    const fetchUsers = async () => {
        setLoading(true);
        try {
            const response = await userAPI.getAll();
            console.log("✅ Users loaded:", response.data);
            setUsers(response.data || []);
        } catch (error) {
            console.error("Failed to load users:", error);
            showMessage('error', 'Failed to load users');
        } finally {
            setLoading(false);
        }
    };

    const handleCreateUser = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const userData = {
                username: newUser.username,
                password: newUser.password,
                email: newUser.email,
                fullName: newUser.fullName,
                role: newUser.role
            };
            await userAPI.create(userData);
            showMessage('success', 'User created successfully!');
            setNewUser({ username: '', password: '', email: '', fullName: '', role: 'OPERATOR' });
            await fetchUsers();
        } catch (error) {
            console.error("Create user error:", error.response?.data);
            if (error.response?.status === 403) {
                showMessage('error', 'Only Admin can create users.');
            } else {
                showMessage('error', error.response?.data?.message || 'Failed to create user');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteUser = async (id) => {
        if (!window.confirm('Delete this user?')) return;
        setLoading(true);
        try {
            await userAPI.delete(id);
            showMessage('success', 'User deleted!');
            await fetchUsers();
        } catch (error) {
            showMessage('error', 'Failed to delete user');
        } finally {
            setLoading(false);
        }
    };

    const showMessage = (type, text) => {
        setMessage({ type, text });
        setTimeout(() => setMessage({ type: '', text: '' }), 3000);
    };

    // ==================== RENDER FUNCTIONS ====================
    const renderProductForm = () => (
        <form onSubmit={editingProduct ? handleUpdateProduct : handleCreateProduct} className="admin-form">
            <h3>{editingProduct ? 'Edit Product' : 'Add New Product'}</h3>
            <div className="form-grid">
                <input type="text" placeholder="SKU *" value={editingProduct ? editingProduct.sku : newProduct.sku}
                    onChange={(e) => editingProduct ? setEditingProduct({...editingProduct, sku: e.target.value}) : setNewProduct({...newProduct, sku: e.target.value})} required />
                <input type="text" placeholder="Product Name *" value={editingProduct ? editingProduct.name : newProduct.name}
                    onChange={(e) => editingProduct ? setEditingProduct({...editingProduct, name: e.target.value}) : setNewProduct({...newProduct, name: e.target.value})} required />
                <input type="text" placeholder="Category" value={editingProduct ? editingProduct.category : newProduct.category}
                    onChange={(e) => editingProduct ? setEditingProduct({...editingProduct, category: e.target.value}) : setNewProduct({...newProduct, category: e.target.value})} />
                <input type="number" step="0.01" placeholder="Price" value={editingProduct ? editingProduct.price : newProduct.price}
                    onChange={(e) => editingProduct ? setEditingProduct({...editingProduct, price: e.target.value}) : setNewProduct({...newProduct, price: e.target.value})} />
                <input type="number" step="0.1" placeholder="Weight (kg)" value={editingProduct ? editingProduct.weight : newProduct.weight}
                    onChange={(e) => editingProduct ? setEditingProduct({...editingProduct, weight: e.target.value}) : setNewProduct({...newProduct, weight: e.target.value})} />
                <textarea placeholder="Description" value={editingProduct ? editingProduct.description : newProduct.description}
                    onChange={(e) => editingProduct ? setEditingProduct({...editingProduct, description: e.target.value}) : setNewProduct({...newProduct, description: e.target.value})} rows="2" />
            </div>
            <div className="form-buttons">
                <button type="submit" disabled={loading}>{loading ? 'Saving...' : (editingProduct ? 'Update' : 'Create')}</button>
                {editingProduct && <button type="button" onClick={() => setEditingProduct(null)} className="cancel-btn">Cancel</button>}
            </div>
        </form>
    );

    const renderProductsList = () => (
        <div className="data-table">
            <h3>📋 Products List</h3>
            <table>
                <thead><tr><th>SKU</th><th>Name</th><th>Category</th><th>Price</th><th>Actions</th></tr></thead>
                <tbody>
                    {products.length === 0 ? <tr><td colSpan="5" className="no-data">No products</td></tr> :
                        products.map(product => (
                            <tr key={product.id}>
                                <td>{product.sku}</td><td>{product.name}</td><td>{product.category || '-'}</td><td>₹{product.price}</td>
                                <td><button className="edit-btn" onClick={() => setEditingProduct(product)}>✏️</button>
                                    <button className="delete-btn" onClick={() => handleDeleteProduct(product.id)}>🗑️</button></td>
                            </tr>
                        ))
                    }
                </tbody>
            </table>
        </div>
    );

    const renderWarehouseForm = () => (
        <form onSubmit={handleCreateWarehouse} className="admin-form">
            <h3>🏭 Add New Warehouse</h3>
            <div className="form-grid">
                <input type="text" placeholder="Warehouse Name *" value={newWarehouse.name} onChange={(e) => setNewWarehouse({...newWarehouse, name: e.target.value})} required />
                <input type="text" placeholder="Warehouse Code *" value={newWarehouse.code} onChange={(e) => setNewWarehouse({...newWarehouse, code: e.target.value})} required />
                <input type="text" placeholder="Address" value={newWarehouse.address} onChange={(e) => setNewWarehouse({...newWarehouse, address: e.target.value})} />
                <input type="text" placeholder="City" value={newWarehouse.city} onChange={(e) => setNewWarehouse({...newWarehouse, city: e.target.value})} />
                <input type="text" placeholder="Country" value={newWarehouse.country} onChange={(e) => setNewWarehouse({...newWarehouse, country: e.target.value})} />
            </div>
            <button type="submit" disabled={loading}>{loading ? 'Creating...' : 'Create Warehouse'}</button>
        </form>
    );

    const renderWarehousesList = () => (
        <div className="data-table">
            <h3>📋 Warehouses List</h3>
            <table>
                <thead><tr><th>Code</th><th>Name</th><th>City</th><th>Country</th><th>Actions</th></tr></thead>
                <tbody>
                    {warehouses.length === 0 ? <tr><td colSpan="5" className="no-data">No warehouses</td></tr> :
                        warehouses.map(warehouse => (
                            <tr key={warehouse.id}>
                                <td>{warehouse.code}</td><td>{warehouse.name}</td><td>{warehouse.city || '-'}</td><td>{warehouse.country || '-'}</td>
                                <td><button className="delete-btn" onClick={() => handleDeleteWarehouse(warehouse.id)}>🗑️</button></td>
                            </tr>
                        ))
                    }
                </tbody>
            </table>
        </div>
    );

    const renderBinForm = () => (
        <form onSubmit={handleCreateBin} className="admin-form">
            <h3>📦 Add New Storage Bin</h3>
            {aisles.length === 0 && (
                <div style={{ background: '#fff3cd', padding: '10px', borderRadius: '8px', marginBottom: '15px', color: '#856404' }}>
                    ⚠️ No aisles found! Create an aisle first via Postman: POST /api/aisles with {`{"aisleNumber":"Aisle 1","zone":{"id":1}}`}
                </div>
            )}
            {aisles.length > 0 && (
                <div style={{ background: '#d4edda', padding: '8px', borderRadius: '8px', marginBottom: '15px', fontSize: '12px', color: '#155724' }}>
                    ✅ {aisles.length} aisle(s) available
                </div>
            )}
            <div className="form-grid">
                <input type="text" placeholder="Bin Code (e.g., A-12-01) *" value={newBin.binCode} onChange={(e) => setNewBin({...newBin, binCode: e.target.value.toUpperCase()})} required />
                <input type="number" placeholder="Max Capacity *" value={newBin.maxCapacity} onChange={(e) => setNewBin({...newBin, maxCapacity: e.target.value})} required />
                <input type="number" placeholder="Shelf Level" value={newBin.shelfLevel} onChange={(e) => setNewBin({...newBin, shelfLevel: e.target.value})} />
                <select value={newBin.aisleId} onChange={(e) => setNewBin({...newBin, aisleId: e.target.value})} required>
                    <option value="">-- Select Aisle --</option>
                    {aisles.map((aisle) => (<option key={aisle.id} value={aisle.id}>{aisle.aisleNumber} (ID: {aisle.id})</option>))}
                </select>
            </div>
            <button type="submit" disabled={loading || aisles.length === 0}>{loading ? 'Creating...' : 'Create Bin'}</button>
        </form>
    );

    const renderBinsList = () => (
        <div className="data-table">
            <h3>📋 Bins List</h3>
            <table>
                <thead><tr><th>Bin Code</th><th>Max Capacity</th><th>Current Occupancy</th><th>Shelf Level</th><th>Actions</th></tr></thead>
                <tbody>
                    {bins.length === 0 ? <tr><td colSpan="5" className="no-data">No bins</td></tr> :
                        bins.map(bin => (
                            <tr key={bin.id}>
                                <td>{bin.binCode}</td><td>{bin.maxCapacity}</td><td>{bin.currentOccupancy || 0}</td><td>{bin.shelfLevel || '-'}</td>
                                <td><button className="delete-btn" onClick={() => handleDeleteBin(bin.id)}>🗑️</button></td>
                            </tr>
                        ))
                    }
                </tbody>
            </table>
        </div>
    );

    const renderUserForm = () => (
        <form onSubmit={handleCreateUser} className="admin-form">
            <h3>👥 Add New User</h3>
            <div className="form-grid">
                <input type="text" placeholder="Username *" value={newUser.username} onChange={(e) => setNewUser({...newUser, username: e.target.value})} required />
                <input type="password" placeholder="Password *" value={newUser.password} onChange={(e) => setNewUser({...newUser, password: e.target.value})} required />
                <input type="email" placeholder="Email *" value={newUser.email} onChange={(e) => setNewUser({...newUser, email: e.target.value})} required />
                <input type="text" placeholder="Full Name" value={newUser.fullName} onChange={(e) => setNewUser({...newUser, fullName: e.target.value})} />
                <select value={newUser.role} onChange={(e) => setNewUser({...newUser, role: e.target.value})}>
                    <option value="OPERATOR">Operator</option>
                    <option value="ADMIN">Admin</option>
                </select>
            </div>
            <button type="submit" disabled={loading}>{loading ? 'Creating...' : 'Create User'}</button>
        </form>
    );

    const renderUsersList = () => (
        <div className="data-table">
            <h3>👥 Users List</h3>
            <table>
                <thead><tr><th>Username</th><th>Email</th><th>Full Name</th><th>Role</th><th>Actions</th></tr></thead>
                <tbody>
                    {users.length === 0 ? <tr><td colSpan="5" className="no-data">No users</td></tr> :
                        users.map(user => (
                            <tr key={user.id}>
                                <td>{user.username}</td><td>{user.email}</td><td>{user.fullName || '-'}</td>
                                <td><span className={`role-badge role-${user.role?.toLowerCase()}`}>{user.role}</span></td>
                                <td>{user.username !== 'admin' && <button className="delete-btn" onClick={() => handleDeleteUser(user.id)}>🗑️</button>}</td>
                            </tr>
                        ))
                    }
                </tbody>
            </table>
        </div>
    );

    // ==================== MAIN RENDER ====================
    return (
        <div className="admin-panel">
            <h1>⚙️ Admin Control Panel</h1>
            {message.text && <div className={`message ${message.type}`}>{message.type === 'success' ? '✅' : '❌'} {message.text}</div>}
            
            <div className="admin-tabs">
                <button className={activeTab === 'products' ? 'active' : ''} onClick={() => setActiveTab('products')}>📦 Products</button>
                <button className={activeTab === 'warehouses' ? 'active' : ''} onClick={() => setActiveTab('warehouses')}>🏭 Warehouses</button>
                <button className={activeTab === 'bins' ? 'active' : ''} onClick={() => setActiveTab('bins')}>📍 Storage Bins</button>
                <button className={activeTab === 'users' ? 'active' : ''} onClick={() => setActiveTab('users')}>👥 Users</button>
            </div>

            <div className="admin-content">
                {activeTab === 'products' && (<div className="tab-content">{renderProductForm()}{renderProductsList()}</div>)}
                {activeTab === 'warehouses' && (<div className="tab-content">{renderWarehouseForm()}{renderWarehousesList()}</div>)}
                {activeTab === 'bins' && (<div className="tab-content">{renderBinForm()}{renderBinsList()}</div>)}
                {activeTab === 'users' && (<div className="tab-content">{renderUserForm()}{renderUsersList()}</div>)}
            </div>
        </div>
    );
};

export default Admin;