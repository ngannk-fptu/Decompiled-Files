/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.query.AllQuery
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.query.AllQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;

public class OpenSearchAllQueryMapper
implements OpenSearchQueryMapper<AllQuery> {
    @Override
    public Query mapQueryToOpenSearch(AllQuery query) {
        return Query.of(q -> q.matchAll(a -> a));
    }

    @Override
    public String getKey() {
        return "all";
    }
}

