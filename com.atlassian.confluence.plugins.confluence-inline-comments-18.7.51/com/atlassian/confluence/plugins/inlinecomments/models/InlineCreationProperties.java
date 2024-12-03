/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.serialization.RestEnrichable
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.inlinecomments.models;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public final class InlineCreationProperties {
    @JsonProperty
    private String originalSelection;
    @JsonProperty
    private Integer numMatches;
    @JsonProperty
    private Integer matchIndex;
    @JsonProperty
    private Long lastFetchTime;
    @JsonProperty
    private String serializedHighlights;

    public String getOriginalSelection() {
        return this.originalSelection;
    }

    public void setOriginalSelection(String originalSelection) {
        this.originalSelection = originalSelection;
    }

    public Integer getNumMatches() {
        return this.numMatches;
    }

    public void setNumMatches(Integer numMatches) {
        this.numMatches = numMatches;
    }

    public Integer getMatchIndex() {
        return this.matchIndex;
    }

    public void setMatchIndex(Integer matchIndex) {
        this.matchIndex = matchIndex;
    }

    public Long getLastFetchTime() {
        return this.lastFetchTime;
    }

    public void setLastFetchTime(Long lastFetchTime) {
        this.lastFetchTime = lastFetchTime;
    }

    public String getSerializedHighlights() {
        return this.serializedHighlights;
    }

    public void setSerializedHighlights(String serializedHighlights) {
        this.serializedHighlights = serializedHighlights;
    }
}

