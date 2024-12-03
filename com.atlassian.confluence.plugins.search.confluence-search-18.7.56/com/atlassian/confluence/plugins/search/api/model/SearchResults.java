/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.search.api.model;

import com.atlassian.confluence.plugins.search.api.model.SearchResult;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SearchResults {
    private final int totalSize;
    private final int extendedTotalSize;
    private final List<SearchResult> results;
    private final UUID uuid = UUID.randomUUID();
    private final long timeSpent;
    private final String searchQuery;

    public SearchResults(int totalSize, int extendedTotalSize, List<SearchResult> results, long timeSpent, String searchQuery) {
        this.results = Objects.requireNonNull(results);
        this.totalSize = totalSize;
        this.timeSpent = timeSpent;
        this.extendedTotalSize = extendedTotalSize;
        this.searchQuery = searchQuery;
    }

    @Deprecated
    public SearchResults(int totalSize, List<SearchResult> results, long timeSpent) {
        this(totalSize, 0, results, timeSpent, "");
    }

    @Deprecated
    public SearchResults(int totalSize, int extendedTotalSize, List<SearchResult> results, long timeSpent) {
        this(totalSize, extendedTotalSize, results, timeSpent, "");
    }

    public int getTotalSize() {
        return this.totalSize;
    }

    public List<SearchResult> getResults() {
        return this.results;
    }

    public int getExtendedTotalSize() {
        return this.extendedTotalSize;
    }

    public long getTimeSpent() {
        return this.timeSpent;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getSearchQuery() {
        return this.searchQuery;
    }

    public static class Metadata {
        private final long timeSpent;
        private final UUID uuid;

        public Metadata(long timeSpent, UUID uuid) {
            this.timeSpent = timeSpent;
            this.uuid = uuid;
        }
    }
}

