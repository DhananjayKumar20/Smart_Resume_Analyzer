package com.dhananjay.agnhub.controller;

import com.dhananjay.agnhub.dto.*;
import com.dhananjay.agnhub.exception.DataNotFoundException;
import com.dhananjay.agnhub.exception.JobMatchException;
import com.dhananjay.agnhub.exception.ResumeAnalysisException;
import com.dhananjay.agnhub.service.Interface.ResumeAnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ResumeAnalyzerController {

    @Autowired
    private ResumeAnalyzerService service;

//<<-----------------------------------Create Analyze-Resume---------------------------------->>

    @PostMapping("/analyze-resume")
    public ResponseEntity<ApiResponse<ResumeAnalyzeResponseDTO>> analyzeResume(
            @RequestBody ResumeAnalyzeRequestDTO request) {

        try {
            ResumeAnalyzeResponseDTO response = service.analyzeResume(request);
            return ResponseEntity.ok(new ApiResponse<>(response, null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(null, new ErrorResponse(e.getMessage())));

        } catch (ResumeAnalysisException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, new ErrorResponse(e.getMessage())));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, new ErrorResponse("Unexpected error: " + e.getMessage())));
        }
    }

//<<-----------------------------------Compare-Job-Description---------------------------------->>

    @PostMapping("/compare-job-description")
    public ResponseEntity<ApiResponse<JobDescriptionCompareResponseDTO>> compareJob(
            @RequestBody JobDescriptionCompareRequestDTO request) {

        try {
            JobDescriptionCompareResponseDTO response = service.compareWithJob(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(response, null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(null, new ErrorResponse(e.getMessage())));

        } catch (JobMatchException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, new ErrorResponse(e.getMessage())));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, new ErrorResponse("Unexpected error: " + e.getMessage())));
        }
    }

    //<<-----------------------------------Get Health Check---------------------------------->>

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        try {
            return ResponseEntity.ok(new ApiResponse<>("Resume Analyzer API is running!", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, new ErrorResponse(e.getMessage())));
        }
    }

//<<---------------------------------------Get All Analyze-Resume--------------------------------------->>

    @GetMapping("/analyze-resume")
    public ResponseEntity<ApiResponse<Page<ResumeAnalyzeResponseDTO>>> getAllResumes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        try {
            Page<ResumeAnalyzeResponseDTO> resumes = service.getAllResumes(page, size);
            return ResponseEntity.ok(new ApiResponse<>(resumes, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, new ErrorResponse("Failed to fetch resumes: " + e.getMessage())));
        }
    }

//<<-----------------------------------Get All Compare-Job-Description---------------------------------->>

    @GetMapping("/compare-job-description")
    public ResponseEntity<ApiResponse<Page<JobDescriptionCompareResponseDTO>>> getAllJobComparisons(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        try {
            Page<JobDescriptionCompareResponseDTO> comparisons = service.getAllJobComparisons(page, size);
            return ResponseEntity.ok(new ApiResponse<>(comparisons, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, new ErrorResponse("Failed to fetch job comparisons: " + e.getMessage())));
        }
    }

//<<------------------------------------------Delete Analyze-Resume------------------------------------->>

    @DeleteMapping("/analyze-resume/{id}")
    public ResponseEntity<ApiResponse<String>> deleteResumeById(@PathVariable("id") Long id) {
        try {
            service.deleteResume(id);
            return ResponseEntity.ok(new ApiResponse<>("Resume deleted successfully", null));

        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(null, new ErrorResponse(e.getMessage())));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, new ErrorResponse("Unexpected error occurred: " + e.getMessage())));
        }
    }

//<<-----------------------------------Delete Compare-Job-Description---------------------------------->>

    @DeleteMapping("/compare-job-description/{id}")
    public ResponseEntity<ApiResponse<String>> deleteJobMatchById(@PathVariable("id") Long id) {
        try {
            service.deleteJobMatch(id);
            return ResponseEntity.ok(new ApiResponse<>("Job match deleted successfully", null));

        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(null, new ErrorResponse(e.getMessage())));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, new ErrorResponse("Unexpected error occurred: " + e.getMessage())));
        }
    }

//<<--------------------------------------Get Analyze-Resume By Id--------------------------------------->>

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResumeAnalyzeResponseDTO>> getResumeById(@PathVariable Long id) {
        try {
            ResumeAnalyzeResponseDTO dto = service.getResumeById(id);
            return ResponseEntity.ok(new ApiResponse<>(dto, null));

        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(null, new ErrorResponse(e.getMessage())));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, new ErrorResponse("Unexpected error: " + e.getMessage())));
        }
    }

//<<-----------------------------------Get Compare-Job-Description by Id---------------------------------->>

    @GetMapping("/job-matches/{id}")
    public ResponseEntity<ApiResponse<JobDescriptionCompareResponseDTO>> getJobMatchById(@PathVariable Long id) {
        try {
            JobDescriptionCompareResponseDTO dto = service.getJobMatchById(id);
            return ResponseEntity.ok(new ApiResponse<>(dto, null));

        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(null, new ErrorResponse(e.getMessage())));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, new ErrorResponse("Unexpected error: " + e.getMessage())));
        }
    }

}
