package api.util;

import java.util.regex.Pattern;

/**
 * Immutable utility class for input validation and trimming.
 * All methods throw IllegalArgumentException on failure.
 */
public final class Validator {

    private Validator() {
        throw new UnsupportedOperationException("Utility class - do not instantiate");
    }

    // ===== Basic Validation =====

    /**
     * Validates that a string is not null, not empty, and not only whitespace.
     * Returns the trimmed string.
     */
    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or blank");
        }
        return value.trim();
    }

    /**
     * Validates non-blank AND matches a regex pattern.
     * Returns the trimmed, validated string.
     */
    public static String requireMatches(String value, String fieldName, String regex) {
        String trimmed = requireNonBlank(value, fieldName);
        if (!trimmed.matches(regex)) {
            throw new IllegalArgumentException(fieldName + " is invalid: " + trimmed);
        }
        return trimmed;
    }

    /**
     * Validates non-blank AND matches a compiled Pattern (more performant).
     */
    public static String requireMatches(String value, String fieldName, Pattern pattern) {
        String trimmed = requireNonBlank(value, fieldName);
        if (!pattern.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(fieldName + " is invalid: " + trimmed);
        }
        return trimmed;
    }

    // ===== Optional Validation (does not throw) =====

    /**
     * Returns Optional.empty() if invalid, otherwise Optional.of(trimmed).
     * Useful when you want to handle validation without exceptions.
     */
    public static java.util.Optional<String> tryValidate(String value, Pattern pattern) {
        if (value == null || value.isBlank()) {
            return java.util.Optional.empty();
        }
        String trimmed = value.trim();
        return pattern.matcher(trimmed).matches()
                ? java.util.Optional.of(trimmed)
                : java.util.Optional.empty(); // Returns the trimmed or empty
    }

    // Predefined Patterns

    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[\\p{L}\\p{N}+_.-]+@[\\p{L}\\p{N}.-]+\\.[\\p{L}]{2,}$"
    );
    public static final Pattern PHONE_PATTERN = Pattern.compile("\\d{11}");
    public static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9 ]+$");
}