package com.viettoria.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "online");
        res.put("framework", "Spring Boot 3.4.2 (Java 17)");
        res.put("project", "VIETTORIA Heritage Collectibles API");
        res.put("version", "1.0.0");
        res.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(res);
    }
}
