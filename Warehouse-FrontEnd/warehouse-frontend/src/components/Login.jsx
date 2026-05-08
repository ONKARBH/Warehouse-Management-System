import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import '../styles/Login.css';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        
        try {
            await login(username, password);
            navigate('/dashboard');
        } catch (err) {
            setError('Invalid username or password');
        } finally {
            setLoading(false);
        }
    };

    const fillDemo = (role) => {
        if (role === 'admin') {
            setUsername('admin');
            setPassword('admin123');
        } else {
            setUsername('operator');
            setPassword('operator123');
        }
    };

    return (
        <div className="login-container">
            <div className="login-bg-animation">
                <div className="cube"></div>
                <div className="cube"></div>
                <div className="cube"></div>
                <div className="cube"></div>
                <div className="cube"></div>
            </div>
            
            <div className="login-card">
                <div className="login-header">
                    <div className="login-icon">🏭</div>
                    <h1>Warehouse <span>Management</span></h1>
                    <p>System</p>
                </div>
                
                <form onSubmit={handleSubmit}>
                    <div className="input-group">
                        <input
                            type="text"
                            placeholder="Username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            autoComplete="off"
                        />
                        <span className="input-icon">👤</span>
                    </div>
                    
                    <div className="input-group">
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                        <span className="input-icon">🔒</span>
                    </div>
                    
                    {error && <div className="error-message shake">{error}</div>}
                    
                    <button type="submit" disabled={loading} className="login-btn">
                        {loading ? 'Logging in...' : 'Login'}
                    </button>
                </form>
                
                <div className="demo-section">
                    <p>Demo Credentials</p>
                    <div className="demo-buttons">
                        <button onClick={() => fillDemo('admin')} className="demo-btn admin">
                            👑 Admin
                        </button>
                        <button onClick={() => fillDemo('operator')} className="demo-btn operator">
                            👷 Operator
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;