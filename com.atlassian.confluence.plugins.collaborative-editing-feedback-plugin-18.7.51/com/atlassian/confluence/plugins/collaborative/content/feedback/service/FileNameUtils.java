/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class FileNameUtils {
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("^collab_data_[0-9]+_[0-9]+\\.zip$");

    protected FileNameUtils() {
    }

    public static String buildResultFileName(long contentId, long currentTimestamp) {
        return "collab_data_" + contentId + "_" + currentTimestamp + ".zip";
    }

    public static boolean isValidFileName(String filename) {
        return FILE_NAME_PATTERN.matcher(Objects.requireNonNull(filename)).matches();
    }

    public static long getCreatedTimestamp(String fileName) {
        return Long.parseLong(fileName.split("_")[3].split("\\.")[0]);
    }

    public static long getContentId(String fileName) {
        return Long.parseLong(fileName.split("_")[2]);
    }

    public static File eventsTmpFile(long contentId, long currentTimestamp) throws IOException {
        return File.createTempFile("events_" + contentId + "_" + currentTimestamp, ".exp");
    }

    public static File pageHistoryTmpFile(long contentId, long currentTimestamp) throws IOException {
        return File.createTempFile("pageHistory_" + contentId + "_" + currentTimestamp, ".exp");
    }

    public static File reconciliationHistoryTmpFile(long contentId, long currentTimestamp) throws IOException {
        return File.createTempFile("reconciliationHistory_" + contentId + "_" + currentTimestamp, ".exp");
    }

    public static File synchronyRequestHistoryTmpFile(long contentId, long currentTimestamp) throws IOException {
        return File.createTempFile("synchronyRequestHistory_" + contentId + "_" + currentTimestamp, ".exp");
    }

    public static File snapshotsTmpFile(long contentId, long currentTimestamp) throws IOException {
        return File.createTempFile("snapshots_" + contentId + "_" + currentTimestamp, ".exp");
    }

    public static File descriptorTmpFile(long contentId, long currentTimestamp) throws IOException {
        return File.createTempFile("descriptor_" + contentId + "_" + currentTimestamp, ".properties");
    }
}

