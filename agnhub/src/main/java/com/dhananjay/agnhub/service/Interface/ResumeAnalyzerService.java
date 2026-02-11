package com.dhananjay.agnhub.service.Interface;

import com.dhananjay.agnhub.dto.JobDescriptionCompareRequestDTO;
import com.dhananjay.agnhub.dto.JobDescriptionCompareResponseDTO;
import com.dhananjay.agnhub.dto.ResumeAnalyzeRequestDTO;
import com.dhananjay.agnhub.dto.ResumeAnalyzeResponseDTO;
import org.springframework.data.domain.Page;

public interface ResumeAnalyzerService {
    ResumeAnalyzeResponseDTO analyzeResume(ResumeAnalyzeRequestDTO request);

    JobDescriptionCompareResponseDTO compareWithJob(JobDescriptionCompareRequestDTO request);

    Page<ResumeAnalyzeResponseDTO> getAllResumes(int page, int size);

    Page<JobDescriptionCompareResponseDTO> getAllJobComparisons(int page, int size);

    void deleteResume(Long resumeAnalyzeId);

    void deleteJobMatch(Long jobDescriptionCompareId);

    JobDescriptionCompareResponseDTO getJobMatchById(Long id);

    ResumeAnalyzeResponseDTO getResumeById(Long id);
}
