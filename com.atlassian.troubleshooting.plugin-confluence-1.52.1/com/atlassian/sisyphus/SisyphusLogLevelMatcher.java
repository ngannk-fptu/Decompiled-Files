/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SisyphusLogLevelMatcher {
    private static final Pattern logLevelPattern = Pattern.compile("(TRACE|DEBUG|INFO|WARN|ERROR|FATAL)");

    public static String extractLogLevel(String text) {
        String logLevel = "";
        Matcher matcher = logLevelPattern.matcher(text);
        if (matcher.find()) {
            logLevel = matcher.group(0);
        }
        return logLevel;
    }
}

