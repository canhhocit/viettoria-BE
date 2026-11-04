package com.viettoria.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.CacheControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    @Operation(summary = "Kiểm tra backend", description = "Endpoint công khai trả trạng thái cơ bản và thời điểm phản hồi của backend. Endpoint này không kiểm tra kết nối database.", tags = "Health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "online");
        response.put("framework", "Spring Boot 3.4.13 (Java 17)");
        response.put("project", "VIETTORIA Heritage Collectibles API");
        response.put("version", "1.0.0");
        response.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(response);
    }

    @GetMapping("/ping")
    @Operation(summary = "Ping backend và database", description = "Endpoint công khai dành cho cronjob/monitor bên ngoài. Mỗi lần gọi chạy SELECT 1 trên database; trả 200 khi backend và DB hoạt động, hoặc 503 khi không truy cập được DB.", tags = "Health")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("backend", "up");
        response.put("checkedAt", Instant.now().toString());

        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (!Integer.valueOf(1).equals(result)) {
                throw new IllegalStateException("Database health query returned an unexpected result");
            }

            response.put("status", "ok");
            response.put("database", "up");
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(response);
        } catch (DataAccessException exception) {
            logger.warn("Health ping could not reach the database");
            response.put("status", "degraded");
            response.put("database", "down");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .cacheControl(CacheControl.noStore())
                    .body(response);
        }
    }
}
