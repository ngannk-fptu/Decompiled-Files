/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jdk.utilities.runtimeinformation;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MemorySizeUtils {
    private static final String UNITS = "KMGT";
    private static final Pattern VALUE_REGEX = Pattern.compile("(\\d+)([KMGT]?)", 2);

    MemorySizeUtils() {
    }

    static Optional<Long> displaySizeToBytes(String memoryValue) {
        Objects.requireNonNull(memoryValue);
        if (memoryValue.length() == 0) {
            return Optional.empty();
        }
        Matcher matcher = VALUE_REGEX.matcher(memoryValue);
        if (matcher.matches()) {
            long value;
            String unitPrefix = matcher.group(2).toUpperCase();
            try {
                value = Long.valueOf(matcher.group(1));
            }
            catch (NumberFormatException ignored) {
                return Optional.empty();
            }
            if (unitPrefix.equals("")) {
                return Optional.of(value);
            }
            return Optional.of((long)((double)value * Math.pow(1024.0, UNITS.indexOf(unitPrefix) + 1)));
        }
        return Optional.empty();
    }
}

