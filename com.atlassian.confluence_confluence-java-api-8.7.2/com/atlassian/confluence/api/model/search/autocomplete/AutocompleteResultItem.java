/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.search.autocomplete;

import com.atlassian.annotations.ExperimentalApi;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class AutocompleteResultItem {
    @JsonProperty
    private final String id;
    @JsonProperty
    private final String text;

    @JsonCreator
    private AutocompleteResultItem() {
        this(AutocompleteResultItem.builder());
    }

    private AutocompleteResultItem(Builder builder) {
        this.id = builder.id;
        this.text = builder.text;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public static class Builder {
        private String id;
        private String text;

        private Builder() {
        }

        public AutocompleteResultItem build() {
            return new AutocompleteResultItem(this);
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }
    }
}

