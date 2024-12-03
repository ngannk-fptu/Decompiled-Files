/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.listener;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.MapListener;

public interface EntryUpdatedListener<K, V>
extends MapListener {
    public void entryUpdated(EntryEvent<K, V> var1);
}

