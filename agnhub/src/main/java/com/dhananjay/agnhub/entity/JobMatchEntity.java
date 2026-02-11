package com.dhananjay.agnhub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job_match")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobMatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private ResumeAnalysisEntity resume;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "matched_skills", joinColumns = @JoinColumn(name = "job_match_id"))
    @Column(name = "skill")
    private List<String> matchedSkills;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "missing_skills", joinColumns = @JoinColumn(name = "job_match_id"))
    @Column(name = "skill")
    private List<String> missingSkills;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "improvement_suggestions", joinColumns = @JoinColumn(name = "job_match_id"))
    @Column(name = "suggestion")
    private List<String> improvementSuggestions;

    private double matchPercentage;

    private LocalDateTime createdAt;
}
