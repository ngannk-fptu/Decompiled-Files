/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.Range
 *  com.atlassian.confluence.search.v2.query.LongRangeQuery
 *  org.opensearch.client.json.JsonData
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.opensearch.client.opensearch._types.query_dsl.RangeQuery$Builder
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.Range;
import com.atlassian.confluence.search.v2.query.LongRangeQuery;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;

public class OpenSearchLongRangeQueryMapper
implements OpenSearchQueryMapper<LongRangeQuery> {
    @Override
    public Query mapQueryToOpenSearch(LongRangeQuery query) {
        Range longRange = query.getRange();
        RangeQuery.Builder longRangeQueryBuilder = new RangeQuery.Builder();
        longRangeQueryBuilder.field(query.getFieldName()).boost(Float.valueOf(query.getBoost()));
        if (longRange.isIncludeFrom()) {
            longRangeQueryBuilder.gte(JsonData.of((Object)((Long)longRange.getFrom())));
        } else {
            longRangeQueryBuilder.gt(JsonData.of((Object)((Long)longRange.getFrom())));
        }
        if (longRange.isIncludeTo()) {
            longRangeQueryBuilder.lte(JsonData.of((Object)((Long)longRange.getTo())));
        } else {
            longRangeQueryBuilder.lt(JsonData.of((Object)((Long)longRange.getTo())));
        }
        return Query.of(r -> r.range(longRangeQueryBuilder.build()));
    }

    @Override
    public String getKey() {
        return "longRange";
    }
}

