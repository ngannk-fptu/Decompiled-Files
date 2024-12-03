/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.catalogue.model;

import com.atlassian.migration.agent.service.catalogue.Space;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ParametersAreNonnullByDefault
@JsonIgnoreProperties(ignoreUnknown=true)
public class SpaceResponse {
    @JsonProperty
    public int start;
    @JsonProperty
    public int limit;
    @JsonProperty
    public int size;
    @JsonProperty
    public List<Space> results;

    public SpaceResponse() {
    }

    @JsonCreator
    public SpaceResponse(@JsonProperty(value="start") int start, @JsonProperty(value="limit") int limit, @JsonProperty(value="size") int size, @JsonProperty(value="results") List<Space> results) {
        this.start = start;
        this.limit = limit;
        this.size = size;
        this.results = results;
    }

    @Generated
    public int getStart() {
        return this.start;
    }

    @Generated
    public int getLimit() {
        return this.limit;
    }

    @Generated
    public int getSize() {
        return this.size;
    }

    @Generated
    public List<Space> getResults() {
        return this.results;
    }

    @Generated
    public void setStart(int start) {
        this.start = start;
    }

    @Generated
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Generated
    public void setSize(int size) {
        this.size = size;
    }

    @Generated
    public void setResults(List<Space> results) {
        this.results = results;
    }
}

