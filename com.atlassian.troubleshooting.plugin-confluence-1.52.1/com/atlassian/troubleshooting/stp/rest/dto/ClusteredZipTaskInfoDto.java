/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.stp.rest.dto;

import com.atlassian.troubleshooting.stp.rest.dto.NodeLocalSupportZipTaskInfoDto;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ClusteredZipTaskInfoDto {
    @JsonProperty
    private final String clusterTaskId;
    @JsonProperty
    private final Collection<NodeLocalSupportZipTaskInfoDto> tasks;

    @JsonCreator
    public ClusteredZipTaskInfoDto(@JsonProperty(value="clusterTaskId") String clusterTaskId, @JsonProperty(value="tasks") Collection<NodeLocalSupportZipTaskInfoDto> tasks) {
        this.clusterTaskId = clusterTaskId;
        this.tasks = ImmutableList.copyOf(tasks);
    }

    public String getClusterTaskId() {
        return this.clusterTaskId;
    }

    public Collection<NodeLocalSupportZipTaskInfoDto> getTasks() {
        return this.tasks;
    }
}

