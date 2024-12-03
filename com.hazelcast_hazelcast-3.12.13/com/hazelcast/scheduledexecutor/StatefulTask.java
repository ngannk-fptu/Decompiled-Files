/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor;

import java.util.Map;

public interface StatefulTask<K, V> {
    public void save(Map<K, V> var1);

    public void load(Map<K, V> var1);
}

