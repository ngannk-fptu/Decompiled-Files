/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.ConstantScoreQuery
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.search.v2.SearchQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Query;

public abstract class BaseConstantScoreQueryMapper<T extends SearchQuery>
implements LuceneQueryMapper<T> {
    protected abstract Query internalConvertToLuceneQuery(T var1);

    @Override
    public Query convertToLuceneQuery(T searchQuery) {
        Query toWrap = this.internalConvertToLuceneQuery(searchQuery);
        return toWrap == null ? null : new ConstantScoreQuery(toWrap);
    }
}

