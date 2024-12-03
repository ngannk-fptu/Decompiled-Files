/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.NumericRangeQuery
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.search.v2.Range;
import com.atlassian.confluence.search.v2.query.IntegerRangeQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;

public class IntegerRangeQueryMapper
implements LuceneQueryMapper<IntegerRangeQuery> {
    @Override
    public Query convertToLuceneQuery(IntegerRangeQuery searchQuery) {
        Range<Integer> range = searchQuery.getRange();
        return NumericRangeQuery.newIntRange((String)searchQuery.getFieldName(), (Integer)range.getFrom(), (Integer)range.getTo(), (boolean)range.isIncludeFrom(), (boolean)range.isIncludeTo());
    }
}

