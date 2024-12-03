/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.catalogue.model;

import java.util.List;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PagedResponse<R> {
    @JsonProperty
    public int start;
    @JsonProperty
    public int limit;
    @JsonProperty
    public int size;
    @JsonProperty
    public List<R> results;

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
    public List<R> getResults() {
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
    public void setResults(List<R> results) {
        this.results = results;
    }
}

