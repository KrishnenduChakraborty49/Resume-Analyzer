package com.substring.resume.analyzer.controllers;

import com.substring.resume.analyzer.entity.ResumeData;
import com.substring.resume.analyzer.repository.ResumeRepository;
import com.substring.resume.analyzer.services.OllamaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired private ResumeRepository repository;
    @Autowired private OllamaService ollamaService;

    @PostMapping("/generate")
    public ResponseEntity<?> save(@RequestBody ResumeData data) {
        return ResponseEntity.ok(repository.save(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(repository.findAllByOrderByCreatedAtDesc());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok(Map.of("deleted", true));
    }

    // AI-powered summary generation endpoint
    @PostMapping("/generate-summary")
    public ResponseEntity<?> generateSummary(@RequestBody Map<String, String> body) {
        String name = body.getOrDefault("name", "");
        String domain = body.getOrDefault("domain", "Software Engineer");
        String skills = body.getOrDefault("skills", "");

        String system = "You are a professional resume writer. Write concise, impactful summaries. " +
            "Return ONLY the summary text, no quotes, no labels, no extra text.";
        String prompt = "Write a 3-sentence professional summary for a resume.\n" +
            "Name: " + name + "\nDomain: " + domain + "\nKey Skills: " + skills + "\n" +
            "Make it ATS-friendly and results-oriented.";

        String summary = ollamaService.chat(system, prompt);
        return ResponseEntity.ok(Map.of("summary", summary.trim()));
    }
}
