import React, { useState, useEffect } from 'react';
import { orderAPI, inventoryAPI } from '../services/api';
import '../styles/Picking.css';

const Picking = () => {
    const [pickingTasks, setPickingTasks] = useState([]);
    const [currentTask, setCurrentTask] = useState(null);
    const [scannedBin, setScannedBin] = useState('');
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState({ type: '', text: '' });

    useEffect(() => {
        loadPickingTasks();
    }, []);

    const loadPickingTasks = async () => {
        setLoading(true);
        try {
            // Get all orders in PICKING state
            const response = await orderAPI.getAll();
            const pickingOrders = response.data.filter(order => order.state === 'PICKING');
            
            // Extract items to pick from orders
            const tasks = [];
            for (const order of pickingOrders) {
                for (const line of order.orderLines || []) {
                    // Find bin location for this product
                    let binCode = 'Unknown';
                    try {
                        const invResponse = await inventoryAPI.getByProduct(line.productSku);
                        if (invResponse.data && invResponse.data.length > 0) {
                            binCode = invResponse.data[0].storageBin?.binCode || 'Unknown';
                        }
                    } catch (err) {
                        console.error('Failed to get bin for product:', line.productSku);
                    }
                    
                    tasks.push({
                        orderNumber: order.orderNumber,
                        productSku: line.productSku,
                        productName: line.productName,
                        quantity: line.quantity,
                        binCode: binCode
                    });
                }
            }
            
            setPickingTasks(tasks);
            if (tasks.length > 0) {
                setCurrentTask(tasks[0]);
            }
        } catch (error) {
            console.error('Failed to load picking tasks:', error);
            setMessage({ type: 'error', text: 'Failed to load picking tasks' });
        } finally {
            setLoading(false);
        }
    };

    const handleConfirmPick = async () => {
        if (!currentTask) return;
        
        // Validate bin scan
        if (scannedBin !== currentTask.binCode) {
            setMessage({ type: 'error', text: `Wrong bin! Go to bin: ${currentTask.binCode}` });
            return;
        }
        
        setLoading(true);
        try {
            // Update order state to PACKED (stock will decrease)
            await orderAPI.updateState(currentTask.orderNumber, 'PACKED');
            setMessage({ type: 'success', text: '✓ Items picked successfully!' });
            
            // Remove completed task
            const remainingTasks = pickingTasks.filter(t => t.orderNumber !== currentTask.orderNumber);
            setPickingTasks(remainingTasks);
            setScannedBin('');
            
            if (remainingTasks.length > 0) {
                setCurrentTask(remainingTasks[0]);
                setMessage({ type: '', text: '' });
            } else {
                setCurrentTask(null);
                setMessage({ type: 'success', text: 'All picking tasks completed!' });
            }
        } catch (error) {
            console.error('Failed to complete pick:', error);
            setMessage({ type: 'error', text: error.response?.data?.message || 'Failed to complete pick' });
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div className="loading">Loading picking tasks...</div>;

    return (
        <div className="picking">
            <h2>🎯 Picking Dashboard</h2>
            
            {!currentTask ? (
                <div className="no-tasks-card">
                    <div className="no-tasks-icon">📭</div>
                    <h3>No Active Picking Tasks</h3>
                    <p>Orders in "PICKING" state will appear here</p>
                    <p style={{ fontSize: '12px', marginTop: '10px', color: '#666' }}>
                        Move an order to PICKING from the Orders page
                    </p>
                </div>
            ) : (
                <>
                    <div className="picking-card">
                        <div className="task-header">
                            <h3>Current Picking Task</h3>
                            <span className="order-ref">Order: {currentTask.orderNumber}</span>
                        </div>
                        
                        <div className="task-details">
                            <div className="product-info">
                                <h4>{currentTask.productName}</h4>
                                <p className="sku">SKU: {currentTask.productSku}</p>
                                <p className="quantity">
                                    Quantity to Pick: <strong>{currentTask.quantity}</strong>
                                </p>
                            </div>
                            
                            <div className="location-info">
                                <h4>📍 Go to Bin:</h4>
                                <div className="bin-code">{currentTask.binCode}</div>
                                <p className="instruction">Scan the bin barcode to confirm location</p>
                            </div>
                        </div>
                        
                        <div className="scan-section">
                            <div className="scan-form">
                                <input
                                    type="text"
                                    className="scan-input"
                                    placeholder="Scan bin barcode..."
                                    value={scannedBin}
                                    onChange={(e) => setScannedBin(e.target.value)}
                                    onKeyPress={(e) => {
                                        if (e.key === 'Enter') {
                                            handleConfirmPick();
                                        }
                                    }}
                                    autoFocus
                                />
                                <button onClick={handleConfirmPick} disabled={loading}>
                                    {loading ? 'Processing...' : 'Confirm Pick'}
                                </button>
                            </div>
                        </div>
                        
                        {message.text && (
                            <div className={message.type === 'success' ? 'success-message' : 'error-message'}>
                                {message.text}
                            </div>
                        )}
                    </div>
                    
                    {pickingTasks.length > 1 && (
                        <div className="pending-tasks">
                            <h4>Pending Tasks: {pickingTasks.length - 1}</h4>
                            <ul>
                                {pickingTasks.slice(1).map((task, idx) => (
                                    <li key={idx}>
                                        <span>{task.productName}</span>
                                        <span>{task.quantity} units</span>
                                        <span className="pending-badge">Bin: {task.binCode}</span>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default Picking;