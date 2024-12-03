/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.stp.rest.dto;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ClusterNodeDto {
    @JsonProperty
    private final String nodeId;

    @JsonCreator
    public ClusterNodeDto(@Nonnull @JsonProperty(value="nodeId") String nodeId) {
        this.nodeId = Objects.requireNonNull(nodeId);
    }

    public String getNodeId() {
        return this.nodeId;
    }
}

