/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.confluence.util.io.ConfluenceFileUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class AttachmentDataFileSystemMigrationBackupHelper {
    private static final Logger log = LoggerFactory.getLogger(AttachmentDataFileSystemMigrationBackupHelper.class);
    private static final String ATTACHMENT_BACKUP_DATE_FORMAT = "yyyyMMddHHmmss";
    private final File confluenceAttachmentsDirectory;

    AttachmentDataFileSystemMigrationBackupHelper(File confluenceAttachmentsDirectory) {
        this.confluenceAttachmentsDirectory = confluenceAttachmentsDirectory;
    }

    public void backupAttachments() {
        if (!this.confluenceAttachmentsDirectory.exists()) {
            log.info("Creating attachments directory on the filesystem at '" + this.confluenceAttachmentsDirectory.getAbsolutePath() + "'");
            if (!this.confluenceAttachmentsDirectory.mkdirs()) {
                log.warn("Failed to create directory at {}", (Object)this.confluenceAttachmentsDirectory.getAbsolutePath());
            }
            return;
        }
        File[] fileList = this.confluenceAttachmentsDirectory.listFiles();
        File attachmentsBackupDir = this.createAttachmentsBackupDirectory(this.confluenceAttachmentsDirectory, 10);
        for (File file : fileList) {
            File newLocation = new File(attachmentsBackupDir, file.getName());
            try {
                ConfluenceFileUtils.moveDir(file, newLocation);
            }
            catch (Exception e) {
                log.warn("Error while moving " + file + " to " + newLocation, (Throwable)e);
            }
        }
    }

    private File createAttachmentsBackupDirectory(File attachmentsDirFile, int maxTries) {
        String isoFormatDate = new SimpleDateFormat(ATTACHMENT_BACKUP_DATE_FORMAT).format(new Date());
        Object suffix = "";
        for (int tries = 1; tries <= maxTries; ++tries) {
            File attachmentsBackupDir = new File(attachmentsDirFile, "attachment-backup-" + isoFormatDate + (String)suffix);
            if (attachmentsBackupDir.mkdirs()) {
                return attachmentsBackupDir;
            }
            suffix = "-" + tries;
        }
        throw new RuntimeException("Unable to create backup directory in " + attachmentsDirFile + " for old attachments after " + maxTries + " attempts");
    }
}

