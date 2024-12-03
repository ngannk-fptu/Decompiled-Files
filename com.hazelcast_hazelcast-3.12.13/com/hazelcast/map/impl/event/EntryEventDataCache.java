/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.event;

import com.hazelcast.map.impl.event.EntryEventData;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import java.util.Collection;

public interface EntryEventDataCache {
    public EntryEventData getOrCreateEventData(String var1, Address var2, Data var3, Object var4, Object var5, Object var6, int var7, boolean var8);

    public boolean isEmpty();

    public Collection<EntryEventData> eventDataIncludingValues();

    public Collection<EntryEventData> eventDataExcludingValues();
}

