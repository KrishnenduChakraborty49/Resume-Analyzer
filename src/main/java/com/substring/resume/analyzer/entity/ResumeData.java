package com.substring.resume.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName, email, phone, location;
    private String linkedIn, github, portfolio;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String skills;           // JSON: ["Java","React",...]

    @Column(columnDefinition = "TEXT")
    private String experience;       // JSON array of experience objects

    @Column(columnDefinition = "TEXT")
    private String education;        // JSON array of education objects

    @Column(columnDefinition = "TEXT")
    private String projects;         // JSON array of project objects

    @Column(columnDefinition = "TEXT")
    private String certifications;   // JSON array of certification objects

    private Integer templateId;      // 1 to 5
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
