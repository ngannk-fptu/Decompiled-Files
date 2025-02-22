/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapClearedListener;
import com.hazelcast.map.listener.MapEvictedListener;

public interface EntryListener<K, V>
extends EntryAddedListener<K, V>,
EntryUpdatedListener<K, V>,
EntryRemovedListener<K, V>,
EntryEvictedListener<K, V>,
MapClearedListener,
MapEvictedListener {
}

