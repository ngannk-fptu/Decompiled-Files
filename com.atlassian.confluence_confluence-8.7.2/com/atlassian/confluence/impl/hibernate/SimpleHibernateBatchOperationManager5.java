/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.HibernateSessionManager5
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.hibernate;

import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.impl.hibernate.HibernateSessionManager5;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SimpleHibernateBatchOperationManager5
implements BatchOperationManager {
    private static final int ARBITRARY_BATCH_SIZE = 50;
    private HibernateSessionManager5 sessionManager;

    public SimpleHibernateBatchOperationManager5(HibernateSessionManager5 sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    @Deprecated
    public <I, O> Iterable<O> performAsBatch(Iterable<I> input, int batchSize, int expectedTotal, Function<I, O> task) {
        return this.applyInBatches(input, batchSize, expectedTotal, (java.util.function.Function<I, O>)task);
    }

    @Override
    public <I, O> Iterable<O> applyInBatches(Iterable<I> input, int batchSize, int expectedTotal, java.util.function.Function<I, O> task) {
        return this.sessionManager.executeThenClearSession(input, batchSize, expectedTotal, task);
    }

    @Override
    @Deprecated
    public <I, O> Iterable<O> performAsBatch(Iterable<I> input, int expectedTotal, Function<I, O> task) {
        return this.applyInBatches(input, expectedTotal, (java.util.function.Function<I, O>)task);
    }

    @Override
    public <I, O> Iterable<O> applyInBatches(Iterable<I> input, int expectedTotal, java.util.function.Function<I, O> task) {
        return this.applyInBatches(input, 50, expectedTotal, task);
    }

    @Override
    @Deprecated
    public <I, O> Iterable<O> performInChunks(Iterable<I> input, int chunkSize, int sizeToCollect, Function<List<I>, @NonNull List<O>> task) {
        return this.applyInChunks(input, chunkSize, sizeToCollect, (java.util.function.Function<List<I>, List<O>>)task);
    }

    @Override
    public <I, O> Iterable<O> applyInChunks(Iterable<I> input, int chunkSize, int sizeToCollect, java.util.function.Function<List<I>, @NonNull List<O>> task) {
        Iterable parts = Iterables.partition(input, (int)chunkSize);
        ArrayList output = Lists.newArrayListWithCapacity((int)sizeToCollect);
        for (List part : parts) {
            List<O> chunkProcessed = task.apply(part);
            Iterables.addAll((Collection)output, (Iterable)chunkProcessed.stream().filter(Objects::nonNull).collect(Collectors.toList()));
            if (output.size() < sizeToCollect) continue;
            break;
        }
        return output;
    }
}

