/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.query.MatchNoDocsQuery
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;

public class OpenSearchMatchNoDocsQueryMapper
implements OpenSearchQueryMapper<MatchNoDocsQuery> {
    @Override
    public Query mapQueryToOpenSearch(MatchNoDocsQuery query) {
        return Query.of(q -> q.matchNone(v -> v));
    }

    @Override
    public String getKey() {
        return "none";
    }
}

