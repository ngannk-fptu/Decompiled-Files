/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.accumulator;

import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;

public interface CyclicBuffer<E extends Sequenced> {
    public void add(E var1);

    public E getAndAdvance();

    public E get(long var1);

    public boolean setHead(long var1);

    public long getHeadSequence();

    public void reset();

    public int size();
}

