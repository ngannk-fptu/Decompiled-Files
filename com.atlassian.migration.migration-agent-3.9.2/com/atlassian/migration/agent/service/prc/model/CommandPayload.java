/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.prc.model;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CommandPayload {
    @JsonProperty
    private String jobId;
    @JsonProperty
    private String taskId;
    @JsonProperty
    private String destId;

    @Generated
    public CommandPayload(String jobId, String taskId, String destId) {
        this.jobId = jobId;
        this.taskId = taskId;
        this.destId = destId;
    }

    @Generated
    public CommandPayload() {
    }

    @Generated
    public String getJobId() {
        return this.jobId;
    }

    @Generated
    public String getTaskId() {
        return this.taskId;
    }

    @Generated
    public String getDestId() {
        return this.destId;
    }
}

