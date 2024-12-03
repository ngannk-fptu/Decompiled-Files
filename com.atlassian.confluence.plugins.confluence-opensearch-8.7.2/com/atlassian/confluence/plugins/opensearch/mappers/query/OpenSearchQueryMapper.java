/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.search.v2.SearchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;

public interface OpenSearchQueryMapper<T extends SearchQuery> {
    public Query mapQueryToOpenSearch(T var1);

    public String getKey();
}

