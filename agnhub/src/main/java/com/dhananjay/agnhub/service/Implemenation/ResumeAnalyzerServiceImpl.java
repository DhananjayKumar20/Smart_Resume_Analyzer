package com.dhananjay.agnhub.service.Implemenation;

import com.dhananjay.agnhub.dto.JobDescriptionCompareRequestDTO;
import com.dhananjay.agnhub.dto.JobDescriptionCompareResponseDTO;
import com.dhananjay.agnhub.dto.ResumeAnalyzeRequestDTO;
import com.dhananjay.agnhub.dto.ResumeAnalyzeResponseDTO;
import com.dhananjay.agnhub.entity.JobMatchEntity;
import com.dhananjay.agnhub.entity.ResumeAnalysisEntity;
import com.dhananjay.agnhub.exception.DataNotFoundException;
import com.dhananjay.agnhub.exception.JobMatchException;
import com.dhananjay.agnhub.exception.ResumeAnalysisException;
import com.dhananjay.agnhub.repository.JobMatchRepository;
import com.dhananjay.agnhub.repository.ResumeAnalysisRepository;
import com.dhananjay.agnhub.service.HelperMethod.ResumeParser;
import com.dhananjay.agnhub.service.Interface.ResumeAnalyzerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeAnalyzerServiceImpl implements ResumeAnalyzerService {

    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final JobMatchRepository jobMatchRepository;

//<<--------------------------------------Create Analyze-Resume--------------------------------------->>

    @Override
    @Transactional
    public ResumeAnalyzeResponseDTO analyzeResume(ResumeAnalyzeRequestDTO request) {

        // Validation
        validateRequest(request);

        String resumeText = request.getResumeText();

        try {
            // Extract sections
            var skills = removeDuplicates(ResumeParser.extractSkills(resumeText));
            var experience = ResumeParser.extractExperience(resumeText);
            var education = removeDuplicates(ResumeParser.extractEducation(resumeText));
            var projects = removeDuplicates(ResumeParser.extractProjects(resumeText));

            // ATS scoring logic
            int atsScore = calculateAtsScore(skills, experience, education, projects);

            // Generate suggestions
            var suggestions = removeDuplicates(generateSuggestions(skills, experience, education, projects));

            // Save to database
            ResumeAnalysisEntity entity = ResumeAnalysisEntity.builder()
                    .resumeText(resumeText)
                    .skills(skills)
                    .experience(experience)
                    .education(education)
                    .projects(projects)
                    .atsScore(atsScore)
                    .improvementSuggestions(suggestions)
                    .createdAt(LocalDateTime.now())
                    .build();

            ResumeAnalysisEntity savedEntity = resumeAnalysisRepository.save(entity);
            return convertToResumeDTO(savedEntity);

        } catch (Exception e) {
            throw new ResumeAnalysisException("Failed to analyze resume: " + e.getMessage(), e);
        }
    }

    private void validateRequest(ResumeAnalyzeRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body must not be null");
        }

        if (request.getResumeText() == null || request.getResumeText().isBlank()) {
            throw new IllegalArgumentException("Resume text must not be empty");
        }

        if (request.getResumeText().length() < 50) {
            throw new IllegalArgumentException("Resume text is too short to analyze (minimum 50 characters)");
        }
    }

    private int calculateAtsScore(List<String> skills, String experience,
                                  List<String> education, List<String> projects) {
        int atsScore = 0;
        if (!skills.isEmpty()) atsScore += 30;
        if (!experience.equals("Not specified")) atsScore += 30;
        if (!education.isEmpty()) atsScore += 20;
        if (!projects.isEmpty()) atsScore += 20;
        return Math.min(atsScore, 100);
    }

    private ArrayList<String> generateSuggestions(List<String> skills, String experience,
                                                  List<String> education, List<String> projects) {
        var suggestions = new ArrayList<String>();
        if (skills.isEmpty()) suggestions.add("Add your technical skills to improve visibility.");
        if (experience.equals("Not specified")) suggestions.add("Mention your work experience with dates.");
        if (education.isEmpty()) suggestions.add("Add your education details.");
        if (projects.isEmpty()) suggestions.add("Add projects to showcase your practical experience.");
        return suggestions;
    }

//<<-----------------------------------Compare-Job-Description---------------------------------->>

    @Override
    @Transactional
    public JobDescriptionCompareResponseDTO compareWithJob(JobDescriptionCompareRequestDTO request) {

        // Validation
        validateJobRequest(request);

        try {
            // Extract skills from resume and job description
            List<String> resumeSkills = removeDuplicates(ResumeParser.extractSkills(request.getResumeText()));
            List<String> jobSkills = removeDuplicates(ResumeParser.extractSkills(request.getJobDescriptionText()));

            // Find matched and missing skills (case-insensitive comparison)
            List<String> matchedSkills = new ArrayList<>();
            List<String> missingSkills = new ArrayList<>();

            // Create a case-insensitive set for comparison
            List<String> resumeSkillsLower = resumeSkills.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            for (String jobSkill : jobSkills) {
                String jobSkillLower = jobSkill.toLowerCase();

                if (resumeSkillsLower.contains(jobSkillLower)) {
                    // Add the original case from job description
                    if (!matchedSkills.contains(jobSkill)) {
                        matchedSkills.add(jobSkill);
                    }
                } else {
                    // Add to missing skills
                    if (!missingSkills.contains(jobSkill)) {
                        missingSkills.add(jobSkill);
                    }
                }
            }

            // Remove duplicates one more time to be safe
            matchedSkills = removeDuplicates(matchedSkills);
            missingSkills = removeDuplicates(missingSkills);

            // Calculate match percentage
            double matchPercentage = jobSkills.isEmpty() ? 0 :
                    ((double) matchedSkills.size() / jobSkills.size()) * 100;
            matchPercentage = Math.round(matchPercentage * 100.0) / 100.0;

            // Generate suggestions
            List<String> suggestions = removeDuplicates(generateJobSuggestions(missingSkills, matchPercentage));

            // Find or create resume analysis entity
            ResumeAnalysisEntity resumeEntity = findOrCreateResumeEntity(request.getResumeText());

            JobMatchEntity jobMatchEntity = JobMatchEntity.builder()
                    .resume(resumeEntity)
                    .jobDescription(request.getJobDescriptionText())
                    .matchedSkills(matchedSkills)
                    .missingSkills(missingSkills)
                    .matchPercentage(matchPercentage)
                    .improvementSuggestions(suggestions)
                    .createdAt(LocalDateTime.now())
                    .build();

            JobMatchEntity savedEntity = jobMatchRepository.save(jobMatchEntity);
            return convertToJobDTO(savedEntity, suggestions);

        } catch (Exception e) {
            throw new JobMatchException("Failed to compare job description: " + e.getMessage(), e);
        }
    }

    private void validateJobRequest(JobDescriptionCompareRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body must not be null");
        }

        if (request.getResumeText() == null || request.getResumeText().isBlank()) {
            throw new IllegalArgumentException("Resume text must not be empty");
        }

        if (request.getJobDescriptionText() == null || request.getJobDescriptionText().isBlank()) {
            throw new IllegalArgumentException("Job description text must not be empty");
        }

        if (request.getResumeText().length() < 50) {
            throw new IllegalArgumentException("Resume text is too short (minimum 50 characters)");
        }
    }

    private ResumeAnalysisEntity findOrCreateResumeEntity(String resumeText) {
        // Extract data from resume
        var skills = removeDuplicates(ResumeParser.extractSkills(resumeText));
        var experience = ResumeParser.extractExperience(resumeText);
        var education = removeDuplicates(ResumeParser.extractEducation(resumeText));
        var projects = removeDuplicates(ResumeParser.extractProjects(resumeText));

        ResumeAnalysisEntity entity = ResumeAnalysisEntity.builder()
                .resumeText(resumeText)
                .skills(skills)
                .experience(experience)
                .education(education)
                .projects(projects)
                .atsScore(0)
                .improvementSuggestions(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();
        return resumeAnalysisRepository.save(entity);
    }

    private List<String> generateJobSuggestions(List<String> missingSkills, double matchPercentage) {
        List<String> suggestions = new ArrayList<>();

        if (!missingSkills.isEmpty()) {
            if (missingSkills.size() <= 5) {
                suggestions.add("Add these missing skills: " + String.join(", ", missingSkills));
            } else {
                suggestions.add("Add these key missing skills: " +
                        String.join(", ", missingSkills.subList(0, 5)) + " and " +
                        (missingSkills.size() - 5) + " more");
            }
        }

        if (matchPercentage < 50) {
            suggestions.add("Your resume matches less than 50% of job requirements. Consider gaining experience in missing skills.");
        } else if (matchPercentage < 75) {
            suggestions.add("Good match! Adding the missing skills would make your profile stronger.");
        } else {
            suggestions.add("Excellent match! You have most of the required skills.");
        }

        return suggestions;
    }

    //<<---------------------Remove duplicates from list while preserving order---------------------->>

    private List<String> removeDuplicates(List<String> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        LinkedHashSet<String> seen = new LinkedHashSet<>();
        List<String> result = new ArrayList<>();

        for (String item : list) {
            String normalized = item.toLowerCase().trim();
            if (!seen.contains(normalized)) {
                seen.add(normalized);
                result.add(item.trim());
            }
        }

        return result;
    }

//<<--------------------------------------Get All Analyze-Resume--------------------------------------->>

    @Override
    @Transactional
    public Page<ResumeAnalyzeResponseDTO> getAllResumes(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ResumeAnalysisEntity> resumePage = resumeAnalysisRepository.findAll(pageable);

        return resumePage.map(this::convertToResumeDTO);
    }

//<<-----------------------------------Get All Compare-Job-Description---------------------------------->>

    @Override
    @Transactional
    public Page<JobDescriptionCompareResponseDTO> getAllJobComparisons(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JobMatchEntity> jobPage = jobMatchRepository.findAll(pageable);

        return jobPage.map(entity -> convertToJobDTO(entity, Collections.emptyList()));
    }

//<<--------------------------------------Delete Analyze-Resume--------------------------------------->>

    @Override
    @Transactional
    public void deleteResume(Long id) {

        ResumeAnalysisEntity entity = resumeAnalysisRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Resume not found with id: " + id));

        resumeAnalysisRepository.delete(entity);
    }

//<<-----------------------------------Delete Compare-Job-Description---------------------------------->>

    @Override
    @Transactional
    public void deleteJobMatch(Long id) {

        JobMatchEntity entity = jobMatchRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Job match not found with id: " + id));

        jobMatchRepository.delete(entity);
    }

//<<--------------------------------------Get Analyze-Resume By Id--------------------------------------->>

    @Override
    @Transactional
    public ResumeAnalyzeResponseDTO getResumeById(Long id) {

        ResumeAnalysisEntity entity = resumeAnalysisRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Resume not found with id: " + id));

        return convertToResumeDTO(entity);
    }

//<<-----------------------------------Get Compare-Job-Description by Id---------------------------------->>

    @Override
    @Transactional
    public JobDescriptionCompareResponseDTO getJobMatchById(Long id) {

        JobMatchEntity entity = jobMatchRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Job match not found with id: " + id));

        return convertToJobDTO(entity, entity.getImprovementSuggestions());
    }

    private ResumeAnalyzeResponseDTO convertToResumeDTO(ResumeAnalysisEntity entity) {
        return ResumeAnalyzeResponseDTO.builder()
                .resumeAnalyzeId(entity.getId())
                .skills(entity.getSkills())
                .experience(entity.getExperience())
                .education(entity.getEducation())
                .projects(entity.getProjects())
                .atsScore(entity.getAtsScore())
                .improvementSuggestions(entity.getImprovementSuggestions())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private JobDescriptionCompareResponseDTO convertToJobDTO(JobMatchEntity entity, List<String> improvementSuggestions) {
        return JobDescriptionCompareResponseDTO.builder()
                .jobDescriptionCompareId(entity.getId())
                .matchedSkills(entity.getMatchedSkills())
                .missingSkills(entity.getMissingSkills())
                .matchPercentage(entity.getMatchPercentage())
                .improvementSuggestions(entity.getImprovementSuggestions()) // now passed as argument
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
