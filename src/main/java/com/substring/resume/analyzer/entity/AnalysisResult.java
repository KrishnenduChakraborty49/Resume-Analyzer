package com.substring.resume.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resumeFileName;
    private String domain;

    // Primary Scores
    private Integer atsScore;
    private Integer technicalScore;
    private Integer communicationScore;
    private Integer domainScore;
    private Integer experienceScore;
    private Integer matchingPercentage;

    // Detailed Analysis (Stored as JSON Strings)
    @Column(columnDefinition = "TEXT")
    private String technicalProficiency;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String improvements;

    @Column(columnDefinition = "TEXT")
    private String missingSkills;

    @Column(columnDefinition = "TEXT")
    private String recommendedTechnologies;

    @Column(columnDefinition = "TEXT")
    private String recommendedCertifications;

    @Column(columnDefinition = "TEXT")
    private String keywordGaps;

    @Column(columnDefinition = "TEXT")
    private String experienceGapAnalysis;

    private String roleFitLevel;

    @Column(columnDefinition = "TEXT")
    private String atsRisks;

    @Column(columnDefinition = "TEXT")
    private String resumeOptimizationTips;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @Column(columnDefinition = "TEXT")
    private String improvedResumeMarkdown;

    @Column(columnDefinition = "TEXT")
    private String finalRecruiterVerdict;

    private String careerReadinessLevel;

    @Column(columnDefinition = "TEXT")
    private String shortTermLearningGoals;

    @Column(columnDefinition = "TEXT")
    private String longTermLearningGoals;

    @Column(columnDefinition = "TEXT")
    private String highImpactResumeFixes;

    @Column(columnDefinition = "TEXT")
    private String alternativeRoles;

    @Column(columnDefinition = "TEXT")
    private String positiveSummaryForCandidate;

    @Column(columnDefinition = "TEXT")
    private String overallFeedback;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
