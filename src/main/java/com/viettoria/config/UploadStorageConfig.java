package com.viettoria.config;

import com.viettoria.service.MediaStorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UploadStorageConfig implements WebMvcConfigurer {

    private final MediaStorageService mediaStorageService;

    public UploadStorageConfig(MediaStorageService mediaStorageService) {
        this.mediaStorageService = mediaStorageService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = mediaStorageService.getUploadDirectory().toUri().toString();
        if (!location.endsWith("/")) location += "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
