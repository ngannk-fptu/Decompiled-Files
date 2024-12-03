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
import com.atlassian.confluence.search.v2.query.DoubleRangeQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;

public class DoubleRangeQueryMapper
implements LuceneQueryMapper<DoubleRangeQuery> {
    @Override
    public Query convertToLuceneQuery(DoubleRangeQuery query) {
        Range<Double> range = query.getRange();
        return NumericRangeQuery.newDoubleRange((String)query.getFieldName(), (Double)range.getFrom(), (Double)range.getTo(), (boolean)range.isIncludeFrom(), (boolean)range.isIncludeTo());
    }
}

