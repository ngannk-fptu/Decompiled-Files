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
import com.atlassian.confluence.search.v2.query.LongRangeQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;

public class LongRangeQueryMapper
implements LuceneQueryMapper<LongRangeQuery> {
    @Override
    public Query convertToLuceneQuery(LongRangeQuery query) {
        Range<Long> range = query.getRange();
        return NumericRangeQuery.newLongRange((String)query.getFieldName(), (Long)range.getFrom(), (Long)range.getTo(), (boolean)range.isIncludeFrom(), (boolean)range.isIncludeTo());
    }
}

