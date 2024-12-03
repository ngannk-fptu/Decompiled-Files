/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ReIndexOption;
import com.atlassian.confluence.search.ReIndexTask;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.EnumSet;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@LuceneIndependent
@Internal
public interface FullReindexManager {
    public ReIndexTask reIndex();

    public ReIndexTask reIndex(EnumSet<ReIndexOption> var1);

    public ReIndexTask reIndex(EnumSet<ReIndexOption> var1, SearchQuery var2);

    public @Nullable ReIndexTask getLastReindexingTask();

    public boolean isReIndexing();

    public void unIndexAll();

    public ReIndexTask reIndex(EnumSet<ReIndexOption> var1, @NonNull List<String> var2);
}

