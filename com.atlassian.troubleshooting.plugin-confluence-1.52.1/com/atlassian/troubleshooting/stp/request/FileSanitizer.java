/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.request;

import com.atlassian.troubleshooting.stp.salext.FileSanitizerPatternManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSanitizer {
    private static final Logger LOG = LoggerFactory.getLogger(FileSanitizer.class);
    private static final String SANITIZER_MESSAGE = "Sanitized by Support Utility";
    private final FileSanitizerPatternManager fileSanitizerPatternManager;
    private final List<String> nameExtensions;
    private final File tempDir;

    public FileSanitizer(@Nonnull FileSanitizerPatternManager fileSanitizerPatternManager, @Nonnull List<String> nameExtensions, @Nonnull File tempDir) {
        this.fileSanitizerPatternManager = fileSanitizerPatternManager;
        this.nameExtensions = nameExtensions;
        this.tempDir = tempDir;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public File sanitize(File file) throws IOException {
        if (file == null) {
            return file;
        }
        List<Pattern> patterns = this.fileSanitizerPatternManager.getSanitizationsForFile(file.getName());
        if (patterns.isEmpty()) {
            return file;
        }
        if (!this.tempDir.exists()) {
            File file2 = this.tempDir;
            synchronized (file2) {
                if (!this.tempDir.exists() && !this.tempDir.mkdirs()) {
                    throw new IOException("Couldn't create tmp directory " + this.tempDir.getAbsolutePath());
                }
            }
        }
        File outputFile = File.createTempFile("sanitizer", "out", this.tempDir);
        outputFile.deleteOnExit();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
             BufferedReader reader = new BufferedReader(new FileReader(file));){
            String line;
            while ((line = reader.readLine()) != null) {
                String outLine = this.sanitizeLine(patterns, line);
                writer.write(outLine + System.lineSeparator());
            }
        }
        return outputFile;
    }

    public String sanitizeLine(List<Pattern> patterns, String originalLine) {
        String substitutedLine = originalLine;
        for (Pattern pattern : patterns) {
            ArrayList<MatcherGroup> matches = new ArrayList<MatcherGroup>();
            Matcher m = pattern.matcher(substitutedLine);
            int searchStart = 0;
            while (m.find(searchStart)) {
                for (int a = m.groupCount(); a > 0; --a) {
                    if (null == m.group(a)) continue;
                    int end = m.end(a);
                    matches.add(new MatcherGroup(m.start(a), end));
                    searchStart = Math.max(searchStart, end);
                }
            }
            Collections.sort(matches);
            StringBuilder sb = new StringBuilder();
            int idx = 0;
            for (MatcherGroup g : matches) {
                sb.append(substitutedLine.substring(idx, g.start));
                sb.append(SANITIZER_MESSAGE);
                idx = g.end;
            }
            sb.append(substitutedLine.substring(idx, substitutedLine.length()));
            substitutedLine = sb.toString();
        }
        return substitutedLine;
    }

    @Nullable
    public String sanitizeExtensions(@Nullable String originalName) {
        if (originalName == null) {
            return null;
        }
        String substitutedName = originalName;
        for (String extension : this.nameExtensions) {
            substitutedName = substitutedName.replace(extension, extension.replace(".", "-") + ".txt");
        }
        return substitutedName;
    }

    private static class MatcherGroup
    implements Comparable<MatcherGroup> {
        private final Integer start;
        private final int end;

        MatcherGroup(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public int compareTo(MatcherGroup o) {
            return this.start.compareTo(o.start);
        }

        public String toString() {
            return "MatcherGroup{start=" + this.start + ", end=" + this.end + '}';
        }
    }
}

