/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.confluence.plugins.opensearch.OpenSearchQueryMapperNotFoundException;
import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.opensearch.client.opensearch._types.query_dsl.Query;

public class DelegatingQueryMapper {
    private final Map<String, OpenSearchQueryMapper<?>> queryMappersByKey;

    public DelegatingQueryMapper(List<OpenSearchQueryMapper<?>> queryMappers) {
        this.queryMappersByKey = Objects.requireNonNull(queryMappers, "queryMappers is required").stream().collect(Collectors.toMap(OpenSearchQueryMapper::getKey, Function.identity()));
    }

    public Query mapQueryToOpenSearch(SearchQuery query) {
        return this.getQueryMapperForKey(query.getKey()).mapQueryToOpenSearch(query);
    }

    private <T extends SearchQuery> OpenSearchQueryMapper<T> getQueryMapperForKey(String key) {
        OpenSearchQueryMapper<?> mapper = this.queryMappersByKey.get(key);
        if (mapper == null) {
            throw new OpenSearchQueryMapperNotFoundException(key);
        }
        return mapper;
    }
}

