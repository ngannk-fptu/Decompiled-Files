/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.listener;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.MapListener;

public interface EntryEvictedListener<K, V>
extends MapListener {
    public void entryEvicted(EntryEvent<K, V> var1);
}

