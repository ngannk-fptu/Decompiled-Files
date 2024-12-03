/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.permission;

import java.util.Objects;

public class AdministeredGroupsQuery {
    public static AdministeredGroupsQuery ALL_RESULTS_QUERY = AdministeredGroupsQuery.allResults(null, null);
    private final Long directoryId;
    private final String search;
    private final int start;
    private final int limit;

    public AdministeredGroupsQuery(Long directoryId, String search, int start, int limit) {
        this.directoryId = directoryId;
        this.search = search;
        this.start = start;
        this.limit = limit;
    }

    public static AdministeredGroupsQuery allResults(Long directoryId, String search) {
        return new AdministeredGroupsQuery(directoryId, search, 0, -1);
    }

    public Long getDirectoryId() {
        return this.directoryId;
    }

    public String getSearch() {
        return this.search;
    }

    public int getStart() {
        return this.start;
    }

    public int getLimit() {
        return this.limit;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AdministeredGroupsQuery that = (AdministeredGroupsQuery)o;
        return this.start == that.start && this.limit == that.limit && Objects.equals(this.directoryId, that.directoryId) && Objects.equals(this.search, that.search);
    }

    public int hashCode() {
        return Objects.hash(this.directoryId, this.search, this.start, this.limit);
    }
}

