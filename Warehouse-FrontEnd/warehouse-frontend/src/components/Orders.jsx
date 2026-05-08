import React, { useState, useEffect } from 'react';
import { orderAPI, productAPI } from '../services/api';
import '../styles/Orders.css';

const Orders = () => {
    const [orders, setOrders] = useState([]);
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showCreateForm, setShowCreateForm] = useState(false);
    const [newOrder, setNewOrder] = useState({
        customerName: '',
        customerEmail: '',
        shippingAddress: '',
        orderLines: [{ productSku: '', quantity: 1 }]
    });

    useEffect(() => {
        loadOrders();
        loadProducts();
    }, []);

    const loadOrders = async () => {
        try {
            setLoading(true);
            const response = await orderAPI.getAll();
            setOrders(response.data);
        } catch (err) {
            console.error('Failed to load orders', err);
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

    const handleCreateOrder = async (e) => {
        e.preventDefault();
        try {
            await orderAPI.create(newOrder);
            setShowCreateForm(false);
            setNewOrder({
                customerName: '',
                customerEmail: '',
                shippingAddress: '',
                orderLines: [{ productSku: '', quantity: 1 }]
            });
            loadOrders();
            alert('✅ Order created successfully!');
        } catch (err) {
            alert('❌ Failed to create order: ' + (err.response?.data?.message || err.message));
        }
    };

    const handleStateChange = async (orderNumber, newState) => {
        try {
            await orderAPI.updateState(orderNumber, newState);
            loadOrders();
        } catch (err) {
            alert('❌ Failed to update order state: ' + (err.response?.data?.message || err.message));
        }
    };

    const addOrderLine = () => {
        setNewOrder({
            ...newOrder,
            orderLines: [...newOrder.orderLines, { productSku: '', quantity: 1 }]
        });
    };

    const updateOrderLine = (index, field, value) => {
        const lines = [...newOrder.orderLines];
        lines[index][field] = value;
        setNewOrder({ ...newOrder, orderLines: lines });
    };

    const removeOrderLine = (index) => {
        const lines = newOrder.orderLines.filter((_, i) => i !== index);
        setNewOrder({ ...newOrder, orderLines: lines });
    };

    const getStateColor = (state) => {
        switch(state) {
            case 'PENDING': return 'state-pending';
            case 'PICKING': return 'state-picking';
            case 'PACKED': return 'state-packed';
            case 'SHIPPED': return 'state-shipped';
            case 'CANCELLED': return 'state-cancelled';
            default: return '';
        }
    };

    const getNextStates = (currentState) => {
        switch(currentState) {
            case 'PENDING': return ['PICKING', 'CANCELLED'];
            case 'PICKING': return ['PACKED', 'CANCELLED'];
            case 'PACKED': return ['SHIPPED'];
            default: return [];
        }
    };

    const getOrderTotal = (order) => {
        return order.orderLines?.reduce((sum, line) => sum + (line.totalPrice || 0), 0) || 0;
    };

    if (loading) return <div className="loading">Loading orders...</div>;

    return (
        <div className="orders">
            <div className="orders-header">
                <h2>📋 Order Management</h2>
                <button 
                    className="create-order-btn"
                    onClick={() => setShowCreateForm(!showCreateForm)}
                >
                    {showCreateForm ? '✖ Cancel' : '+ New Order'}
                </button>
            </div>

            {showCreateForm && (
                <form className="create-order-form" onSubmit={handleCreateOrder}>
                    <div className="form-header">
                        <h3>🛒 Create New Order</h3>
                        <p>Fill in the details below</p>
                    </div>
                    
                    <div className="form-group">
                        <input
                            type="text"
                            placeholder="Customer Name *"
                            value={newOrder.customerName}
                            onChange={(e) => setNewOrder({...newOrder, customerName: e.target.value})}
                            required
                        />
                        <input
                            type="email"
                            placeholder="Customer Email *"
                            value={newOrder.customerEmail}
                            onChange={(e) => setNewOrder({...newOrder, customerEmail: e.target.value})}
                            required
                        />
                        <textarea
                            placeholder="Shipping Address *"
                            value={newOrder.shippingAddress}
                            onChange={(e) => setNewOrder({...newOrder, shippingAddress: e.target.value})}
                            required
                            rows="2"
                        />
                    </div>
                    
                    <div className="order-lines-section">
                        <h4>📦 Order Items</h4>
                        {newOrder.orderLines.map((line, index) => (
                            <div key={index} className="order-line">
                                <select
                                    value={line.productSku}
                                    onChange={(e) => updateOrderLine(index, 'productSku', e.target.value)}
                                    required
                                >
                                    <option value="">Select Product</option>
                                    {products.map(p => (
                                        <option key={p.id} value={p.sku}>
                                            {p.name} (₹{p.price})
                                        </option>
                                    ))}
                                </select>
                                <input
                                    type="number"
                                    placeholder="Qty"
                                    value={line.quantity}
                                    onChange={(e) => updateOrderLine(index, 'quantity', parseInt(e.target.value) || 1)}
                                    min="1"
                                    required
                                />
                                {newOrder.orderLines.length > 1 && (
                                    <button type="button" className="remove-line-btn" onClick={() => removeOrderLine(index)}>
                                        ✖
                                    </button>
                                )}
                            </div>
                        ))}
                        <button type="button" className="add-line-btn" onClick={addOrderLine}>
                            + Add Item
                        </button>
                    </div>
                    
                    <button type="submit" className="submit-order-btn">
                        🚀 Create Order
                    </button>
                </form>
            )}

            <div className="orders-list">
                {orders.length === 0 ? (
                    <div className="no-orders">
                        <div className="no-orders-icon">📭</div>
                        <p>No orders found</p>
                        <p className="no-orders-sub">Click "New Order" to create your first order</p>
                    </div>
                ) : (
                    orders.map((order, index) => (
                        <div key={order.id} className="order-card" style={{ animationDelay: `${index * 0.1}s` }}>
                            <div className="order-card-header">
                                <div className="order-info">
                                    <span className="order-number">#{order.orderNumber}</span>
                                    <span className={`order-status ${getStateColor(order.state)}`}>
                                        {order.state}
                                    </span>
                                </div>
                                <div className="order-date">
                                    📅 {new Date(order.createdAt).toLocaleDateString()}
                                </div>
                            </div>
                            
                            <div className="order-card-body">
                                <div className="customer-info">
                                    <div className="info-row">
                                        <span className="info-label">👤 Customer:</span>
                                        <span className="info-value">{order.customerName}</span>
                                    </div>
                                    <div className="info-row">
                                        <span className="info-label">📧 Email:</span>
                                        <span className="info-value">{order.customerEmail}</span>
                                    </div>
                                    <div className="info-row">
                                        <span className="info-label">📍 Address:</span>
                                        <span className="info-value">{order.shippingAddress}</span>
                                    </div>
                                </div>
                                
                                <div className="order-items-list">
                                    <h4>Items:</h4>
                                    {order.orderLines?.map((line, idx) => (
                                        <div key={idx} className="order-item">
                                            <span className="item-name">{line.productName}</span>
                                            <span className="item-qty">x {line.quantity}</span>
                                            <span className="item-price">₹{line.totalPrice}</span>
                                        </div>
                                    ))}
                                    <div className="order-total">
                                        <span>Total:</span>
                                        <span>₹{getOrderTotal(order)}</span>
                                    </div>
                                </div>
                            </div>
                            
                            <div className="order-card-footer">
                                {getNextStates(order.state).map(nextState => (
                                    <button
                                        key={nextState}
                                        className={`action-btn btn-${nextState.toLowerCase()}`}
                                        onClick={() => handleStateChange(order.orderNumber, nextState)}
                                    >
                                        {nextState === 'PICKING' && '🎯 Mark as PICKING'}
                                        {nextState === 'PACKED' && '📦 Mark as PACKED'}
                                        {nextState === 'SHIPPED' && '🚚 Mark as SHIPPED'}
                                        {nextState === 'CANCELLED' && '❌ Cancel Order'}
                                    </button>
                                ))}
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default Orders;