/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermRangeQuery
 *  org.apache.lucene.util.BytesRef
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.search.v2.query.TermRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRef;

public class TermRangeQueryMapper
implements LuceneQueryMapper<TermRangeQuery> {
    @Override
    public Query convertToLuceneQuery(TermRangeQuery query) {
        return new org.apache.lucene.search.TermRangeQuery(query.getFieldName(), query.getLowerTerm() == null ? null : new BytesRef((CharSequence)query.getLowerTerm()), query.getUpperTerm() == null ? null : new BytesRef((CharSequence)query.getUpperTerm()), query.isIncludeLower(), query.isIncludeUpper());
    }
}

