/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore;

import java.util.Collection;
import java.util.Map;

public interface MapDataStore<K, V> {
    public V add(K var1, V var2, long var3);

    public void addTransient(K var1, long var2);

    public V addBackup(K var1, V var2, long var3);

    public void remove(K var1, long var2);

    public void removeBackup(K var1, long var2);

    public void reset();

    public V load(K var1);

    public Map loadAll(Collection var1);

    public void removeAll(Collection var1);

    public boolean loadable(K var1);

    public int notFinishedOperationsCount();

    public boolean isPostProcessingMapStore();

    public long softFlush();

    public void hardFlush();

    public V flush(K var1, V var2, boolean var3);
}

