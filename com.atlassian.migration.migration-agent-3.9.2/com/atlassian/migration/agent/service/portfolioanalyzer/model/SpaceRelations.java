/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.portfolioanalyzer.model;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class SpaceRelations {
    @JsonProperty
    public final String source;
    @JsonProperty
    public final String destination;
    @JsonProperty
    public final Long count;

    public SpaceRelations(@JsonProperty(value="source") String source, @JsonProperty(value="destination") String destination, @JsonProperty(value="count") Long count) {
        this.source = source;
        this.destination = destination;
        this.count = count;
    }

    @Generated
    public String getSource() {
        return this.source;
    }

    @Generated
    public String getDestination() {
        return this.destination;
    }

    @Generated
    public Long getCount() {
        return this.count;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SpaceRelations)) {
            return false;
        }
        SpaceRelations other = (SpaceRelations)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$source = this.getSource();
        String other$source = other.getSource();
        if (this$source == null ? other$source != null : !this$source.equals(other$source)) {
            return false;
        }
        String this$destination = this.getDestination();
        String other$destination = other.getDestination();
        if (this$destination == null ? other$destination != null : !this$destination.equals(other$destination)) {
            return false;
        }
        Long this$count = this.getCount();
        Long other$count = other.getCount();
        return !(this$count == null ? other$count != null : !((Object)this$count).equals(other$count));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof SpaceRelations;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $source = this.getSource();
        result = result * 59 + ($source == null ? 43 : $source.hashCode());
        String $destination = this.getDestination();
        result = result * 59 + ($destination == null ? 43 : $destination.hashCode());
        Long $count = this.getCount();
        result = result * 59 + ($count == null ? 43 : ((Object)$count).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "SpaceRelations(source=" + this.getSource() + ", destination=" + this.getDestination() + ", count=" + this.getCount() + ")";
    }
}

