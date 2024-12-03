/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.serialization.RestEnrichable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.inlinecomments.models;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public final class InlineProperties {
    @JsonProperty
    private final String originalSelection;
    @JsonProperty
    private final String markerRef;

    @JsonCreator
    private InlineProperties(Builder builder) {
        this.originalSelection = builder.originalSelection;
        this.markerRef = builder.markerRef;
    }

    public String getOriginalSelection() {
        return this.originalSelection;
    }

    public String getMarkerRef() {
        return this.markerRef;
    }

    public static class Builder {
        private String originalSelection;
        private String markerRef;

        public Builder setOriginalSelection(String originalSelection) {
            this.originalSelection = originalSelection;
            return this;
        }

        public Builder setMarkerRef(String markerRef) {
            this.markerRef = markerRef;
            return this;
        }

        public InlineProperties build() {
            return new InlineProperties(this);
        }
    }
}

