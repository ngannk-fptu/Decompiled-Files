/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.QueueStore;
import java.util.Properties;

public interface QueueStoreFactory<T> {
    public QueueStore<T> newQueueStore(String var1, Properties var2);
}

