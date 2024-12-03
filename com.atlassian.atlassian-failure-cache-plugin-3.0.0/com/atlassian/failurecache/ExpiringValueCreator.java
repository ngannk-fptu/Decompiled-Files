/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.failurecache;

import com.atlassian.failurecache.ExpiringValue;

public class ExpiringValueCreator {
    public static <V> ExpiringValue<V> create(V value) {
        long now = System.currentTimeMillis();
        return new ExpiringValue<V>(value, now + 60000L, 120000L);
    }
}

