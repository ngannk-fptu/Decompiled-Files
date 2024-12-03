/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IFunction;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.ReadResultSet;
import java.util.Collection;

public interface Ringbuffer<E>
extends DistributedObject {
    public long capacity();

    public long size();

    public long tailSequence();

    public long headSequence();

    public long remainingCapacity();

    public long add(E var1);

    public ICompletableFuture<Long> addAsync(E var1, OverflowPolicy var2);

    public E readOne(long var1) throws InterruptedException;

    public ICompletableFuture<Long> addAllAsync(Collection<? extends E> var1, OverflowPolicy var2);

    public ICompletableFuture<ReadResultSet<E>> readManyAsync(long var1, int var3, int var4, IFunction<E, Boolean> var5);
}

