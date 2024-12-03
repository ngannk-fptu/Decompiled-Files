/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.query.SpanNearQuery
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.opensearch.client.opensearch._types.query_dsl.SpanNearQuery$Builder
 *  org.opensearch.client.opensearch._types.query_dsl.SpanQuery
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.query.SpanNearQuery;
import java.util.List;
import java.util.stream.Collectors;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.SpanNearQuery;
import org.opensearch.client.opensearch._types.query_dsl.SpanQuery;

public class OpenSearchSpanNearQueryMapper
implements OpenSearchQueryMapper<SpanNearQuery> {
    @Override
    public Query mapQueryToOpenSearch(SpanNearQuery query) {
        return Query.of(q -> q.spanNear(s -> ((SpanNearQuery.Builder)s.clauses(this.getClauses(query)).slop(Integer.valueOf(query.getSlop())).boost(Float.valueOf(query.getBoost()))).inOrder(Boolean.valueOf(query.isInOrder()))));
    }

    private List<SpanQuery> getClauses(SpanNearQuery query) {
        return query.getFieldValues().stream().map(value -> SpanQuery.of(s -> s.spanTerm(t -> t.field(query.getFieldName()).value(value)))).collect(Collectors.toList());
    }

    @Override
    public String getKey() {
        return "spanNear";
    }
}

