/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.Range
 *  com.atlassian.confluence.search.v2.query.DoubleRangeQuery
 *  org.opensearch.client.json.JsonData
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.opensearch.client.opensearch._types.query_dsl.RangeQuery$Builder
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.Range;
import com.atlassian.confluence.search.v2.query.DoubleRangeQuery;
import java.util.function.Consumer;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;

public class OpenSearchDoubleRangeQueryMapper
implements OpenSearchQueryMapper<DoubleRangeQuery> {
    @Override
    public Query mapQueryToOpenSearch(DoubleRangeQuery query) {
        Consumer<JsonData> from;
        Range range = query.getRange();
        RangeQuery.Builder builder = new RangeQuery.Builder();
        builder.field(query.getFieldName());
        Consumer<JsonData> consumer = range.isIncludeFrom() ? arg_0 -> ((RangeQuery.Builder)builder).gte(arg_0) : (from = arg_0 -> ((RangeQuery.Builder)builder).gt(arg_0));
        Consumer<JsonData> to = range.isIncludeTo() ? arg_0 -> ((RangeQuery.Builder)builder).lte(arg_0) : arg_0 -> ((RangeQuery.Builder)builder).lt(arg_0);
        from.accept(JsonData.of((Object)((Double)range.getFrom())));
        to.accept(JsonData.of((Object)((Double)range.getTo())));
        return Query.of(q -> q.range(builder.build()));
    }

    @Override
    public String getKey() {
        return "doubleRange";
    }
}

