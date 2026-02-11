package com.dhananjay.agnhub.service.HelperMethod;

import java.time.Month;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResumeParser {

    private static final Map<String, Integer> MONTHS_MAP = new HashMap<>();
    static {
        for (Month m : Month.values()) {
            MONTHS_MAP.put(m.name().toLowerCase(), m.getValue());
        }
    }

    /**
     * Extract skills - Smart detection for Resume vs Job Description
     * For RESUMES: Extract ONLY from TECHNICAL SKILLS section
     * For JOB DESCRIPTIONS: Dynamic extraction from entire text
     */
    public static List<String> extractSkills(String text) {
        Set<String> skills = new LinkedHashSet<>();

        // Try to find TECHNICAL SKILLS section first (for resumes)
        Pattern sectionPattern = Pattern.compile(
                "(?i)TECHNICAL\\s+SKILLS?(.*?)(?=\\n[A-Z][A-Z\\s]{5,}:|\\z)",
                Pattern.DOTALL
        );
        Matcher sectionMatcher = sectionPattern.matcher(text);

        if (sectionMatcher.find()) {
            // RESUME MODE: Extract from TECHNICAL SKILLS section only
            String skillSection = sectionMatcher.group(1);
            String[] lines = skillSection.split("\\n");

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Remove bullet points
                line = line.replaceAll("^[•◦\\-*\\d\\.]+\\s*", "");

                // Extract skills from lines with category: skills format
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        String skillsPart = parts[1].trim();
                        String[] individualSkills = skillsPart.split(",");
                        for (String skill : individualSkills) {
                            skill = skill.trim();
                            // Filter out numbers like CGPA
                            if (!skill.isEmpty() && skill.length() > 1 && !skill.matches("^[0-9.]+$")) {
                                skills.add(skill);
                            }
                        }
                    }
                }
            }
        } else {
            // JOB DESCRIPTION Extraction
            skills.addAll(extractFromSkillSection(text));
            skills.addAll(extractFromLists(text));
            skills.addAll(extractTechnicalTerms(text));
        }

        return deduplicateCaseInsensitive(new ArrayList<>(skills));
    }

    //<<-----------Extract from sections like "TECHNICAL SKILLS:", "Required Skills:", etc.----------->>

    private static Set<String> extractFromSkillSection(String text) {
        Set<String> skills = new LinkedHashSet<>();

        Pattern sectionHeaderPattern = Pattern.compile(
                "(?i)^.*?(skills?|tools?|technologies|tech\\s+stack|expertise|qualifications?|requirements?):.*$",
                Pattern.MULTILINE
        );

        Matcher headerMatcher = sectionHeaderPattern.matcher(text);

        while (headerMatcher.find()) {
            int sectionStart = headerMatcher.end();
            int sectionEnd = findSectionEnd(text, sectionStart);

            if (sectionEnd > sectionStart) {
                String sectionContent = text.substring(sectionStart, sectionEnd);
                String[] lines = sectionContent.split("\\n");

                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;

                    line = line.replaceAll("^\\s*[•◦\\-*\\d\\.]+\\s*", "");

                    if (line.contains(":")) {
                        String[] parts = line.split(":", 2);
                        if (parts.length == 2) {
                            extractSkillsFromText(parts[1], skills);
                        }
                    } else {
                        extractSkillsFromText(line, skills);
                    }
                }
            }
        }
        return skills;
    }

    //<<------------------------Extract from comma/semicolon separated lists--------------------------->>

    private static Set<String> extractFromLists(String text) {
        Set<String> skills = new LinkedHashSet<>();

        String[] lines = text.split("\\n");

        for (String line : lines) {
            line = line.trim();
            if (line.length() > 200) continue;

            long commaCount = line.chars().filter(ch -> ch == ',').count();

            if (commaCount >= 2) {
                extractSkillsFromText(line, skills);
            }
        }

        return skills;
    }

    //<<----------------------------Extract technical terms based on patterns---------------------------->>

    private static Set<String> extractTechnicalTerms(String text) {
        Set<String> terms = new LinkedHashSet<>();

        Pattern capitalPattern = Pattern.compile(
                "\\b([A-Z][A-Za-z]*(?:[A-Z][a-z]*)*|[A-Z]{2,}|[A-Z][a-z]+\\.js|[A-Z]\\+\\+|C#)\\b"
        );

        Matcher matcher = capitalPattern.matcher(text);
        while (matcher.find()) {
            String term = matcher.group(1).trim();
            if (term.length() >= 2 && term.length() <= 30) {
                terms.add(term);
            }
        }

        Pattern specialPattern = Pattern.compile(
                "\\b([A-Za-z]+[/#.+][A-Za-z0-9.+#/]+)\\b"
        );

        Matcher specialMatcher = specialPattern.matcher(text);
        while (specialMatcher.find()) {
            String term = specialMatcher.group(1).trim();
            if (term.length() >= 2 && term.length() <= 30) {
                terms.add(term);
            }
        }
        return filterTechnicalTerms(terms, text);
    }

     //<<-----------------------------------Extract skills from text fragment------------------------------>>

    private static void extractSkillsFromText(String text, Set<String> skills) {
        String[] items = text.split("[,;|/]");

        for (String item : items) {
            item = item.trim();
            item = item.replaceAll("^(?i)(and|or|with|using|via|through|in|on)\\s+", "");
            item = item.replaceAll("^[\\s\\-•◦*]+|[\\s\\-•◦*]+$", "");
            item = item.replaceAll("[.!?]$", "");

            if (isValidSkillCandidate(item)) {
                skills.add(item);
            }
        }
    }

     //<<-------------------------------- Filter technical terms by frequency---------------------------------->>

    private static Set<String> filterTechnicalTerms(Set<String> terms, String fullText) {
        Set<String> filtered = new LinkedHashSet<>();

        Map<String, Integer> wordFrequency = new HashMap<>();
        String[] words = fullText.split("\\s+");
        for (String word : words) {
            word = word.replaceAll("[^A-Za-z0-9#+.]", "").toLowerCase();
            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
        }

        for (String term : terms) {
            String lowerTerm = term.toLowerCase();
            int frequency = wordFrequency.getOrDefault(lowerTerm, 0);

            if (frequency >= 2 || hasTechnicalPattern(term)) {
                filtered.add(term);
            }
        }

        return filtered;
    }

     //<<--------------------------------Check if term has technical patterns-------------------------------->>

    private static boolean hasTechnicalPattern(String term) {
        if (term == null || term.isEmpty()) return false;

        if (term.matches("^[A-Z]{2,}$")) {
            return true;
        }
        if (term.matches(".*\\d+.*")) {
            return true;
        }
        if (term.matches(".*[.#+/].*")) {
            return true;
        }
        if (term.matches("^[A-Z][a-z]+[A-Z].*")) {
            return true;
        }

        String lower = term.toLowerCase();
        return lower.endsWith(".js") ||
                lower.endsWith("sql") ||
                lower.endsWith("script") ||
                lower.equals("c++") ||
                lower.equals("c#");
    }

     //<<---------------------------Validate if string is a valid skill candidate----------------------->>

    private static boolean isValidSkillCandidate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        text = text.trim();

        if (text.length() < 2 || text.length() > 50) {
            return false;
        }
        if (!text.matches(".*[a-zA-Z].*")) {
            return false;
        }
        if (text.matches("^\\d+$")) {
            return false;
        }
        if (text.matches("^[0-9.]+$")) {
            return false;
        }
        if (text.matches("(?i)^(https?://|www\\.).*")) {
            return false;
        }
        if (text.matches(".*[.!?]\\s+.*")) {
            return false;
        }

        String[] words = text.split("\\s+");
        if (words.length > 5) {
            return false;
        }

        if (text.matches("^[a-z]+$") && text.length() <= 4 && !hasTechnicalPattern(text)) {
            return false;
        }

        return true;
    }

    //<<------------------------------------ Find section end-------------------------->>

    private static int findSectionEnd(String text, int start) {
        Pattern nextHeaderPattern = Pattern.compile("\\n\\s*[A-Z][A-Z\\s]{8,}[:\\n]|\\n\\n|$");
        Matcher matcher = nextHeaderPattern.matcher(text);
        if (matcher.find(start)) {
            return matcher.start();
        }
        return text.length();
    }

    //<<-------------------------------Extract education degrees only---------------------------->>

    public static List<String> extractEducation(String text) {
        Set<String> education = new LinkedHashSet<>();

        Pattern degreePattern = Pattern.compile(
                "\\b(B\\.?\\s*Tech|BCA|M\\.?\\s*Tech|MCA|Bachelor'?s?|Master'?s?|B\\.?E\\.?|M\\.?E\\.?|MBA|PhD)\\b",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = degreePattern.matcher(text);
        while (matcher.find()) {
            education.add(matcher.group(1));
        }

        return deduplicateCaseInsensitive(new ArrayList<>(education));
    }

    //<<-----------------------Extract projects ONLY from PROJECTS section---------------------------->>

    public static List<String> extractProjects(String text) {
        Set<String> projects = new LinkedHashSet<>();

        Pattern projectSectionPattern = Pattern.compile(
                "(?i)PROJECTS?(.*?)(?=\\n(?:TECHNICAL\\s+SKILLS?|EDUCATION|CERTIFICATIONS?|EXPERIENCE|$))",
                Pattern.DOTALL
        );

        Matcher sectionMatcher = projectSectionPattern.matcher(text);

        if (sectionMatcher.find()) {
            String projectSection = sectionMatcher.group(1);

            Pattern projectPattern = Pattern.compile(
                    "^[•◦\\-]\\s+([A-Za-z0-9\\s&-]+?)\\s+(January|February|March|April|May|June|July|August|September|October|November|December)\\s+\\d{4}$",
                    Pattern.MULTILINE | Pattern.CASE_INSENSITIVE
            );

            Matcher matcher = projectPattern.matcher(projectSection);
            while (matcher.find()) {
                String projectName = matcher.group(1).trim();
                projectName = projectName.replaceAll("\\s+", " ");
                projects.add(projectName);
            }
        }

        return new ArrayList<>(projects);
    }

    //<<---------------------Extract total experience ONLY from EXPERIENCE section------------------------>>

    public static String extractExperience(String text) {
        Pattern experienceSectionPattern = Pattern.compile(
                "(?i)EXPERIENCE(.*?)(?=\\n(?:PROJECTS?|TECHNICAL\\s+SKILLS?|EDUCATION|CERTIFICATIONS?|$))",
                Pattern.DOTALL
        );

        Matcher sectionMatcher = experienceSectionPattern.matcher(text);
        String experienceSection = "";

        if (sectionMatcher.find()) {
            experienceSection = sectionMatcher.group(1);
        } else {
            return "Not specified";
        }

        Pattern dateRangePattern = Pattern.compile(
                "(January|February|March|April|May|June|July|August|September|October|November|December)\\s+(\\d{4})\\s*-\\s*(January|February|March|April|May|June|July|August|September|October|November|December)\\s+(\\d{4})",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = dateRangePattern.matcher(experienceSection);
        int totalMonths = 0;

        while (matcher.find()) {
            String startMonthStr = matcher.group(1).toLowerCase();
            int startYear = Integer.parseInt(matcher.group(2));
            String endMonthStr = matcher.group(3).toLowerCase();
            int endYear = Integer.parseInt(matcher.group(4));

            Integer startMonth = MONTHS_MAP.get(startMonthStr);
            Integer endMonth = MONTHS_MAP.get(endMonthStr);

            if (startMonth != null && endMonth != null) {
                int months = (endYear - startYear) * 12 + (endMonth - startMonth) + 1;
                totalMonths += months;
            }
        }

        if (totalMonths == 0) return "Not specified";

        int years = totalMonths / 12;
        int months = totalMonths % 12;

        StringBuilder result = new StringBuilder();
        if (years > 0) {
            result.append(years).append(" year").append(years > 1 ? "s" : "");
        }
        if (months > 0) {
            if (years > 0) result.append(" ");
            result.append(months).append(" month").append(months > 1 ? "s" : "");
        }

        return result.toString();
    }

    //<<---------------------------Deduplicate list case-insensitively--------------------------------->>

    private static List<String> deduplicateCaseInsensitive(List<String> items) {
        Map<String, String> seen = new LinkedHashMap<>();

        for (String item : items) {
            if (item == null || item.trim().isEmpty()) continue;

            String normalized = item.trim().toLowerCase();
            if (!seen.containsKey(normalized)) {
                seen.put(normalized, item.trim());
            }
        }

        return new ArrayList<>(seen.values());
    }
}