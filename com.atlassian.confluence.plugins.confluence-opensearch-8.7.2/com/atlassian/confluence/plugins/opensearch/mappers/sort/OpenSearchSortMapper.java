/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchSort
 *  org.opensearch.client.opensearch._types.SortOptions
 */
package com.atlassian.confluence.plugins.opensearch.mappers.sort;

import com.atlassian.confluence.search.v2.SearchSort;
import java.util.List;
import org.opensearch.client.opensearch._types.SortOptions;

public interface OpenSearchSortMapper<T extends SearchSort> {
    public String getKey();

    public List<SortOptions> mapSortToOpenSearch(T var1);
}

