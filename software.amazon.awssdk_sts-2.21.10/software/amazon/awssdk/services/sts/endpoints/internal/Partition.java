/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.services.sts.endpoints.internal.Outputs;
import software.amazon.awssdk.services.sts.endpoints.internal.RegionOverride;

@SdkInternalApi
public class Partition {
    private static final String ID = "id";
    private static final String REGION_REGEX = "regionRegex";
    private static final String REGIONS = "regions";
    private static final String OUTPUTS = "outputs";
    private final String id;
    private final String regionRegex;
    private final Map<String, RegionOverride> regions;
    private final Outputs outputs;

    private Partition(Builder builder) {
        this.id = builder.id;
        this.regionRegex = builder.regionRegex;
        this.regions = new HashMap<String, RegionOverride>(builder.regions);
        this.outputs = builder.outputs;
    }

    public String id() {
        return this.id;
    }

    public String regionRegex() {
        return this.regionRegex;
    }

    public Map<String, RegionOverride> regions() {
        return this.regions;
    }

    public Outputs outputs() {
        return this.outputs;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Partition partition = (Partition)o;
        if (this.id != null ? !this.id.equals(partition.id) : partition.id != null) {
            return false;
        }
        if (this.regionRegex != null ? !this.regionRegex.equals(partition.regionRegex) : partition.regionRegex != null) {
            return false;
        }
        if (this.regions != null ? !this.regions.equals(partition.regions) : partition.regions != null) {
            return false;
        }
        return this.outputs != null ? this.outputs.equals(partition.outputs) : partition.outputs == null;
    }

    public int hashCode() {
        int result = this.id != null ? this.id.hashCode() : 0;
        result = 31 * result + (this.regionRegex != null ? this.regionRegex.hashCode() : 0);
        result = 31 * result + (this.regions != null ? this.regions.hashCode() : 0);
        result = 31 * result + (this.outputs != null ? this.outputs.hashCode() : 0);
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Partition fromNode(JsonNode node) {
        JsonNode outputs;
        JsonNode regions;
        JsonNode regionRegex;
        Builder b = Partition.builder();
        Map objNode = node.asObject();
        JsonNode id = (JsonNode)objNode.get(ID);
        if (id != null) {
            b.id(id.asString());
        }
        if ((regionRegex = (JsonNode)objNode.get(REGION_REGEX)) != null) {
            b.regionRegex(regionRegex.asString());
        }
        if ((regions = (JsonNode)objNode.get(REGIONS)) != null) {
            Map regionsObj = regions.asObject();
            regionsObj.forEach((k, v) -> b.putRegion((String)k, RegionOverride.fromNode(v)));
        }
        if ((outputs = (JsonNode)objNode.get(OUTPUTS)) != null) {
            b.outputs(Outputs.fromNode(outputs));
        }
        return b.build();
    }

    public static class Builder {
        private String id;
        private String regionRegex;
        private Map<String, RegionOverride> regions = new HashMap<String, RegionOverride>();
        private Outputs outputs;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder regionRegex(String regionRegex) {
            this.regionRegex = regionRegex;
            return this;
        }

        public Builder regions(Map<String, RegionOverride> regions) {
            this.regions.clear();
            if (regions != null) {
                this.regions.putAll(regions);
            }
            return this;
        }

        public Builder putRegion(String name, RegionOverride regionOverride) {
            this.regions.put(name, regionOverride);
            return this;
        }

        public Builder outputs(Outputs outputs) {
            this.outputs = outputs;
            return this;
        }

        public Partition build() {
            return new Partition(this);
        }
    }
}

