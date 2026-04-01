package com.substring.resume.analyzer.repository;

import com.substring.resume.analyzer.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis,Long> {
}
