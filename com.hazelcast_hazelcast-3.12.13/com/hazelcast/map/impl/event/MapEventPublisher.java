/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.event;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.EntryView;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;

public interface MapEventPublisher {
    public void publishWanUpdate(String var1, EntryView<Data, Data> var2, boolean var3);

    public void publishWanRemove(String var1, Data var2);

    public void publishMapEvent(Address var1, String var2, EntryEventType var3, int var4);

    public void publishEvent(Address var1, String var2, EntryEventType var3, Data var4, Object var5, Object var6);

    public void publishEvent(Address var1, String var2, EntryEventType var3, Data var4, Object var5, Object var6, Object var7);

    public void publishMapPartitionLostEvent(Address var1, String var2, int var3);

    public void hintMapEvent(Address var1, String var2, EntryEventType var3, int var4, int var5);

    public void addEventToQueryCache(Object var1);

    public boolean hasEventListener(String var1);
}

