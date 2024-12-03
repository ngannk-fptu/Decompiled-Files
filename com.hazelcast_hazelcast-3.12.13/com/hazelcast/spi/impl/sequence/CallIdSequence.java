/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.sequence;

public interface CallIdSequence {
    public int getMaxConcurrentInvocations();

    public long next();

    public long forceNext();

    public void complete();

    public long getLastCallId();

    public long concurrentInvocations();
}

