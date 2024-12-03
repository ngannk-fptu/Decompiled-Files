/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore;

import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.MapStoreWrapper;
import com.hazelcast.map.impl.mapstore.MapStoreManager;
import com.hazelcast.spi.serialization.SerializationService;

public interface MapStoreContext {
    public void start();

    public void stop();

    public MapStoreManager getMapStoreManager();

    public MapStoreWrapper getMapStoreWrapper();

    public boolean isWriteBehindMapStoreEnabled();

    public SerializationService getSerializationService();

    public ILogger getLogger(Class var1);

    public String getMapName();

    public MapServiceContext getMapServiceContext();

    public MapStoreConfig getMapStoreConfig();

    public Iterable<Object> loadAllKeys();

    public boolean isMapLoader();
}

