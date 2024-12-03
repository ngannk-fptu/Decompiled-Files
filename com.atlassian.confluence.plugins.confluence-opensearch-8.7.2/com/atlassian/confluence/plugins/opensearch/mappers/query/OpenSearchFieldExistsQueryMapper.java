/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.query.FieldExistsQuery
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.query.FieldExistsQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;

public class OpenSearchFieldExistsQueryMapper
implements OpenSearchQueryMapper<FieldExistsQuery> {
    @Override
    public Query mapQueryToOpenSearch(FieldExistsQuery query) {
        Query existsQuery = Query.of(q -> q.exists(e -> e.field(query.getFieldName())));
        if (query.isNegate()) {
            return Query.of(q -> q.bool(b -> b.mustNot(existsQuery, new Query[0])));
        }
        return existsQuery;
    }

    @Override
    public String getKey() {
        return "fieldExists";
    }
}

