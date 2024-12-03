/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermQuery
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class TermQueryMapper
implements LuceneQueryMapper<com.atlassian.confluence.search.v2.query.TermQuery> {
    @Override
    public Query convertToLuceneQuery(com.atlassian.confluence.search.v2.query.TermQuery query) {
        return new TermQuery(new Term(query.getFieldName(), query.getValue()));
    }
}

