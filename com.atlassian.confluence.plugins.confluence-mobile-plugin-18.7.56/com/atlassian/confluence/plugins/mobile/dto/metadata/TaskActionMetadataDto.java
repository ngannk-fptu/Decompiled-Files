/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto.metadata;

import com.atlassian.confluence.plugins.mobile.dto.metadata.AbstractActionMetadataDto;
import org.codehaus.jackson.annotate.JsonProperty;

public class TaskActionMetadataDto
extends AbstractActionMetadataDto {
    @JsonProperty
    private long taskId;

    public TaskActionMetadataDto(long taskId) {
        this.taskId = taskId;
    }

    public long getTaskId() {
        return this.taskId;
    }
}

