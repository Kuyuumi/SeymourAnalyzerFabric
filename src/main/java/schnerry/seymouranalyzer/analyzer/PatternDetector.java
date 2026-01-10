package schnerry.seymouranalyzer.analyzer;

import schnerry.seymouranalyzer.config.ClothConfig;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Detects special hex patterns (paired, repeating, palindrome, AxBxCx) and word matches
 */
public class PatternDetector {
    private static PatternDetector INSTANCE;

    private PatternDetector() {}

    public static PatternDetector getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PatternDetector();
        }
        return INSTANCE;
    }

    /**
     * Detect special hex pattern
     * Returns: "paired", "repeating", "palindrome", "axbxcx", or null
     */
    public String detectPattern(String hex) {
        if (hex == null || hex.length() != 6) return null;

        hex = hex.toUpperCase();
        char[] chars = hex.toCharArray();

        // Check paired (AABBCC)
        if (chars[0] == chars[1] && chars[2] == chars[3] && chars[4] == chars[5]) {
            return "paired";
        }

        // Check repeating (ABCABC)
        if (chars[0] == chars[3] && chars[1] == chars[4] && chars[2] == chars[5]) {
            return "repeating";
        }

        // Check palindrome (ABCCBA)
        if (chars[0] == chars[5] && chars[1] == chars[4] && chars[2] == chars[3]) {
            return "palindrome";
        }

        // Check AxBxCx pattern
        if (chars[0] == chars[2] && chars[2] == chars[4]) {
            return "axbxcx_" + Character.toUpperCase(chars[0]);
        }

        return null;
    }

    /**
     * Check if hex contains a word pattern from the word list
     */
    public String detectWordMatch(String hex) {
        ClothConfig config = ClothConfig.getInstance();
        if (!config.isWordsEnabled()) return null;

        hex = hex.toUpperCase();
        Map<String, String> wordList = config.getWordList();

        for (Map.Entry<String, String> entry : wordList.entrySet()) {
            String word = entry.getKey();
            String pattern = entry.getValue().toUpperCase();

            if (matchesPattern(hex, pattern)) {
                return word;
            }
        }

        return null;
    }

    /**
     * Check if hex matches a pattern with X wildcards
     * Supports patterns shorter than hex (substring matching)
     * Matches old ChatTriggers behavior: checks if pattern exists anywhere in hex
     */
    private boolean matchesPattern(String hex, String pattern) {
        // If pattern has wildcards (X), use sliding window with regex-style matching
        if (pattern.contains("X")) {
            int patternLen = pattern.length();
            // Try all possible positions in the hex where this pattern could fit
            for (int startIdx = 0; startIdx + patternLen <= hex.length(); startIdx++) {
                boolean matches = true;
                for (int i = 0; i < patternLen; i++) {
                    char patternChar = pattern.charAt(i);
                    char hexChar = hex.charAt(startIdx + i);

                    // X is wildcard, anything else must match exactly
                    if (patternChar != 'X' && hexChar != patternChar) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    return true;
                }
            }
            return false;
        }

        // No wildcards: simple substring check (indexOf)
        return hex.contains(pattern);
    }

    /**
     * Get all pieces with a specific pattern
     */
    @SuppressWarnings("unused") // Public API method for future use
    public Set<String> getPiecesWithPattern(String patternType, Map<String, String> hexcodeMap) {
        Set<String> matches = new HashSet<>();

        for (Map.Entry<String, String> entry : hexcodeMap.entrySet()) {
            String uuid = entry.getKey();
            String hex = entry.getValue();
            String detected = detectPattern(hex);

            if (detected != null && detected.equals(patternType)) {
                matches.add(uuid);
            }
        }

        return matches;
    }

    /**
     * Get all pieces with word matches
     */
    @SuppressWarnings("unused") // Public API method for future use
    public Set<String> getPiecesWithWords(Map<String, String> hexcodeMap) {
        Set<String> matches = new HashSet<>();

        for (Map.Entry<String, String> entry : hexcodeMap.entrySet()) {
            String uuid = entry.getKey();
            String hex = entry.getValue();

            if (detectWordMatch(hex) != null) {
                matches.add(uuid);
            }
        }

        return matches;
    }
}

