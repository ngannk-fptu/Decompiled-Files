/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore;

import com.hazelcast.map.impl.mapstore.MapDataStore;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

class EmptyMapDataStore
implements MapDataStore {
    EmptyMapDataStore() {
    }

    public Object add(Object key, Object value, long now) {
        return value;
    }

    public void addTransient(Object key, long now) {
    }

    public Object addBackup(Object key, Object value, long now) {
        return value;
    }

    public void remove(Object key, long now) {
    }

    public void removeBackup(Object key, long now) {
    }

    @Override
    public void reset() {
    }

    public Object load(Object key) {
        return null;
    }

    @Override
    public Map loadAll(Collection keys) {
        return Collections.emptyMap();
    }

    @Override
    public void removeAll(Collection keys) {
    }

    public boolean loadable(Object key) {
        return false;
    }

    @Override
    public long softFlush() {
        return 0L;
    }

    @Override
    public void hardFlush() {
    }

    public Object flush(Object key, Object value, boolean backup) {
        return value;
    }

    @Override
    public int notFinishedOperationsCount() {
        return 0;
    }

    @Override
    public boolean isPostProcessingMapStore() {
        return false;
    }
}

