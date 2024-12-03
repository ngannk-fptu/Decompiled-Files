/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.zdu.rest.dto;

import com.atlassian.zdu.internal.api.NodeFinalizationInfo;
import com.atlassian.zdu.internal.api.NodeInfo;
import com.atlassian.zdu.rest.LinkBuilder;
import com.atlassian.zdu.rest.dto.Link;
import com.atlassian.zdu.rest.dto.NodeState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

@Schema(description="Represents a Cluster Node and its current state.")
public class NodeInfoDTO
implements NodeInfo {
    @JsonProperty
    @Schema(description="The id of the Node in cluster.")
    private final String id;
    @JsonProperty
    @Schema(description="The name of the Node.", nullable=true)
    private final String name;
    @JsonProperty
    @Schema(description="The IP address of the Node.")
    private final String ipAddress;
    @JsonProperty
    @Schema(description="The current state of the Node.")
    private NodeState state;
    @JsonProperty
    @Schema(description="The total number of active tasks on the Node.", nullable=true)
    private final Integer tasksTotal;
    @JsonProperty
    @Schema(description="The total number of active users on the Node.", nullable=true)
    private Integer activeUserCount;
    @JsonProperty
    @Schema(description="The build number (DB schema version) of the Node's installed Product.")
    private final String buildNumber;
    @JsonProperty
    @Schema(description="The version of the Node's installed Product.")
    private final String version;
    @JsonProperty
    @Schema(description="True if this is the local node for the current session; otherwise false for remote nodes.")
    private final boolean local;
    @JsonIgnore
    private final int portNumber;
    @JsonProperty
    @Schema(description="Finalization info", nullable=true)
    @Nullable
    private final NodeFinalizationInfo finalization;

    @JsonProperty
    @Schema(description="Hypermedia links")
    public List<Link> getLinks() {
        return LinkBuilder.forClusterNode(this);
    }

    public NodeInfoDTO(Builder builder) {
        this.id = Objects.requireNonNull(builder.id);
        this.portNumber = builder.portNumber;
        this.name = Objects.requireNonNull(builder.name);
        this.ipAddress = Objects.requireNonNull(builder.ipAddress);
        this.state = builder.state;
        this.tasksTotal = builder.tasksTotal;
        this.activeUserCount = builder.activeUserCount;
        this.buildNumber = builder.buildNumber;
        this.version = builder.version;
        this.local = builder.local;
        this.finalization = builder.finalization;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getIpAddress() {
        return this.ipAddress;
    }

    @Override
    public int getPortNumber() {
        return this.portNumber;
    }

    @Override
    public NodeState getState() {
        return this.state;
    }

    public void setState(NodeState state) {
        this.state = state;
    }

    @Override
    public Integer getTasksTotal() {
        return this.tasksTotal;
    }

    @Override
    public Integer getActiveUserCount() {
        return this.activeUserCount;
    }

    @Override
    public String getBuildNumber() {
        return this.buildNumber;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public boolean isLocal() {
        return this.local;
    }

    @Override
    public NodeFinalizationInfo getFinalization() {
        return this.finalization;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(NodeInfo node) {
        return new Builder(node);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NodeInfoDTO that = (NodeInfoDTO)o;
        return this.presistentIdentifier(this).equals(this.presistentIdentifier(that));
    }

    public int hashCode() {
        return Objects.hash(this.presistentIdentifier(this));
    }

    private String presistentIdentifier(NodeInfoDTO node) {
        return String.format("%s:%s", node.ipAddress, node.portNumber);
    }

    public static class Builder {
        private String id;
        private String name;
        private String ipAddress;
        private int portNumber;
        private NodeState state;
        private Integer tasksTotal;
        private Integer activeUserCount;
        private String buildNumber;
        private String version;
        private boolean local;
        private NodeFinalizationInfo finalization;

        private Builder() {
        }

        private Builder(@Nonnull NodeInfo clusterNode) {
            this.id = clusterNode.getId();
            this.name = clusterNode.getName();
            this.ipAddress = clusterNode.getIpAddress();
            this.portNumber = clusterNode.getPortNumber();
            this.state = (NodeState)clusterNode.getState();
            this.tasksTotal = clusterNode.getTasksTotal();
            this.activeUserCount = clusterNode.getActiveUserCount();
            this.buildNumber = clusterNode.getBuildNumber();
            this.version = clusterNode.getVersion();
            this.local = clusterNode.isLocal();
            this.finalization = clusterNode.getFinalization();
        }

        public Builder id(@Nonnull String id) {
            this.id = id;
            return this;
        }

        public Builder name(@Nullable String name) {
            this.name = name;
            return this;
        }

        public Builder ipAddress(@Nonnull String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder portNumber(int portNumber) {
            this.portNumber = portNumber;
            return this;
        }

        public Builder state(NodeState state) {
            this.state = state;
            return this;
        }

        public Builder tasksTotal(@Nullable Integer tasksTotal) {
            this.tasksTotal = tasksTotal;
            return this;
        }

        public Builder activeUserCount(@Nullable Integer activeUserCount) {
            this.activeUserCount = activeUserCount;
            return this;
        }

        public Builder buildNumber(@Nullable String buildNumber) {
            this.buildNumber = buildNumber;
            return this;
        }

        public Builder version(@Nullable String version) {
            this.version = version;
            return this;
        }

        public Builder local(boolean local) {
            this.local = local;
            return this;
        }

        public Builder finalization(NodeFinalizationInfo finalization) {
            this.finalization = finalization;
            return this;
        }

        public NodeInfoDTO build() {
            this.name = this.name == null ? "Not configured" : this.name;
            return new NodeInfoDTO(this);
        }
    }
}

