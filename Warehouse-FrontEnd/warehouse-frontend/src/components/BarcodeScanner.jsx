import React, { useState, useEffect, useRef } from 'react';
import QrReader from 'react-qr-barcode-scanner';
import '../styles/BarcodeScanner.css';

const BarcodeScanner = ({ onScan, onClose, productName }) => {
    const [scanning, setScanning] = useState(true);
    const [scannedData, setScannedData] = useState(null);
    const [error, setError] = useState(null);
    const [torchOn, setTorchOn] = useState(false);
    const [cameraFacing, setCameraFacing] = useState('environment'); // 'environment' for back camera

    const handleScan = (result) => {
        if (result && result.text) {
            setScannedData(result.text);
            setScanning(false);
            onScan(result.text);
        }
    };

    const handleError = (err) => {
        console.error('Scanner error:', err);
        setError('Camera access denied or not available');
    };

    const toggleCamera = () => {
        setCameraFacing(prev => prev === 'environment' ? 'user' : 'environment');
    };

    const restartScan = () => {
        setScannedData(null);
        setScanning(true);
        setError(null);
    };

    return (
        <div className="scanner-overlay">
            <div className="scanner-modal">
                <div className="scanner-header">
                    <div className="scanner-header-content">
                        <span className="scanner-icon">📷</span>
                        <div>
                            <h3>Scan Barcode / QR Code</h3>
                            <p>{productName ? `Scanning for ${productName}` : 'Position barcode in frame'}</p>
                        </div>
                    </div>
                    <button className="scanner-close" onClick={onClose}>✕</button>
                </div>

                <div className="scanner-container">
                    {scanning && !scannedData && (
                        <div className="scanner-view">
                            <QrReader
                                constraints={{
                                    facingMode: cameraFacing,
                                    width: { ideal: 1280 },
                                    height: { ideal: 720 }
                                }}
                                onResult={handleScan}
                                onError={handleError}
                                scanDelay={300}
                                style={{ width: '100%', height: '100%' }}
                            />
                            <div className="scanner-overlay-frame">
                                <div className="scan-frame">
                                    <div className="scan-corner top-left"></div>
                                    <div className="scan-corner top-right"></div>
                                    <div className="scan-corner bottom-left"></div>
                                    <div className="scan-corner bottom-right"></div>
                                    <div className="scan-line-animation"></div>
                                </div>
                            </div>
                        </div>
                    )}

                    {scannedData && (
                        <div className="scan-success">
                            <div className="success-animation">
                                <div className="success-circle">✅</div>
                            </div>
                            <h3>Barcode Scanned Successfully!</h3>
                            <div className="scanned-value">{scannedData}</div>
                            <div className="scanned-actions">
                                <button className="scan-again-btn" onClick={restartScan}>
                                    🔄 Scan Again
                                </button>
                                <button className="confirm-scan-btn" onClick={() => onScan(scannedData)}>
                                    ✅ Use This Barcode
                                </button>
                            </div>
                        </div>
                    )}

                    {error && (
                        <div className="scanner-error">
                            <div className="error-icon">⚠️</div>
                            <h3>Camera Error</h3>
                            <p>{error}</p>
                            <p className="error-hint">Please check camera permissions and try again.</p>
                            <button onClick={restartScan}>Try Again</button>
                        </div>
                    )}

                    <div className="scanner-controls">
                        <button className="control-btn" onClick={toggleCamera}>
                            🔄 {cameraFacing === 'environment' ? 'Front Camera' : 'Back Camera'}
                        </button>
                        <div className="scanner-hint">
                            <span>📌</span> Align barcode within the frame
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default BarcodeScanner;