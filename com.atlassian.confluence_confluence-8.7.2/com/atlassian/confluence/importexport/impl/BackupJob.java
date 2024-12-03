/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.importexport.DefaultExportContext;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.core.util.FileUtils;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class BackupJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(BackupJob.class);
    private final ImportExportManager importExportManager;
    private final SettingsManager settingsManager;

    public BackupJob(ImportExportManager importExportManager, SettingsManager settingsManager) {
        this.importExportManager = importExportManager;
        this.settingsManager = settingsManager;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        if (!this.settingsManager.getGlobalSettings().isBackupDaily()) {
            return JobRunnerResponse.aborted((String)"Backup daily setting off");
        }
        String dailyBackupDirectory = this.settingsManager.getGlobalSettings().getBackupPath();
        if (dailyBackupDirectory == null || dailyBackupDirectory.trim().equals("")) {
            String msg = "No daily backup directory specified to store the backups. Taking no action!";
            log.warn(msg);
            return JobRunnerResponse.aborted((String)msg);
        }
        try {
            Settings globalSettings = this.settingsManager.getGlobalSettings();
            DefaultExportContext exportContext = DefaultExportContext.getXmlBackupInstance();
            exportContext.setExportAttachments(globalSettings.isBackupAttachmentsDaily());
            String exportPath = this.importExportManager.exportAs(exportContext, new ProgressMeter());
            String dailyBackupFilename = globalSettings.getDailyBackupFilePrefix() + new SimpleDateFormat(globalSettings.getDailyBackupDateFormatPattern()).format(new Date()) + ".zip";
            File realDestination = new File(dailyBackupDirectory, dailyBackupFilename);
            try (FileInputStream fis = new FileInputStream(exportPath);){
                FileUtils.copyFile((InputStream)fis, (File)realDestination, (boolean)true);
            }
            return JobRunnerResponse.success((String)("Backup created: " + dailyBackupFilename));
        }
        catch (Exception e) {
            log.error("Error while running the scheduled backup", (Throwable)e);
            return JobRunnerResponse.failed((Throwable)e);
        }
    }
}

