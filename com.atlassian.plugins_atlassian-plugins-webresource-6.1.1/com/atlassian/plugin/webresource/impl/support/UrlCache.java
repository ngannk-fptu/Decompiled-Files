/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.collect.ImmutableSortedSet
 */
package com.atlassian.plugin.webresource.impl.support;

import com.atlassian.plugin.webresource.impl.CachedCondition;
import com.atlassian.plugin.webresource.impl.helpers.url.CalculatedBatches;
import com.atlassian.plugin.webresource.impl.support.Tuple;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSortedSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface UrlCache {
    public CalculatedBatches getBatches(IncludedExcludedConditionsAndBatchingOptions var1, BatchesProvider var2);

    public Set<String> getResolvedExcluded(IncludedExcludedConditionsAndBatchingOptions var1, ResolvedExcludedProvider var2);

    public void clear();

    public static class PassThrough
    implements UrlCache {
        @Override
        public CalculatedBatches getBatches(IncludedExcludedConditionsAndBatchingOptions key, BatchesProvider provider) {
            return provider.get(key);
        }

        @Override
        public Set<String> getResolvedExcluded(IncludedExcludedConditionsAndBatchingOptions key, ResolvedExcludedProvider provider) {
            return provider.get(key);
        }

        @Override
        public void clear() {
        }
    }

    public static class Impl
    implements UrlCache {
        private Cache<IncludedAndExcluded, List<CachedCondition>> cachedConditions;
        private Cache<IncludedExcludedConditionsAndBatchingOptions, CalculatedBatches> cachedBatches;
        private Cache<IncludedExcludedConditionsAndBatchingOptions, Set<String>> cachedResolvedExcluded;

        public Impl(long size) {
            this.cachedConditions = CacheBuilder.newBuilder().maximumSize(size).build();
            this.cachedBatches = CacheBuilder.newBuilder().maximumSize(size).build();
            this.cachedResolvedExcluded = CacheBuilder.newBuilder().maximumSize(size).build();
        }

        @Override
        public CalculatedBatches getBatches(IncludedExcludedConditionsAndBatchingOptions key, BatchesProvider provider) {
            try {
                return (CalculatedBatches)this.cachedBatches.get((Object)key, () -> provider.get(key));
            }
            catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Set<String> getResolvedExcluded(IncludedExcludedConditionsAndBatchingOptions key, ResolvedExcludedProvider provider) {
            try {
                return (Set)this.cachedResolvedExcluded.get((Object)key, () -> provider.get(key));
            }
            catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void clear() {
            this.cachedConditions.invalidateAll();
            this.cachedBatches.invalidateAll();
            this.cachedResolvedExcluded.invalidateAll();
        }
    }

    public static class IncludedExcludedConditionsAndBatchingOptions {
        private final IncludedAndExcluded inludedAndExcluded;
        private final Set<EvaluatedCondition> evaluatedConditions;
        private final boolean resplitMergedContextBatchesForThisRequest;

        public IncludedExcludedConditionsAndBatchingOptions(IncludedAndExcluded includedAndExcluded, Set<EvaluatedCondition> evaluatedConditions, boolean resplitMergedContextBatchesForThisRequest) {
            this.inludedAndExcluded = (IncludedAndExcluded)Preconditions.checkNotNull((Object)includedAndExcluded);
            this.evaluatedConditions = (Set)Preconditions.checkNotNull(evaluatedConditions);
            this.resplitMergedContextBatchesForThisRequest = resplitMergedContextBatchesForThisRequest;
        }

        public LinkedHashSet<String> getIncluded() {
            return this.inludedAndExcluded.getIncluded();
        }

        public Set<String> getExcluded() {
            return this.inludedAndExcluded.getExcluded();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof IncludedExcludedConditionsAndBatchingOptions)) {
                return false;
            }
            IncludedExcludedConditionsAndBatchingOptions that = (IncludedExcludedConditionsAndBatchingOptions)o;
            if (this.resplitMergedContextBatchesForThisRequest != that.resplitMergedContextBatchesForThisRequest) {
                return false;
            }
            if (!this.evaluatedConditions.equals(that.evaluatedConditions)) {
                return false;
            }
            return this.inludedAndExcluded.equals(that.inludedAndExcluded);
        }

        public int hashCode() {
            int result = this.inludedAndExcluded.hashCode();
            result = 31 * result + this.evaluatedConditions.hashCode();
            result = 31 * result + (this.resplitMergedContextBatchesForThisRequest ? 1 : 0);
            return result;
        }
    }

    public static class EvaluatedCondition
    extends Tuple<CachedCondition, Boolean> {
        public EvaluatedCondition(CachedCondition cachedCondition, Boolean evaluationResult) {
            super(cachedCondition, evaluationResult);
        }
    }

    public static class IncludedAndExcluded
    extends Tuple<LinkedHashSet<String>, Set<String>> {
        public IncludedAndExcluded(LinkedHashSet<String> included, Set<String> excluded) {
            super(included, ImmutableSortedSet.copyOf(excluded));
        }

        public LinkedHashSet<String> getIncluded() {
            return (LinkedHashSet)this.getFirst();
        }

        public Set<String> getExcluded() {
            return (Set)this.getLast();
        }
    }

    public static interface ResolvedExcludedProvider {
        public Set<String> get(IncludedExcludedConditionsAndBatchingOptions var1);
    }

    public static interface BatchesProvider {
        public CalculatedBatches get(IncludedExcludedConditionsAndBatchingOptions var1);
    }
}

