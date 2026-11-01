import { Router } from 'express';
import type { Request, Response } from 'express';
import { db } from '../db/database.js';

const router = Router();

// 1. Get all published collections
router.get('/collections', (_req: Request, res: Response) => {
  const collections = db.getCollections(true);
  const products = db.getProducts({ onlyPublished: true });

  const enriched = collections.map(col => ({
    ...col,
    productsCount: products.filter(p => p.collectionId === col.id).length
  }));

  res.json({ success: true, data: enriched });
});

// 2. Get single collection by slug
router.get('/collections/:slug', (req: Request, res: Response) => {
  const slug = String(req.params.slug);
  const collection = db.getCollectionBySlug(slug);

  if (!collection || collection.status !== 'published') {
    res.status(404).json({ success: false, message: 'Bộ sưu tập không tồn tại hoặc đã ẩn' });
    return;
  }

  const products = db.getProducts({ collectionId: collection.id, onlyPublished: true });

  res.json({
    success: true,
    data: {
      ...collection,
      products
    }
  });
});

// 3. Get published products (with optional filtering)
router.get('/products', (req: Request, res: Response) => {
  const { collectionId, search } = req.query;

  const products = db.getProducts({
    collectionId: collectionId as string | undefined,
    search: search as string | undefined,
    onlyPublished: true
  });

  res.json({ success: true, data: products });
});

// 4. Get product detail by SLUG or by CODE (Crucial for QR: /products/chua-mot-cot OR /products/VT-HN-001 OR /product?code=VT-HN-001)
router.get('/products/:slugOrCode', (req: Request, res: Response) => {
  const slugOrCode = String(req.params.slugOrCode);
  const product = db.getProductBySlugOrCode(slugOrCode);

  if (!product || product.status !== 'published') {
    res.status(404).json({ 
      success: false, 
      message: 'Sản phẩm di sản không tồn tại hoặc đang ở chế độ bản nháp/đã ẩn' 
    });
    return;
  }

  const collection = db.getCollectionById(product.collectionId);
  const storyBlocks = db.getStoryBlocks(product.id, true);
  const relatedProducts = db.getProducts({ 
    collectionId: product.collectionId, 
    onlyPublished: true 
  }).filter(p => p.id !== product.id).slice(0, 3);

  res.json({
    success: true,
    data: {
      ...product,
      collection,
      storyBlocks,
      relatedProducts
    }
  });
});

// 5. QR Code Resolver endpoint: GET /api/public/qr-resolve?code=VT-HN-001
router.get('/qr-resolve', (req: Request, res: Response) => {
  const code = req.query.code as string;
  if (!code) {
    res.status(400).json({ success: false, message: 'Thiếu mã sản phẩm QR trong query parameter' });
    return;
  }

  const product = db.getProductBySlugOrCode(code);
  if (!product || product.status !== 'published') {
    res.status(404).json({ 
      success: false, 
      message: `Mã sản phẩm "${code}" không tồn tại hoặc chưa được xuất bản công khai.` 
    });
    return;
  }

  res.json({
    success: true,
    data: {
      productId: product.id,
      code: product.code,
      name: product.name,
      slug: product.slug,
      targetUrl: `/products/${product.slug}?code=${encodeURIComponent(product.code)}`
    }
  });
});

// 6. Get published blogs
router.get('/blogs', (_req: Request, res: Response) => {
  const blogs = db.getBlogs(true);
  res.json({ success: true, data: blogs });
});

// 7. Get single blog by slug
router.get('/blogs/:slug', (req: Request, res: Response) => {
  const slug = String(req.params.slug);
  const blog = db.getBlogBySlug(slug);

  if (!blog || blog.status !== 'published') {
    res.status(404).json({ success: false, message: 'Bài viết không tồn tại hoặc đã ẩn' });
    return;
  }

  res.json({ success: true, data: blog });
});

// 8. Homepage curated showcase
router.get('/featured', (_req: Request, res: Response) => {
  const collections = db.getCollections(true).slice(0, 3);
  const allProducts = db.getProducts({ onlyPublished: true });
  const heroProduct = allProducts.find(p => p.isHeroRelease) || allProducts[0];
  const featuredProducts = allProducts.filter(p => p.id !== heroProduct?.id).slice(0, 4);
  const latestBlogs = db.getBlogs(true).slice(0, 3);

  res.json({
    success: true,
    data: {
      heroProduct,
      featuredProducts,
      collections,
      latestBlogs
    }
  });
});

export default router;
