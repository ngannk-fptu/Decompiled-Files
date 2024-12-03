/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.backuprestore;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import java.time.Instant;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class JobDetails {
    @JsonProperty
    private Long id;
    @JsonProperty
    private JobOperation jobOperation;
    @JsonProperty
    private JobScope jobScope;
    @JsonProperty
    private JobState jobState;
    @JsonProperty
    private Instant createTime;
    @JsonProperty
    private Instant startProcessingTime;
    @JsonProperty
    private Instant finishProcessingTime;
    @JsonProperty
    private Instant cancelTime;
    @JsonProperty
    private String errorMessage;
    @JsonProperty
    private String owner;
    @JsonProperty
    private String cancelledBy;
    @JsonProperty
    private String fileName;
    @JsonProperty
    private String spaceKeys;
    @JsonProperty
    private Instant fileDeleteTime;
    @JsonProperty
    private Boolean fileExists;

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
        this.createTime = createTime;
    }

    public Instant getStartProcessingTime() {
        return this.startProcessingTime;
    }

    public void setStartProcessingTime(Instant startProcessingTime) {
        this.startProcessingTime = startProcessingTime;
    }

    public Instant getFinishProcessingTime() {
        return this.finishProcessingTime;
    }

    public void setFinishProcessingTime(Instant finishProcessingTime) {
        this.finishProcessingTime = finishProcessingTime;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCancelledBy() {
        return this.cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSpaceKeys() {
        return this.spaceKeys;
    }

    public void setSpaceKeys(String spaceKeys) {
        this.spaceKeys = spaceKeys;
    }

    public Instant getFileDeleteTime() {
        return this.fileDeleteTime;
    }

    public void setFileDeleteTime(Instant fileDeleteTime) {
        this.fileDeleteTime = fileDeleteTime;
    }

    public Boolean isFileExists() {
        return this.fileExists;
    }

    public void setFileExists(Boolean fileExists) {
        this.fileExists = fileExists;
    }

    public Instant getCancelTime() {
        return this.cancelTime;
    }

    public void setCancelTime(Instant cancelTime) {
        this.cancelTime = cancelTime;
    }
}

