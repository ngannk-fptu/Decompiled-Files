/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.collection.impl.queue.LatencyTrackingQueueStore;
import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.core.QueueStore;
import com.hazelcast.core.QueueStoreFactory;
import com.hazelcast.internal.diagnostics.Diagnostics;
import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class QueueStoreWrapper
implements QueueStore<Data> {
    private static final int DEFAULT_MEMORY_LIMIT = 1000;
    private static final int DEFAULT_BULK_LOAD = 250;
    private static final String STORE_BINARY = "binary";
    private static final String STORE_MEMORY_LIMIT = "memory-limit";
    private static final String STORE_BULK_LOAD = "bulk-load";
    private final String name;
    private int memoryLimit = 1000;
    private int bulkLoad = 250;
    private boolean enabled;
    private boolean binary;
    private QueueStore store;
    private SerializationService serializationService;

    private QueueStoreWrapper(String name) {
        this.name = name;
    }

    public static QueueStoreWrapper create(String name, QueueStoreConfig storeConfig, SerializationService serializationService, ClassLoader classLoader) {
        Preconditions.checkNotNull(name, "name should not be null");
        Preconditions.checkNotNull(serializationService, "serializationService should not be null");
        QueueStoreWrapper storeWrapper = new QueueStoreWrapper(name);
        storeWrapper.setSerializationService(serializationService);
        if (storeConfig == null || !storeConfig.isEnabled()) {
            return storeWrapper;
        }
        QueueStore queueStore = QueueStoreWrapper.createQueueStore(name, storeConfig, classLoader);
        if (queueStore != null) {
            storeWrapper.setEnabled(storeConfig.isEnabled());
            storeWrapper.setBinary(Boolean.parseBoolean(storeConfig.getProperty(STORE_BINARY)));
            storeWrapper.setMemoryLimit(QueueStoreWrapper.parseInt(STORE_MEMORY_LIMIT, 1000, storeConfig));
            storeWrapper.setBulkLoad(QueueStoreWrapper.parseInt(STORE_BULK_LOAD, 250, storeConfig));
            storeWrapper.setStore(queueStore);
        }
        return storeWrapper;
    }

    private static QueueStore createQueueStore(String name, QueueStoreConfig storeConfig, ClassLoader classLoader) {
        QueueStore store = QueueStoreWrapper.getQueueStore(storeConfig, classLoader);
        if (store == null) {
            store = QueueStoreWrapper.getQueueStoreFactory(name, storeConfig, classLoader);
        }
        return store;
    }

    private static QueueStore getQueueStore(QueueStoreConfig storeConfig, ClassLoader classLoader) {
        if (storeConfig == null) {
            return null;
        }
        QueueStore store = storeConfig.getStoreImplementation();
        if (store != null) {
            return store;
        }
        try {
            store = (QueueStore)ClassLoaderUtil.newInstance(classLoader, storeConfig.getClassName());
        }
        catch (Exception ignored) {
            EmptyStatement.ignore(ignored);
        }
        return store;
    }

    private static QueueStore getQueueStoreFactory(String name, QueueStoreConfig storeConfig, ClassLoader classLoader) {
        if (storeConfig == null) {
            return null;
        }
        QueueStoreFactory factory = storeConfig.getFactoryImplementation();
        if (factory == null) {
            try {
                factory = (QueueStoreFactory)ClassLoaderUtil.newInstance(classLoader, storeConfig.getFactoryClassName());
            }
            catch (Exception ignored) {
                EmptyStatement.ignore(ignored);
            }
        }
        return factory == null ? null : factory.newQueueStore(name, storeConfig.getProperties());
    }

    void instrument(NodeEngine nodeEngine) {
        Diagnostics diagnostics = ((NodeEngineImpl)nodeEngine).getDiagnostics();
        StoreLatencyPlugin storeLatencyPlugin = diagnostics.getPlugin(StoreLatencyPlugin.class);
        if (!this.enabled || storeLatencyPlugin == null) {
            return;
        }
        this.store = new LatencyTrackingQueueStore(this.store, storeLatencyPlugin, this.name);
    }

    @Override
    public void store(Long key, Data value) {
        if (!this.enabled) {
            return;
        }
        Object actualValue = this.binary ? (Object)Arrays.copyOf(value.toByteArray(), value.totalSize()) : this.serializationService.toObject(value);
        this.store.store(key, actualValue);
    }

    @Override
    public void storeAll(Map<Long, Data> map) {
        if (!this.enabled) {
            return;
        }
        Map<Long, Object> objectMap = MapUtil.createHashMap(map.size());
        if (this.binary) {
            for (Map.Entry<Long, Data> entry : map.entrySet()) {
                Data value = entry.getValue();
                byte[] copy = Arrays.copyOf(value.toByteArray(), value.totalSize());
                objectMap.put(entry.getKey(), copy);
            }
        } else {
            for (Map.Entry<Long, Data> entry : map.entrySet()) {
                objectMap.put(entry.getKey(), this.serializationService.toObject(entry.getValue()));
            }
        }
        this.store.storeAll(objectMap);
    }

    @Override
    public void delete(Long key) {
        if (this.enabled) {
            this.store.delete(key);
        }
    }

    @Override
    public void deleteAll(Collection<Long> keys) {
        if (this.enabled) {
            this.store.deleteAll(keys);
        }
    }

    @Override
    public Data load(Long key) {
        if (!this.enabled) {
            return null;
        }
        Object val = this.store.load(key);
        if (this.binary) {
            byte[] dataBuffer = (byte[])val;
            return new HeapData(Arrays.copyOf(dataBuffer, dataBuffer.length));
        }
        return this.serializationService.toData(val);
    }

    @Override
    public Map<Long, Data> loadAll(Collection<Long> keys) {
        if (!this.enabled) {
            return null;
        }
        Map map = this.store.loadAll(keys);
        if (map == null) {
            return Collections.emptyMap();
        }
        Map<Long, Data> dataMap = MapUtil.createHashMap(map.size());
        if (this.binary) {
            for (Map.Entry entry : map.entrySet()) {
                byte[] dataBuffer = (byte[])entry.getValue();
                HeapData data = new HeapData(Arrays.copyOf(dataBuffer, dataBuffer.length));
                dataMap.put(entry.getKey(), data);
            }
        } else {
            for (Map.Entry entry : map.entrySet()) {
                dataMap.put(entry.getKey(), (Data)this.serializationService.toData(entry.getValue()));
            }
        }
        return dataMap;
    }

    @Override
    public Set<Long> loadAllKeys() {
        if (this.enabled) {
            return this.store.loadAllKeys();
        }
        return null;
    }

    private static int parseInt(String name, int defaultValue, QueueStoreConfig storeConfig) {
        String val = storeConfig.getProperty(name);
        if (val == null || val.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isBinary() {
        return this.binary;
    }

    public int getMemoryLimit() {
        return this.memoryLimit;
    }

    public int getBulkLoad() {
        return this.bulkLoad;
    }

    void setSerializationService(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    void setStore(QueueStore store) {
        this.store = store;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    void setBulkLoad(int bulkLoad) {
        if (bulkLoad < 1) {
            bulkLoad = 1;
        }
        this.bulkLoad = bulkLoad;
    }

    void setBinary(boolean binary) {
        this.binary = binary;
    }
}

