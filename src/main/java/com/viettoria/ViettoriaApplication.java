package com.viettoria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class ViettoriaApplication {

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(ViettoriaApplication.class, args);
    }

    /**
     * Tự động nạp cấu hình từ tệp .env vào System Properties
     * nếu không có biến môi trường hệ thống tương ứng được gán trước.
     */
    private static void loadDotEnv() {
        File envFile = new File(".env");
        if (!envFile.exists()) {
            envFile = new File("source/viettoria-BE/.env");
        }
        if (envFile.exists() && envFile.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    int eqIdx = line.indexOf('=');
                    if (eqIdx > 0) {
                        String key = line.substring(0, eqIdx).trim();
                        String value = line.substring(eqIdx + 1).trim();
                        // Gỡ bỏ dấu nháy bọc nếu có
                        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                            (value.startsWith("'") && value.endsWith("'"))) {
                            value = value.substring(1, value.length() - 1);
                        }
                        if (System.getProperty(key) == null && System.getenv(key) == null) {
                            System.setProperty(key, value);
                        }
                    }
                }
                System.out.println("==> [VIETTORIA] Đã nạp thành công biến môi trường từ: " + envFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("==> [VIETTORIA] Không thể đọc tệp .env: " + e.getMessage());
            }
        }
    }
}
