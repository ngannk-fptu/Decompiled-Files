/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer;

public interface ReadResultSet<E>
extends Iterable<E> {
    public static final int SEQUENCE_UNAVAILABLE = -1;

    public int readCount();

    public E get(int var1);

    public long getSequence(int var1);

    public int size();

    public long getNextSequenceToReadFrom();
}

