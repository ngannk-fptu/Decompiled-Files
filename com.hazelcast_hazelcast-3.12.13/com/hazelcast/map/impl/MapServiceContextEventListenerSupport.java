/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.listener.MapPartitionLostListener;
import com.hazelcast.spi.EventFilter;

public interface MapServiceContextEventListenerSupport {
    public String addLocalEventListener(Object var1, String var2);

    public String addLocalEventListener(Object var1, EventFilter var2, String var3);

    public String addLocalPartitionLostListener(MapPartitionLostListener var1, String var2);

    public String addEventListener(Object var1, EventFilter var2, String var3);

    public String addPartitionLostListener(MapPartitionLostListener var1, String var2);

    public boolean removeEventListener(String var1, String var2);

    public boolean removePartitionLostListener(String var1, String var2);
}

