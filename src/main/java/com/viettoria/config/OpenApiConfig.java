package com.viettoria.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "VIETTORIA Backend API",
                version = "1.0.0",
                description = "API cho website di sản và hệ thống quản trị VIETTORIA. " +
                        "Mỗi nhóm trong danh sách chọn bên Swagger UI tương ứng một module nghiệp vụ. " +
                        "Các response nghiệp vụ dùng cấu trúc ApiResponse; API health trả JSON trạng thái trực tiếp."
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("01-public-api")
                .displayName("01 · Public API — Website")
                .pathsToMatch("/api/public/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authServiceApi() {
        return GroupedOpenApi.builder()
                .group("02-auth-service")
                .displayName("02 · Auth Service — Đăng nhập")
                .pathsToMatch("/api/admin/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi catalogServiceApi() {
        return GroupedOpenApi.builder()
                .group("03-catalog-service")
                .displayName("03 · Catalog Service — Tác phẩm & bộ sưu tập")
                .pathsToMatch(
                        "/api/admin/products",
                        "/api/admin/products/*",
                        "/api/admin/collections",
                        "/api/admin/collections/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi editorialServiceApi() {
        return GroupedOpenApi.builder()
                .group("04-editorial-service")
                .displayName("04 · Editorial Service — Story & bài viết")
                .pathsToMatch(
                        "/api/admin/products/*/stories",
                        "/api/admin/products/*/stories/**",
                        "/api/admin/stories/**",
                        "/api/admin/blogs",
                        "/api/admin/blogs/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi mediaServiceApi() {
        return GroupedOpenApi.builder()
                .group("05-media-service")
                .displayName("05 · Media Service — Thư viện & tải tệp")
                .pathsToMatch("/api/admin/media", "/api/admin/media/**")
                .build();
    }

    @Bean
    public GroupedOpenApi operationsApi() {
        return GroupedOpenApi.builder()
                .group("06-operations-service")
                .displayName("06 · Operations — Dashboard & health check")
                .pathsToMatch("/api/admin/dashboard", "/api/health", "/api/health/**")
                .build();
    }

    @Bean
    public GlobalOpenApiCustomizer adminEndpointsRequireJwt() {
        return openApi -> openApi.getPaths().forEach((path, pathItem) -> {
            boolean publicAuthenticationEndpoint = path.equals("/api/admin/auth/login")
                    || path.equals("/api/admin/auth/demo");
            if (path.startsWith("/api/admin/") && !publicAuthenticationEndpoint) {
                pathItem.readOperations().forEach(operation ->
                        operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                );
            }
        });
    }
}
