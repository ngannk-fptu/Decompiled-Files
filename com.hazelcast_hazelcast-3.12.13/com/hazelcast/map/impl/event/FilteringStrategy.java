/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.event;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.map.impl.event.EntryEventDataCache;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventFilter;

public interface FilteringStrategy {
    public static final int FILTER_DOES_NOT_MATCH = -1;

    public int doFilter(EventFilter var1, Data var2, Object var3, Object var4, EntryEventType var5, String var6);

    public EntryEventDataCache getEntryEventDataCache();
}

