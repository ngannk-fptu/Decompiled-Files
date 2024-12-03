/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.accumulator;

import com.hazelcast.map.impl.querycache.accumulator.AccumulatorHandler;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public interface Accumulator<E extends Sequenced>
extends Iterable<E> {
    public void accumulate(E var1);

    public int poll(AccumulatorHandler<E> var1, int var2);

    public int poll(AccumulatorHandler<E> var1, long var2, TimeUnit var4);

    @Override
    public Iterator<E> iterator();

    public AccumulatorInfo getInfo();

    public boolean setHead(long var1);

    public int size();

    public boolean isEmpty();

    public void reset();
}

