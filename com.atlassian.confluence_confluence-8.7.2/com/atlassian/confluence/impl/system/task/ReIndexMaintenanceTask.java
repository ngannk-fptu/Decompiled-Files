/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.impl.system.task;

import com.atlassian.confluence.event.events.admin.ReIndexRequestEvent;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTask;
import com.atlassian.confluence.search.ReIndexOption;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.EnumSet;
import java.util.List;

public class ReIndexMaintenanceTask
implements SystemMaintenanceTask {
    private final String sourceNodeId;
    private final String jobId;
    private final EnumSet<ReIndexOption> options;
    private final List<String> spaceKeys;

    @JsonCreator
    public ReIndexMaintenanceTask(@JsonProperty(value="jobId") String jobId, @JsonProperty(value="sourceNodeId") String sourceNodeId, @JsonProperty(value="options") EnumSet<ReIndexOption> options, @JsonProperty(value="spaceKeys") List<String> spaceKeys) {
        this.jobId = jobId;
        this.sourceNodeId = sourceNodeId;
        this.options = options;
        this.spaceKeys = spaceKeys;
    }

    public ReIndexMaintenanceTask(ReIndexRequestEvent reIndexRequestEvent) {
        this.sourceNodeId = reIndexRequestEvent.getSourceNodeId();
        this.jobId = reIndexRequestEvent.getJobId();
        this.options = reIndexRequestEvent.getOptions();
        this.spaceKeys = reIndexRequestEvent.getSpaceKeys();
    }

    @JsonProperty(value="sourceNodeId")
    public String getSourceNodeId() {
        return this.sourceNodeId;
    }

    @JsonProperty(value="jobId")
    public String getJobId() {
        return this.jobId;
    }

    @JsonProperty(value="options")
    public EnumSet<ReIndexOption> getOptions() {
        return this.options;
    }

    @JsonProperty(value="spaceKeys")
    public List<String> getSpaceKeys() {
        return this.spaceKeys;
    }

    public String toString() {
        return "ReIndexMaintenanceTask{sourceNodeId='" + this.sourceNodeId + "', jobId='" + this.jobId + "', options=" + this.options + ", spaceKeys=" + this.spaceKeys + "}";
    }
}

