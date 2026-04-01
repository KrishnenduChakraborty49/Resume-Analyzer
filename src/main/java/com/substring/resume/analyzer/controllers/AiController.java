package com.substring.resume.analyzer.controllers;

import com.substring.resume.analyzer.payload.AiResponse;
import com.substring.resume.analyzer.services.implementation.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    @PostMapping("/ask")
    public ResponseEntity<AiResponse> askAi(
            @RequestParam("query") String query
    ){
        return new ResponseEntity<>(aiService.askAi(query), HttpStatus.OK);
    }
}
