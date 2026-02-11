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
public class JobDescriptionCompareResponseDTO {
    private Long jobDescriptionCompareId;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private double matchPercentage;
    private List<String> improvementSuggestions;
    private LocalDateTime createdAt;
}