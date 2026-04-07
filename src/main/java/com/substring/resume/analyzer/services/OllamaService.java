package com.substring.resume.analyzer.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${ollama.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String systemPrompt, String userMessage) {
        String url = ollamaBaseUrl + "/api/chat";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("stream", false);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userMessage));
        requestBody.put("messages", messages);

        // Important: set high num_ctx for long resumes
        Map<String, Object> options = new HashMap<>();
        options.put("num_ctx", 8192);
        options.put("temperature", 0.3);  // lower = more consistent JSON output
        requestBody.put("options", options);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);
        Map<String, Object> responseBody = response.getBody();
        Map<String, String> message = (Map<String, String>) responseBody.get("message");
        return message.get("content");
    }

    public float[] embed(String text) {
        String url = ollamaBaseUrl + "/api/embeddings";
        Map<String, Object> body = new HashMap<>();
        body.put("model", "mxbai-embed-large");
        body.put("prompt", text);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);
        List<Double> embedding = (List<Double>) response.getBody().get("embedding");
        float[] result = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) result[i] = embedding.get(i).floatValue();
        return result;
    }
}
