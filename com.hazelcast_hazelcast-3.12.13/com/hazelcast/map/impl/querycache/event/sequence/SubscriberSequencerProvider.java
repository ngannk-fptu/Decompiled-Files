/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.event.sequence;

public interface SubscriberSequencerProvider {
    public boolean compareAndSetSequence(long var1, long var3, int var5);

    public long getSequence(int var1);

    public void reset(int var1);

    public void resetAll();
}

