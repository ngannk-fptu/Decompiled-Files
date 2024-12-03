/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.search;

import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.search.SearchMatch;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchResults {
    private final List<Message> errors = new ArrayList<Message>();
    private final List<SearchMatch> matches = new ArrayList<SearchMatch>();
    private final long searchTime;
    private final int totalResults;

    public SearchResults(List<Message> errors) {
        this.errors.addAll(errors);
        this.searchTime = 0L;
        this.totalResults = 0;
    }

    public SearchResults(List<SearchMatch> matches, int totalResults, long searchTime) {
        this.totalResults = totalResults;
        this.matches.addAll(matches);
        this.searchTime = searchTime;
    }

    public List<Message> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    public List<SearchMatch> getMatches() {
        return Collections.unmodifiableList(this.matches);
    }

    public long getSearchTime() {
        return this.searchTime;
    }

    public int getTotalResults() {
        return this.totalResults;
    }
}

