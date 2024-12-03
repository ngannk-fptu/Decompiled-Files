/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchSort
 *  javax.annotation.Nullable
 *  org.opensearch.client.opensearch._types.SortOptions
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.confluence.plugins.opensearch.OpenSearchSortMapperNotFoundException;
import com.atlassian.confluence.plugins.opensearch.mappers.sort.OpenSearchSortMapper;
import com.atlassian.confluence.search.v2.SearchSort;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.opensearch.client.opensearch._types.SortOptions;

public class DelegatingSortMapper {
    private final Map<String, OpenSearchSortMapper<?>> sortMappersByKey;

    public DelegatingSortMapper(List<OpenSearchSortMapper<?>> sortMappers) {
        this.sortMappersByKey = Objects.requireNonNull(sortMappers, "sortMappers is required").stream().collect(Collectors.toMap(OpenSearchSortMapper::getKey, Function.identity()));
    }

    public List<SortOptions> mapSortToOpenSearch(@Nullable SearchSort sort) {
        if (sort == null) {
            return Collections.emptyList();
        }
        return this.getSortMapperForKey(sort.getKey()).mapSortToOpenSearch(sort);
    }

    private <T extends SearchSort> OpenSearchSortMapper<T> getSortMapperForKey(String key) {
        OpenSearchSortMapper<?> mapper = this.sortMappersByKey.get(key);
        if (mapper == null) {
            throw new OpenSearchSortMapperNotFoundException(key);
        }
        return mapper;
    }
}

