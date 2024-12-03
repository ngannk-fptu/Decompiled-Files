/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.stp.rest.dto;

import com.atlassian.troubleshooting.stp.action.Message;
import com.atlassian.troubleshooting.stp.rest.dto.LocalSupportZipTaskInfoDto;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipMonitor;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class NodeLocalSupportZipTaskInfoDto
extends LocalSupportZipTaskInfoDto {
    @JsonProperty
    private final String nodeId;

    @JsonCreator
    @VisibleForTesting
    NodeLocalSupportZipTaskInfoDto(@JsonProperty(value="taskId") String taskId, @JsonProperty(value="progressPercentage") int progressPercentage, @JsonProperty(value="progressMessage") String progressMessage, @JsonProperty(value="nodeId") String nodeId, @JsonProperty(value="fileName") String fileName, @JsonProperty(value="warnings") Collection<Message> warnings, @JsonProperty(value="status") TaskMonitor.Status status, @JsonProperty(value="truncatedFiles") List<String> truncatedFiles, @JsonProperty(value="ageExcludedFiles") List<String> ageExcludedFiles, @JsonProperty(value="disabledButton") Boolean disabledButton) {
        super(taskId, progressPercentage, progressMessage, fileName, warnings, status, truncatedFiles, ageExcludedFiles, disabledButton);
        this.nodeId = nodeId;
    }

    public static NodeLocalSupportZipTaskInfoDto nodeAwareLocalSupportZipInfo(CreateSupportZipMonitor taskMonitor, @Nullable Boolean disabledButton) {
        LocalSupportZipTaskInfoDto localInfo = NodeLocalSupportZipTaskInfoDto.localSupportZipTaskInfo(taskMonitor, disabledButton);
        return new NodeLocalSupportZipTaskInfoDto(localInfo.getTaskId(), localInfo.getProgressPercentage(), localInfo.getProgressMessage(), taskMonitor.getNodeId().orElse(null), localInfo.getFileName(), localInfo.getWarnings(), localInfo.getStatus(), localInfo.getTruncatedFiles(), localInfo.getAgeExcludedFiles(), localInfo.isDisabledButton());
    }

    public String getNodeId() {
        return this.nodeId;
    }
}

