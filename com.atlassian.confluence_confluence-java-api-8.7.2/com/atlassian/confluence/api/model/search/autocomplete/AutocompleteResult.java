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
import com.atlassian.confluence.api.model.search.autocomplete.AutocompleteResultItem;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class AutocompleteResult {
    @JsonProperty
    private final List<AutocompleteResultItem> suggestedResults;
    @JsonProperty
    private final List<AutocompleteResultItem> searchResults;

    @JsonCreator
    private AutocompleteResult() {
        this(AutocompleteResult.builder());
    }

    private AutocompleteResult(Builder builder) {
        this.suggestedResults = Collections.unmodifiableList(builder.suggestedResults);
        this.searchResults = Collections.unmodifiableList(builder.searchResults);
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<AutocompleteResultItem> getSuggestedResults() {
        return this.suggestedResults;
    }

    public List<AutocompleteResultItem> getSearchResults() {
        return this.searchResults;
    }

    public static class Builder {
        private List<AutocompleteResultItem> suggestedResults = Collections.emptyList();
        private List<AutocompleteResultItem> searchResults = Collections.emptyList();

        private Builder() {
        }

        public Builder suggestedResults(List<AutocompleteResultItem> suggestedResults) {
            this.suggestedResults = suggestedResults;
            return this;
        }

        public Builder searchResults(List<AutocompleteResultItem> searchResults) {
            this.searchResults = searchResults;
            return this;
        }

        public AutocompleteResult build() {
            return new AutocompleteResult(this);
        }
    }
}

