/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.dispatcher.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class DiscardingMap<K, V>
extends LinkedHashMap<K, V> {
    private final int capacity;

    public DiscardingMap(int capacity) {
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > this.capacity;
    }
}

