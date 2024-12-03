/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.DateUtils
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.apache.commons.lang3.ArrayUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.security.DownloadGateKeeper;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.core.util.DateUtils;
import com.atlassian.core.util.FileUtils;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.io.File;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanTempDirectoryJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(CleanTempDirectoryJob.class);
    protected static final int DELETE_FILES_OLDER_THAN_HOURS = Integer.getInteger("confluence.temp-files.ttl-in-hours", 24);
    private final BootstrapManager bootstrapManager;
    private final DownloadGateKeeper gateKeeper;
    private final long maxFileAgeMillis;

    public CleanTempDirectoryJob(BootstrapManager bootstrapManager, DownloadGateKeeper gateKeeper) {
        this.bootstrapManager = bootstrapManager;
        this.gateKeeper = gateKeeper;
        this.maxFileAgeMillis = (long)DELETE_FILES_OLDER_THAN_HOURS * DateUtils.HOUR_MILLIS;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        long oldestMillisToKeepFile = System.currentTimeMillis() - this.maxFileAgeMillis;
        File[] files = this.getTemporaryFiles();
        if (files != null) {
            for (File file : files) {
                if (this.isFileFresh(file, oldestMillisToKeepFile) || FileUtils.deleteDir((File)file)) continue;
                log.error("Failed to delete " + file.getAbsolutePath());
            }
        }
        this.gateKeeper.cleanAllKeysOlderThan(this.maxFileAgeMillis);
        return null;
    }

    private File[] getTemporaryFiles() {
        File tempDirectory = new File(this.bootstrapManager.getLocalHome(), "temp");
        File exportDirectory = new File(this.bootstrapManager.getFilePathProperty("struts.multipart.saveDir"));
        if (tempDirectory.getAbsolutePath().equals(exportDirectory.getAbsolutePath())) {
            return tempDirectory.listFiles();
        }
        Object[] tempDirectoryFiles = tempDirectory.listFiles();
        Object[] exportDirectoryFiles = exportDirectory.listFiles();
        return (File[])ArrayUtils.addAll((Object[])tempDirectoryFiles, (Object[])exportDirectoryFiles);
    }

    private boolean isFileFresh(File file, Long oldestMillisToKeepFile) {
        if (file.lastModified() > oldestMillisToKeepFile) {
            return true;
        }
        if (file.isDirectory()) {
            return Arrays.stream(file.listFiles()).anyMatch(f -> this.isFileFresh((File)f, oldestMillisToKeepFile));
        }
        return false;
    }
}

