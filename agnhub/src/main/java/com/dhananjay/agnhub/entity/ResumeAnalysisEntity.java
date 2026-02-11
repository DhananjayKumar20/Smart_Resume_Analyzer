package com.dhananjay.agnhub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "resume_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String resumeText;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "resume_skills", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "skill")
    private List<String> skills;

    private String experience;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "resume_education", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "education")
    private List<String> education;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "resume_projects", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "project")
    private List<String> projects;

    private int atsScore;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "resume_suggestions", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "suggestion")
    private List<String> improvementSuggestions;

    private LocalDateTime createdAt;
}
