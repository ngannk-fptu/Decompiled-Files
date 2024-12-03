/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.google.common.base.Stopwatch
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.retentionrules.rest.model;

import com.atlassian.confluence.plugins.retentionrules.rest.model.SpaceDescriptor;
import com.atlassian.confluence.search.v2.SearchResults;
import com.google.common.base.Stopwatch;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonProperty;

public class SpaceSearchResult
implements Serializable {
    private final List<SpaceDescriptor> results;
    private final int size;
    private final long searchDuration;

    SpaceSearchResult(SearchResults searchResults, long searchDuration) {
        this.results = this.extractSpaces(searchResults);
        this.size = searchResults.size();
        this.searchDuration = searchDuration;
    }

    public static SpaceSearchResult from(Callable<SearchResults> searchResultsCallable) throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        SearchResults searchResults = searchResultsCallable.call();
        return new SpaceSearchResult(searchResults, stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    private List<SpaceDescriptor> extractSpaces(SearchResults searchResults) {
        return searchResults.getAll().stream().map(SpaceDescriptor::new).collect(Collectors.toList());
    }

    @JsonProperty
    public List<SpaceDescriptor> getResults() {
        return this.results;
    }

    @JsonProperty
    public int getSize() {
        return this.size;
    }

    @JsonProperty
    public long getSearchDuration() {
        return this.searchDuration;
    }
}

