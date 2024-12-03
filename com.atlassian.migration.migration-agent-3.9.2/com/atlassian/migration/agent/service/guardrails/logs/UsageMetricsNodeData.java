/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.migration.agent.service.guardrails.logs;

import java.util.Arrays;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonValue;

public class UsageMetricsNodeData {
    @JsonProperty(value="id")
    private final String id;
    @JsonProperty(value="data_collection_status")
    private final DataCollectionStatus dataCollectionStatus;
    @JsonProperty(value="node_status")
    private final NodeStatus nodeStatus;

    @JsonCreator
    private static UsageMetricsNodeData create(@JsonProperty(value="id") String id, @JsonProperty(value="data_collection_status") DataCollectionStatus dataCollectionStatus, @JsonProperty(value="node_status") NodeStatus nodeStatus) {
        return new UsageMetricsNodeData(id, dataCollectionStatus, nodeStatus);
    }

    @Generated
    UsageMetricsNodeData(String id, DataCollectionStatus dataCollectionStatus, NodeStatus nodeStatus) {
        this.id = id;
        this.dataCollectionStatus = dataCollectionStatus;
        this.nodeStatus = nodeStatus;
    }

    @Generated
    public static UsageMetricsNodeDataBuilder builder() {
        return new UsageMetricsNodeDataBuilder();
    }

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public DataCollectionStatus getDataCollectionStatus() {
        return this.dataCollectionStatus;
    }

    @Generated
    public NodeStatus getNodeStatus() {
        return this.nodeStatus;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UsageMetricsNodeData)) {
            return false;
        }
        UsageMetricsNodeData other = (UsageMetricsNodeData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        DataCollectionStatus this$dataCollectionStatus = this.getDataCollectionStatus();
        DataCollectionStatus other$dataCollectionStatus = other.getDataCollectionStatus();
        if (this$dataCollectionStatus == null ? other$dataCollectionStatus != null : !((Object)((Object)this$dataCollectionStatus)).equals((Object)other$dataCollectionStatus)) {
            return false;
        }
        NodeStatus this$nodeStatus = this.getNodeStatus();
        NodeStatus other$nodeStatus = other.getNodeStatus();
        return !(this$nodeStatus == null ? other$nodeStatus != null : !((Object)((Object)this$nodeStatus)).equals((Object)other$nodeStatus));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UsageMetricsNodeData;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        DataCollectionStatus $dataCollectionStatus = this.getDataCollectionStatus();
        result = result * 59 + ($dataCollectionStatus == null ? 43 : ((Object)((Object)$dataCollectionStatus)).hashCode());
        NodeStatus $nodeStatus = this.getNodeStatus();
        result = result * 59 + ($nodeStatus == null ? 43 : ((Object)((Object)$nodeStatus)).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "UsageMetricsNodeData(id=" + this.getId() + ", dataCollectionStatus=" + (Object)((Object)this.getDataCollectionStatus()) + ", nodeStatus=" + (Object)((Object)this.getNodeStatus()) + ")";
    }

    @Generated
    public static class UsageMetricsNodeDataBuilder {
        @Generated
        private String id;
        @Generated
        private DataCollectionStatus dataCollectionStatus;
        @Generated
        private NodeStatus nodeStatus;

        @Generated
        UsageMetricsNodeDataBuilder() {
        }

        @Generated
        public UsageMetricsNodeDataBuilder id(String id) {
            this.id = id;
            return this;
        }

        @Generated
        public UsageMetricsNodeDataBuilder dataCollectionStatus(DataCollectionStatus dataCollectionStatus) {
            this.dataCollectionStatus = dataCollectionStatus;
            return this;
        }

        @Generated
        public UsageMetricsNodeDataBuilder nodeStatus(NodeStatus nodeStatus) {
            this.nodeStatus = nodeStatus;
            return this;
        }

        @Generated
        public UsageMetricsNodeData build() {
            return new UsageMetricsNodeData(this.id, this.dataCollectionStatus, this.nodeStatus);
        }

        @Generated
        public String toString() {
            return "UsageMetricsNodeData.UsageMetricsNodeDataBuilder(id=" + this.id + ", dataCollectionStatus=" + (Object)((Object)this.dataCollectionStatus) + ", nodeStatus=" + (Object)((Object)this.nodeStatus) + ")";
        }
    }

    public static enum NodeStatus {
        AVAILABLE("available"),
        UNAVAILABLE("unavailable");

        private final String value;

        private NodeStatus(String value) {
            this.value = value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        private static NodeStatus fromValue(String value) {
            return Arrays.stream(NodeStatus.values()).filter(it -> it.value.equals(value)).findAny().orElseThrow(() -> new IllegalArgumentException(value));
        }
    }

    public static enum DataCollectionStatus {
        COMPLETE("complete"),
        MISSING_FILE("missing file"),
        FAILED("failed");

        private final String value;

        private DataCollectionStatus(String value) {
            this.value = value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        private static DataCollectionStatus fromValue(String value) {
            return Arrays.stream(DataCollectionStatus.values()).filter(it -> it.value.equals(value)).findAny().orElseThrow(() -> new IllegalArgumentException(value));
        }
    }
}

