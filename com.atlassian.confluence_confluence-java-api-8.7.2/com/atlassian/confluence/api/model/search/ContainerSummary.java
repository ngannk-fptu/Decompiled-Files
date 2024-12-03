/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.search;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class ContainerSummary {
    @JsonProperty
    private final String title;
    @JsonProperty
    private final String displayUrl;

    @JsonCreator
    private ContainerSummary() {
        this(ContainerSummary.builder());
    }

    private ContainerSummary(Builder builder) {
        this.title = builder.title;
        this.displayUrl = builder.displayUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDisplayUrl() {
        return this.displayUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title = "";
        private String displayUrl = "";

        private Builder() {
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder displayUrl(String displayUrl) {
            this.displayUrl = displayUrl;
            return this;
        }

        public ContainerSummary build() {
            return new ContainerSummary(this);
        }
    }
}

