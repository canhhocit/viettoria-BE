import fs from 'fs';
import path from 'path';
import bcrypt from 'bcryptjs';
import type { 
  Collection, 
  Product, 
  StoryBlock, 
  BlogPost, 
  MediaItem, 
  AdminUser, 
  DashboardStats 
} from '../types/index.js';

interface DatabaseSchema {
  collections: Collection[];
  products: Product[];
  storyBlocks: StoryBlock[];
  blogs: BlogPost[];
  media: MediaItem[];
  users: AdminUser[];
}

const DB_DIR = path.resolve(process.cwd(), 'data');
const DB_FILE = path.join(DB_DIR, 'database.json');

// Initial seed data
const getInitialSeed = (): DatabaseSchema => {
  const passwordHash = bcrypt.hashSync('viettoria@2026', 10);

  const collections: Collection[] = [
    {
      id: 'col-1',
      code: 'COL-THANG-LONG',
      name: 'Dấu Ấn Hoàng Thành Thăng Long',
      slug: 'dau-an-hoang-thanh-thang-long',
      description: 'Hành trình nghìn năm văn vật nơi kinh thành Thăng Long, hội tụ tinh hoa kiến trúc cung đình và triết lý Phật giáo Đại Việt.',
      heroImage: '/images/hoang-thanh-thang-long.webp',
      displayOrder: 1,
      status: 'published',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    },
    {
      id: 'col-2',
      code: 'COL-HUE',
      name: 'Quần Thể Di Tích Cố Đô Huế',
      slug: 'quan-the-di-tich-co-do-hue',
      description: 'Kiệt tác kiến trúc cung đình triều Nguyễn, vẻ đẹp trầm mặc uy nghiêm bên bờ dòng sông Hương êm đềm.',
      heroImage: '/images/thua-thien-hue.webp',
      displayOrder: 2,
      status: 'published',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    },
    {
      id: 'col-3',
      code: 'COL-SAI-GON',
      name: 'Dấu Ấn Sài Gòn - Gia Định',
      slug: 'dau-an-sai-gon-gia-dinh',
      description: 'Nơi giao thoa phong cách kiến trúc Đông Dương, chứng nhân của những bước chân lịch sử vĩ đại của dân tộc.',
      heroImage: '/images/dinh-doc-lap.webp',
      displayOrder: 3,
      status: 'published',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
  ];

  const products: Product[] = [
    {
      id: 'prod-1',
      code: 'VT-HN-001',
      name: 'Chùa Một Cột',
      subName: 'Liên Hoa Đài — Diên Hựu Tự',
      slug: 'chua-mot-cot',
      collectionId: 'col-1',
      epoch: 'Thời Lý (Năm 1049)',
      location: 'Ba Đình, Hà Nội',
      shortDescription: 'Đóa sen nghìn năm ngự giữa lòng hồ Linh Chiểu, đỉnh cao triết lý Phật giáo Đại Việt và kỹ nghệ mộng gỗ truyền thống.',
      modelSpecs: {
        scale: '1:120',
        heightCm: 12,
        widthCm: 10,
        weightGrams: 185,
        material: 'Nhựa kỹ thuật sinh thái BioPETG (an toàn, bền vững 15+ năm)',
        partsCount: 24,
        finishing: 'Phủ bóng mờ sáp ong, gờ ngói cổ chạm nổi, giả lập chất gỗ lim già'
      },
      heroImage: '/images/chua-mot-cot.webp',
      galleryImages: [
        '/images/chua-mot-cot.webp',
        '/images/van-mieu-quoc-tu-giam.webp',
        '/images/hoang-thanh-thang-long.webp'
      ],
      audioGuide: {
        title: 'Hào Khí Thăng Long & Giấc Mơ Đài Sen',
        duration: '03:15',
        audioUrl: '/audio/chua-mot-cot-audio-guide.mp3',
        narrator: 'Nghệ nhân Thuyết minh Viettoria (Giọng Hà Nội truyền cảm)',
        transcript: 'Mùa đông năm 1049, kinh thành Thăng Long sương sớm bảng lảng. Vua Lý Thái Tông mộng thấy Phật Bà Quan Âm ngự trên tòa sen dắt vua bước lên. Khi tỉnh giấc, triều thần bàn việc lập chùa Diên Hựu giữa lòng hồ Linh Chiểu với trụ đá vươn cao như đóa hoa sen thanh tịnh, cầu chúc cho xã tắc trường thọ thái bình nghìn thu.'
      },
      displayOrder: 1,
      status: 'published',
      isHeroRelease: true,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    },
    {
      id: 'prod-2',
      code: 'VT-HN-002',
      name: 'Khuê Văn Các',
      subName: 'Gác Sao Khuê — Văn Miếu Quốc Tử Giám',
      slug: 'khue-van-cac',
      collectionId: 'col-1',
      epoch: 'Thời Nguyễn (Năm 1805)',
      location: 'Đống Đa, Hà Nội',
      shortDescription: 'Biểu tượng đỉnh cao của nền văn hiến và tinh thần hiếu học Thăng Long - Hà Nội với kết cấu Trời tròn Đất vuông độc đáo.',
      modelSpecs: {
        scale: '1:120',
        heightCm: 13,
        widthCm: 11,
        weightGrams: 210,
        material: 'Nhựa sinh thái BioPETG kết hợp chi tiết thếp vàng đồng',
        partsCount: 28,
        finishing: 'Sơn son thếp vàng thủ công viền cửa tròn sao Khuê, mái ngói giả cổ'
      },
      heroImage: '/images/van-mieu-quoc-tu-giam.webp',
      galleryImages: [
        '/images/van-mieu-quoc-tu-giam.webp',
        '/images/hoang-thanh-thang-long.webp'
      ],
      audioGuide: {
        title: 'Sao Khuê Tỏa Sáng Đất Tràng An',
        duration: '02:45',
        audioUrl: '/audio/khue-van-cac-audio-guide.mp3',
        narrator: 'Thuyết minh Viettoria Atelier',
        transcript: 'Khuê Văn Các soi bóng xuống giếng Thiên Quang phẳng lặng như gương soi lòng kẻ sĩ. Công trình được Tổng trấn Nguyễn Văn Thành cho dựng năm 1805, tượng trưng cho nền học vấn thanh cao và truyền thống tôn sư trọng đạo của dân tộc.'
      },
      displayOrder: 2,
      status: 'published',
      isHeroRelease: false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    },
    {
      id: 'prod-3',
      code: 'VT-HN-003',
      name: 'Cột Cờ Hà Nội',
      subName: 'Kỳ Đài Thăng Long',
      slug: 'cot-co-ha-noi',
      collectionId: 'col-1',
      epoch: 'Thời Nguyễn (Năm 1812)',
      location: 'Hoàng Diệu, Hà Nội',
      shortDescription: 'Chứng nhân lịch sử kiên cường qua bao biến thiên, lá cờ đỏ sao vàng kiêu hãnh tung bay trên bầu trời Thủ đô.',
      modelSpecs: {
        scale: '1:150',
        heightCm: 16,
        widthCm: 12,
        weightGrams: 230,
        material: 'Nhựa sinh thái BioPETG vân gạch vồ Thăng Long',
        partsCount: 18,
        finishing: 'Màu gạch nung cổ kính, 39 lỗ hoa thị đón sáng tinh tế'
      },
      heroImage: '/images/hoang-thanh-thang-long.webp',
      galleryImages: ['/images/hoang-thanh-thang-long.webp'],
      audioGuide: {
        title: 'Hào Khí Kỳ Đài Thăng Long',
        duration: '02:30',
        audioUrl: '/audio/cot-co-audio-guide.mp3',
        narrator: 'Thuyết minh Viettoria Atelier',
        transcript: 'Kỳ đài Hà Nội xây dựng năm 1812, là một trong số ít công trình còn nguyên vẹn trong khuôn viên Hoàng thành Thăng Long, ghi dấu thời khắc thiêng liêng ngày giải phóng Thủ đô 10/10/1954.'
      },
      displayOrder: 3,
      status: 'published',
      isHeroRelease: false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    },
    {
      id: 'prod-4',
      code: 'VT-HUE-004',
      name: 'Ngọ Môn Huế',
      subName: 'Cổng Chính Hoàng Thành Cố Đô',
      slug: 'ngo-mon-hue',
      collectionId: 'col-2',
      epoch: 'Thời Nguyễn (Năm 1833)',
      location: 'Thành phố Huế',
      shortDescription: 'Đỉnh cao nghệ thuật kiến trúc cung đình triều Nguyễn với Lầu Ngũ Phụng 100 cây cột lim tráng lệ.',
      modelSpecs: {
        scale: '1:200',
        heightCm: 11,
        widthCm: 18,
        weightGrams: 260,
        material: 'Nhựa sinh thái BioPETG phối hợp màu sơn truyền thống',
        partsCount: 32,
        finishing: 'Ngói hoàng lưu ly vàng rực rỡ và ngói thanh lưu ly xanh biếc'
      },
      heroImage: '/images/thua-thien-hue.webp',
      galleryImages: ['/images/thua-thien-hue.webp', '/images/lang-vua-tu-duc.webp'],
      audioGuide: {
        title: 'Âm Sắc Cung Đình Bên Dòng Hương Giang',
        duration: '03:10',
        audioUrl: '/audio/ngo-mon-audio-guide.mp3',
        narrator: 'Thuyết minh Cố Đô Viettoria',
        transcript: 'Ngọ Môn nhìn về hướng nam theo dịch lý, nơi hội tụ ánh sáng và quyền uy vương triều. Phía trên là Lầu Ngũ Phụng, nơi tổ chức các đại lễ truyền lô, ban sóc và là nơi chứng kiến sự kiện thoái vị năm 1945.'
      },
      displayOrder: 4,
      status: 'published',
      isHeroRelease: false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    },
    {
      id: 'prod-5',
      code: 'VT-SG-005',
      name: 'Bến Nhà Rồng',
      subName: 'Bảo Tàng Hồ Chí Minh — Cảng Sài Gòn',
      slug: 'ben-nha-rong',
      collectionId: 'col-3',
      epoch: 'Thời Cận Đại (Năm 1863)',
      location: 'Quận 4, TP. Hồ Chí Minh',
      shortDescription: 'Nơi người thanh niên Nguyễn Tất Thành bước chân ra đi tìm đường cứu nước năm 1911, biểu tượng lịch sử son sắt của phương Nam.',
      modelSpecs: {
        scale: '1:180',
        heightCm: 12,
        widthCm: 16,
        weightGrams: 220,
        material: 'Nhựa sinh thái BioPETG màu gạch cổ thuộc địa',
        partsCount: 22,
        finishing: 'Chi tiết đôi rồng gốm men xanh ngậm ngọc chầu trăng trên đỉnh mái'
      },
      heroImage: '/images/dinh-doc-lap.webp',
      galleryImages: ['/images/dinh-doc-lap.webp'],
      audioGuide: {
        title: 'Khúc Tráng Ca Trên Bến Cảng Sài Gòn',
        duration: '02:50',
        audioUrl: '/audio/ben-nha-rong-audio-guide.mp3',
        narrator: 'Thuyết minh Viettoria Atelier',
        transcript: 'Ngày 5 tháng 6 năm 1911, con tàu Đô đốc Latouche-Tréville rời bến cảng Sài Gòn chở theo khát vọng độc lập tự do cháy bỏng của người thanh niên 21 tuổi Nguyễn Tất Thành.'
      },
      displayOrder: 5,
      status: 'published',
      isHeroRelease: false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
  ];

  const storyBlocks: StoryBlock[] = [
    {
      id: 'block-1',
      productId: 'prod-1',
      type: 'text_image',
      title: 'Giấc Mộng Đài Sen Mùa Đông Kỷ Sửu (1049)',
      subtitle: 'Khởi nguồn vương triều Lý và ước vọng thái bình',
      content: 'Chùa Một Cột (Diên Hựu Tự) được khởi dựng vào mùa đông năm Kỷ Sửu (1049) dưới triều vua Lý Thái Tông. Theo Đại Việt Sử Ký Toàn Thư, vua chiêm bao thấy Phật Bà Quan Âm ngự trên tòa sen dắt vua bước lên. Tỉnh mộng, triều thần bàn việc lập chùa với trụ đá vươn cao giữa hồ sen vuông vức Linh Chiểu, tựa như đóa sen tinh khiết ngàn năm tỏa hương ngự giữa lòng kinh thành Thăng Long.',
      mediaUrl: '/images/chua-mot-cot.webp',
      mediaCaption: 'Diên Hựu Tự soi bóng bên hồ Linh Chiểu, quận Ba Đình, Hà Nội.',
      displayOrder: 1,
      status: 'published'
    },
    {
      id: 'block-2',
      productId: 'prod-1',
      type: 'highlight',
      title: 'Kiến Trúc Độc Tôn: Một Cột Đá Nâng Đỡ Tòa Sen',
      subtitle: 'Đỉnh cao kết cấu kiến trúc gỗ và đá của người Việt cổ',
      content: 'Toàn bộ công trình Liên Hoa Đài tựa trên một cột đá hình trụ tròn đường kính 1.2 mét. Phía trên đầu cột đá là hệ thống 8 thanh consol gỗ lớn xòe ra như 8 cánh hoa sen nở rộ, nâng đỡ trọn vẹn gian thờ vuông vức mà không cần bất kỳ cột phụ trợ nào.',
      highlightBadge: 'Độc bản thế giới',
      displayOrder: 2,
      status: 'published'
    },
    {
      id: 'block-3',
      productId: 'prod-1',
      type: 'assembly',
      title: 'Kỹ Nghệ Khớp Mộng Âm Dương Không Dùng Keo Dán',
      subtitle: '4 giai đoạn lắp ráp tái hiện cấu trúc mộng gỗ Thăng Long',
      content: 'Mô hình thu nhỏ Chùa Một Cột của Viettoria được phân rã thành 24 cấu kiện mộng gỗ tinh xảo. Người sở hữu sẽ tự tay ghép nối từng chi tiết theo đúng quy chuẩn kiến trúc cổ truyền mà không cần đến bất kỳ giọt keo dán công nghiệp nào.',
      assemblySteps: [
        {
          step: 1,
          title: 'Khởi Trụ Liên Hoa',
          nameVi: 'Khởi trụ liên hoa',
          description: 'Đặt chân đế hồ sen Linh Chiểu phẳng phiu, gài khối cột đá tròn nguyên khối vào khớp khóa chống xoay 360 độ.',
          detail: 'Khớp mộng đá vững chãi đảm bảo phương thẳng đứng tuyệt đối của trục vũ trụ.',
          highlight: 'Khối đế vững chãi'
        },
        {
          step: 2,
          title: 'Bung Cánh Đòn Gánh (8 Thanh Consol)',
          nameVi: 'Bung cánh đòn gánh',
          description: 'Lần lượt gài 8 thanh tay đòn gỗ lim vào ngàm âm dương trên đỉnh trụ đá, xòe đều 8 hướng như đài sen đang nở.',
          detail: 'Cảm nhận độ khít khao chính xác đến 0.1mm khi mộng khóa ăn khớp êm ái.',
          highlight: 'Mộng khóa âm dương'
        },
        {
          step: 3,
          title: 'An Vị Liên Hoa Đài',
          nameVi: 'An vị Liên Hoa Đài',
          description: 'Hạ gian thờ vuông vức đặt lên bề mặt của hệ 8 đòn consol, 4 góc có chốt khóa mộng với khung mái.',
          detail: 'Gian thờ trang nghiêm, cửa mở hướng về phía nam đón nguồn sinh khí thánh thiện.',
          highlight: 'Điện thờ trang nghiêm'
        },
        {
          step: 4,
          title: 'Viên Mãn Đỉnh Sen (Úp Mái Đao Hoàng Triều)',
          nameVi: 'Viên mãn đỉnh sen',
          description: 'Đặt khối mái ngói đao cong lên 4 cột góc của lầu Phật, bờ nóc đắp nổi lưỡng long chầu nguyệt.',
          detail: 'Tác phẩm hoàn thiện kiêu hãnh trên lòng bàn tay với chiều cao chuẩn 12cm.',
          highlight: 'Đóa sen trọn vẹn'
        }
      ],
      displayOrder: 3,
      status: 'published'
    },
    {
      id: 'block-4',
      productId: 'prod-1',
      type: 'timeline',
      title: 'Biên Niên Sử Diên Hựu Tự Qua 10 Thế Kỷ',
      subtitle: 'Những cột mốc thăng trầm cùng vận mệnh non sông',
      content: 'Chùa Một Cột đã trải qua nhiều lần trùng tu và thử thách của lịch sử để trở thành biểu tượng bất tử của Thăng Long.',
      timelineItems: [
        { year: '1049', event: 'Khởi Dựng Chùa Diên Hựu', detail: 'Vua Lý Thái Tông cho dựng chùa sau giấc mộng thấy Phật Bà Quan Âm.' },
        { year: '1105', event: 'Vua Lý Nhân Tông Mở Rộng', detail: 'Đào thêm hồ Bích Trì xung quanh, xây cầu vồng chạm khắc cầu kỳ dẫn vào chùa.' },
        { year: '1249', event: 'Trùng Tu Thời Trần', detail: 'Vương triều Trần tiếp tục tôn tạo, duy trì lễ tắm Phật định kỳ hàng tháng.' },
        { year: '1805', event: 'Thời Nguyễn Trùng Tu', detail: 'Quy hoạch lại cảnh quan xung quanh cụm di tích Đội Cấn - Ba Đình.' },
        { year: '1954', event: 'Phục Dựng Nguyên Trạng', detail: 'Bộ Văn hóa và các nghệ nhân kiến trúc phục dựng chính xác chùa theo kiến trúc thời Lý sau khi thủ đô giải phóng.' },
        { year: '2026', event: 'Số Hóa & Phygital Viettoria', detail: 'Dự án VIETTORIA tái hiện mô hình in 3D kết hợp nền tảng số hóa tương tác đa giác quan.' }
      ],
      displayOrder: 4,
      status: 'published'
    },
    {
      id: 'block-5',
      productId: 'prod-1',
      type: 'text',
      title: 'Triết Lý Phật Giáo & Khát Vọng Trường Tồn Dân Tộc',
      subtitle: 'Đóa sen nở từ bùn đen tượng trưng cho sự thanh tịnh vô nhiễm',
      content: 'Trong văn hóa Phật giáo Đại Việt, hoa sen là biểu tượng của sự thuần khiết, vượt lên trên mọi cám dỗ và thăng trầm của cõi tục. Chùa Một Cột ngự giữa lòng hồ nước không chỉ là một công trình tín ngưỡng, mà là tuyên ngôn về bản sắc văn hóa tự chủ, dung dị nhưng thâm sâu của cha ông ta nghìn năm trước.',
      displayOrder: 5,
      status: 'published'
    }
  ];

  const blogs: BlogPost[] = [
    {
      id: 'blog-1',
      title: 'Kỹ nghệ mộng gỗ âm dương trong kiến trúc cổ Việt Nam và hành trình số hóa 3D',
      slug: 'ky-nghe-mong-go-am-duong-trong-kien-truc-co-viet-nam',
      excerpt: 'Tìm hiểu triết lý không dùng một giọt keo dán hay đinh vít sắt của người xưa, và cách Viettoria module hóa từng khớp mộng thành mô hình in 3D sinh thái.',
      content: 'Trong nền kiến trúc truyền thống Việt Nam, kỹ thuật liên kết rường cột hoàn toàn dựa trên các mộng gỗ âm dương. Các thợ mộc xưa đo đạc bằng thước tầm, đục đẽo từng thanh đòn consol để khi gài vào nhau sẽ tự khóa chặt dưới sức nặng của chính công trình. Viettoria đã ứng dụng công nghệ quét 3D và thuật toán phân tích dung sai 0.1mm để tái hiện trọn vẹn vẻ đẹp này trên mô hình in 3D BioPETG.',
      coverImage: '/images/chua-mot-cot.webp',
      author: 'Ban Nghiên Cứu Di Sản Viettoria',
      category: 'Kỹ Nghệ & Kiến Trúc',
      readTime: '5 phút đọc',
      publishedAt: '2026-10-25',
      status: 'published'
    },
    {
      id: 'blog-2',
      title: 'Diên Hựu Tự: Đóa sen nghìn năm từ giấc mộng vua Lý Thái Tông',
      slug: 'dien-huu-tu-doa-sen-nghin-nam-tu-giac-mong-vua-ly-thai-tong',
      excerpt: 'Giải mã những ẩn ngữ phong thủy, triết lý Trời tròn Đất vuông và bối cảnh lịch sử Thăng Long thời Lý thế kỷ 11.',
      content: 'Mùa đông năm Kỷ Sửu (1049), kinh thành Thăng Long đón nhận một công trình có một không hai trong lịch sử kiến trúc nhân loại. Chùa Một Cột không chỉ ghi dấu lòng sùng đạo của vua Lý Thái Tông mà còn biểu trưng cho lời chúc quốc thái dân an, đưa Đại Việt bước vào kỷ nguyên văn minh rực rỡ.',
      coverImage: '/images/van-mieu-quoc-tu-giam.webp',
      author: 'Nguyễn Văn Cảnh',
      category: 'Lịch Sử & Giai Thoại',
      readTime: '7 phút đọc',
      publishedAt: '2026-10-28',
      status: 'published'
    },
    {
      id: 'blog-3',
      title: 'Phygital Heritage: Xu hướng đưa hiện vật lịch sử đến gần thế hệ trẻ',
      slug: 'phygital-heritage-xu-huong-dua-hien-vat-lich-su-den-the-he-tre',
      excerpt: 'Tại sao việc kết hợp giữa mô hình vật thể cầm tay và trải nghiệm số QR là chìa khóa mở cánh cửa tình yêu văn hóa cho Gen Z.',
      content: 'Mô hình vật lý mang lại cảm giác xúc giác (tactile feeling), trong khi nền tảng web số mang lại chiều sâu thông tin qua âm thanh, đồ họa và dữ liệu lịch sử chuẩn xác. Đó chính là sứ mệnh Phygital mà Viettoria kiên định theo đuổi tại cuộc thi Sáng tạo Khởi nghiệp Phenikaa PSI 2026.',
      coverImage: '/images/hoang-thanh-thang-long.webp',
      author: 'Đội Ngũ Sáng Lập Viettoria',
      category: 'Đổi Mới Sáng Tạo',
      readTime: '4 phút đọc',
      publishedAt: '2026-11-01',
      status: 'published'
    }
  ];

  const media: MediaItem[] = [
    {
      id: 'media-1',
      name: 'Chùa Một Cột Chính Diện',
      fileName: 'chua-mot-cot.webp',
      fileType: 'image/webp',
      fileSize: 299290,
      url: '/images/chua-mot-cot.webp',
      altText: 'Ảnh chụp chính diện Chùa Một Cột Ba Đình Hà Nội',
      uploadedAt: new Date().toISOString()
    },
    {
      id: 'media-2',
      name: 'Văn Miếu Quốc Tử Giám',
      fileName: 'van-mieu-quoc-tu-giam.webp',
      fileType: 'image/webp',
      fileSize: 297804,
      url: '/images/van-mieu-quoc-tu-giam.webp',
      altText: 'Khuê Văn Các soi bóng xuống giếng Thiên Quang',
      uploadedAt: new Date().toISOString()
    },
    {
      id: 'media-3',
      name: 'Kỳ Đài Thăng Long',
      fileName: 'hoang-thanh-thang-long.webp',
      fileType: 'image/webp',
      fileSize: 191866,
      url: '/images/hoang-thanh-thang-long.webp',
      altText: 'Cột Cờ Hà Nội trong khuôn viên di tích Hoàng thành',
      uploadedAt: new Date().toISOString()
    }
  ];

  const users: AdminUser[] = [
    {
      id: 'user-admin',
      username: 'admin',
      passwordHash,
      fullName: 'Quản Trị Viên Viettoria',
      role: 'admin'
    }
  ];

  return { collections, products, storyBlocks, blogs, media, users };
};

export class DatabaseService {
  private data: DatabaseSchema;

  constructor() {
    if (!fs.existsSync(DB_DIR)) {
      fs.mkdirSync(DB_DIR, { recursive: true });
    }

    if (fs.existsSync(DB_FILE)) {
      try {
        const raw = fs.readFileSync(DB_FILE, 'utf-8');
        this.data = JSON.parse(raw);
      } catch (err) {
        console.warn('Could not read existing database.json, re-seeding...', err);
        this.data = getInitialSeed();
        this.save();
      }
    } else {
      this.data = getInitialSeed();
      this.save();
    }
  }

  private save(): void {
    fs.writeFileSync(DB_FILE, JSON.stringify(this.data, null, 2), 'utf-8');
  }

  // --- Collections ---
  getCollections(onlyPublished = false): Collection[] {
    const list = onlyPublished 
      ? this.data.collections.filter(c => c.status === 'published')
      : this.data.collections;
    return [...list].sort((a, b) => a.displayOrder - b.displayOrder);
  }

  getCollectionBySlug(slug: string): Collection | undefined {
    return this.data.collections.find(c => c.slug === slug);
  }

  getCollectionById(id: string): Collection | undefined {
    return this.data.collections.find(c => c.id === id);
  }

  createCollection(data: Omit<Collection, 'id' | 'createdAt' | 'updatedAt'>): Collection {
    const newCol: Collection = {
      ...data,
      id: `col-${Date.now()}`,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    this.data.collections.push(newCol);
    this.save();
    return newCol;
  }

  updateCollection(id: string, data: Partial<Collection>): Collection | undefined {
    const index = this.data.collections.findIndex(c => c.id === id);
    if (index === -1) return undefined;
    this.data.collections[index] = {
      ...this.data.collections[index],
      ...data,
      updatedAt: new Date().toISOString()
    };
    this.save();
    return this.data.collections[index];
  }

  deleteCollection(id: string): boolean {
    const initialLen = this.data.collections.length;
    this.data.collections = this.data.collections.filter(c => c.id !== id);
    if (this.data.collections.length !== initialLen) {
      this.save();
      return true;
    }
    return false;
  }

  // --- Products ---
  getProducts(options?: { collectionId?: string; search?: string; onlyPublished?: boolean }): Product[] {
    let list = this.data.products;

    if (options?.onlyPublished) {
      list = list.filter(p => p.status === 'published');
    }

    if (options?.collectionId) {
      list = list.filter(p => p.collectionId === options.collectionId);
    }

    if (options?.search) {
      const q = options.search.toLowerCase();
      list = list.filter(p => 
        p.name.toLowerCase().includes(q) || 
        p.code.toLowerCase().includes(q) ||
        p.location.toLowerCase().includes(q) ||
        p.epoch.toLowerCase().includes(q)
      );
    }

    return [...list].sort((a, b) => a.displayOrder - b.displayOrder);
  }

  getProductById(id: string): Product | undefined {
    return this.data.products.find(p => p.id === id);
  }

  getProductBySlugOrCode(identifier: string): Product | undefined {
    const lower = identifier.toLowerCase();
    return this.data.products.find(p => 
      p.slug.toLowerCase() === lower || p.code.toLowerCase() === lower
    );
  }

  createProduct(data: Omit<Product, 'id' | 'createdAt' | 'updatedAt'>): Product {
    const newProd: Product = {
      ...data,
      id: `prod-${Date.now()}`,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    this.data.products.push(newProd);
    this.save();
    return newProd;
  }

  updateProduct(id: string, data: Partial<Product>): Product | undefined {
    const index = this.data.products.findIndex(p => p.id === id);
    if (index === -1) return undefined;
    this.data.products[index] = {
      ...this.data.products[index],
      ...data,
      updatedAt: new Date().toISOString()
    };
    this.save();
    return this.data.products[index];
  }

  deleteProduct(id: string): boolean {
    const initialLen = this.data.products.length;
    this.data.products = this.data.products.filter(p => p.id !== id);
    // Also cascade delete story blocks
    this.data.storyBlocks = this.data.storyBlocks.filter(b => b.productId !== id);
    if (this.data.products.length !== initialLen) {
      this.save();
      return true;
    }
    return false;
  }

  // --- Story Blocks ---
  getStoryBlocks(productId: string, onlyPublished = false): StoryBlock[] {
    let list = this.data.storyBlocks.filter(b => b.productId === productId);
    if (onlyPublished) {
      list = list.filter(b => b.status === 'published');
    }
    return [...list].sort((a, b) => a.displayOrder - b.displayOrder);
  }

  createStoryBlock(data: Omit<StoryBlock, 'id'>): StoryBlock {
    const newBlock: StoryBlock = {
      ...data,
      id: `block-${Date.now()}`
    };
    this.data.storyBlocks.push(newBlock);
    this.save();
    return newBlock;
  }

  updateStoryBlock(id: string, data: Partial<StoryBlock>): StoryBlock | undefined {
    const index = this.data.storyBlocks.findIndex(b => b.id === id);
    if (index === -1) return undefined;
    this.data.storyBlocks[index] = {
      ...this.data.storyBlocks[index],
      ...data
    };
    this.save();
    return this.data.storyBlocks[index];
  }

  reorderStoryBlocks(productId: string, blockIds: string[]): boolean {
    blockIds.forEach((id, idx) => {
      const block = this.data.storyBlocks.find(b => b.id === id && b.productId === productId);
      if (block) {
        block.displayOrder = idx + 1;
      }
    });
    this.save();
    return true;
  }

  deleteStoryBlock(id: string): boolean {
    const initialLen = this.data.storyBlocks.length;
    this.data.storyBlocks = this.data.storyBlocks.filter(b => b.id !== id);
    if (this.data.storyBlocks.length !== initialLen) {
      this.save();
      return true;
    }
    return false;
  }

  // --- Blogs ---
  getBlogs(onlyPublished = false): BlogPost[] {
    const list = onlyPublished 
      ? this.data.blogs.filter(b => b.status === 'published')
      : this.data.blogs;
    return [...list].sort((a, b) => new Date(b.publishedAt).getTime() - new Date(a.publishedAt).getTime());
  }

  getBlogBySlug(slug: string): BlogPost | undefined {
    return this.data.blogs.find(b => b.slug === slug);
  }

  createBlog(data: Omit<BlogPost, 'id'>): BlogPost {
    const newBlog: BlogPost = {
      ...data,
      id: `blog-${Date.now()}`
    };
    this.data.blogs.push(newBlog);
    this.save();
    return newBlog;
  }

  updateBlog(id: string, data: Partial<BlogPost>): BlogPost | undefined {
    const index = this.data.blogs.findIndex(b => b.id === id);
    if (index === -1) return undefined;
    this.data.blogs[index] = {
      ...this.data.blogs[index],
      ...data
    };
    this.save();
    return this.data.blogs[index];
  }

  deleteBlog(id: string): boolean {
    const initialLen = this.data.blogs.length;
    this.data.blogs = this.data.blogs.filter(b => b.id !== id);
    if (this.data.blogs.length !== initialLen) {
      this.save();
      return true;
    }
    return false;
  }

  // --- Media ---
  getMedia(): MediaItem[] {
    return [...this.data.media].sort((a, b) => new Date(b.uploadedAt).getTime() - new Date(a.uploadedAt).getTime());
  }

  createMedia(data: Omit<MediaItem, 'id' | 'uploadedAt'>): MediaItem {
    const newMedia: MediaItem = {
      ...data,
      id: `media-${Date.now()}`,
      uploadedAt: new Date().toISOString()
    };
    this.data.media.push(newMedia);
    this.save();
    return newMedia;
  }

  deleteMedia(id: string): boolean {
    const initialLen = this.data.media.length;
    this.data.media = this.data.media.filter(m => m.id !== id);
    if (this.data.media.length !== initialLen) {
      this.save();
      return true;
    }
    return false;
  }

  // --- Auth / User ---
  findUserByUsername(username: string): AdminUser | undefined {
    return this.data.users.find(u => u.username === username);
  }

  // --- Dashboard Stats ---
  getDashboardStats(): DashboardStats {
    return {
      totalProducts: this.data.products.length,
      publishedProducts: this.data.products.filter(p => p.status === 'published').length,
      totalCollections: this.data.collections.length,
      totalBlogs: this.data.blogs.length,
      totalStoryBlocks: this.data.storyBlocks.length,
      totalMedia: this.data.media.length
    };
  }
}

export const db = new DatabaseService();
