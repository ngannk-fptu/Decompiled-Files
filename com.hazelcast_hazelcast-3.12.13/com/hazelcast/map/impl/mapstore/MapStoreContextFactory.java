/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.MapStoreWrapper;
import com.hazelcast.map.impl.mapstore.BasicMapStoreContext;
import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.mapstore.MapStoreManager;
import com.hazelcast.map.impl.mapstore.MapStoreManagers;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Collections;

public final class MapStoreContextFactory {
    private static final MapStoreContext EMPTY_MAP_STORE_CONTEXT = new EmptyMapStoreContext();

    private MapStoreContextFactory() {
    }

    public static MapStoreContext createMapStoreContext(MapContainer mapContainer) {
        MapConfig mapConfig = mapContainer.getMapConfig();
        MapStoreConfig mapStoreConfig = mapConfig.getMapStoreConfig();
        if (mapStoreConfig == null || !mapStoreConfig.isEnabled()) {
            return EMPTY_MAP_STORE_CONTEXT;
        }
        return BasicMapStoreContext.create(mapContainer);
    }

    private static final class EmptyMapStoreContext
    implements MapStoreContext {
        private EmptyMapStoreContext() {
        }

        @Override
        public MapStoreManager getMapStoreManager() {
            return MapStoreManagers.emptyMapStoreManager();
        }

        @Override
        public MapStoreWrapper getMapStoreWrapper() {
            return null;
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public boolean isWriteBehindMapStoreEnabled() {
            return false;
        }

        @Override
        public SerializationService getSerializationService() {
            throw new UnsupportedOperationException("This method should not be called. No defined map store exists.");
        }

        @Override
        public ILogger getLogger(Class clazz) {
            throw new UnsupportedOperationException("This method should not be called. No defined map store exists.");
        }

        @Override
        public String getMapName() {
            throw new UnsupportedOperationException("This method should not be called. No defined map store exists.");
        }

        @Override
        public MapServiceContext getMapServiceContext() {
            throw new UnsupportedOperationException("This method should not be called. No defined map store exists.");
        }

        @Override
        public MapStoreConfig getMapStoreConfig() {
            throw new UnsupportedOperationException("This method should not be called. No defined map store exists.");
        }

        @Override
        public Iterable<Object> loadAllKeys() {
            return Collections.emptyList();
        }

        @Override
        public boolean isMapLoader() {
            return false;
        }
    }
}

