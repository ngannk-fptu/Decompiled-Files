/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.BooleanOperator
 *  com.atlassian.confluence.search.v2.query.QueryStringQuery
 *  org.opensearch.client.opensearch._types.query_dsl.Operator
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.opensearch.client.util.ObjectBuilder
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.query.QueryStringQuery;
import java.util.List;
import java.util.stream.Collectors;
import org.opensearch.client.opensearch._types.query_dsl.Operator;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.util.ObjectBuilder;

public class OpenSearchQueryStringQueryMapper
implements OpenSearchQueryMapper<QueryStringQuery> {
    @Override
    public Query mapQueryToOpenSearch(QueryStringQuery query) {
        return Query.of(q -> q.queryString(qs -> (ObjectBuilder)qs.query(query.getQuery()).defaultOperator(this.mapOperator(query.getOperator())).fields(this.getBoostedFields(query)).boost(Float.valueOf(query.getBoost()))));
    }

    private Operator mapOperator(BooleanOperator operator) {
        switch (operator) {
            case AND: {
                return Operator.And;
            }
            case OR: {
                return Operator.Or;
            }
        }
        throw new IllegalArgumentException("Invalid operator " + operator);
    }

    private List<String> getBoostedFields(QueryStringQuery query) {
        return query.getFieldNames().stream().map(f -> {
            Float boost = (Float)query.getFieldsBoost().get(f);
            return boost == null ? f : String.format("%s^%s", f, boost);
        }).collect(Collectors.toList());
    }

    @Override
    public String getKey() {
        return "queryString";
    }
}

