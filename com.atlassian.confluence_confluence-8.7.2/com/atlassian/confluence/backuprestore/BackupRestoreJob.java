/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobState
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 */
package com.atlassian.confluence.backuprestore;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.backuprestore.BackupRestoreJobResult;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.core.NotExportable;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Objects;

public class BackupRestoreJob
implements Serializable,
NotExportable {
    private static final long serialVersionUID = -5870763933897699009L;
    private int configuration;
    private Long id;
    private JobOperation jobOperation;
    private JobScope jobScope;
    private JobState jobState;
    private Instant createTime;
    private Instant startProcessingTime;
    private Instant finishProcessingTime;
    private Instant cancelTime;
    private Instant fileDeleteTime;
    private Boolean fileExists;
    @Deprecated
    private transient BackupRestoreSettings backupRestoreSettings;
    @Deprecated
    private transient BackupRestoreJobResult backupRestoreJobResult;
    private String errorMessage;
    private String owner;
    private String whoCancelledTheJob;
    String fileName;
    String singleSpaceKey;
    String spaceKeys;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobOperation getJobOperation() {
        return this.jobOperation;
    }

    public void setJobOperation(JobOperation jobOperation) {
        this.jobOperation = jobOperation;
    }

    public JobScope getJobScope() {
        return this.jobScope;
    }

    public void setJobScope(JobScope jobScope) {
        this.jobScope = jobScope;
    }

    public JobState getJobState() {
        return this.jobState;
    }

    public void setJobState(JobState jobState) {
        this.jobState = jobState;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = this.truncateToSecondsIfNotNull(createTime);
    }

    public Instant getStartProcessingTime() {
        return this.startProcessingTime;
    }

    public void setStartProcessingTime(Instant startProcessingTime) {
        this.startProcessingTime = this.truncateToSecondsIfNotNull(startProcessingTime);
    }

    public Instant getFinishProcessingTime() {
        return this.finishProcessingTime;
    }

    public void setFinishProcessingTime(Instant finishProcessingTime) {
        this.finishProcessingTime = this.truncateToSecondsIfNotNull(finishProcessingTime);
    }

    public Instant getCancelTime() {
        return this.cancelTime;
    }

    public void setCancelTime(Instant cancelTime) {
        this.cancelTime = this.truncateToSecondsIfNotNull(cancelTime);
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getWhoCancelledTheJob() {
        return this.whoCancelledTheJob;
    }

    public void setWhoCancelledTheJob(String whoCancelledTheJob) {
        this.whoCancelledTheJob = whoCancelledTheJob;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public BackupRestoreSettings getBackupRestoreSettings() {
        return this.backupRestoreSettings;
    }

    public void setBackupRestoreSettings(BackupRestoreSettings backupRestoreSettings) {
        this.backupRestoreSettings = backupRestoreSettings;
    }

    public Instant getFileDeleteTime() {
        return this.fileDeleteTime;
    }

    public void setFileDeleteTime(Instant fileDeleteTime) {
        this.fileDeleteTime = this.truncateToSecondsIfNotNull(fileDeleteTime);
    }

    public Boolean isFileExists() {
        return this.fileExists;
    }

    public void setFileExists(Boolean fileExists) {
        this.fileExists = fileExists;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BackupRestoreJob that = (BackupRestoreJob)o;
        return this.id.equals(that.id);
    }

    public BackupRestoreJobResult getBackupRestoreJobResult() {
        return this.backupRestoreJobResult;
    }

    public void setBackupRestoreJobResult(BackupRestoreJobResult backupRestoreJobResult) {
        this.backupRestoreJobResult = backupRestoreJobResult;
    }

    public int getConfiguration() {
        return this.configuration;
    }

    public boolean hasFlag(ConfigurationFlag flag) {
        return (this.configuration & flag.getValue()) != 0;
    }

    public void setFlag(ConfigurationFlag flag) {
        this.configuration |= flag.getValue();
    }

    public void resetFlag(ConfigurationFlag flag) {
        this.configuration &= ~flag.getValue();
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSingleSpaceKey() {
        return this.singleSpaceKey;
    }

    public void setSingleSpaceKey(String singleSpaceKey) {
        this.singleSpaceKey = singleSpaceKey;
    }

    public String getSpaceKeys() {
        return this.spaceKeys;
    }

    public void setSpaceKeys(String spaceKeys) {
        this.spaceKeys = spaceKeys;
    }

    public void addSpaceKeys(Collection<String> spaceKeys) {
        String spaceKeysString;
        if (spaceKeys.size() == 1) {
            String spaceKey = spaceKeys.iterator().next();
            this.setSingleSpaceKey(spaceKey);
        }
        if ((spaceKeysString = String.join((CharSequence)", ", spaceKeys)).length() != 0) {
            this.setSpaceKeys(spaceKeysString);
        }
    }

    private Instant truncateToSecondsIfNotNull(Instant timestamp) {
        return timestamp == null ? null : timestamp.truncatedTo(ChronoUnit.SECONDS);
    }

    public Long getTotalTimeElapsed(Instant finishTime) throws BadRequestException {
        long elapsed;
        if (this.startProcessingTime != null && (elapsed = finishTime.toEpochMilli() - this.startProcessingTime.toEpochMilli()) >= 0L) {
            return elapsed;
        }
        return null;
    }

    public int hashCode() {
        return Objects.hash(this.id);
    }

    public String toString() {
        return "BackupRestoreJob{id=" + this.id + ", jobOperation=" + this.jobOperation + ", jobScope=" + this.jobScope + ", jobState=" + this.jobState + ", createTime=" + this.createTime + ", startProcessingTime=" + this.startProcessingTime + ", finishProcessingTime=" + this.finishProcessingTime + ", cancelTime=" + this.cancelTime + ", owner='" + this.owner + "', whoCancelledTheJob='" + this.whoCancelledTheJob + "', errorMessage='" + this.errorMessage + "'}";
    }

    public static enum ConfigurationFlag {
        LEGACY(0),
        GZIP(1),
        NO_COLLECTIONS(2);

        final int value;

        private ConfigurationFlag(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}

