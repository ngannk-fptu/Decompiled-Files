/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BatchResult<T> {
    private final List<T> successfulEntities;
    private final List<T> failedEntities;

    public static <T, V, K extends BatchResult<V>> K transform(BatchResult<T> batchResult, Function<T, V> transformer, Supplier<K> supplier) {
        BatchResult transformedBatchResult = (BatchResult)supplier.get();
        batchResult.getSuccessfulEntities().forEach(entity -> transformedBatchResult.addSuccess(transformer.apply(entity)));
        batchResult.getFailedEntities().forEach(entity -> transformedBatchResult.addFailure(transformer.apply(entity)));
        return (K)transformedBatchResult;
    }

    public static <T, V> BatchResult<V> transform(BatchResult<T> batchResult, Function<T, V> transformer) {
        return BatchResult.transform(batchResult, transformer, () -> new BatchResult(batchResult.getTotalAttempted()));
    }

    public BatchResult(int totalEntities) {
        this.successfulEntities = new ArrayList<T>(totalEntities);
        this.failedEntities = new ArrayList<T>();
    }

    public void addSuccess(T entity) {
        this.successfulEntities.add(entity);
    }

    public void addSuccesses(Collection<? extends T> entities) {
        this.successfulEntities.addAll(entities);
    }

    public void addFailure(T entity) {
        this.failedEntities.add(entity);
    }

    public void addFailures(Collection<? extends T> entities) {
        this.failedEntities.addAll(entities);
    }

    public boolean hasFailures() {
        return !this.failedEntities.isEmpty();
    }

    public boolean hasSuccesses() {
        return !this.successfulEntities.isEmpty();
    }

    public int getTotalAttempted() {
        return this.successfulEntities.size() + this.failedEntities.size();
    }

    public List<T> getSuccessfulEntities() {
        return this.successfulEntities;
    }

    public List<T> getFailedEntities() {
        return this.failedEntities;
    }

    public int getTotalSuccessful() {
        return this.successfulEntities.size();
    }
}

