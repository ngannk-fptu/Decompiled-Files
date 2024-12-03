/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import org.apache.lucene.search.Query;

public abstract class WrappingQuery
extends Query {
    protected final Query wrappedQuery;

    protected WrappingQuery(Query wrappedQuery) {
        this.wrappedQuery = wrappedQuery;
    }

    public Query getWrappedQuery() {
        return this.wrappedQuery;
    }
}

