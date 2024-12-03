/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.portfolioanalyzer.model;

import com.atlassian.migration.agent.service.portfolioanalyzer.model.AnalysisMetadata;
import com.atlassian.migration.agent.service.portfolioanalyzer.model.SpaceNode;
import com.atlassian.migration.agent.service.portfolioanalyzer.model.SpaceRelations;
import java.util.List;
import java.util.Set;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class SpaceRelationsGraph {
    @JsonProperty
    private final AnalysisMetadata metadata;
    @JsonProperty
    private final Set<SpaceNode> nodes;
    @JsonProperty
    private final List<SpaceRelations> relations;

    public SpaceRelationsGraph(@JsonProperty(value="metadata") AnalysisMetadata metadata, @JsonProperty(value="nodes") Set<SpaceNode> nodes, @JsonProperty(value="relations") List<SpaceRelations> relations) {
        this.metadata = metadata;
        this.nodes = nodes;
        this.relations = relations;
    }

    @Generated
    public AnalysisMetadata getMetadata() {
        return this.metadata;
    }

    @Generated
    public Set<SpaceNode> getNodes() {
        return this.nodes;
    }

    @Generated
    public List<SpaceRelations> getRelations() {
        return this.relations;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SpaceRelationsGraph)) {
            return false;
        }
        SpaceRelationsGraph other = (SpaceRelationsGraph)o;
        if (!other.canEqual(this)) {
            return false;
        }
        AnalysisMetadata this$metadata = this.getMetadata();
        AnalysisMetadata other$metadata = other.getMetadata();
        if (this$metadata == null ? other$metadata != null : !((Object)this$metadata).equals(other$metadata)) {
            return false;
        }
        Set<SpaceNode> this$nodes = this.getNodes();
        Set<SpaceNode> other$nodes = other.getNodes();
        if (this$nodes == null ? other$nodes != null : !((Object)this$nodes).equals(other$nodes)) {
            return false;
        }
        List<SpaceRelations> this$relations = this.getRelations();
        List<SpaceRelations> other$relations = other.getRelations();
        return !(this$relations == null ? other$relations != null : !((Object)this$relations).equals(other$relations));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof SpaceRelationsGraph;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        AnalysisMetadata $metadata = this.getMetadata();
        result = result * 59 + ($metadata == null ? 43 : ((Object)$metadata).hashCode());
        Set<SpaceNode> $nodes = this.getNodes();
        result = result * 59 + ($nodes == null ? 43 : ((Object)$nodes).hashCode());
        List<SpaceRelations> $relations = this.getRelations();
        result = result * 59 + ($relations == null ? 43 : ((Object)$relations).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "SpaceRelationsGraph(metadata=" + this.getMetadata() + ", nodes=" + this.getNodes() + ", relations=" + this.getRelations() + ")";
    }
}

