/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.ConstantScoreQuery
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchMapper;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Query;

public class ConstantScoreQueryMapper
implements LuceneQueryMapper<com.atlassian.confluence.search.v2.query.ConstantScoreQuery> {
    private LuceneSearchMapper searchMapper;

    @Override
    public Query convertToLuceneQuery(com.atlassian.confluence.search.v2.query.ConstantScoreQuery searchQuery) {
        Query luceneQuery = this.searchMapper.convertToLuceneQuery(searchQuery.getWrappedQuery());
        ConstantScoreQuery ret = new ConstantScoreQuery(luceneQuery);
        ret.setBoost(searchQuery.getBoost());
        return ret;
    }

    public void setSearchMapper(LuceneSearchMapper searchMapper) {
        this.searchMapper = searchMapper;
    }
}

