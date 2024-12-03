/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.listener;

import com.hazelcast.core.MapEvent;
import com.hazelcast.map.listener.MapListener;

public interface MapEvictedListener
extends MapListener {
    public void mapEvicted(MapEvent var1);
}

