/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.io.FilenameUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.backuprestore;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class BackupRestoreSettings
implements Serializable {
    private final JobOperation jobOperation;
    private final JobScope jobScope;
    private final boolean skipAttachments;
    private final boolean skipHistoricalVersions;
    private final boolean skipAuditRecordsExport;
    private final Collection<String> notificationEmails;
    private final Set<String> spaceKeys;
    private final String fileName;
    private final boolean keepPermanently;
    private final String workingDir;
    private final String fileNamePrefix;
    private final Boolean skipReindex;

    public BackupRestoreSettings(Builder builder) {
        this.jobOperation = builder.jobOperation;
        this.jobScope = builder.jobScope;
        this.skipAttachments = builder.skipAttachments;
        this.skipHistoricalVersions = builder.skipHistoricalVersions;
        this.skipAuditRecordsExport = builder.skipAuditRecordsExport;
        this.notificationEmails = ImmutableSet.copyOf(builder.notificationEmails);
        this.spaceKeys = ImmutableSet.copyOf(builder.spaceKeys);
        this.fileName = builder.fileName;
        this.fileNamePrefix = builder.fileNamePrefix;
        this.workingDir = builder.workingDir;
        this.keepPermanently = builder.keepPermanently;
        this.skipReindex = builder.skipReindex;
    }

    public JobOperation getJobOperation() {
        return this.jobOperation;
    }

    public JobScope getJobScope() {
        return this.jobScope;
    }

    public boolean isSkipAttachments() {
        return this.skipAttachments;
    }

    public boolean isSkipHistoricalVersions() {
        return this.skipHistoricalVersions;
    }

    public boolean isSkipAuditRecordsExport() {
        return this.skipAuditRecordsExport;
    }

    public Collection<String> getNotificationEmails() {
        return this.notificationEmails;
    }

    public Set<String> getSpaceKeys() {
        return this.spaceKeys;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFileNamePrefix() {
        return this.fileNamePrefix;
    }

    public String getFilePath() {
        return Path.of(this.workingDir, this.fileName).toString();
    }

    public boolean isKeepPermanently() {
        return this.keepPermanently;
    }

    public Boolean getSkipReindex() {
        return this.skipReindex;
    }

    public boolean isSkipReindex() {
        return this.skipReindex == null ? false : this.skipReindex;
    }

    public String toString() {
        return "BackupRestoreSettings{jobOperation=" + this.jobOperation + ", jobScope=" + this.jobScope + ", skipAttachments=" + this.skipAttachments + ", skipReindex=" + this.isSkipReindex() + ", skipHistoricalVersions=" + this.skipHistoricalVersions + ", skipAuditRecordsExport=" + this.skipAuditRecordsExport + ", notificationEmails=" + this.notificationEmails + ", spaceKeys=" + this.spaceKeys + ", fileName='" + this.fileName + "', fileNamePrefix='" + this.fileNamePrefix + "', workingDir='" + this.workingDir + "'}";
    }

    public static class Builder {
        private final JobOperation jobOperation;
        private final JobScope jobScope;
        public Boolean skipReindex;
        private boolean skipAttachments;
        private boolean skipAuditRecordsExport;
        private boolean skipHistoricalVersions;
        private final Collection<String> notificationEmails = new HashSet<String>();
        private final Collection<String> spaceKeys = new HashSet<String>();
        private String fileNamePrefix;
        private String fileName;
        private String workingDir;
        private boolean keepPermanently;

        public Builder(JobOperation jobOperation, JobScope jobScope) {
            this.jobOperation = jobOperation;
            this.jobScope = jobScope;
        }

        public Builder skipAuditRecordsExport(boolean skipAuditRecordsExport) {
            this.skipAuditRecordsExport = skipAuditRecordsExport;
            return this;
        }

        public Builder skipAttachments(boolean skipAttachments) {
            this.skipAttachments = skipAttachments;
            return this;
        }

        public Builder setSkipReindex(Boolean skipReindex) {
            this.skipReindex = skipReindex;
            return this;
        }

        public Builder skipHistoricalVersions(boolean skipHistoricalVersions) {
            this.skipHistoricalVersions = skipHistoricalVersions;
            return this;
        }

        public Builder addNotificationEmail(String notificationEmail) {
            if (StringUtils.isEmpty((CharSequence)notificationEmail)) {
                throw new IllegalArgumentException("Notification email cannot be empty");
            }
            this.notificationEmails.add(notificationEmail);
            return this;
        }

        public Builder setFilenamePrefix(String fileNamePrefix) {
            this.fileNamePrefix = fileNamePrefix;
            return this;
        }

        public Builder addSpaceKey(String spaceKey) {
            this.spaceKeys.add(spaceKey);
            return this;
        }

        public Builder keepPermanently(boolean keepPermanently) {
            this.keepPermanently = keepPermanently;
            return this;
        }

        public Builder setFilename(String workingFile) {
            this.fileName = workingFile;
            return this;
        }

        public Builder setWorkingDir(String workingDir) {
            this.workingDir = workingDir;
            return this;
        }

        private void validate() {
            this.validateFilePrefix();
            this.validateFileName();
            this.validateWorkingDir();
            this.validateSpaceKeys();
            this.validateSkipAttachments();
            this.validateSkipAuditRecordsExport();
        }

        private void validateWorkingDir() {
            if (StringUtils.isBlank((CharSequence)this.workingDir) && this.jobOperation.equals((Object)JobOperation.RESTORE)) {
                throw new IllegalStateException("workingDir must be set for RESTORE operation");
            }
        }

        private void validateFileName() {
            if (this.jobOperation.equals((Object)JobOperation.RESTORE)) {
                if (StringUtils.isBlank((CharSequence)this.fileName)) {
                    throw new IllegalArgumentException("fileName must be set");
                }
                if (!this.fileName.equals(FilenameUtils.getName((String)this.fileName))) {
                    throw new IllegalArgumentException("fileName must not contain any path");
                }
            } else if (StringUtils.isBlank((CharSequence)this.fileName)) {
                throw new AssertionError((Object)"fileName must be set");
            }
        }

        private void validateFilePrefix() {
            int i;
            if (this.fileNamePrefix != null && !this.jobOperation.equals((Object)JobOperation.BACKUP)) {
                throw new IllegalArgumentException("fileNamePrefix can be set only for BACKUP operation");
            }
            StringBuilder forbiddenCharacters = new StringBuilder("/?<>:*|\"\\");
            for (i = 0; i <= 31; ++i) {
                forbiddenCharacters.append(Character.toString(i));
            }
            for (i = 128; i <= 159; ++i) {
                forbiddenCharacters.append(Character.toString(i));
            }
            if (this.fileNamePrefix != null && StringUtils.containsAny((CharSequence)this.fileNamePrefix, (CharSequence)forbiddenCharacters.toString())) {
                throw new IllegalArgumentException("fileNamePrefix cannot contain characters (0x00\u20130x1f and 0x80\u20130x9f), /, ?, <, >, :, *, |, \\, or \"");
            }
        }

        private void validateSpaceKeys() {
            if (JobScope.SPACE.equals((Object)this.jobScope) && JobOperation.BACKUP.equals((Object)this.jobOperation)) {
                if (this.spaceKeys.isEmpty()) {
                    throw new IllegalArgumentException("Space backup operation requires at least one space key.");
                }
            } else if (!this.spaceKeys.isEmpty()) {
                throw new IllegalArgumentException(String.format("Space key shouldn't be set for job with scope %s and operation %s", this.jobScope, this.jobOperation));
            }
        }

        private void validateSkipAttachments() {
            if (!(!this.skipAttachments || JobScope.SITE.equals((Object)this.jobScope) && JobOperation.BACKUP.equals((Object)this.jobOperation))) {
                throw new IllegalArgumentException(String.format("skipAttachments flag shouldn't be set for job with scope %s and operation %s", this.jobScope, this.jobOperation));
            }
        }

        private void validateSkipAuditRecordsExport() {
            if (!(!this.skipAuditRecordsExport || JobScope.SITE.equals((Object)this.jobScope) && JobOperation.BACKUP.equals((Object)this.jobOperation))) {
                throw new IllegalArgumentException(String.format("skipAuditRecordsExport flag shouldn't be set for job with scope %s and operation %s", this.jobScope, this.jobOperation));
            }
        }

        public BackupRestoreSettings build() {
            this.validate();
            return new BackupRestoreSettings(this);
        }
    }
}

