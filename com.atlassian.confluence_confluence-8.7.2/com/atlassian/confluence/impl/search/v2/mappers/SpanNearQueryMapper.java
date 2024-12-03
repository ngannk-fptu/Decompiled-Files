/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.spans.SpanNearQuery
 *  org.apache.lucene.search.spans.SpanQuery
 *  org.apache.lucene.search.spans.SpanTermQuery
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

public class SpanNearQueryMapper
implements LuceneQueryMapper<com.atlassian.confluence.search.v2.query.SpanNearQuery> {
    @Override
    public Query convertToLuceneQuery(com.atlassian.confluence.search.v2.query.SpanNearQuery query) {
        List<SpanQuery> clauses = query.getFieldValues().stream().map(x -> new SpanTermQuery(new Term(query.getFieldName(), x))).collect(Collectors.toList());
        SpanNearQuery luceneQuery = new SpanNearQuery(clauses.toArray(new SpanQuery[clauses.size()]), query.getSlop(), query.isInOrder());
        luceneQuery.setBoost(query.getBoost());
        return luceneQuery;
    }
}

