package com.substring.resume.analyzer.controllers;

import com.substring.resume.analyzer.entity.AnalysisResult;
import com.substring.resume.analyzer.repository.AnalysisResultRepository;
import com.substring.resume.analyzer.services.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalysisController {

    @Autowired private AnalysisService analysisService;
    @Autowired private AnalysisResultRepository repository;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyze(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("domain") String domain) {
        try {
            AnalysisResult result = analysisService.analyze(resume, domain);
            // Just sending back the result is enough, but adding a flush can help sometimes
            return ResponseEntity.ok().body(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/analysis/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(repository.findAllByOrderByCreatedAtDesc());
    }

    @GetMapping("/analysis/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
