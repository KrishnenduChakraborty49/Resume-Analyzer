package com.substring.resume.analyzer.repository;

import com.substring.resume.analyzer.entity.ResumeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<ResumeData, Long> {
    List<ResumeData> findAllByOrderByCreatedAtDesc();
}
