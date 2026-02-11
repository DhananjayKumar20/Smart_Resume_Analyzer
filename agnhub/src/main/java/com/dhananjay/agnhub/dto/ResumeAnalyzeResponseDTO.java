package com.dhananjay.agnhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalyzeResponseDTO {
    private Long resumeAnalyzeId;
    private List<String> skills;
    private String experience;
    private List<String> education;
    private List<String> projects;
    private int atsScore;
    private List<String> improvementSuggestions;
    private LocalDateTime createdAt;
}
