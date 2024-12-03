/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.search.v2.ScoringStrategy;
import com.atlassian.confluence.search.v2.SearchIndexAccessException;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface CustomSearchIndexRegistry {
    @Deprecated
    public SearchIndexAccessor add(String var1, String var2, ScoringStrategy var3, @Nullable AnalyzerDescriptorProvider var4) throws SearchIndexAccessException;

    @Deprecated
    default public SearchIndexAccessor add(String name, ScoringStrategy scoringStrategy, @Nullable AnalyzerDescriptorProvider analyzerDescriptorProvider) throws SearchIndexAccessException {
        return this.add(name, name, scoringStrategy, analyzerDescriptorProvider);
    }

    default public SearchIndexAccessor add(String name, @Nullable AnalyzerDescriptorProvider analyzerDescriptorProvider) throws SearchIndexAccessException {
        return this.add(name, name, ScoringStrategy.DEFAULT, analyzerDescriptorProvider);
    }

    public SearchIndexAccessor get(String var1) throws SearchIndexAccessException;

    public void remove(String var1) throws SearchIndexAccessException;
}

