/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.event.sequence;

public interface PartitionSequencer {
    public long nextSequence();

    public void setSequence(long var1);

    public boolean compareAndSetSequence(long var1, long var3);

    public long getSequence();

    public void reset();
}

