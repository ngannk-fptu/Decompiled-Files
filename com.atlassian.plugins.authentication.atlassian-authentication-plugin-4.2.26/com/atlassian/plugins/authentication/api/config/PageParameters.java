/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.plugins.authentication.api.config;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public class PageParameters {
    public static final PageParameters ALL_RESULTS = new PageParameters(0, -1);
    private final int start;
    private final int limit;

    public boolean isAllResultsQuery() {
        return this.limit == -1;
    }

    public PageParameters(int start, int limit) {
        this.start = start;
        this.limit = limit;
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
        PageParameters that = (PageParameters)o;
        return Objects.equals(this.getStart(), that.getStart()) && Objects.equals(this.getLimit(), that.getLimit());
    }

    public int hashCode() {
        return Objects.hash(this.getStart(), this.getLimit());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("start", this.getStart()).add("limit", this.getLimit()).toString();
    }
}

