/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pocketknife.api.querydsl.stream;

import com.atlassian.pocketknife.api.querydsl.stream.ClosePromise;
import com.atlassian.pocketknife.api.querydsl.stream.CloseableIterable;
import com.atlassian.pocketknife.internal.querydsl.stream.CloseableIterableImpl;
import com.atlassian.pocketknife.internal.querydsl.stream.PartitionedCloseableIterable;
import com.atlassian.pocketknife.internal.querydsl.util.fp.Fp;
import com.mysema.commons.lang.CloseableIterator;
import java.util.List;

public class CloseableIterables {
    public static <T> CloseableIterable<T> iterable(CloseableIterator<T> closeableIterator, ClosePromise closePromise) {
        return new CloseableIterableImpl(closeableIterator, Fp.identity(), closePromise);
    }

    public static <T> CloseableIterable<T> iterable(CloseableIterator<T> closeableIterator) {
        return new CloseableIterableImpl(closeableIterator, Fp.identity(), ClosePromise.NOOP());
    }

    public static <T> CloseableIterable<T> partitioned(List<CloseableIterable<T>> closeableIterables) {
        return new PartitionedCloseableIterable<T>(closeableIterables);
    }
}

