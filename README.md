# 🏭 Enterprise Warehouse Management System (WMS)

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Overview

A comprehensive **Warehouse Management System** designed to automate core warehouse operations including real-time inventory tracking, optimized putaway logic, and efficient order picking workflows. Built for mid-sized logistics companies transitioning to automated supply chain management.

### 🎯 Key Features

- ✅ **Real-time Inventory Tracking** - ACID-compliant inventory management
- ✅ **Putaway Algorithm** - Smart bin suggestion based on consolidation strategy
- ✅ **Barcode/QR Code Generation** - Printable labels for products and orders
- ✅ **Order Management** - State machine workflow (PENDING → PICKING → PACKED → SHIPPED)
- ✅ **Role-Based Access** - ADMIN and OPERATOR roles with JWT authentication
- ✅ **Audit Trail** - Complete stock movement history
- ✅ **Responsive UI** - Modern React frontend with animations





## 🛠️ Technology Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Core language |
| Spring Boot | 3.2.5 | Application framework |
| Spring Security | 6.x | Authentication & Authorization |
| Spring Data JPA | 3.x | Database operations |
| PostgreSQL | 15 | Primary database |
| JWT | 0.11.5 | Token-based authentication |
| ZXing | 3.5.3 | Barcode/QR code generation |
| Lombok | 1.18.32 | Boilerplate reduction |

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| React | 18 | UI framework |
| Vite | 5.x | Build tool |
| React Router DOM | 6.x | Routing |
| Axios | 1.x | API calls |
| CSS3 | - | Styling & animations |

## 📁 Project Structure



warehouse-management-system/
├── backend/
│ └── warehouse-backend/
│ ├── src/main/java/com/wms/warehouse/
│ │ ├── config/ # Security & JWT configuration
│ │ ├── controller/ # REST API controllers
│ │ ├── dto/ # Data transfer objects
│ │ ├── entity/ # JPA entities
│ │ ├── exception/ # Custom exceptions
│ │ ├── repository/ # Data repositories
│ │ └── service/ # Business logic
│ └── src/main/resources/
│ └── application.properties
└── frontend/
└── warehouse-frontend/
├── src/
│ ├── components/ # React components
│ ├── context/ # Auth context
│ ├── services/ # API services
│ └── styles/ # CSS files
└── package.json




## 🗄️ Database Schema

```sql
-- Core Tables
warehouses      → Warehouse locations
zones           → Warehouse zones
aisles          → Aisles within zones
storage_bins    → Individual storage locations
products        → Product catalog
inventory_items → Product-bin mapping with quantity
stock_movements → Audit trail of all inventory changes
orders          → Customer orders
order_lines     → Items within orders
users           → System users with roles


Entity Relationships

Warehouse (1) ──→ (N) Zone
Zone (1) ──→ (N) Aisle
Aisle (1) ──→ (N) StorageBin
StorageBin (1) ──→ (N) InventoryItem
Product (1) ──→ (N) InventoryItem
InventoryItem (N) ──→ (1) StockMovement
Order (1) ──→ (N) OrderLine
OrderLine (N) ──→ (1) Product


🚀 Getting Started
Prerequisites
Java 17 or higher

Node.js 18+ and npm

PostgreSQL 15+

Maven (or use included wrapper)


Installation
1. Clone the Repository
git clone https://github.com/yourusername/warehouse-management-system.git
cd warehouse-management-system

2. Setup Database
-- Create database
CREATE DATABASE wms_db;

-- Update application.properties with your credentials
spring.datasource.url=jdbc:postgresql://localhost:5432/wms_db
spring.datasource.username=postgres
spring.datasource.password=your_password

3. Run Backend
cd backend/warehouse-backend
./mvnw clean install
./mvnw spring-boot:run

4. Run Frontend
cd frontend/warehouse-frontend
npm install
npm run dev


🔑 Default Credentials

Role	Username	Password
Admin	admin	admin123
Operator	operator	operator123

📡 API Endpoints
Authentication
Method	Endpoint	Description
POST	/api/auth/login	User login
POST	/api/auth/register	Register new user (Admin only)

Products
Method	Endpoint	Description
GET	/api/products	Get all products
GET	/api/products/{id}	Get product by ID
GET	/api/products/sku/{sku}	Get product by SKU
POST	/api/products	Create product (Admin only)
PUT	/api/products/{id}	Update product (Admin only)
DELETE	/api/products/{id}	Delete product (Admin only)

Inventory
Method	Endpoint	Description
GET	/api/inventory	Get all inventory
GET	/api/inventory/product/{sku}	Get inventory by product
POST	/api/inventory	Create inventory item

Receiving
Method	Endpoint	Description
POST	/api/receiving/suggest-bin	Get putaway suggestion
POST	/api/receiving/receive	Receive shipment (Transactional)

Orders
Method	Endpoint	Description
GET	/api/orders	Get all orders
POST	/api/orders	Create order
PUT	/api/orders/{number}/state	Update order state

Barcode
Method	Endpoint	Description
GET	/api/barcode/product/{sku}	Generate product barcode
GET	/api/barcode/qrcode/{sku}	Generate product QR code
