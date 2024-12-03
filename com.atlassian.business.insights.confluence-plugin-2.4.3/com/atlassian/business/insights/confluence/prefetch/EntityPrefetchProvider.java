/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.business.insights.api.LogRecord
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.business.insights.confluence.prefetch;

import com.atlassian.bonnie.Searchable;
import com.atlassian.business.insights.api.LogRecord;
import com.atlassian.business.insights.confluence.prefetch.DocIdsHolder;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EntityPrefetchProvider {
    @Nonnull
    public Stream<LogRecord> prefetchAndConvert(int var1, @Nonnull SearchQuery var2, @Nonnull Function<Searchable, LogRecord> var3);

    @Nonnull
    public DocIdsHolder prefetchDocIds(@Nonnull SearchQuery var1);

    @Nonnull
    public ContentSearch contentSearch(@Nonnull SearchQuery var1, @Nullable SearchSort var2, int var3, int var4);
}

