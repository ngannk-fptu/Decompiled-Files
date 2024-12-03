/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.FieldValueFilter
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.search.FilteredQuery
 *  org.apache.lucene.search.MatchAllDocsQuery
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.search.v2.query.FieldExistsQuery;
import org.apache.lucene.search.FieldValueFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

public class FieldExistsQueryMapper
implements LuceneQueryMapper<FieldExistsQuery> {
    @Override
    public Query convertToLuceneQuery(FieldExistsQuery query) {
        return new FilteredQuery((Query)new MatchAllDocsQuery(), (Filter)new FieldValueFilter(query.getFieldName(), query.isNegate()));
    }
}

