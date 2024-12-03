/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache;

import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.spi.EventFilter;

public interface QueryCacheEventService<E> {
    public void publish(String var1, String var2, E var3, int var4, Extractors var5);

    public String addPublisherListener(String var1, String var2, ListenerAdapter var3);

    public boolean removePublisherListener(String var1, String var2, String var3);

    public String addListener(String var1, String var2, MapListener var3);

    public String addListener(String var1, String var2, MapListener var3, EventFilter var4);

    public boolean removeListener(String var1, String var2, String var3);

    public void removeAllListeners(String var1, String var2);

    public boolean hasListener(String var1, String var2);

    public void sendEventToSubscriber(String var1, Object var2, int var3);
}

