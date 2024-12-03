/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl;

public interface Ringbuffer<E>
extends Iterable<E> {
    public long getCapacity();

    public long size();

    public long tailSequence();

    public long peekNextTailSequence();

    public void setTailSequence(long var1);

    public long headSequence();

    public void setHeadSequence(long var1);

    public boolean isEmpty();

    public long add(E var1);

    public E read(long var1);

    public void checkBlockableReadSequence(long var1);

    public void checkReadSequence(long var1);

    public void set(long var1, E var3);

    public void clear();

    public E[] getItems();
}

