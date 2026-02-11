package com.dhananjay.agnhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDescriptionCompareRequestDTO {
    private String resumeText;
    private String jobDescriptionText;
}