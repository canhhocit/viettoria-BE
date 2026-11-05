package com.viettoria.config;

import com.viettoria.entity.*;
import com.viettoria.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements ApplicationRunner {

    private final CollectionRepository collectionRepo;
    private final ProductRepository productRepo;
    private final StoryBlockRepository storyBlockRepo;
    private final BlogPostRepository blogPostRepo;
    private final MediaItemRepository mediaItemRepo;
    private final AdminUserRepository adminUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public DataInitializer(
            CollectionRepository collectionRepo,
            ProductRepository productRepo,
            StoryBlockRepository storyBlockRepo,
            BlogPostRepository blogPostRepo,
            MediaItemRepository mediaItemRepo,
            AdminUserRepository adminUserRepo,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username}") String adminUsername,
            @Value("${app.admin.password}") String adminPassword) {
        this.collectionRepo = collectionRepo;
        this.productRepo = productRepo;
        this.storyBlockRepo = storyBlockRepo;
        this.blogPostRepo = blogPostRepo;
        this.mediaItemRepo = mediaItemRepo;
        this.adminUserRepo = adminUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Keep the configured admin credentials as the source of truth across restarts.
        syncAdminUserFromEnvironment();

        // Seed Collections
        if (collectionRepo.count() == 0) {
            CollectionEntity c1 = new CollectionEntity(
                    "col-1",
                    "COL-THANG-LONG",
                    "Dấu Ấn Hoàng Thành Thăng Long",
                    "dau-an-hoang-thanh-thang-long",
                    "Hành trình nghìn năm văn vật nơi kinh thành Thăng Long, hội tụ tinh hoa kiến trúc cung đình và triết lý Phật giáo Đại Việt.",
                    "/images/hoang-thanh-thang-long.webp",
                    1,
                    "published"
            );
            CollectionEntity c2 = new CollectionEntity(
                    "col-2",
                    "COL-HUE",
                    "Quần Thể Di Tích Cố Đô Huế",
                    "quan-the-di-tich-co-do-hue",
                    "Kiệt tác kiến trúc cung đình triều Nguyễn, vẻ đẹp trầm mặc uy nghiêm bên bờ dòng sông Hương êm đềm.",
                    "/images/thua-thien-hue.webp",
                    2,
                    "published"
            );
            CollectionEntity c3 = new CollectionEntity(
                    "col-3",
                    "COL-SAI-GON",
                    "Dấu Ấn Sài Gòn - Gia Định",
                    "dau-an-sai-gon-gia-dinh",
                    "Nơi giao thoa phong cách kiến trúc Đông Dương, chứng nhân của những bước chân lịch sử vĩ đại của dân tộc.",
                    "/images/dinh-doc-lap.webp",
                    3,
                    "published"
            );
            collectionRepo.saveAll(Arrays.asList(c1, c2, c3));
        }

        // Seed Products
        if (productRepo.count() == 0) {
            // 1. Chùa Một Cột (Hero)
            ProductEntity p1 = new ProductEntity();
            p1.setId("prod-1");
            p1.setCode("VT-HN-001");
            p1.setName("Chùa Một Cột");
            p1.setSubName("Diên Hựu Tự — Liên Hoa Đài");
            p1.setSlug("chua-mot-cot");
            p1.setCollectionId("col-1");
            p1.setEpoch("Thời Lý (1049)");
            p1.setLocation("Ba Đình, Hà Nội");
            p1.setShortDescription("Đóa sen ngàn năm trên hồ Linh Chiểu. Biểu tượng bất hủ của lòng từ bi, sự giác ngộ và trí tuệ kiến trúc mộng gỗ Việt Nam.");
            p1.setScaleRatio("1:120");
            p1.setHeightCm(12.0);
            p1.setWidthCm(10.0);
            p1.setWeightGrams(480.0);
            p1.setMaterial("BioPETG Khoáng Đá Tự Nhiên");
            p1.setPartsCount(24);
            p1.setFinishing("Sơn then & thếp vàng thủ công");
            p1.setHeroImage("/images/chua-mot-cot.webp");
            p1.setGalleryImagesJson("[\"/images/chua-mot-cot.webp\"]");
            p1.setAudioTitle("Âm Vang Diên Hựu — Đóa Sen Ngàn Năm");
            p1.setAudioDuration("04:35");
            p1.setAudioUrl(null);
            p1.setAudioNarrator("Ban Cố Vấn Lịch Sử Viettoria");
            p1.setAudioTranscript("Chùa Một Cột, tên chữ là Diên Hựu Tự, khởi dựng vào mùa đông năm Kỷ Sửu (1049) dưới triều vua Lý Thái Tông.\n\nTheo truyền thuyết Đại Việt Sử Ký Toàn Thư, vua chiêm bao thấy Phật Bà Quan Âm ngồi trên tòa sen dắt vua lên đài. Khi tỉnh dậy, vua đem việc ấy hỏi quần thần, nhà sư Thiền Tuệ khuyên dựng chùa như hình đài sen trên cột đá.\n\nĐiểm độc đáo vô song của Chùa Một Cột nằm ở kiến trúc một cột đá duy nhất chịu lực toàn bộ tòa điện gỗ vuông vắn bên trên. Hệ thống xà nách 8 cánh vươn ra đỡ lấy sàn chùa, mô phỏng đóa sen nở rộ giữa lòng hồ Linh Chiểu. Toàn bộ cấu trúc được lắp ráp tinh vi bằng các khớp mộng gỗ âm dương, thể hiện đỉnh cao tài hoa mộc bản của người thợ Đại Việt.");
            p1.setDisplayOrder(1);
            p1.setStatus("published");
            p1.setHeroRelease(true);

            // 2. Khuê Văn Các
            ProductEntity p2 = new ProductEntity();
            p2.setId("prod-2");
            p2.setCode("VT-HN-002");
            p2.setName("Khuê Văn Các");
            p2.setSubName("Gác Vẻ Đẹp Sao Khuê");
            p2.setSlug("khue-van-cac");
            p2.setCollectionId("col-1");
            p2.setEpoch("Triều Nguyễn (1805)");
            p2.setLocation("Đống Đa, Hà Nội");
            p2.setShortDescription("Biểu tượng đỉnh cao của nền văn hiến và tinh thần hiếu học Thăng Long - Hà Nội. Công trình kiến trúc gỗ hai tầng tám mái độc đáo soi bóng xuống hồ Thiên Quang.");
            p2.setScaleRatio("1:120");
            p2.setHeightCm(14.5);
            p2.setWidthCm(11.0);
            p2.setWeightGrams(520.0);
            p2.setMaterial("BioPETG Khoáng Đá Tự Nhiên");
            p2.setPartsCount(32);
            p2.setFinishing("Sơn then thếp vàng giả cổ");
            p2.setHeroImage("/images/van-mieu-quoc-tu-giam.webp");
            p2.setGalleryImagesJson("[\"/images/van-mieu-quoc-tu-giam.webp\"]");
            p2.setAudioTitle("Sao Khuê Tỏa Sáng — Đất Học Thăng Long");
            p2.setAudioDuration("03:50");
            p2.setAudioUrl(null);
            p2.setAudioNarrator("Ban Cố Vấn Văn Hóa");
            p2.setAudioTranscript("Khuê Văn Các được xây dựng năm 1805 dưới thời vua Gia Long, do Tổng trấn Bắc thành Nguyễn Văn Thành chủ trì hưng công.\n\nGác Khuê Văn gồm hai tầng: tầng dưới là 4 trụ gạch vồ vuông vắn vững chãi tượng trưng cho Đất, tầng trên là lầu gỗ với các cửa sổ tròn tỏa tia sáng tượng trưng cho Trời và vì sao Khuê chủ về văn chương.");
            p2.setDisplayOrder(2);
            p2.setStatus("published");
            p2.setHeroRelease(false);

            // 3. Cột Cờ Hà Nội
            ProductEntity p3 = new ProductEntity();
            p3.setId("prod-3");
            p3.setCode("VT-HN-003");
            p3.setName("Cột Cờ Hà Nội");
            p3.setSubName("Kỳ Đài Thăng Long");
            p3.setSlug("cot-co-ha-noi");
            p3.setCollectionId("col-1");
            p3.setEpoch("Triều Nguyễn (1812)");
            p3.setLocation("Ba Đình, Hà Nội");
            p3.setShortDescription("Kỳ đài uy nghiêm xây dựng từ gạch vồ thời Lê - Nguyễn, chứng nhân lịch sử kiên cường qua bao thăng trầm của thủ đô.");
            p3.setScaleRatio("1:200");
            p3.setHeightCm(16.5);
            p3.setWidthCm(12.0);
            p3.setWeightGrams(600.0);
            p3.setMaterial("BioPETG Khoáng Đá Tự Nhiên");
            p3.setPartsCount(18);
            p3.setFinishing("Gạch vồ nung thủ công");
            p3.setHeroImage("/3D/cot-co-ha-noi-poster.webp");
            p3.setGalleryImagesJson("[\"/3D/cot-co-ha-noi-poster.webp\"]");
            p3.setAudioTitle("Ngọn Cờ Độc Lập Giữa Lòng Thăng Long");
            p3.setAudioDuration("03:20");
            p3.setAudioUrl(null);
            p3.setAudioNarrator("Ban Cố Vấn Văn Hóa");
            p3.setAudioTranscript("Cột cờ Hà Nội hay Kỳ đài Hà Nội là di tích lịch sử đặc biệt nằm trong khuôn viên Bảo tàng Lịch sử Quân sự Việt Nam. Được xây dựng năm 1812, đây là một trong số ít công trình nguyên vẹn của Hoàng thành Thăng Long sau sự tàn phá của thực dân Pháp.");
            p3.setDisplayOrder(3);
            p3.setStatus("published");
            p3.setHeroRelease(false);

            // 4. Ngọ Môn Huế
            ProductEntity p4 = new ProductEntity();
            p4.setId("prod-4");
            p4.setCode("VT-HUE-004");
            p4.setName("Ngọ Môn Huế");
            p4.setSubName("Cửa Chính Hoàng Thành Huế");
            p4.setSlug("ngo-mon-hue");
            p4.setCollectionId("col-2");
            p4.setEpoch("Triều Nguyễn (1833)");
            p4.setLocation("Thành phố Huế");
            p4.setShortDescription("Kiệt tác kiến trúc cung đình triều Nguyễn với lầu Ngũ Phụng chín gian mái ngói hoàng lưu ly và thanh lưu ly rực rỡ.");
            p4.setScaleRatio("1:200");
            p4.setHeightCm(13.0);
            p4.setWidthCm(22.0);
            p4.setWeightGrams(750.0);
            p4.setMaterial("BioPETG Khoáng Đá Tự Nhiên");
            p4.setPartsCount(40);
            p4.setFinishing("Thếp vàng & Ngói lưu ly");
            p4.setHeroImage("/3D/ngo-mon-hue-poster.webp");
            p4.setGalleryImagesJson("[\"/3D/ngo-mon-hue-poster.webp\"]");
            p4.setAudioTitle("Dấu Ấn Cung Đình Trên Lầu Ngũ Phụng");
            p4.setAudioDuration("04:10");
            p4.setAudioUrl(null);
            p4.setAudioNarrator("Ban Cố Vấn Lịch Sử");
            p4.setAudioTranscript("Ngọ Môn là cổng chính phía nam của Hoàng thành Huế, được xây dựng năm Minh Mạng thứ 14 (1833). Phía trên là lầu Ngũ Phụng với kết cấu gỗ lim tinh xảo, nơi diễn ra các lễ truyền lô công bố danh sách tiến sĩ và lễ thoái vị của vua Bảo Đại năm 1945.");
            p4.setDisplayOrder(4);
            p4.setStatus("published");
            p4.setHeroRelease(false);

            // 5. Bến Nhà Rồng
            ProductEntity p5 = new ProductEntity();
            p5.setId("prod-5");
            p5.setCode("VT-SG-005");
            p5.setName("Bến Nhà Rồng");
            p5.setSubName("Thương Cảng Sài Gòn 1863");
            p5.setSlug("ben-nha-rong");
            p5.setCollectionId("col-3");
            p5.setEpoch("Cận Đại (1863)");
            p5.setLocation("Quận 4, TP. Hồ Chí Minh");
            p5.setShortDescription("Công trình kiến trúc phong cách Đông Dương với đôi rồng ngậm ngọc trên nóc, dấu mốc thiêng liêng nơi người thanh niên Nguyễn Tất Thành ra đi tìm đường cứu nước.");
            p5.setScaleRatio("1:150");
            p5.setHeightCm(12.5);
            p5.setWidthCm(18.0);
            p5.setWeightGrams(580.0);
            p5.setMaterial("BioPETG Khoáng Đá Tự Nhiên");
            p5.setPartsCount(28);
            p5.setFinishing("Gạch phục cổ & Chi tiết đắp nổi");
            p5.setHeroImage("/3D/ben-nha-rong-poster.webp");
            p5.setGalleryImagesJson("[\"/3D/ben-nha-rong-poster.webp\"]");
            p5.setAudioTitle("Khởi Đầu Hành Trình Cứu Nước 1911");
            p5.setAudioDuration("03:45");
            p5.setAudioUrl(null);
            p5.setAudioNarrator("Ban Cố Vấn Văn Hóa");
            p5.setAudioTranscript("Bến Nhà Rồng khởi công xây dựng năm 1863 làm trụ sở cho hãng vận tải Messageries Maritimes. Tòa nhà mang dấu ấn kiến trúc phương Tây pha trộn hoa văn rồng Á Đông đặc trưng trên đỉnh mái.");
            p5.setDisplayOrder(5);
            p5.setStatus("published");
            p5.setHeroRelease(false);

            productRepo.saveAll(Arrays.asList(p1, p2, p3, p4, p5));
        }

        // Seed Story Blocks for Chùa Một Cột
        if (storyBlockRepo.count() == 0) {
            StoryBlockEntity b1 = new StoryBlockEntity();
            b1.setId("b-1");
            b1.setProductId("prod-1");
            b1.setType("text");
            b1.setTitle("Điềm Lành Đóa Sen & Giấc Mơ Của Vua Lý");
            b1.setSubtitle("Niên hiệu Sùng Hưng Đại Bảo thứ nhất (1049)");
            b1.setContent("Mùa đông năm Kỷ Sửu 1049, vua Lý Thái Tông tuổi đã cao mà chưa có hoàng tử nối dõi, thường đến các chùa cầu tự. Một đêm, vua chiêm bao thấy Phật Bà Quan Âm ngồi trên tòa sen lộng lẫy dắt vua lên đài.\n\nKhi tỉnh dậy, vua đem việc ấy hỏi đình thần. Thiền sư Thiền Tuệ khuyên vua dựng chùa dựng cột đá giữa hồ, làm tòa sen của Phật Bà đặt trên cột như đã thấy trong mộng, cho các nhà sư đi vòng quanh tụng kinh cầu nguyện kéo dài sự sống, vì thế đặt tên là chùa Diên Hựu (Kéo dài phúc lành).");
            b1.setDisplayOrder(1);
            b1.setStatus("published");

            StoryBlockEntity b2 = new StoryBlockEntity();
            b2.setId("b-2");
            b2.setProductId("prod-1");
            b2.setType("text_image");
            b2.setTitle("Đỉnh Cao Kiến Trúc Gỗ Đại Việt");
            b2.setSubtitle("Kết cấu xà nách 8 cánh vươn tỏa độc nhất vô nhị");
            b2.setContent("Khác biệt hoàn toàn với mọi ngôi chùa Phật giáo tại Đông Á, Chùa Một Cột chỉ sử dụng một trụ đá tròn đường kính 1.25m làm trụ cột nâng đỡ toàn bộ gian điện gỗ nặng hàng chục tấn.\n\nTám thanh xà gỗ lim to bản được gài chéo góc 45 độ tỏa đều như tám cánh sen đỡ lấy đài Phật. Toàn bộ trọng lực được truyền thẳng đứng xuống đáy hồ Linh Chiểu qua cơ chế khóa trọng lực tự nhiên không cần bất kỳ chiếc đinh kim loại nào.");
            b2.setMediaUrl("/images/chua-mot-cot.webp");
            b2.setMediaCaption("Chi tiết xà nách và chân đài sen nâng đỡ chính điện Chùa Một Cột");
            b2.setDisplayOrder(2);
            b2.setStatus("published");

            StoryBlockEntity b3 = new StoryBlockEntity();
            b3.setId("b-3");
            b3.setProductId("prod-1");
            b3.setType("timeline");
            b3.setTitle("Biến Thiên Ngàn Năm Của Đài Sen Thăng Long");
            b3.setContent("Trải qua gần một thiên niên kỷ với bao phen binh lửa và trùng tu, Chùa Một Cột vẫn trường tồn như một biểu tượng bất diệt của thủ đô.");
            b3.setTimelineItemsJson("[" +
                    "{\"year\":\"1049\",\"event\":\"Khởi dựng Diên Hựu Tự\",\"detail\":\"Vua Lý Thái Tông cho dựng chùa sau giấc mộng đài sen quan âm.\"}," +
                    "{\"year\":\"1105\",\"event\":\"Mở rộng quy mô thời Lý Nhân Tông\",\"detail\":\"Đào hồ Linh Chiểu bao quanh, bắc cầu vòng hoa dẫn vào chính điện.\"}," +
                    "{\"year\":\"1249\",\"event\":\"Trùng tu thời Trần\",\"detail\":\"Vua Trần Thái Tông cho tu bổ chùa sau chiến thắng kháng chiến Nguyên Mông lần một.\"}," +
                    "{\"year\":\"1954\",\"event\":\"Bi kịch phá hủy\",\"detail\":\"Ngày 09/11/1954, trước khi rút khỏi Hà Nội, quân viễn chinh Pháp đã đặt mìn giật sập ngôi chùa.\"}," +
                    "{\"year\":\"1955\",\"event\":\"Phục dựng nguyên trạng\",\"detail\":\"Kiến trúc sư Nguyễn Bá Lăng cùng chính phủ Việt Nam phục dựng hoàn hảo theo hồ sơ khảo cứu triều Nguyễn.\"}" +
                    "]");
            b3.setDisplayOrder(3);
            b3.setStatus("published");

            StoryBlockEntity b4 = new StoryBlockEntity();
            b4.setId("b-4");
            b4.setProductId("prod-1");
            b4.setType("highlight");
            b4.setTitle("Đài Sen Trên Cột Đá — Biểu Tượng Kiến Trúc Độc Nhất Vô Nhị Châu Á");
            b4.setSubtitle("Sự dung hợp tuyệt mỹ giữa Đạo Phật và Nghệ Thuật Điêu Khắc Đại Việt");
            b4.setContent("Hoa sen vươn lên từ bùn lầy nở rộ giữa cõi trần, cột đá sừng sững như tâm bất biến giữa dòng đời vạn biến.");
            b4.setHighlightBadge("Di Sản Quốc Gia Đặc Biệt");
            b4.setDisplayOrder(4);
            b4.setStatus("published");

            storyBlockRepo.saveAll(Arrays.asList(b1, b2, b3, b4));
        }

        // Tự động dọn dẹp khối b-5 nếu còn sót lại trong cơ sở dữ liệu
        if (storyBlockRepo.existsById("b-5")) {
            storyBlockRepo.deleteById("b-5");
        }

        // Seed Blogs
        if (blogPostRepo.count() == 0) {
            BlogPostEntity blog1 = new BlogPostEntity();
            blog1.setId("blog-1");
            blog1.setTitle("Bí quyết ghép mộng gỗ trong kiến trúc cổ truyền Việt Nam");
            blog1.setSlug("bi-quyet-ghep-mong-go-kien-truc-co-truyen");
            blog1.setExcerpt("Nghệ thuật liên kết rường cột không một giọt keo dán hay chiếc đinh sắt, bí mật tạo nên sự dẻo dai trường tồn qua bão táp ngàn năm.");
            blog1.setContent("Kiến trúc truyền thống Việt Nam dựa trên kết cấu khung gỗ chịu lực. Trong đó, các cấu kiện từ cột, xà, quá giang, kẻ, bẩy đều được nối với nhau bằng các khớp mộng gỗ tinh xảo.\n\nNgười thợ mộc xưa quan niệm: Ngôi nhà cũng như cơ thể con người, các khớp mộng là khớp xương giúp ngôi nhà có thể co giãn, uốn lượn theo từng cơn địa chấn hay giông bão mà không bị đổ gãy.");
            blog1.setCoverImage("/images/chua-mot-cot.webp");
            blog1.setAuthor("TS. Trần Đức Anh Sơn");
            blog1.setCategory("Kỹ Nghệ Di Sản");
            blog1.setReadTime("6 phút đọc");
            blog1.setPublishedAt("2026-03-28");
            blog1.setStatus("published");

            BlogPostEntity blog2 = new BlogPostEntity();
            blog2.setId("blog-2");
            blog2.setTitle("Số hóa di sản: Khi công nghệ in 3D làm sống lại ký ức Thăng Long");
            blog2.setSlug("so-hoa-di-san-in-3d-thang-long");
            blog2.setExcerpt("Hành trình tái hiện Chùa Một Cột và Khuê Văn Các với độ chính xác đến từng micro-mét từ dữ liệu khảo cổ học.");
            blog2.setContent("Trong thời đại công nghệ số, việc bảo tồn di sản không còn giới hạn ở các bức ảnh chụp hay mô hình tĩnh sau tủ kính bảo tàng. VIETTORIA ứng dụng công nghệ in 3D nano-layer và vật liệu sinh học BioPETG để mang di sản đến tận tay công chúng.");
            blog2.setCoverImage("/images/hoang-thanh-thang-long.webp");
            blog2.setAuthor("Nhóm Nghiên Cứu Viettoria");
            blog2.setCategory("Bảo Tồn Số");
            blog2.setReadTime("8 phút đọc");
            blog2.setPublishedAt("2026-03-25");
            blog2.setStatus("published");

            BlogPostEntity blog3 = new BlogPostEntity();
            blog3.setId("blog-3");
            blog3.setTitle("Hồ Linh Chiểu và triết lý đóa sen ngàn năm nơi lòng đất Việt");
            blog3.setSlug("ho-linh-chieu-triet-ly-doa-sen");
            blog3.setExcerpt("Khám phá chiều sâu tâm linh và biểu tượng học Phật giáo trong cảnh quan tổng thể chùa Diên Hựu.");
            blog3.setContent("Hồ Linh Chiểu không chỉ đơn thuần là một hồ nước tạo cảnh quan, mà là một biểu tượng thiêng liêng của cõi Niết Bàn tịnh độ trong vũ trụ quan Phật giáo Đại thừa.");
            blog3.setCoverImage("/images/van-mieu-quoc-tu-giam.webp");
            blog3.setAuthor("Thích Tuệ Minh");
            blog3.setCategory("Lịch Sử & Triết Lý");
            blog3.setReadTime("5 phút đọc");
            blog3.setPublishedAt("2026-03-20");
            blog3.setStatus("published");

            blogPostRepo.saveAll(Arrays.asList(blog1, blog2, blog3));
        }

        // Seed Media Items
        if (mediaItemRepo.count() == 0) {
            MediaItemEntity m1 = new MediaItemEntity();
            m1.setId("m-1");
            m1.setName("Chùa Một Cột Thực Địa");
            m1.setFileName("chua-mot-cot.webp");
            m1.setFileType("image/webp");
            m1.setFileSize(299290);
            m1.setUrl("/images/chua-mot-cot.webp");
            m1.setAltText("Ảnh chụp góc chính diện Chùa Một Cột Hà Nội");

            MediaItemEntity m2 = new MediaItemEntity();
            m2.setId("m-2");
            m2.setName("Văn Miếu Quốc Tử Giám");
            m2.setFileName("van-mieu-quoc-tu-giam.webp");
            m2.setFileType("image/webp");
            m2.setFileSize(297804);
            m2.setUrl("/images/van-mieu-quoc-tu-giam.webp");
            m2.setAltText("Khuê Văn Các và giếng Thiên Quang");

            MediaItemEntity m3 = new MediaItemEntity();
            m3.setId("m-3");
            m3.setName("Hoàng Thành Thăng Long");
            m3.setFileName("hoang-thanh-thang-long.webp");
            m3.setFileType("image/webp");
            m3.setFileSize(191866);
            m3.setUrl("/images/hoang-thanh-thang-long.webp");
            m3.setAltText("Kỳ Đài Cột Cờ Hà Nội");

            MediaItemEntity m4 = new MediaItemEntity();
            m4.setId("m-4");
            m4.setName("Hoa Văn Trống Đồng");
            m4.setFileName("trong-dong.webp");
            m4.setFileType("image/webp");
            m4.setFileSize(132220);
            m4.setUrl("/images/trong-dong.webp");
            m4.setAltText("Họa tiết mặt trống đồng Đông Sơn");

            mediaItemRepo.saveAll(Arrays.asList(m1, m2, m3, m4));
        }
    }

    private void syncAdminUserFromEnvironment() {
        AdminUserEntity admin = adminUserRepo.findByUsername(adminUsername)
                .or(() -> adminUserRepo.findById("admin-1"))
                .orElseGet(() -> new AdminUserEntity(
                        "admin-1",
                        adminUsername,
                        passwordEncoder.encode(adminPassword),
                        "Quản Trị Viên Viettoria",
                        "ADMIN"));

        admin.setUsername(adminUsername);
        if (admin.getPassword() == null || !passwordEncoder.matches(adminPassword, admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(adminPassword));
        }
        admin.setFullName("Quản Trị Viên Viettoria");
        admin.setRole("ADMIN");
        adminUserRepo.save(admin);
    }
}
