package com.substring.resume.analyzer.utils;

import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;

public class PromptLoader {
    public static String load(String path) {
        try (var is = new ClassPathResource(path).getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load prompt: " + path, e);
        }
    }
}
