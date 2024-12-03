/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.MapStoreWrapper;
import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.mapstore.MapStoreManager;
import com.hazelcast.map.impl.mapstore.MapStoreManagers;
import com.hazelcast.map.impl.mapstore.StoreConstructor;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.IterableUtil;
import java.util.Properties;

final class BasicMapStoreContext
implements MapStoreContext {
    private String mapName;
    private MapStoreManager mapStoreManager;
    private MapStoreWrapper storeWrapper;
    private MapServiceContext mapServiceContext;
    private MapStoreConfig mapStoreConfig;

    private BasicMapStoreContext() {
    }

    @Override
    public void start() {
        this.mapStoreManager.start();
    }

    @Override
    public void stop() {
        this.mapStoreManager.stop();
    }

    @Override
    public boolean isWriteBehindMapStoreEnabled() {
        MapStoreConfig mapStoreConfig = this.getMapStoreConfig();
        return mapStoreConfig != null && mapStoreConfig.isEnabled() && mapStoreConfig.getWriteDelaySeconds() > 0;
    }

    @Override
    public boolean isMapLoader() {
        return this.storeWrapper.isMapLoader();
    }

    @Override
    public SerializationService getSerializationService() {
        return this.mapServiceContext.getNodeEngine().getSerializationService();
    }

    @Override
    public ILogger getLogger(Class clazz) {
        return this.mapServiceContext.getNodeEngine().getLogger(clazz);
    }

    @Override
    public String getMapName() {
        return this.mapName;
    }

    @Override
    public MapServiceContext getMapServiceContext() {
        return this.mapServiceContext;
    }

    @Override
    public MapStoreConfig getMapStoreConfig() {
        return this.mapStoreConfig;
    }

    @Override
    public MapStoreManager getMapStoreManager() {
        return this.mapStoreManager;
    }

    @Override
    public MapStoreWrapper getMapStoreWrapper() {
        return this.storeWrapper;
    }

    static MapStoreContext create(MapContainer mapContainer) {
        BasicMapStoreContext context = new BasicMapStoreContext();
        String mapName = mapContainer.getName();
        MapServiceContext mapServiceContext = mapContainer.getMapServiceContext();
        NodeEngine nodeEngine = mapServiceContext.getNodeEngine();
        PartitioningStrategy partitioningStrategy = mapContainer.getPartitioningStrategy();
        MapConfig mapConfig = mapContainer.getMapConfig();
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        ClassLoader configClassLoader = nodeEngine.getConfigClassLoader();
        Object store = StoreConstructor.createStore(mapName, mapStoreConfig, configClassLoader);
        MapStoreWrapper storeWrapper = new MapStoreWrapper(mapName, store);
        storeWrapper.instrument(nodeEngine);
        context.setMapName(mapName);
        context.setMapStoreConfig(mapStoreConfig);
        context.setPartitioningStrategy(partitioningStrategy);
        context.setMapServiceContext(mapServiceContext);
        context.setStoreWrapper(storeWrapper);
        MapStoreManager mapStoreManager = BasicMapStoreContext.createMapStoreManager(context);
        context.setMapStoreManager(mapStoreManager);
        BasicMapStoreContext.callLifecycleSupportInit(context);
        return context;
    }

    private static MapStoreManager createMapStoreManager(MapStoreContext mapStoreContext) {
        MapStoreConfig mapStoreConfig = mapStoreContext.getMapStoreConfig();
        if (BasicMapStoreContext.isWriteBehindMapStoreEnabled(mapStoreConfig)) {
            return MapStoreManagers.createWriteBehindManager(mapStoreContext);
        }
        return MapStoreManagers.createWriteThroughManager(mapStoreContext);
    }

    private static boolean isWriteBehindMapStoreEnabled(MapStoreConfig mapStoreConfig) {
        return mapStoreConfig != null && mapStoreConfig.isEnabled() && mapStoreConfig.getWriteDelaySeconds() > 0;
    }

    private static void callLifecycleSupportInit(MapStoreContext mapStoreContext) {
        MapStoreWrapper mapStoreWrapper = mapStoreContext.getMapStoreWrapper();
        MapServiceContext mapServiceContext = mapStoreContext.getMapServiceContext();
        NodeEngine nodeEngine = mapServiceContext.getNodeEngine();
        HazelcastInstance hazelcastInstance = nodeEngine.getHazelcastInstance();
        MapStoreConfig mapStoreConfig = mapStoreContext.getMapStoreConfig();
        Properties properties = mapStoreConfig.getProperties();
        String mapName = mapStoreContext.getMapName();
        mapStoreWrapper.init(hazelcastInstance, properties, mapName);
    }

    @Override
    public Iterable<Object> loadAllKeys() {
        return IterableUtil.nullToEmpty(this.storeWrapper.loadAllKeys());
    }

    void setMapStoreManager(MapStoreManager mapStoreManager) {
        this.mapStoreManager = mapStoreManager;
    }

    void setStoreWrapper(MapStoreWrapper storeWrapper) {
        this.storeWrapper = storeWrapper;
    }

    void setMapServiceContext(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
    }

    void setMapName(String mapName) {
        this.mapName = mapName;
    }

    void setPartitioningStrategy(PartitioningStrategy partitioningStrategy) {
    }

    void setMapStoreConfig(MapStoreConfig mapStoreConfig) {
        this.mapStoreConfig = mapStoreConfig;
    }
}

