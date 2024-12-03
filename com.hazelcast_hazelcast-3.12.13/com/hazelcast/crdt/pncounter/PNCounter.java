/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt.pncounter;

import com.hazelcast.core.DistributedObject;

public interface PNCounter
extends DistributedObject {
    public long get();

    public long getAndAdd(long var1);

    public long addAndGet(long var1);

    public long getAndSubtract(long var1);

    public long subtractAndGet(long var1);

    public long decrementAndGet();

    public long incrementAndGet();

    public long getAndDecrement();

    public long getAndIncrement();

    public void reset();
}

