/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

public interface PartitionSpecificRunnable
extends Runnable {
    public int getPartitionId();
}

