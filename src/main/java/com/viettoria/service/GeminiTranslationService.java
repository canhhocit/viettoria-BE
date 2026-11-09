package com.viettoria.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Service
public class GeminiTranslationService {

    private static final Logger log = LoggerFactory.getLogger(GeminiTranslationService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.gemini.api-key:${GEMINI_API_KEY:}}")
    private String apiKey;

    @Value("${app.gemini.model:${GEMINI_MODEL:gemini-2.5-flash}}")
    private String model;

    public GeminiTranslationService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = objectMapper;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }

    /**
     * Translates a key-value map of text fields into the target language.
     *
     * @param sourceFields Map of field names to Vietnamese strings
     * @param targetLang   "en", "fr", "zh", "ko", or "ja"
     * @param domainContext Context description e.g. "Architectural blog post" or "Imperial monument"
     * @return Translated map of field names to localized strings, or null if translation failed
     */
    public Map<String, String> translateFields(Map<String, String> sourceFields, String targetLang, String domainContext) {
        if (sourceFields == null || sourceFields.isEmpty()) {
            return Collections.emptyMap();
        }

        if (!isConfigured()) {
            log.debug("Gemini API key is not configured. Automated translation skipped.");
            return null;
        }

        String langName = getLanguageFullName(targetLang);
        if (langName == null) {
            log.warn("Unsupported target language code: {}", targetLang);
            return null;
        }

        try {
            String inputJson = objectMapper.writeValueAsString(sourceFields);

            String prompt = String.format(
                    "You are the Chief Academic Curator and Cultural Heritage Translator for VIETTORIA " +
                    "(a prestigious Vietnamese museum & architectural heritage atelier).\n\n" +
                    "Task: Translate the following JSON object from Vietnamese into %s (%s).\n" +
                    "Context: %s.\n\n" +
                    "Strict Translation Guidelines:\n" +
                    "1. Tone: Heritage Atelier / High Editorial / Scholarly & Respectful. Avoid casual or generic machine translations.\n" +
                    "2. Historical terms & proper nouns: Translate accurately according to standard international academic nomenclature " +
                    "(e.g., Thăng Long, Diên Hựu, Khuê Văn Các, Văn Miếu, Ngọ Môn, Bến Nhà Rồng, dynasty names like Lý, Trần, Lê, Nguyễn).\n" +
                    "3. Architectural terminology: Accurately render traditional timber framing terms (mộng gỗ, liên hoa đài, xà nách, đấu củng, mái cong, etc.).\n" +
                    "4. Output format: Return strictly a valid JSON object matching the exact input keys. Do NOT add explanation or markdown code fences if possible, just the raw JSON object.\n\n" +
                    "Input JSON:\n%s",
                    langName, targetLang, domainContext, inputJson
            );

            Map<String, Object> requestBody = buildGeminiRequest(prompt);
            String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                    model, apiKey.trim());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseGeminiResponse(response.getBody());
            } else {
                log.warn("Gemini translation API returned HTTP {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to translate fields via Gemini (target: {}): {}", targetLang, e.getMessage());
            return null;
        }
    }

    private Map<String, Object> buildGeminiRequest(String prompt) {
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(textPart));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.2);
        generationConfig.put("responseMimeType", "application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("contents", Collections.singletonList(content));
        body.put("generationConfig", generationConfig);

        return body;
    }

    private Map<String, String> parseGeminiResponse(String responseJson) {
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode first = candidates.get(0);
                JsonNode textNode = first.path("content").path("parts").get(0).path("text");
                String rawText = textNode.asText().trim();

                // Clean markdown code blocks if present
                if (rawText.startsWith("```json")) {
                    rawText = rawText.substring(7);
                } else if (rawText.startsWith("```")) {
                    rawText = rawText.substring(3);
                }
                if (rawText.endsWith("```")) {
                    rawText = rawText.substring(0, rawText.length() - 3);
                }
                rawText = rawText.trim();

                return objectMapper.readValue(rawText, new TypeReference<Map<String, String>>() {});
            }
        } catch (Exception e) {
            log.error("Could not parse JSON output from Gemini response: {}", e.getMessage());
        }
        return null;
    }

    public static String getLanguageFullName(String code) {
        if (code == null) return null;
        switch (code.toLowerCase()) {
            case "en":
                return "English";
            case "fr":
                return "French (Français)";
            case "zh":
                return "Simplified Chinese (简体中文)";
            case "ko":
                return "Korean (한국어)";
            case "ja":
                return "Japanese (日本語)";
            default:
                return null;
        }
    }
}
