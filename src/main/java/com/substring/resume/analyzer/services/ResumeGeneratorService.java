package com.substring.resume.analyzer.services;

import com.itextpdf.html2pdf.HtmlConverter;
import com.substring.resume.analyzer.entity.ResumeAnalysis;
import com.substring.resume.analyzer.repository.ResumeAnalysisRepository;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class ResumeGeneratorService {

    private final ResumeAnalysisRepository repository;
    private final SpringTemplateEngine templateEngine;

    public byte[] generatePdf(Long analysisId) throws Exception {
        // 1. Fetch data
        ResumeAnalysis analysis = repository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analysis not found"));

        // 2. Convert Markdown to HTML
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        String markdownHtml = renderer.render(parser.parse(analysis.getImprovedResumeMarkdown()));

        // 3. Prepare Thymeleaf
        Context context = new Context();
        context.setVariable("analysis", analysis);
        context.setVariable("improvedContentHtml", markdownHtml);
        String finalHtml = templateEngine.process("ats-resume", context);

        // 4. Generate PDF using iText
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            HtmlConverter.convertToPdf(finalHtml, os);
            return os.toByteArray();
        }
    }
}