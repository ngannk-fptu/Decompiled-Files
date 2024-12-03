/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.services.s3.endpoints.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.services.s3.endpoints.internal.Partition;

@SdkInternalApi
public final class Partitions {
    private static final String VERSION = "version";
    private static final String PARTITIONS = "partitions";
    private final String version;
    private final List<Partition> partitions;

    private Partitions(Builder builder) {
        this.version = builder.version;
        this.partitions = new ArrayList<Partition>(builder.partitions);
    }

    public String version() {
        return this.version;
    }

    public List<Partition> partitions() {
        return this.partitions;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Partitions that = (Partitions)o;
        if (this.version != null ? !this.version.equals(that.version) : that.version != null) {
            return false;
        }
        return this.partitions != null ? this.partitions.equals(that.partitions) : that.partitions == null;
    }

    public int hashCode() {
        int result = this.version != null ? this.version.hashCode() : 0;
        result = 31 * result + (this.partitions != null ? this.partitions.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Partitions{version='" + this.version + '\'' + ", partitions=" + this.partitions + '}';
    }

    public static Partitions fromNode(JsonNode node) {
        JsonNode partitions;
        Map objNode = node.asObject();
        Builder b = Partitions.builder();
        JsonNode version = (JsonNode)objNode.get(VERSION);
        if (version != null) {
            b.version(version.asString());
        }
        if ((partitions = (JsonNode)objNode.get(PARTITIONS)) != null) {
            partitions.asArray().forEach(partNode -> b.addPartition(Partition.fromNode(partNode)));
        }
        return b.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String version;
        private List<Partition> partitions = new ArrayList<Partition>();

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder partitions(List<Partition> partitions) {
            this.partitions.clear();
            if (partitions != null) {
                this.partitions.addAll(partitions);
            }
            return this;
        }

        public Builder addPartition(Partition p) {
            this.partitions.add(p);
            return this;
        }

        public Partitions build() {
            return new Partitions(this);
        }
    }
}

