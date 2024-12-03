/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.BooleanOperator
 *  com.atlassian.confluence.search.v2.query.WildcardTextFieldQuery
 *  org.opensearch.client.opensearch._types.query_dsl.Operator
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.opensearch.client.util.ObjectBuilder
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.query.WildcardTextFieldQuery;
import org.opensearch.client.opensearch._types.query_dsl.Operator;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.util.ObjectBuilder;

public class OpenSearchWildcardTextFieldQueryMapper
implements OpenSearchQueryMapper<WildcardTextFieldQuery> {
    @Override
    public Query mapQueryToOpenSearch(WildcardTextFieldQuery query) {
        return Query.of(q -> q.queryString(qs -> (ObjectBuilder)qs.query(query.getRawQuery()).analyzeWildcard(Boolean.TRUE).allowLeadingWildcard(Boolean.TRUE).defaultOperator(OpenSearchWildcardTextFieldQueryMapper.mapOperator(query.getOperator())).fields(query.getFieldName(), new String[0]).boost(Float.valueOf(query.getBoost()))));
    }

    @Override
    public String getKey() {
        return "wildcardTextField";
    }

    private static Operator mapOperator(BooleanOperator op) {
        switch (op) {
            case AND: {
                return Operator.And;
            }
            case OR: {
                return Operator.Or;
            }
        }
        throw new IllegalArgumentException("Invalid operator " + op);
    }
}

