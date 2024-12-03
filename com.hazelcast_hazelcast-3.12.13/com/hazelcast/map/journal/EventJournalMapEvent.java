/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.journal;

import com.hazelcast.core.EntryEventType;

public interface EventJournalMapEvent<K, V> {
    public K getKey();

    public V getNewValue();

    public V getOldValue();

    public EntryEventType getType();
}

