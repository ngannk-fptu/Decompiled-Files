/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.stp.rest.dto;

import com.atlassian.troubleshooting.stp.action.Message;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipMonitor;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class LocalSupportZipTaskInfoDto {
    @JsonProperty
    private final String taskId;
    @JsonProperty
    private final int progressPercentage;
    @JsonProperty
    private final String progressMessage;
    @JsonProperty
    private final String fileName;
    @JsonProperty
    private final List<Message> warnings;
    @JsonProperty
    private final TaskMonitor.Status status;
    @JsonProperty
    private final List<String> truncatedFiles;
    @JsonProperty
    private final List<String> ageExcludedFiles;
    @JsonProperty
    private final Boolean disabledButton;

    @JsonCreator
    LocalSupportZipTaskInfoDto(@JsonProperty(value="taskId") String taskId, @JsonProperty(value="progressPercentage") int progressPercentage, @JsonProperty(value="progressMessage") String progressMessage, @JsonProperty(value="fileName") String fileName, @JsonProperty(value="warnings") Collection<Message> warnings, @JsonProperty(value="status") TaskMonitor.Status status, @JsonProperty(value="truncatedFiles") List<String> truncatedFiles, @JsonProperty(value="ageExcludedFiles") List<String> ageExcludedFiles) {
        this(taskId, progressPercentage, progressMessage, fileName, warnings, status, truncatedFiles, ageExcludedFiles, null);
    }

    LocalSupportZipTaskInfoDto(String taskId, int progressPercentage, String progressMessage, String fileName, Collection<Message> warnings, TaskMonitor.Status status, List<String> truncatedFiles, List<String> ageExcludedFiles, Boolean disabledButton) {
        this.fileName = fileName;
        this.progressMessage = progressMessage;
        this.progressPercentage = progressPercentage;
        this.taskId = taskId;
        this.warnings = warnings == null ? Collections.emptyList() : ImmutableList.copyOf(warnings);
        this.status = status;
        this.truncatedFiles = truncatedFiles;
        this.ageExcludedFiles = ageExcludedFiles;
        this.disabledButton = disabledButton;
    }

    @Nonnull
    public static LocalSupportZipTaskInfoDto localSupportZipTaskInfo(@Nonnull CreateSupportZipMonitor taskMonitor) {
        return new LocalSupportZipTaskInfoDto(taskMonitor.getTaskId(), taskMonitor.getProgressPercentage(), taskMonitor.getProgressMessage(), (String)((Object)taskMonitor.getAttributes().get("zipFileName")), taskMonitor.getWarnings(), taskMonitor.getStatus(), taskMonitor.getTruncatedFiles(), taskMonitor.getAgeExcludedFiles());
    }

    @Nonnull
    public static LocalSupportZipTaskInfoDto localSupportZipTaskInfo(@Nonnull CreateSupportZipMonitor taskMonitor, @Nullable Boolean disabledButton) {
        return new LocalSupportZipTaskInfoDto(taskMonitor.getTaskId(), taskMonitor.getProgressPercentage(), taskMonitor.getProgressMessage(), (String)((Object)taskMonitor.getAttributes().get("zipFileName")), taskMonitor.getWarnings(), taskMonitor.getStatus(), taskMonitor.getTruncatedFiles(), taskMonitor.getAgeExcludedFiles(), disabledButton);
    }

    public String getTaskId() {
        return this.taskId;
    }

    public int getProgressPercentage() {
        return this.progressPercentage;
    }

    public String getProgressMessage() {
        return this.progressMessage;
    }

    @Nullable
    public String getFileName() {
        return this.fileName;
    }

    @Nonnull
    public List<Message> getWarnings() {
        return this.warnings;
    }

    public TaskMonitor.Status getStatus() {
        return this.status;
    }

    public List<String> getTruncatedFiles() {
        return this.truncatedFiles;
    }

    public List<String> getAgeExcludedFiles() {
        return this.ageExcludedFiles;
    }

    public Boolean isDisabledButton() {
        return this.disabledButton;
    }
}

