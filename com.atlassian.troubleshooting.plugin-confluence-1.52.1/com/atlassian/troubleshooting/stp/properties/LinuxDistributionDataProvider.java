/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Pair
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.LineIterator
 *  org.apache.commons.io.filefilter.WildcardFileFilter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.properties;

import com.atlassian.fugue.Pair;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinuxDistributionDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(LinuxDistributionDataProvider.class);
    private static final String NEW_LINE_PATTERN = "\\n";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String QUOTE = "\"";
    private static final String OTHER_PROPERTIES = "OTHER_PROPERTIES";
    private static final Path ETC_PATH = Paths.get("/etc", new String[0]);
    private static final int MAX_OTHER_PROPERTIES_LENGTH = 100;
    private static final int MAX_ENTRIES = 2000;

    public Map<String, String> fetchDistributionData() {
        List<File> releaseFiles = this.getAllReleaseFiles();
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        ArrayList<String> linesWithoutKeyValue = new ArrayList<String>();
        for (File releaseFile : releaseFiles) {
            if (!releaseFile.canRead()) continue;
            this.readFile(releaseFile, result, linesWithoutKeyValue);
        }
        if (!linesWithoutKeyValue.isEmpty()) {
            result.put(OTHER_PROPERTIES, String.join((CharSequence)NEW_LINE_PATTERN, linesWithoutKeyValue));
        }
        return result;
    }

    @VisibleForTesting
    Path getEtcPath() {
        return ETC_PATH;
    }

    private void readFile(File file, Map<String, String> result, List<String> linesWithoutKeyValue) {
        try {
            LineIterator lineIterator = FileUtils.lineIterator((File)file);
            while (lineIterator.hasNext()) {
                String line = lineIterator.nextLine().replace(QUOTE, "");
                LineOutput lineOutput = this.parseLine(line);
                if (lineOutput.hasKeyValue()) {
                    if (result.size() < 2000) {
                        result.put((String)lineOutput.keyValue.left(), (String)lineOutput.keyValue.right());
                        continue;
                    }
                    result.put("WARNING", String.format("Reached the limit of %d entries", 2000));
                    break;
                }
                if (!lineOutput.hasNonKeyValue()) continue;
                this.handleNonKeyValue(linesWithoutKeyValue, lineOutput);
            }
        }
        catch (IOException exception) {
            LOG.error("Error during reading *-release files", (Throwable)exception);
        }
    }

    private void handleNonKeyValue(List<String> linesWithoutKeyValue, LineOutput lineOutput) {
        if (linesWithoutKeyValue.size() < 100) {
            linesWithoutKeyValue.add(lineOutput.nonKeyValue);
        }
    }

    private List<File> getAllReleaseFiles() {
        WildcardFileFilter releaseFileFilter = new WildcardFileFilter("*-release");
        File[] releaseFiles = this.getEtcPath().toFile().listFiles((FileFilter)releaseFileFilter);
        if (releaseFiles == null) {
            return Collections.emptyList();
        }
        return Stream.of(releaseFiles).filter(File::isFile).collect(Collectors.toList());
    }

    private LineOutput parseLine(String line) {
        String[] potentialKeyValuePair;
        if (line.contains(KEY_VALUE_SEPARATOR) && (potentialKeyValuePair = line.split(KEY_VALUE_SEPARATOR)).length == 2) {
            return new LineOutput((Pair<String, String>)Pair.pair((Object)potentialKeyValuePair[0], (Object)potentialKeyValuePair[1]));
        }
        return new LineOutput(line);
    }

    private static class LineOutput {
        final Pair<String, String> keyValue;
        final String nonKeyValue;

        LineOutput(Pair<String, String> keyValue, String nonKeyValue) {
            this.keyValue = keyValue;
            this.nonKeyValue = nonKeyValue;
        }

        LineOutput(Pair<String, String> keyValue) {
            this(keyValue, null);
        }

        LineOutput(String nonKeyValue) {
            this(null, nonKeyValue);
        }

        boolean hasKeyValue() {
            return this.keyValue != null;
        }

        boolean hasNonKeyValue() {
            return this.nonKeyValue != null;
        }
    }
}

