/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskReportIndexPersistedStateService {
    private static final Logger log = LoggerFactory.getLogger(TaskReportIndexPersistedStateService.class);
    public static final String TASK_REPORT_IS_FULLY_INITIALISED_FLAG = "task_report_index_initialised_flag";
    private final AtomicBoolean indexIsReady = new AtomicBoolean();
    private final Path indexInitialisationStateFile;

    public TaskReportIndexPersistedStateService(@ComponentImport ApplicationProperties applicationProperties) {
        Optional localHomeDirectory = applicationProperties.getLocalHomeDirectory();
        if (localHomeDirectory.isEmpty()) {
            log.error("Unable to retrieve local home directory from application properties. Task Report Macro will not use search index.");
            this.indexInitialisationStateFile = null;
            return;
        }
        Path journalDir = ((Path)localHomeDirectory.get()).resolve("journal");
        this.indexInitialisationStateFile = journalDir.resolve(TASK_REPORT_IS_FULLY_INITIALISED_FLAG);
        this.indexIsReady.set(this.isIndexMarkedAsReadyInHomeFolder(this.indexInitialisationStateFile));
        log.info("Task report index status: {} (based on the existence of file {}).", (Object)(this.indexIsReady.get() ? "ready" : " NOT ready"), (Object)this.indexInitialisationStateFile);
    }

    public synchronized void markAsReady() {
        this.indexIsReady.set(true);
        log.debug("Task report index is going to be marked as ready. All task report macros will use search index to retrieve tasks.");
        try {
            Files.createFile(this.indexInitialisationStateFile, new FileAttribute[0]);
            log.info("Task report index was marked as ready. All task report macros will use search index to retrieve tasks (they will be rendered very fast).");
        }
        catch (IOException e) {
            log.error("Unable to create file " + this.indexInitialisationStateFile + ". Permissions issue or no free disk space?", (Throwable)e);
        }
    }

    public synchronized void markAsNotReady() {
        this.indexIsReady.set(false);
        log.debug("Task report index will be marked as NOT ready. All task report macros will use database queries to retrieve tasks.");
        if (!Files.exists(this.indexInitialisationStateFile, new LinkOption[0])) {
            return;
        }
        try {
            Files.delete(this.indexInitialisationStateFile);
            log.info("Task report index was marked as NOT ready. All task report macros will use search index to retrieve tasks.");
        }
        catch (Exception e) {
            log.error("Unable to delete file " + this.indexInitialisationStateFile + ". Permissions issue or no free disk space?", (Throwable)e);
        }
    }

    public boolean isIndexReady() {
        return this.indexIsReady.get();
    }

    private boolean isIndexMarkedAsReadyInHomeFolder(Path file) {
        return Files.exists(file, new LinkOption[0]);
    }
}

