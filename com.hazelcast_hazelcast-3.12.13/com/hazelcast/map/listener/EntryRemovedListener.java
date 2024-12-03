/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.listener;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.MapListener;

public interface EntryRemovedListener<K, V>
extends MapListener {
    public void entryRemoved(EntryEvent<K, V> var1);
}

