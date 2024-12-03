/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.event;

import com.hazelcast.cache.impl.event.CachePartitionLostEvent;
import java.util.EventListener;

public interface CachePartitionLostListener
extends EventListener {
    public void partitionLost(CachePartitionLostEvent var1);
}

