package com.substring.resume.analyzer.services.implementation;

import com.substring.resume.analyzer.Exception.InvalidRequestException;
import com.substring.resume.analyzer.payload.ExperienceGapAnalysis;
import com.substring.resume.analyzer.entity.ResumeAnalysis;
import com.substring.resume.analyzer.payload.ResumeAnalysisResult;
import com.substring.resume.analyzer.repository.ResumeAnalysisRepository;
import com.substring.resume.analyzer.services.ResumeService;
import com.substring.resume.analyzer.services.TextCleaner;
import com.substring.resume.analyzer.services.TextExtractor;
import com.substring.resume.analyzer.utils.PromptLoader;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private static final Logger logger = LoggerFactory.getLogger(ResumeServiceImpl.class);

    private final TextExtractor textExtractor;
    private final TextCleaner textCleaner;
    private final ChatClient chatClient;
    private final ResumeAnalysisRepository repository; // 1️⃣ Inject Repository

    @Override
    public ResumeAnalysisResult analyzeResume(MultipartFile resumeFile, String jobProfile) throws IOException {

        // 1. Validation
        if (resumeFile == null || resumeFile.isEmpty()) {
            throw new InvalidRequestException("Resume file is missing");
        }
        if (!"application/pdf".equals(resumeFile.getContentType())) {
            throw new InvalidRequestException("Only PDF resumes are supported");
        }

        // 2. Extraction & Cleaning
        String resumeText = textExtractor.fromPdf(resumeFile);
        if (resumeText == null || resumeText.isBlank()) {
            throw new InvalidRequestException("Resume contains no readable text");
        }
        String cleanedText = textCleaner.clean(resumeText);

        // 3. Setup Structured Output Converter
        var converter = new BeanOutputConverter<>(ResumeAnalysisResult.class);

        // 4. Load and Format Prompt
        String templateText = PromptLoader.load("prompts/resume-analyze.prompt");

        try {
            // 5. Execute AI call
            ResumeAnalysisResult result = chatClient.prompt()
                    .user(u -> u.text(templateText)
                            .params(Map.of(
                                    "resume", cleanedText,
                                    "jobProfile", jobProfile,
                                    "format", converter.getFormat()
                            )))
                    .call()
                    .entity(converter);

            // 6️⃣ Map DTO to Entity and Save to DB
            if (result != null) {
                saveAnalysisToDatabase(result);
            }

            logger.info("Resume analysis completed and saved to database");
            return result;

        } catch (Exception ex) {
            logger.error("AI Analysis or Parsing failed: {}", ex.getMessage());
            throw new InvalidRequestException("Failed to analyze resume. The AI response could not be processed.");
        }
    }

    // Helper method to handle the mapping logic
    private void saveAnalysisToDatabase(ResumeAnalysisResult dto) {
        ResumeAnalysis entity = ResumeAnalysis.builder()
                .technicalScore(dto.technicalScore())
                .communicationScore(dto.communicationScore())
                .domainScore(dto.domainScore())
                .experienceScore(dto.experienceScore())
                .atsScore(dto.atsScore())
                .matchingPercentage(dto.matchingPercentage())
                .strengths(dto.strengths())
                .improvements(dto.improvements())
                .missingSkills(dto.missingSkills())
                .recommendedTechnologies(dto.recommendedTechnologies())
                .recommendedCertifications(dto.recommendedCertifications())
                .keywordGaps(dto.keywordGaps())
                .experienceGapAnalysis(new ExperienceGapAnalysis(
                        dto.experienceGapAnalysis().getRequiredYears(),
                        dto.experienceGapAnalysis().getActualYears(),
                        dto.experienceGapAnalysis().getGapSummary()
                ))
                .roleFitLevel(dto.roleFitLevel())
                .atsRisks(dto.atsRisks())
                .resumeOptimizationTips(dto.resumeOptimizationTips())
                .suggestions(dto.suggestions())
                .improvedResumeMarkdown(dto.improvedResumeMarkdown())
                .finalRecruiterVerdict(dto.finalRecruiterVerdict())
                .careerReadinessLevel(dto.careerReadinessLevel())
                .shortTermLearningGoals(dto.shortTermLearningGoals())
                .longTermLearningGoals(dto.longTermLearningGoals())
                .highImpactResumeFixes(dto.highImpactResumeFixes())
                .alternativeRoles(dto.alternativeRoles())
                .positiveSummaryForCandidate(dto.positiveSummaryForCandidate())
                .build();

        repository.save(entity);
    }
}