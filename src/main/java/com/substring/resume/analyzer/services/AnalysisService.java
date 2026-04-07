package com.substring.resume.analyzer.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.substring.resume.analyzer.entity.AnalysisResult;
import com.substring.resume.analyzer.repository.AnalysisResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@Slf4j
public class AnalysisService {

    @Autowired private GroqService groqService;
    @Autowired private FileParserService fileParserService;
    @Autowired private AnalysisResultRepository repository;

    private final ObjectMapper mapper = new ObjectMapper();

    public AnalysisResult analyze(MultipartFile file, String domain) throws Exception {
        log.info("Starting Groq AI analysis for file: {} in domain: {}", file.getOriginalFilename(), domain);

        String resumeText = fileParserService.extractText(file);

        // Groq Context Limit is high, but 8000 chars is usually enough for a resume
        if (resumeText.length() > 8000) resumeText = resumeText.substring(0, 8000);

        String systemPrompt = "You are an elite ATS system and career consultant. " +
                "Analyze the provided resume text against the target domain. " +
                "Always respond ONLY with valid JSON. No markdown, no talking.";

        String userPrompt = "Target Domain: " + domain + "\n\n" +
                "Resume Material:\n" + resumeText + "\n\n" +
                "Perform a rigorous analysis. Respond EXACTLY in this JSON format:\n" +
                "{\n" +
                "  \"atsScore\": 0-100,\n" +
                "  \"technicalScore\": 0-100,\n" +
                "  \"communicationScore\": 0-100,\n" +
                "  \"domainScore\": 0-100,\n" +
                "  \"experienceScore\": 0-100,\n" +
                "  \"matchingPercentage\": 0-100,\n" +
                "  \"technicalProficiency\": { \"level\": \"\", \"score\": 0, \"breakdown\": {} },\n" +
                "  \"strengths\": [],\n" +
                "  \"improvements\": [],\n" +
                "  \"missingSkills\": [],\n" +
                "  \"recommendedTechnologies\": [],\n" +
                "  \"recommendedCertifications\": [],\n" +
                "  \"keywordGaps\": [],\n" +
                "  \"experienceGapAnalysis\": \"\",\n" +
                "  \"roleFitLevel\": \"High/Medium/Low\",\n" +
                "  \"atsRisks\": [],\n" +
                "  \"resumeOptimizationTips\": [],\n" +
                "  \"suggestions\": \"\",\n" +
                "  \"improvedResumeMarkdown\": \"\",\n" +
                "  \"finalRecruiterVerdict\": \"\",\n" +
                "  \"careerReadinessLevel\": \"\",\n" +
                "  \"shortTermLearningGoals\": [],\n" +
                "  \"longTermLearningGoals\": [],\n" +
                "  \"highImpactResumeFixes\": [],\n" +
                "  \"alternativeRoles\": [],\n" +
                "  \"positiveSummaryForCandidate\": \"\",\n" +
                "  \"overallFeedback\": \"\"\n" +
                "}";

        try {
            String raw = groqService.analyze(systemPrompt, userPrompt);
            JsonNode json = mapper.readTree(raw);

            AnalysisResult result = AnalysisResult.builder()
                    .resumeFileName(file.getOriginalFilename())
                    .domain(domain)
                    .atsScore(json.path("atsScore").asInt(0))
                    .technicalScore(json.path("technicalScore").asInt(0))
                    .communicationScore(json.path("communicationScore").asInt(0))
                    .domainScore(json.path("domainScore").asInt(0))
                    .experienceScore(json.path("experienceScore").asInt(0))
                    .matchingPercentage(json.path("matchingPercentage").asInt(0))
                    .technicalProficiency(json.path("technicalProficiency").toString())
                    .strengths(json.path("strengths").toString())
                    .improvements(json.path("improvements").toString())
                    .missingSkills(json.path("missingSkills").toString())
                    .recommendedTechnologies(json.path("recommendedTechnologies").toString())
                    .recommendedCertifications(json.path("recommendedCertifications").toString())
                    .keywordGaps(json.path("keywordGaps").toString())
                    .experienceGapAnalysis(json.path("experienceGapAnalysis").asText())
                    .roleFitLevel(json.path("roleFitLevel").asText())
                    .atsRisks(json.path("atsRisks").toString())
                    .resumeOptimizationTips(json.path("resumeOptimizationTips").toString())
                    .suggestions(json.path("suggestions").asText())
                    .improvedResumeMarkdown(json.path("improvedResumeMarkdown").asText())
                    .finalRecruiterVerdict(json.path("finalRecruiterVerdict").asText())
                    .careerReadinessLevel(json.path("careerReadinessLevel").asText())
                    .shortTermLearningGoals(json.path("shortTermLearningGoals").toString())
                    .longTermLearningGoals(json.path("longTermLearningGoals").toString())
                    .highImpactResumeFixes(json.path("highImpactResumeFixes").toString())
                    .alternativeRoles(json.path("alternativeRoles").toString())
                    .positiveSummaryForCandidate(json.path("positiveSummaryForCandidate").asText())
                    .overallFeedback(json.path("overallFeedback").asText())
                    .build();

            log.info("Saving full analysis result to DB.");
            return repository.save(result);

        } catch (Exception e) {
            log.error("Analysis failed: {}", e.getMessage());
            throw new RuntimeException("AI Analysis Error: " + e.getMessage());
        }
    }
}
