/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import java.util.Map;

public interface ExpiringMap<K, V>
extends Map<K, V> {
    public void processRemoved(Map.Entry<K, V> var1);
}

