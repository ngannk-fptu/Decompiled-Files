/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.google.errorprone.annotations.Immutable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSanitizer {
    private static final Logger log = LoggerFactory.getLogger(FileSanitizer.class);
    private Map<String, List<Pattern>> filePatterns;
    private Set<File> tmpFiles;
    private String message;

    public FileSanitizer(Map<String, List<Pattern>> filePatterns, String message) {
        this.filePatterns = filePatterns;
        this.message = message;
        this.tmpFiles = new HashSet<File>();
    }

    public File sanitize(File file) throws IOException {
        if (!this.filePatterns.containsKey(file.getName())) {
            return file;
        }
        File outputFile = File.createTempFile("sanitizer", "out");
        this.tmpFiles.add(outputFile);
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));){
            String line;
            List<Pattern> patterns = this.filePatterns.get(file.getName());
            while ((line = reader.readLine()) != null) {
                String outLine = line;
                for (Pattern pattern : patterns) {
                    Matcher m = pattern.matcher(outLine);
                    if (!m.matches()) continue;
                    ArrayList<MatcherGroup> matches = new ArrayList<MatcherGroup>(m.groupCount());
                    for (int a = m.groupCount(); a > 0; --a) {
                        if (null == m.group(a)) continue;
                        matches.add(new MatcherGroup(m.start(a), m.end(a)));
                    }
                    Collections.sort(matches);
                    StringBuilder sb = new StringBuilder();
                    int idx = 0;
                    for (MatcherGroup g : matches) {
                        sb.append(outLine.substring(idx, g.start));
                        sb.append(this.message);
                        idx = g.end;
                    }
                    sb.append(outLine.substring(idx, outLine.length()));
                    outLine = sb.toString();
                }
                writer.write(outLine + "\n");
            }
        }
        return outputFile;
    }

    public void cleanUpTempFiles() {
        Iterator<File> fileIterator = this.tmpFiles.iterator();
        while (fileIterator.hasNext()) {
            File file = fileIterator.next();
            if (!file.delete()) {
                log.warn("Unable to delete temp file: " + file.getAbsolutePath());
                file.deleteOnExit();
            }
            fileIterator.remove();
        }
    }

    @Immutable
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
            return "MatcherGroup{start=" + this.start + ", end=" + this.end + "}";
        }
    }
}

