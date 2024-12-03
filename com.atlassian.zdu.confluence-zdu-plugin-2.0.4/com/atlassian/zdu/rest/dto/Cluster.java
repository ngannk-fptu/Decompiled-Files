/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.zdu.rest.dto;

import com.atlassian.zdu.rest.LinkBuilder;
import com.atlassian.zdu.rest.dto.ClusterState;
import com.atlassian.zdu.rest.dto.Link;
import com.atlassian.zdu.rest.dto.NodeInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.annotate.JsonProperty;

@Schema(description="Represents the state of the Cluster, including all Nodes and its current state.")
@ParametersAreNonnullByDefault
public class Cluster {
    @JsonProperty
    @Schema(description="If true, it's safe to upgrade the nodes of the cluster")
    private final boolean upgradeModeEnabled;
    @JsonProperty
    @Schema(description="If true, there are finalization tasks that need to be run")
    private final boolean hasFinalizationTasks;
    @JsonProperty
    @Schema(description="The current state of the Cluster.")
    private final ClusterState state;
    @JsonProperty
    @Schema(description="The original product version when ZDU was enabled", required=false)
    private final String originalVersion;
    @JsonProperty
    @Schema(description="The nodes which form the Cluster.")
    private final List<NodeInfoDTO> nodes;

    public Cluster(boolean upgradeModeEnabled, boolean hasFinalizationTasks, ClusterState state, @Nullable String originalVersion, List<NodeInfoDTO> nodes) {
        this.upgradeModeEnabled = upgradeModeEnabled;
        this.hasFinalizationTasks = hasFinalizationTasks;
        this.state = Objects.requireNonNull(state);
        this.originalVersion = originalVersion;
        this.nodes = Objects.requireNonNull(nodes);
    }

    public boolean isUpgradeModeEnabled() {
        return this.upgradeModeEnabled;
    }

    public boolean hasFinalizationTasks() {
        return this.hasFinalizationTasks;
    }

    public ClusterState getState() {
        return this.state;
    }

    public String getOriginalVersion() {
        return this.originalVersion;
    }

    public List<NodeInfoDTO> getNodes() {
        return this.nodes;
    }

    @JsonProperty
    @Schema(description="Hypermedia links")
    public List<Link> getLinks() {
        return LinkBuilder.forCluster(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Cluster cluster = (Cluster)o;
        return this.state == cluster.state && this.nodes.equals(cluster.nodes);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.state, this.nodes});
    }
}

