package com.viettoria.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "VIETTORIA Backend API",
                version = "1.0.0",
                description = "REST API cho nội dung di sản, mô hình 3D và hệ thống quản trị VIETTORIA."
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
    public OpenApiCustomizer adminEndpointsRequireJwt() {
        return openApi -> openApi.getPaths().forEach((path, pathItem) -> {
            if (path.startsWith("/api/admin/") && !path.equals("/api/admin/auth/login")) {
                pathItem.readOperations().forEach(operation ->
                        operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                );
            }
        });
    }
}
