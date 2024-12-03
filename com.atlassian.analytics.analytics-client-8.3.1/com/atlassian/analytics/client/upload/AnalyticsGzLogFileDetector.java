/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.upload;

import java.io.File;
import java.util.regex.Pattern;

public final class AnalyticsGzLogFileDetector {
    private static final String ATLASSIAN_ANALYTICS_LOG_PREFIX = ".atlassian-analytics.log".replace(".", "\\.") + "\\.";
    private static final Pattern ANALYTICS_GZ_LOG_FILES_REGEX = Pattern.compile("^.+" + ATLASSIAN_ANALYTICS_LOG_PREFIX + "[0-9\\-]+\\.\\d+\\.gz$");

    private AnalyticsGzLogFileDetector() {
        throw new UnsupportedOperationException();
    }

    static boolean isAnalyticsGzLogFile(File file) {
        return ANALYTICS_GZ_LOG_FILES_REGEX.matcher(file.getName()).matches();
    }
}

