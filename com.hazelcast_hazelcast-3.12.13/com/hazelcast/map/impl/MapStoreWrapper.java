/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapLoaderLifecycleSupport;
import com.hazelcast.core.MapStore;
import com.hazelcast.core.PostProcessingMapStore;
import com.hazelcast.internal.diagnostics.Diagnostics;
import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import com.hazelcast.map.impl.LatencyTrackingMapLoader;
import com.hazelcast.map.impl.LatencyTrackingMapStore;
import com.hazelcast.query.impl.getters.ReflectionHelper;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class MapStoreWrapper
implements MapStore,
MapLoaderLifecycleSupport {
    private MapLoader mapLoader;
    private MapStore mapStore;
    private final String mapName;
    private final Object impl;

    public MapStoreWrapper(String mapName, Object impl) {
        this.mapName = mapName;
        this.impl = impl;
        MapLoader loader = null;
        MapStore store = null;
        if (impl instanceof MapStore) {
            store = (MapStore)impl;
        }
        if (impl instanceof MapLoader) {
            loader = (MapLoader)impl;
        }
        this.mapLoader = loader;
        this.mapStore = store;
    }

    public MapStore getMapStore() {
        return this.mapStore;
    }

    @Override
    public void destroy() {
        if (this.impl instanceof MapLoaderLifecycleSupport) {
            ((MapLoaderLifecycleSupport)this.impl).destroy();
        }
    }

    @Override
    public void init(HazelcastInstance hazelcastInstance, Properties properties, String mapName) {
        if (this.impl instanceof MapLoaderLifecycleSupport) {
            ((MapLoaderLifecycleSupport)this.impl).init(hazelcastInstance, properties, mapName);
        }
    }

    private boolean isMapStore() {
        return this.mapStore != null;
    }

    public boolean isMapLoader() {
        return this.mapLoader != null;
    }

    public void instrument(NodeEngine nodeEngine) {
        Diagnostics diagnostics = ((NodeEngineImpl)nodeEngine).getDiagnostics();
        StoreLatencyPlugin storeLatencyPlugin = diagnostics.getPlugin(StoreLatencyPlugin.class);
        if (storeLatencyPlugin == null) {
            return;
        }
        if (this.mapLoader != null) {
            this.mapLoader = new LatencyTrackingMapLoader(this.mapLoader, storeLatencyPlugin, this.mapName);
        }
        if (this.mapStore != null) {
            this.mapStore = new LatencyTrackingMapStore(this.mapStore, storeLatencyPlugin, this.mapName);
        }
    }

    public void delete(Object key) {
        if (this.isMapStore()) {
            this.mapStore.delete(key);
        }
    }

    public void store(Object key, Object value) {
        if (this.isMapStore()) {
            this.mapStore.store(key, value);
        }
    }

    public void storeAll(Map map) {
        if (this.isMapStore()) {
            this.mapStore.storeAll(map);
        }
    }

    public void deleteAll(Collection keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        if (this.isMapStore()) {
            this.mapStore.deleteAll(keys);
        }
    }

    @Override
    public Iterable<Object> loadAllKeys() {
        if (this.isMapLoader()) {
            Iterable allKeys;
            try {
                allKeys = this.mapLoader.loadAllKeys();
            }
            catch (AbstractMethodError e) {
                allKeys = (Iterable)ReflectionHelper.invokeMethod(this.mapLoader, "loadAllKeys");
            }
            return allKeys;
        }
        return null;
    }

    @Override
    public Object load(Object key) {
        if (this.isMapLoader()) {
            return this.mapLoader.load(key);
        }
        return null;
    }

    @Override
    public Map loadAll(Collection keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        if (this.isMapLoader()) {
            return this.mapLoader.loadAll(keys);
        }
        return null;
    }

    public Object getImpl() {
        return this.impl;
    }

    public boolean isPostProcessingMapStore() {
        return this.isMapStore() && this.mapStore instanceof PostProcessingMapStore;
    }

    public String toString() {
        return "MapStoreWrapper{mapName='" + this.mapName + '\'' + ", mapStore=" + this.mapStore + ", mapLoader=" + this.mapLoader + '}';
    }
}

