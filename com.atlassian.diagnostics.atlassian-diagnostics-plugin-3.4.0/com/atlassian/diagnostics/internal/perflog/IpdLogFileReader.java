/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.filefilter.WildcardFileFilter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.perflog;

import com.atlassian.sal.api.ApplicationProperties;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpdLogFileReader {
    private static final Logger log = LoggerFactory.getLogger(IpdLogFileReader.class);
    private static final String LOG_FILE_PATH = "/log/";
    private static final String LOG_FILE_WILDCARD = "atlassian-jira-ipd-monitoring.log*";
    private final ApplicationProperties applicationProperties;

    public Stream<String> readLogLines() {
        return Arrays.stream(this.getIpdLogFiles()).map(this::readLines).flatMap(Collection::stream);
    }

    private File[] getIpdLogFiles() {
        try {
            return this.getFromLocalHomeDirectory();
        }
        catch (NullPointerException e) {
            log.error("Unable to get local home directory", (Throwable)e);
            return new File[0];
        }
    }

    private File[] getFromLocalHomeDirectory() {
        WildcardFileFilter filter;
        Path localHomeDirectory = (Path)this.applicationProperties.getLocalHomeDirectory().orElseThrow(NullPointerException::new);
        String homeDir = localHomeDirectory.toString();
        File dir = new File(homeDir + LOG_FILE_PATH);
        File[] files = dir.listFiles((FileFilter)(filter = new WildcardFileFilter(LOG_FILE_WILDCARD)));
        if (files == null) {
            throw new NullPointerException();
        }
        return files;
    }

    private List<String> readLines(File file) {
        try {
            return FileUtils.readLines((File)file, (Charset)StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            log.error("Log file reading Exception", (Throwable)e);
            return Collections.emptyList();
        }
    }

    public IpdLogFileReader(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }
}

