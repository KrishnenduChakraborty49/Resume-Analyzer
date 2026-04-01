package com.substring.resume.analyzer.controllers;

import com.substring.resume.analyzer.payload.ResumeAnalysisResult;
import com.substring.resume.analyzer.services.ResumeGeneratorService;
import com.substring.resume.analyzer.services.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/resume")
@RequiredArgsConstructor
public class ResumeController {
    private  final ResumeService resumeService;
    private final ResumeGeneratorService resumeGeneratorService;
    @PostMapping
    public ResponseEntity<?> analyzeResume(
            @RequestParam("resume") MultipartFile resumeFile,
            @RequestParam("jobProfile") String jobProfile
    ) throws IOException {

        ResumeAnalysisResult result = resumeService.analyzeResume(resumeFile, jobProfile);
        return  new ResponseEntity<>(result, HttpStatus.OK);


    }

    @GetMapping("/{id}/generate-pdf")
    public ResponseEntity<byte[]> downloadResume(@PathVariable Long id) {
        try {
            byte[] pdfContent = resumeGeneratorService.generatePdf(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Improved_Resume.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
