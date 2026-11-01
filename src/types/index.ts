export type ContentStatus = 'draft' | 'published' | 'hidden';

export interface Collection {
  id: string;
  code: string;
  name: string;
  slug: string;
  description: string;
  heroImage: string;
  displayOrder: number;
  status: ContentStatus;
  createdAt: string;
  updatedAt: string;
}

export interface ModelSpecifications {
  scale: string;
  heightCm: number;
  widthCm: number;
  weightGrams: number;
  material: string;
  partsCount: number;
  finishing: string;
}

export interface AudioGuideData {
  title: string;
  duration: string;
  audioUrl: string;
  narrator?: string;
  transcript: string;
}

export interface Product {
  id: string;
  code: string; // e.g. "VT-HN-001" - unique identifier for QR
  name: string;
  subName: string;
  slug: string;
  collectionId: string;
  epoch: string;
  location: string;
  shortDescription: string;
  modelSpecs: ModelSpecifications;
  heroImage: string;
  galleryImages: string[];
  model3dUrl?: string;
  audioGuide?: AudioGuideData;
  displayOrder: number;
  status: ContentStatus;
  isHeroRelease?: boolean;
  createdAt: string;
  updatedAt: string;
}

export type StoryBlockType = 'text' | 'image' | 'text_image' | 'timeline' | 'highlight' | 'assembly';

export interface TimelineItem {
  year: string;
  event: string;
  detail: string;
}

export interface AssemblyStepData {
  step: number;
  title: string;
  nameVi: string;
  description: string;
  detail: string;
  highlight: string;
}

export interface StoryBlock {
  id: string;
  productId: string;
  type: StoryBlockType;
  title: string;
  subtitle?: string;
  content: string;
  mediaUrl?: string;
  mediaCaption?: string;
  timelineItems?: TimelineItem[];
  assemblySteps?: AssemblyStepData[];
  highlightBadge?: string;
  displayOrder: number;
  status: 'published' | 'hidden';
}

export interface BlogPost {
  id: string;
  title: string;
  slug: string;
  excerpt: string;
  content: string;
  coverImage: string;
  author: string;
  category: string;
  readTime: string;
  publishedAt: string;
  status: ContentStatus;
}

export interface MediaItem {
  id: string;
  name: string;
  fileName: string;
  fileType: string;
  fileSize: number;
  url: string;
  altText: string;
  uploadedAt: string;
}

export interface AdminUser {
  id: string;
  username: string;
  passwordHash: string;
  fullName: string;
  role: 'admin';
}

export interface DashboardStats {
  totalProducts: number;
  publishedProducts: number;
  totalCollections: number;
  totalBlogs: number;
  totalStoryBlocks: number;
  totalMedia: number;
}
