import React, { useState, useEffect, useRef } from 'react';
import { receivingAPI, productAPI } from '../services/api';
import '../styles/Receiving.css';

const Receiving = () => {
    const [formData, setFormData] = useState({
        productSku: '',
        quantity: '',
        referenceNumber: '',
        targetBinCode: ''
    });
    const [suggestion, setSuggestion] = useState(null);
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(false);
    const [scanMode, setScanMode] = useState(false);
    const [products, setProducts] = useState([]);
    const [scanInput, setScanInput] = useState('');
    const scanInputRef = useRef(null);

    useEffect(() => {
        loadProducts();
    }, []);

    useEffect(() => {
        if (scanMode && scanInputRef.current) {
            scanInputRef.current.focus();
        }
    }, [scanMode]);

    const loadProducts = async () => {
        try {
            const response = await productAPI.getAll();
            setProducts(response.data);
        } catch (error) {
            console.error('Failed to load products', error);
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
        setSuggestion(null);
        setResult(null);
    };

    const handleSuggestBin = async () => {
        if (!formData.productSku || !formData.quantity) {
            alert('Please enter Product SKU and Quantity');
            return;
        }

        setLoading(true);
        try {
            const response = await receivingAPI.suggestBin(formData.productSku, parseInt(formData.quantity));
            setSuggestion(response.data);
        } catch (error) {
            alert(error.response?.data?.message || 'Failed to suggest bin');
        } finally {
            setLoading(false);
        }
    };

    const handleReceive = async () => {
        if (!formData.productSku || !formData.quantity || !formData.referenceNumber) {
            alert('Please fill all required fields');
            return;
        }

        setLoading(true);
        try {
            const payload = {
                productSku: formData.productSku,
                quantity: parseInt(formData.quantity),
                referenceNumber: formData.referenceNumber,
                targetBinCode: suggestion?.suggestedBinCode || formData.targetBinCode
            };
            const response = await receivingAPI.receive(payload);
            setResult(response.data);
            setFormData({ productSku: '', quantity: '', referenceNumber: '', targetBinCode: '' });
            setSuggestion(null);
            setScanInput('');
            
            setTimeout(() => setResult(null), 5000);
        } catch (error) {
            alert(error.response?.data?.message || 'Failed to receive shipment');
        } finally {
            setLoading(false);
        }
    };

    const handleBarcodeScan = async (barcode) => {
        if (!barcode) {
            alert('Please scan a valid barcode');
            return;
        }
        
        setLoading(true);
        try {
            // Check if product exists
            const productResponse = await productAPI.getBySku(barcode);
            if (productResponse.data) {
                setFormData(prev => ({ ...prev, productSku: barcode }));
                
                // If quantity is already entered, suggest bin automatically
                if (formData.quantity) {
                    const suggestionResponse = await receivingAPI.suggestBin(barcode, parseInt(formData.quantity));
                    setSuggestion(suggestionResponse.data);
                }
            }
        } catch (error) {
            alert('Invalid barcode: Product not found');
        } finally {
            setLoading(false);
            setScanInput('');
        }
    };

    const handleQuickProductSelect = (sku) => {
        setFormData(prev => ({ ...prev, productSku: sku }));
        setSuggestion(null);
    };

    const getProductName = (sku) => {
        const product = products.find(p => p.sku === sku);
        return product ? product.name : sku;
    };

    return (
        <div className="receiving-container">
            {/* Header Section */}
            <div className="receiving-header">
                <div className="header-content">
                    <div className="header-icon">📦</div>
                    <div className="header-text">
                        <h1>Receive Shipment</h1>
                        <p>Add new stock to your warehouse inventory</p>
                    </div>
                </div>
                <div className="mode-toggle">
                    <button 
                        className={`mode-btn ${!scanMode ? 'active' : ''}`}
                        onClick={() => setScanMode(false)}
                    >
                        ⌨️ Manual Entry
                    </button>
                    <button 
                        className={`mode-btn ${scanMode ? 'active' : ''}`}
                        onClick={() => setScanMode(true)}
                    >
                        📷 Scan Barcode
                    </button>
                </div>
            </div>

            {/* Main Content */}
            <div className="receiving-main">
                {/* Left Panel - Form */}
                <div className="form-panel">
                    {scanMode ? (
                        // SCAN MODE UI
                        <div className="scan-mode-panel">
                            <div className="scan-animation">
                                <div className="scan-line"></div>
                                <div className="scan-icon">📷</div>
                            </div>
                            <h3>Ready to Scan</h3>
                            <p>Position the barcode in front of the scanner</p>
                            
                            <input
                                ref={scanInputRef}
                                type="text"
                                className="scan-input"
                                placeholder="Scan product barcode..."
                                value={scanInput}
                                onChange={(e) => setScanInput(e.target.value)}
                                onKeyPress={(e) => {
                                    if (e.key === 'Enter') {
                                        handleBarcodeScan(scanInput);
                                    }
                                }}
                                autoFocus
                            />
                            
                            <div className="scan-instruction">
                                <span>📌</span> Scan product barcode, then enter quantity
                            </div>

                            {/* Show scanned product */}
                            {formData.productSku && (
                                <div className="scanned-product-card">
                                    <div className="scanned-icon">✅</div>
                                    <div className="scanned-info">
                                        <div className="scanned-label">Scanned Product</div>
                                        <div className="scanned-sku">{formData.productSku}</div>
                                        <div className="scanned-name">{getProductName(formData.productSku)}</div>
                                    </div>
                                </div>
                            )}

                            {/* Quantity entry after scan */}
                            {formData.productSku && (
                                <div className="quantity-prompt">
                                    <input
                                        id="quantity-input"
                                        type="number"
                                        placeholder="Enter quantity..."
                                        value={formData.quantity}
                                        onChange={(e) => setFormData(prev => ({...prev, quantity: e.target.value}))}
                                        onKeyPress={async (e) => {
                                            if (e.key === 'Enter' && formData.quantity) {
                                                setLoading(true);
                                                try {
                                                    const res = await receivingAPI.suggestBin(formData.productSku, parseInt(formData.quantity));
                                                    setSuggestion(res.data);
                                                } catch (err) {
                                                    alert('Failed to suggest bin');
                                                } finally {
                                                    setLoading(false);
                                                }
                                            }
                                        }}
                                    />
                                    <small>Press Enter after quantity to get bin suggestion</small>
                                </div>
                            )}
                            
                            {/* Reference Number in Scan Mode */}
                            {suggestion && (
                                <div className="reference-prompt">
                                    <input
                                        type="text"
                                        placeholder="PO / Reference Number *"
                                        value={formData.referenceNumber}
                                        onChange={(e) => setFormData(prev => ({...prev, referenceNumber: e.target.value}))}
                                    />
                                </div>
                            )}
                        </div>
                    ) : (
                        // MANUAL MODE UI
                        <div className="form-panel-content">
                            <div className="form-group">
                                <label>Product SKU <span className="required">*</span></label>
                                <div className="sku-input-wrapper">
                                    <input
                                        type="text"
                                        name="productSku"
                                        value={formData.productSku}
                                        onChange={handleChange}
                                        placeholder="e.g., MOUSE-001"
                                        list="products-list"
                                        className="sku-input"
                                    />
                                    <datalist id="products-list">
                                        {products.map(product => (
                                            <option key={product.id} value={product.sku}>
                                                {product.name} ({product.sku})
                                            </option>
                                        ))}
                                    </datalist>
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Quantity <span className="required">*</span></label>
                                <div className="quantity-wrapper">
                                    <input
                                        type="number"
                                        name="quantity"
                                        value={formData.quantity}
                                        onChange={handleChange}
                                        placeholder="Number of units"
                                        min="1"
                                    />
                                    <span className="units-badge">units</span>
                                </div>
                            </div>

                            <div className="form-group">
                                <label>PO / Reference Number <span className="required">*</span></label>
                                <input
                                    type="text"
                                    name="referenceNumber"
                                    value={formData.referenceNumber}
                                    onChange={handleChange}
                                    placeholder="e.g., PO-12345, SO-001"
                                />
                            </div>

                            <button 
                                className="suggest-btn"
                                onClick={handleSuggestBin}
                                disabled={loading || !formData.productSku || !formData.quantity}
                            >
                                {loading ? '🔄 Processing...' : '🔍 Suggest Bin'}
                            </button>
                        </div>
                    )}

                    {/* Suggestion Card */}
                    {suggestion && (
                        <div className="suggestion-card animate-slide-up">
                            <div className="suggestion-header">
                                <span className="suggestion-icon">🎯</span>
                                <h3>Putaway Suggestion</h3>
                            </div>
                            <div className="suggestion-content">
                                <div className="bin-display">
                                    <span className="bin-label">Suggested Bin</span>
                                    <div className="bin-code">{suggestion.suggestedBinCode}</div>
                                </div>
                                <div className="suggestion-details">
                                    <div className="detail-item">
                                        <span className="detail-label">Available Space</span>
                                        <span className="detail-value">{suggestion.availableSpace} units</span>
                                    </div>
                                    <div className="detail-item">
                                        <span className="detail-label">Reason</span>
                                        <span className="detail-value">{suggestion.reason}</span>
                                    </div>
                                </div>
                                <button 
                                    className="confirm-btn"
                                    onClick={handleReceive}
                                    disabled={loading || !formData.referenceNumber}
                                >
                                    {loading ? '⏳ Processing...' : '✅ Confirm Receiving'}
                                </button>
                            </div>
                        </div>
                    )}

                    {/* Success Result Card */}
                    {result && (
                        <div className="result-card animate-slide-up success">
                            <div className="result-header">
                                <span className="result-icon">✅</span>
                                <h3>Receiving Complete!</h3>
                            </div>
                            <div className="result-content">
                                <div className="result-details">
                                    <div className="result-row">
                                        <span className="result-label">Product:</span>
                                        <span className="result-value">{result.productName}</span>
                                    </div>
                                    <div className="result-row">
                                        <span className="result-label">Bin Location:</span>
                                        <span className="result-value highlight">{result.binCode}</span>
                                    </div>
                                    <div className="result-row">
                                        <span className="result-label">Quantity Received:</span>
                                        <span className="result-value">{result.quantityReceived} units</span>
                                    </div>
                                    <div className="result-row">
                                        <span className="result-label">New Total:</span>
                                        <span className="result-value">{result.newTotalQuantity} units</span>
                                    </div>
                                    <div className="result-row">
                                        <span className="result-label">Reference:</span>
                                        <span className="result-value">{result.referenceNumber}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                {/* Right Panel - Quick Products & Info */}
                <div className="info-panel">
                    {/* Quick Products */}
                    <div className="quick-products">
                        <h3>📋 Quick Products</h3>
                        <div className="product-list">
                            {products.slice(0, 5).map(product => (
                                <div 
                                    key={product.id} 
                                    className="product-chip"
                                    onClick={() => handleQuickProductSelect(product.sku)}
                                >
                                    <span className="product-sku">{product.sku}</span>
                                    <span className="product-name">{product.name}</span>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Info Card */}
                    <div className="info-card">
                        <div className="info-icon">💡</div>
                        <div className="info-text">
                            <h4>How Barcode Scanning Works</h4>
                            <p>1. Generate barcode from product page</p>
                            <p>2. Print and stick on product box</p>
                            <p>3. Scan barcode during receiving</p>
                            <p>4. System auto-fills product details</p>
                        </div>
                    </div>

                    <div className="info-card">
                        <div className="info-icon">🎯</div>
                        <div className="info-text">
                            <h4>Putaway Algorithm</h4>
                            <p>The system automatically finds the optimal bin location based on product consolidation and space availability.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Receiving;