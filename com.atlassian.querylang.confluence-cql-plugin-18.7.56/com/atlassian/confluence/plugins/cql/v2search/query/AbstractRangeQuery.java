/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.Range
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.cql.v2search.query;

import com.atlassian.confluence.search.v2.Range;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

public class AbstractRangeQuery<T>
implements SearchQuery {
    private final Range<T> range;
    private final String key;

    public AbstractRangeQuery(String key, @Nonnull T from, @Nonnull T to, boolean includeFrom, boolean includeTo) {
        this.range = new Range(from, to, includeFrom, includeTo);
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public List getParameters() {
        return Arrays.asList(this.range.getFrom(), this.range.getTo(), this.range.isIncludeFrom(), this.range.isIncludeTo());
    }

    public T getFrom() {
        return (T)this.range.getFrom();
    }

    public T getTo() {
        return (T)this.range.getTo();
    }

    public boolean isIncludeFrom() {
        return this.range.isIncludeFrom();
    }

    public boolean isIncludeTo() {
        return this.range.isIncludeTo();
    }
}

