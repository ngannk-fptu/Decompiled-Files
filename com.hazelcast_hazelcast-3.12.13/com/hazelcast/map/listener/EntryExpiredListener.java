/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.listener;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.MapListener;

public interface EntryExpiredListener<K, V>
extends MapListener {
    public void entryExpired(EntryEvent<K, V> var1);
}

