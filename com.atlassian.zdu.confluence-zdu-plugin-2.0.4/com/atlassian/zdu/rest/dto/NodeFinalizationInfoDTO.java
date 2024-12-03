/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.zdu.rest.dto;

import com.atlassian.zdu.internal.api.NodeFinalizationInfo;
import com.atlassian.zdu.internal.api.UpgradeTaskError;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

@Schema(description="Finalization information of a cluster node.")
public class NodeFinalizationInfoDTO
implements NodeFinalizationInfo {
    @JsonProperty
    @Schema(description="Time when finalization was last requested on this node")
    private final Date lastRequested;
    @JsonProperty
    @Schema(description="True if this is the node that would run cluster-wide finalization tasks")
    private final boolean runsClusterWideTasks;
    @JsonProperty
    @Schema(description="Any errors which occurred when performing finalization upgrade tasks.")
    private final List<UpgradeTaskError> errors;

    public NodeFinalizationInfoDTO(Builder builder) {
        this.lastRequested = builder.lastRequested;
        this.runsClusterWideTasks = builder.runsClusterWideTasks;
        this.errors = builder.errors;
    }

    @Override
    public Date getLastRequested() {
        return this.lastRequested;
    }

    @Override
    public boolean runsClusterWideTasks() {
        return this.runsClusterWideTasks;
    }

    @Override
    public List<UpgradeTaskError> getErrors() {
        return this.errors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Date lastRequested;
        private boolean runsClusterWideTasks;
        private List<UpgradeTaskError> errors = new ArrayList<UpgradeTaskError>();

        private Builder() {
        }

        public Builder lastRequested(Date lastRequested) {
            this.lastRequested = lastRequested;
            return this;
        }

        public Builder runsClusterWideTasks(boolean clusterWide) {
            this.runsClusterWideTasks = clusterWide;
            return this;
        }

        public Builder errors(List<UpgradeTaskError> errors) {
            this.errors = errors;
            return this;
        }

        public NodeFinalizationInfoDTO build() {
            return new NodeFinalizationInfoDTO(this);
        }
    }
}

