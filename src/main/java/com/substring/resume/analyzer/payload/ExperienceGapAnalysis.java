package com.substring.resume.analyzer.payload;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceGapAnalysis{
    private  int requiredYears;
    private int actualYears;
    private String gapSummary;
}


