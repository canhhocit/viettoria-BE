import { Router } from 'express';
import type { Request, Response } from 'express';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { db } from '../db/database.js';
import { requireAdmin, JWT_SECRET, type AuthenticatedRequest } from '../middleware/auth.js';

const router = Router();

// --- AUTH ---
router.post('/auth/login', (req: Request, res: Response) => {
  const { username, password } = req.body;

  if (!username || !password) {
    res.status(400).json({ success: false, message: 'Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu' });
    return;
  }

  const user = db.findUserByUsername(username);
  if (!user || !bcrypt.compareSync(password, user.passwordHash)) {
    res.status(401).json({ success: false, message: 'Tên đăng nhập hoặc mật khẩu không chính xác' });
    return;
  }

  const token = jwt.sign(
    { id: user.id, username: user.username, role: user.role },
    JWT_SECRET,
    { expiresIn: '7d' }
  );

  res.json({
    success: true,
    data: {
      token,
      user: {
        id: user.id,
        username: user.username,
        fullName: user.fullName,
        role: user.role
      }
    }
  });
});

router.get('/auth/me', requireAdmin, (req: AuthenticatedRequest, res: Response) => {
  res.json({ success: true, data: req.user });
});

// --- DASHBOARD ---
router.get('/dashboard', requireAdmin, (_req: Request, res: Response) => {
  const stats = db.getDashboardStats();
  const recentProducts = db.getProducts().slice(0, 5);
  const recentBlogs = db.getBlogs().slice(0, 5);

  res.json({
    success: true,
    data: {
      stats,
      recentProducts,
      recentBlogs
    }
  });
});

// --- COLLECTIONS CRUD ---
router.get('/collections', requireAdmin, (_req: Request, res: Response) => {
  const collections = db.getCollections(false);
  res.json({ success: true, data: collections });
});

router.post('/collections', requireAdmin, (req: Request, res: Response) => {
  const { name, code, slug, description, heroImage, displayOrder, status } = req.body;
  if (!name || !slug) {
    res.status(400).json({ success: false, message: 'Tên và đường dẫn slug là bắt buộc' });
    return;
  }

  const created = db.createCollection({
    name,
    code: code || `COL-${Date.now()}`,
    slug,
    description: description || '',
    heroImage: heroImage || '/images/hoang-thanh-thang-long.webp',
    displayOrder: Number(displayOrder) || 1,
    status: status || 'draft'
  });

  res.status(201).json({ success: true, data: created });
});

router.put('/collections/:id', requireAdmin, (req: Request, res: Response) => {
  const id = String(req.params.id);
  const updated = db.updateCollection(id, req.body);
  if (!updated) {
    res.status(404).json({ success: false, message: 'Bộ sưu tập không tồn tại' });
    return;
  }
  res.json({ success: true, data: updated });
});

router.delete('/collections/:id', requireAdmin, (req: Request, res: Response) => {
  const id = String(req.params.id);
  const deleted = db.deleteCollection(id);
  if (!deleted) {
    res.status(404).json({ success: false, message: 'Bộ sưu tập không tồn tại' });
    return;
  }
  res.json({ success: true, message: 'Đã xóa bộ sưu tập thành công' });
});

// --- PRODUCTS CRUD ---
router.get('/products', requireAdmin, (req: Request, res: Response) => {
  const { collectionId, search } = req.query;
  const products = db.getProducts({
    collectionId: collectionId as string | undefined,
    search: search as string | undefined,
    onlyPublished: false
  });
  res.json({ success: true, data: products });
});

router.get('/products/:id', requireAdmin, (req: Request, res: Response) => {
  const id = String(req.params.id);
  const product = db.getProductById(id);
  if (!product) {
    res.status(404).json({ success: false, message: 'Sản phẩm không tồn tại' });
    return;
  }
  const storyBlocks = db.getStoryBlocks(id, false);
  res.json({ success: true, data: { ...product, storyBlocks } });
});

router.post('/products', requireAdmin, (req: Request, res: Response) => {
  const { 
    code, name, subName, slug, collectionId, epoch, location, 
    shortDescription, modelSpecs, heroImage, galleryImages, audioGuide, 
    displayOrder, status, isHeroRelease 
  } = req.body;

  if (!code || !name || !slug) {
    res.status(400).json({ success: false, message: 'Mã sản phẩm, tên và đường dẫn slug là bắt buộc' });
    return;
  }

  // Check unique code
  const existing = db.getProductBySlugOrCode(code);
  if (existing) {
    res.status(400).json({ success: false, message: `Mã sản phẩm "${code}" đã tồn tại. Vui lòng chọn mã khác.` });
    return;
  }

  const created = db.createProduct({
    code,
    name,
    subName: subName || '',
    slug,
    collectionId: collectionId || 'col-1',
    epoch: epoch || 'Thời Lý',
    location: location || 'Hà Nội',
    shortDescription: shortDescription || '',
    modelSpecs: modelSpecs || {
      scale: '1:120',
      heightCm: 12,
      widthCm: 10,
      weightGrams: 180,
      material: 'Nhựa sinh thái BioPETG',
      partsCount: 20,
      finishing: 'Phủ bóng mờ sáp ong'
    },
    heroImage: heroImage || '/images/chua-mot-cot.webp',
    galleryImages: galleryImages || ['/images/chua-mot-cot.webp'],
    audioGuide,
    displayOrder: Number(displayOrder) || 1,
    status: status || 'draft',
    isHeroRelease: Boolean(isHeroRelease)
  });

  res.status(201).json({ success: true, data: created });
});

router.put('/products/:id', requireAdmin, (req: Request, res: Response) => {
  const id = String(req.params.id);
  const updated = db.updateProduct(id, req.body);
  if (!updated) {
    res.status(404).json({ success: false, message: 'Sản phẩm không tồn tại' });
    return;
  }
  res.json({ success: true, data: updated });
});

router.delete('/products/:id', requireAdmin, (req: Request, res: Response) => {
  const id = String(req.params.id);
  const deleted = db.deleteProduct(id);
  if (!deleted) {
    res.status(404).json({ success: false, message: 'Sản phẩm không tồn tại' });
    return;
  }
  res.json({ success: true, message: 'Đã xóa sản phẩm và các khối nội dung liên kết thành công' });
});

// --- STORY BLOCKS CRUD ---
router.get('/products/:productId/stories', requireAdmin, (req: Request, res: Response) => {
  const productId = String(req.params.productId);
  const blocks = db.getStoryBlocks(productId, false);
  res.json({ success: true, data: blocks });
});

router.post('/products/:productId/stories', requireAdmin, (req: Request, res: Response) => {
  const productId = String(req.params.productId);
  const { 
    type, title, subtitle, content, mediaUrl, mediaCaption, 
    timelineItems, assemblySteps, highlightBadge, displayOrder, status 
  } = req.body;

  if (!title || !type) {
    res.status(400).json({ success: false, message: 'Tiêu đề và loại khối nội dung là bắt buộc' });
    return;
  }

  const existingBlocks = db.getStoryBlocks(productId, false);
  const nextOrder = existingBlocks.length > 0 
    ? Math.max(...existingBlocks.map(b => b.displayOrder)) + 1 
    : 1;

  const created = db.createStoryBlock({
    productId,
    type,
    title,
    subtitle: subtitle || '',
    content: content || '',
    mediaUrl,
    mediaCaption,
    timelineItems,
    assemblySteps,
    highlightBadge,
    displayOrder: Number(displayOrder) || nextOrder,
    status: status || 'published'
  });

  res.status(201).json({ success: true, data: created });
});

router.put('/stories/:id', requireAdmin, (req: Request, res: Response) => {
  const id = String(req.params.id);
  const updated = db.updateStoryBlock(id, req.body);
  if (!updated) {
    res.status(404).json({ success: false, message: 'Khối câu chuyện không tồn tại' });
    return;
  }
  res.json({ success: true, data: updated });
});

router.delete('/stories/:id', requireAdmin, (req: Request, res: Response) => {
  const id = String(req.params.id);
  const deleted = db.deleteStoryBlock(id);
  if (!deleted) {
    res.status(404).json({ success: false, message: 'Khối câu chuyện không tồn tại' });
    return;
  }
  res.json({ success: true, message: 'Đã xóa khối câu chuyện thành công' });
});

router.put('/products/:productId/stories/reorder', requireAdmin, (req: Request, res: Response) => {
  const productId = String(req.params.productId);
  const { blockIds } = req.body;

  if (!Array.isArray(blockIds)) {
    res.status(400).json({ success: false, message: 'blockIds phải là mảng id các khối' });
    return;
  }

  db.reorderStoryBlocks(productId, blockIds);
  res.json({ success: true, message: 'Đã sắp xếp thứ tự các khối câu chuyện thành công' });
});

// --- BLOGS CRUD ---
router.get('/blogs', requireAdmin, (_req: Request, res: Response) => {
  const blogs = db.getBlogs(false);
  res.json({ success: true, data: blogs });
});

router.post('/blogs', requireAdmin, (req: Request, res: Response) => {
  const { title, slug, excerpt, content, coverImage, author, category, readTime, publishedAt, status } = req.body;

  if (!title || !slug) {
    res.status(400).json({ success: false, message: 'Tiêu đề và đường dẫn slug là bắt buộc' });
    return;
  }

  const created = db.createBlog({
    title,
    slug,
    excerpt: excerpt || '',
    content: content || '',
    coverImage: coverImage || '/images/chua-mot-cot.webp',
    author: author || 'Ban Nghiên Cứu Viettoria',
    category: category || 'Di Sản & Kiến Trúc',
    readTime: readTime || '5 phút đọc',
    publishedAt: publishedAt || new Date().toISOString().split('T')[0],
    status: status || 'draft'
  });

  res.status(201).json({ success: true, data: created });
});

router.put('/blogs/:id', requireAdmin, (req: Request, res: Response) => {
  const id = String(req.params.id);
  const updated = db.updateBlog(id, req.body);
  if (!updated) {
    res.status(404).json({ success: false, message: 'Bài viết không tồn tại' });
    return;
  }
  res.json({ success: true, data: updated });
});

router.delete('/blogs/:id', requireAdmin, (req: Request, res: Response) => {
  const id = String(req.params.id);
  const deleted = db.deleteBlog(id);
  if (!deleted) {
    res.status(404).json({ success: false, message: 'Bài viết không tồn tại' });
    return;
  }
  res.json({ success: true, message: 'Đã xóa bài viết thành công' });
});

// --- MEDIA LIBRARY ---
router.get('/media', requireAdmin, (_req: Request, res: Response) => {
  const media = db.getMedia();
  res.json({ success: true, data: media });
});

router.post('/media', requireAdmin, (req: Request, res: Response) => {
  const { name, fileName, fileType, fileSize, url, altText } = req.body;

  if (!name || !url) {
    res.status(400).json({ success: false, message: 'Tên và đường dẫn tệp là bắt buộc' });
    return;
  }

  const created = db.createMedia({
    name,
    fileName: fileName || name.toLowerCase().replace(/\s+/g, '-'),
    fileType: fileType || 'image/webp',
    fileSize: Number(fileSize) || 150000,
    url,
    altText: altText || name
  });

  res.status(201).json({ success: true, data: created });
});

router.delete('/media/:id', requireAdmin, (req: Request, res: Response) => {
  const id = String(req.params.id);
  const deleted = db.deleteMedia(id);
  if (!deleted) {
    res.status(404).json({ success: false, message: 'Tài nguyên media không tồn tại' });
    return;
  }
  res.json({ success: true, message: 'Đã xóa tài nguyên media thành công' });
});

export default router;
