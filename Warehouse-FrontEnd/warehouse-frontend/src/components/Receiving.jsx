import React, { useState } from 'react';
import { receivingAPI, barcodeAPI } from '../services/api';

function Receiving() {
    const [productSku, setProductSku] = useState('');
    const [quantity, setQuantity] = useState('');
    const [referenceNumber, setReferenceNumber] = useState('');
    const [suggestion, setSuggestion] = useState(null);
    const [result, setResult] = useState(null);
    const [scanMode, setScanMode] = useState(false);

    const handleSuggestBin = async () => {
        try {
            const response = await receivingAPI.suggestBin(productSku, parseInt(quantity));
            setSuggestion(response.data);
        } catch (error) {
            alert('Error: ' + error.response?.data?.message || error.message);
        }
    };

    const handleReceive = async () => {
        try {
            const response = await receivingAPI.receive({
                productSku,
                quantity: parseInt(quantity),
                referenceNumber
            });
            setResult(response.data);
            setProductSku('');
            setQuantity('');
            setReferenceNumber('');
            setSuggestion(null);
        } catch (error) {
            alert('Error: ' + error.response?.data?.message || error.message);
        }
    };

    const handleBarcodeScan = async (barcode) => {
        try {
            const response = await barcodeAPI.scan(barcode, 'RECEIVING', parseInt(quantity) || 1);
            if (response.data.suggestedBinCode) {
                setProductSku(barcode);
                setSuggestion(response.data);
            }
        } catch (error) {
            alert('Invalid barcode');
        }
    };

    return (
        <div className="receiving">
            <h2>Receive Shipment</h2>
            
            <div className="scan-mode">
                <button onClick={() => setScanMode(!scanMode)}>
                    {scanMode ? 'Manual Entry' : 'Scan Barcode Mode'}
                </button>
            </div>

            {scanMode ? (
                <div className="scan-input">
                    <input
                        type="text"
                        placeholder="Scan product barcode..."
                        onKeyPress={(e) => {
                            if (e.key === 'Enter') {
                                handleBarcodeScan(e.target.value);
                                e.target.value = '';
                            }
                        }}
                        autoFocus
                    />
                </div>
            ) : (
                <div className="manual-input">
                    <input
                        type="text"
                        placeholder="Product SKU"
                        value={productSku}
                        onChange={(e) => setProductSku(e.target.value)}
                    />
                    <input
                        type="number"
                        placeholder="Quantity"
                        value={quantity}
                        onChange={(e) => setQuantity(e.target.value)}
                    />
                    <input
                        type="text"
                        placeholder="Reference Number (PO)"
                        value={referenceNumber}
                        onChange={(e) => setReferenceNumber(e.target.value)}
                    />
                    <button onClick={handleSuggestBin}>Suggest Bin</button>
                </div>
            )}

            {suggestion && (
                <div className="suggestion">
                    <h3>Suggested Bin: {suggestion.suggestedBinCode}</h3>
                    <p>Available Space: {suggestion.availableSpace} units</p>
                    <p>Reason: {suggestion.reason}</p>
                    <button onClick={handleReceive}>Confirm Receiving</button>
                </div>
            )}

            {result && (
                <div className="result success">
                    <h3>✓ Receiving Complete!</h3>
                    <p>Product: {result.productName}</p>
                    <p>Bin: {result.binCode}</p>
                    <p>Quantity: {result.quantityReceived}</p>
                    <p>New Total: {result.newTotalQuantity}</p>
                </div>
            )}
        </div>
    );
}

export default Receiving;