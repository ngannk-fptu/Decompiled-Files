/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.event;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.nio.serialization.Data;

public interface CacheWanEventPublisher {
    public void publishWanUpdate(String var1, CacheEntryView<Data, Data> var2);

    public void publishWanRemove(String var1, Data var2);
}

