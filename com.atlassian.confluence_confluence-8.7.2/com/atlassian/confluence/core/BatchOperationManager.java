/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.core;

import com.google.common.base.Function;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface BatchOperationManager {
    @Deprecated
    default public <I, O> Iterable<O> performAsBatch(Iterable<I> input, int batchSize, int expectedTotal, Function<I, O> task) {
        return this.applyInBatches(input, batchSize, expectedTotal, (java.util.function.Function<I, O>)task);
    }

    public <I, O> Iterable<O> applyInBatches(Iterable<I> var1, int var2, int var3, java.util.function.Function<I, O> var4);

    @Deprecated
    default public <I, O> Iterable<O> performAsBatch(Iterable<I> input, int expectedTotal, Function<I, O> task) {
        return this.applyInBatches(input, expectedTotal, (java.util.function.Function<I, O>)task);
    }

    public <I, O> Iterable<O> applyInBatches(Iterable<I> var1, int var2, java.util.function.Function<I, O> var3);

    @Deprecated
    default public <I, O> Iterable<O> performInChunks(Iterable<I> input, int chunkSize, int sizeToCollect, Function<List<I>, @NonNull List<O>> task) {
        return this.applyInChunks(input, chunkSize, sizeToCollect, (java.util.function.Function<List<I>, List<O>>)task);
    }

    public <I, O> Iterable<O> applyInChunks(Iterable<I> var1, int var2, int var3, java.util.function.Function<List<I>, @NonNull List<O>> var4);
}

