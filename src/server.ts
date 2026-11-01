import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import publicRoutes from './routes/publicRoutes.js';
import adminRoutes from './routes/adminRoutes.js';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 5000;

// Middleware
app.use(cors({
  origin: true, // Allow frontend during local development
  credentials: true
}));
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// Request logger for local dev
app.use((req, _res, next) => {
  console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`);
  next();
});

// Health check
app.get('/api/health', (_req, res) => {
  res.json({
    status: 'online',
    project: 'VIETTORIA Heritage Collectibles API',
    version: '1.0.0',
    timestamp: new Date().toISOString()
  });
});

// Mount Routes
app.use('/api/public', publicRoutes);
app.use('/api/admin', adminRoutes);

// 404 handler
app.use((_req, res) => {
  res.status(404).json({ success: false, message: 'API Endpoint không tồn tại' });
});

// Start Server
app.listen(PORT, () => {
  console.log(`
╔══════════════════════════════════════════════════════════════════╗
║  VIETTORIA BACKEND SERVER STARTED SUCCESSFULLY                  ║
║  URL: http://localhost:${PORT}                                     ║
║  Health Check: http://localhost:${PORT}/api/health                 ║
║  Public API:   http://localhost:${PORT}/api/public/featured        ║
║  Admin CMS:    http://localhost:${PORT}/api/admin/dashboard        ║
╚══════════════════════════════════════════════════════════════════╝
  `);
});

export default app;
