/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.troubleshooting.jfr.util;

import com.atlassian.troubleshooting.jfr.exception.JfrException;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public final class JfrRecordingUtils {
    public static final String RECORDING_FILE_EXTENSION = ".jfr";
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
    public static final DateTimeFormatter DATE_TIME_FORMAT_UTC = DATE_TIME_FORMAT.withZone(ZoneId.of("UTC"));
    private static final String RECORDING_FILE_NAME = "%s_%s";

    private JfrRecordingUtils() {
    }

    public static String formatRecordingFileName(String recordingName) {
        return String.format(RECORDING_FILE_NAME, recordingName, DATE_TIME_FORMAT_UTC.format(Instant.now())) + RECORDING_FILE_EXTENSION;
    }

    public static Set<Path> listJfrDumps(Path pathToDir) {
        if (pathToDir == null || !pathToDir.toFile().isDirectory()) {
            throw new JfrException("Path to dir < " + pathToDir + " > is not valid.");
        }
        HashSet<Path> dumpPaths = new HashSet<Path>();
        File[] files = pathToDir.toFile().listFiles();
        if (files == null) {
            return dumpPaths;
        }
        for (File file : files) {
            Path filePath = file.toPath();
            if (!file.isFile() || !JfrRecordingUtils.isJfrRecording(filePath) || file.length() <= 0L) continue;
            dumpPaths.add(filePath);
        }
        return dumpPaths;
    }

    @VisibleForTesting
    static boolean isJfrRecording(Path pathToFile) {
        return pathToFile.toString().toLowerCase().endsWith(RECORDING_FILE_EXTENSION);
    }

    public static String formatAsPercentage(float value) {
        return String.format("%.2f", Float.valueOf(value * 100.0f)) + "%";
    }
}

