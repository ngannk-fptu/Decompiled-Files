/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.concurrent;

import com.hazelcast.util.function.Predicate;
import java.util.Collection;

public interface Pipe<E> {
    public long addedCount();

    public long removedCount();

    public int capacity();

    public int remainingCapacity();

    public int drain(Predicate<? super E> var1);

    public int drainTo(Collection<? super E> var1, int var2);
}

