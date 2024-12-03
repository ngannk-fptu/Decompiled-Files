/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.index;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.List;

@ExperimentalApi
public class ReIndexSpec {
    private final SearchQuery deleteQuery;
    private final int concurrencyLevel;
    private final List<HibernateHandle> handles;
    private final String name;
    private final boolean shouldOptimize;

    public ReIndexSpec(SearchQuery deleteQuery, int concurrencyLevel, List<HibernateHandle> handles, String name) {
        this(deleteQuery, concurrencyLevel, handles, name, true);
    }

    public ReIndexSpec(SearchQuery deleteQuery, int concurrencyLevel, List<HibernateHandle> handles, String name, boolean shouldOptimize) {
        this.deleteQuery = deleteQuery;
        this.concurrencyLevel = concurrencyLevel;
        this.handles = handles;
        this.name = name;
        this.shouldOptimize = shouldOptimize;
    }

    public SearchQuery getDeleteQuery() {
        return this.deleteQuery;
    }

    public int getConcurrencyLevel() {
        return this.concurrencyLevel;
    }

    public List<HibernateHandle> getHandles() {
        return this.handles;
    }

    public String getName() {
        return this.name;
    }

    public boolean shouldOptimize() {
        return this.shouldOptimize;
    }
}

