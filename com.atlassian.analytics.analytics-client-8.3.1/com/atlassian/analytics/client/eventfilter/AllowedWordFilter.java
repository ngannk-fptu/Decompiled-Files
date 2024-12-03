/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.eventfilter;

import java.util.regex.Pattern;

public class AllowedWordFilter {
    private static final Pattern UUID_PATTERN = Pattern.compile("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    public static final Pattern DATE_PATTERN = Pattern.compile("[1-9]\\d{3}-(1[0-2]|0[1-9])-(0[1-9]|[1-2]\\d|3[01]) ([01]\\d|2[0-3]):[0-5][0-9]:[0-5][0-9]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9.,_%\\-]+");

    public String processAllowedWords(String propertyValue) {
        if (NUMBER_PATTERN.matcher(propertyValue).matches()) {
            return propertyValue;
        }
        if (UUID_PATTERN.matcher(propertyValue).matches()) {
            return propertyValue;
        }
        if (DATE_PATTERN.matcher(propertyValue).matches()) {
            return propertyValue;
        }
        return "";
    }
}

