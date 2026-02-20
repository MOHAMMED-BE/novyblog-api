package org.example.blogapi.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class Slugify {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");

    private Slugify() {
    }

    public static String slugify(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");

        return slug
                .toLowerCase(Locale.ROOT)
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
    }
}
