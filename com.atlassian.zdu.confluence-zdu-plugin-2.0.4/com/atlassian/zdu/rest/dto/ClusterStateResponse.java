/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.zdu.rest.dto;

import com.atlassian.zdu.rest.dto.ClusterState;
import com.atlassian.zdu.rest.dto.NodeInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonProperty;

@Schema(description="Represents a Cluster state response including the current state and a specific Node's information.")
public class ClusterStateResponse {
    @JsonProperty
    @Schema(description="The current state of the Cluster.")
    private final ClusterState state;
    @JsonProperty
    @Schema(description="The responding Node's information and state.")
    private final NodeInfoDTO buildInfo;

    public ClusterStateResponse(@Nonnull ClusterState state, NodeInfoDTO buildInfo) {
        this.state = Objects.requireNonNull(state);
        this.buildInfo = buildInfo;
    }

    public ClusterState getState() {
        return this.state;
    }

    public NodeInfoDTO getBuildInfo() {
        return this.buildInfo;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClusterStateResponse that = (ClusterStateResponse)o;
        return this.state == that.state && this.buildInfo.equals(that.buildInfo);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.state, this.buildInfo});
    }
}

