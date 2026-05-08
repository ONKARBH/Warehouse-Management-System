import React, { useState, useEffect } from 'react';
import { orderAPI, inventoryAPI } from '../services/api';

const Picking = () => {
    const [pickingTasks, setPickingTasks] = useState([]);
    const [currentTask, setCurrentTask] = useState(null);
    const [scannedBin, setScannedBin] = useState('');
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState('');

    useEffect(() => {
        loadPickingTasks();
    }, []);

    const loadPickingTasks = async () => {
        try {
            setLoading(true);
            const response = await orderAPI.getAll();
            const pickingOrders = response.data.filter(order => order.state === 'PICKING');
            // Extract items to pick from orders
            const tasks = [];
            pickingOrders.forEach(order => {
                order.orderLines?.forEach(line => {
                    tasks.push({
                        orderNumber: order.orderNumber,
                        productSku: line.productSku,
                        productName: line.productName,
                        quantity: line.quantity,
                        binCode: null // Will be fetched from inventory
                    });
                });
            });
            
            // Get bin locations for each product
            for (let task of tasks) {
                try {
                    const invResponse = await inventoryAPI.getByProduct(task.productSku);
                    if (invResponse.data && invResponse.data.length > 0) {
                        task.binCode = invResponse.data[0].storageBin?.binCode || 'Unknown';
                    }
                } catch (err) {
                    task.binCode = 'Not found';
                }
            }
            
            setPickingTasks(tasks);
            if (tasks.length > 0) {
                setCurrentTask(tasks[0]);
            }
        } catch (err) {
            console.error('Failed to load picking tasks', err);
        } finally {
            setLoading(false);
        }
    };

    const handleBinScan = async (e) => {
        e.preventDefault();
        if (!currentTask) return;

        if (scannedBin === currentTask.binCode) {
            setMessage('✓ Bin confirmed! Pick the items.');
            // Here you would update the count, but for demo we just move to next
            setTimeout(() => {
                completeCurrentTask();
            }, 1500);
        } else {
            setMessage(`✗ Wrong bin! Go to bin: ${currentTask.binCode}`);
        }
        setScannedBin('');
    };

    const completeCurrentTask = async () => {
        try {
            // Update order state to PACKED after picking
            await orderAPI.updateState(currentTask.orderNumber, 'PACKED');
            setMessage('✓ Items picked successfully!');
            
            // Move to next task
            const remainingTasks = pickingTasks.filter(t => t.orderNumber !== currentTask.orderNumber);
            setPickingTasks(remainingTasks);
            if (remainingTasks.length > 0) {
                setCurrentTask(remainingTasks[0]);
            } else {
                setCurrentTask(null);
                setMessage('All picking tasks completed!');
            }
        } catch (err) {
            setMessage('Error completing task: ' + (err.response?.data?.message || err.message));
        }
    };

    if (loading) return <div className="loading">Loading picking tasks...</div>;

    return (
        <div className="picking">
            <h2>Picking Dashboard</h2>
            
            {!currentTask ? (
                <div className="no-tasks">
                    <p>No active picking tasks</p>
                    <p>Orders in PICKING state will appear here</p>
                </div>
            ) : (
                <div className="picking-card">
                    <div className="task-header">
                        <h3>Current Picking Task</h3>
                        <span className="order-ref">Order: {currentTask.orderNumber}</span>
                    </div>
                    
                    <div className="task-details">
                        <div className="product-info">
                            <h4>{currentTask.productName}</h4>
                            <p className="sku">SKU: {currentTask.productSku}</p>
                            <p className="quantity">Quantity to Pick: <strong>{currentTask.quantity}</strong></p>
                        </div>
                        
                        <div className="location-info">
                            <h4>📍 Go to Bin:</h4>
                            <div className="bin-code">{currentTask.binCode}</div>
                            <p className="instruction">Scan the bin barcode to confirm location</p>
                        </div>
                    </div>
                    
                    <form onSubmit={handleBinScan} className="scan-form">
                        <input
                            type="text"
                            placeholder="Scan bin barcode..."
                            value={scannedBin}
                            onChange={(e) => setScannedBin(e.target.value)}
                            autoFocus
                            className="scan-input"
                        />
                        <button type="submit">Confirm Bin</button>
                    </form>
                    
                    {message && (
                        <div className={message.includes('✓') ? 'success-message' : 'error-message'}>
                            {message}
                        </div>
                    )}
                </div>
            )}
            
            {pickingTasks.length > 1 && (
                <div className="pending-tasks">
                    <h4>Pending Tasks: {pickingTasks.length - 1}</h4>
                    <ul>
                        {pickingTasks.slice(1).map((task, idx) => (
                            <li key={idx}>
                                {task.productName} - {task.quantity} units at {task.binCode}
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
};

export default Picking;