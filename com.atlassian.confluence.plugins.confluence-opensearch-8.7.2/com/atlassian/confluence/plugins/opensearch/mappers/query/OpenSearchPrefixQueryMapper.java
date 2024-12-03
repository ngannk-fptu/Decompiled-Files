/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.query.PrefixQuery
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.opensearch.client.util.ObjectBuilder
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.query.PrefixQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.util.ObjectBuilder;

public class OpenSearchPrefixQueryMapper
implements OpenSearchQueryMapper<PrefixQuery> {
    @Override
    public Query mapQueryToOpenSearch(PrefixQuery query) {
        return Query.of(q -> q.prefix(p -> (ObjectBuilder)p.field(query.getFieldName()).value(query.getPrefix()).boost(Float.valueOf(query.getBoost()))));
    }

    @Override
    public String getKey() {
        return "prefix";
    }
}

