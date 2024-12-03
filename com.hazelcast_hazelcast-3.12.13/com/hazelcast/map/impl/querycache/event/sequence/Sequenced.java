/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.event.sequence;

public interface Sequenced {
    public long getSequence();

    public int getPartitionId();

    public void setSequence(long var1);
}

