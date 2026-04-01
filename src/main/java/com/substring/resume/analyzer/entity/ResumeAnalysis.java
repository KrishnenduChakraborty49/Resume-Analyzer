package com.substring.resume.analyzer.entity;

import com.substring.resume.analyzer.payload.ExperienceGapAnalysis;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "resume_analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Scores
    private int technicalScore;
    private int communicationScore;
    private int domainScore;
    private int experienceScore;
    private int atsScore;
    private int matchingPercentage;

    // Nested Object (Stored in the same table)
    @Embedded
    private ExperienceGapAnalysis experienceGapAnalysis;

    // Verdicts and Summaries
    private String roleFitLevel;
    @Column(columnDefinition = "TEXT")
    private String suggestions;
    @Column(columnDefinition = "TEXT")
    private String improvedResumeMarkdown;
    @Column(columnDefinition = "TEXT")
    private String finalRecruiterVerdict;
    private String careerReadinessLevel;
    @Column(columnDefinition = "TEXT")
    private String positiveSummaryForCandidate;

    // Lists (Stored in separate joined tables)
    @ElementCollection
    private List<String> strengths;

    @ElementCollection
    private List<String> improvements;

    @ElementCollection
    private List<String> missingSkills;

    @ElementCollection
    private List<String> recommendedTechnologies;

    @ElementCollection
    private List<String> recommendedCertifications;

    @ElementCollection
    private List<String> keywordGaps;

    @ElementCollection
    private List<String> atsRisks;

    @ElementCollection
    private List<String> resumeOptimizationTips;

    @ElementCollection
    private List<String> shortTermLearningGoals;

    @ElementCollection
    private List<String> longTermLearningGoals;

    @ElementCollection
    private List<String> highImpactResumeFixes;

    @ElementCollection
    private List<String> alternativeRoles;
}
