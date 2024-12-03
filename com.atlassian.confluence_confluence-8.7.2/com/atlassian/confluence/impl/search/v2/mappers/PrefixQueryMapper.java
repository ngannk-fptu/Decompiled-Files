/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.PrefixQuery
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;

public class PrefixQueryMapper
implements LuceneQueryMapper<com.atlassian.confluence.search.v2.query.PrefixQuery> {
    @Override
    public Query convertToLuceneQuery(com.atlassian.confluence.search.v2.query.PrefixQuery query) {
        return new PrefixQuery(new Term(query.getFieldName(), query.getPrefix()));
    }
}

