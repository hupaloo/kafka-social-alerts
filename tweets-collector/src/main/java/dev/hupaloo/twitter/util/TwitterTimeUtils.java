package dev.hupaloo.twitter.util;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class TwitterTimeUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public static String instantToFormattedString(Instant instant) {
        return DATE_TIME_FORMATTER.format(instant);
    }
}
