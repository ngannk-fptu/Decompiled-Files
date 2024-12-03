/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.query.TermQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;

public class OpenSearchTermQueryMapper
implements OpenSearchQueryMapper<TermQuery> {
    @Override
    public Query mapQueryToOpenSearch(TermQuery query) {
        return Query.of(q -> q.term(t -> t.field(query.getFieldName()).value(v -> v.stringValue(query.getValue()))));
    }

    @Override
    public String getKey() {
        return "term";
    }
}

