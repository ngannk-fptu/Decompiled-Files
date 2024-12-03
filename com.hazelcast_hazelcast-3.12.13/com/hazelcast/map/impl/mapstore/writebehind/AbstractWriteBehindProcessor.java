/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.MapStore;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.mapstore.writebehind.DefaultWriteBehindProcessor;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindProcessor;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.List;
import java.util.Map;

abstract class AbstractWriteBehindProcessor<T>
implements WriteBehindProcessor<T> {
    protected final int writeBatchSize;
    protected final boolean writeCoalescing;
    protected final ILogger logger;
    protected final MapStore mapStore;
    private final SerializationService serializationService;

    AbstractWriteBehindProcessor(MapStoreContext mapStoreContext) {
        this.serializationService = mapStoreContext.getSerializationService();
        this.mapStore = mapStoreContext.getMapStoreWrapper();
        this.logger = mapStoreContext.getLogger(DefaultWriteBehindProcessor.class);
        MapStoreConfig mapStoreConfig = mapStoreContext.getMapStoreConfig();
        this.writeBatchSize = mapStoreConfig.getWriteBatchSize();
        this.writeCoalescing = mapStoreConfig.isWriteCoalescing();
    }

    protected Object toObject(Object obj) {
        return this.serializationService.toObject(obj);
    }

    protected List<T> getBatchChunk(List<T> list, int batchSize, int chunkNumber) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int start = chunkNumber * batchSize;
        int end = Math.min(start + batchSize, list.size());
        if (start >= end) {
            return null;
        }
        return list.subList(start, end);
    }

    static enum StoreOperationType {
        DELETE{

            @Override
            boolean processSingle(Object key, Object value, MapStore mapStore) {
                mapStore.delete(key);
                return true;
            }

            @Override
            boolean processBatch(Map map, MapStore mapStore) {
                mapStore.deleteAll(map.keySet());
                return true;
            }
        }
        ,
        WRITE{

            @Override
            boolean processSingle(Object key, Object value, MapStore mapStore) {
                mapStore.store(key, value);
                return true;
            }

            @Override
            boolean processBatch(Map map, MapStore mapStore) {
                mapStore.storeAll(map);
                return true;
            }
        };


        abstract boolean processSingle(Object var1, Object var2, MapStore var3);

        abstract boolean processBatch(Map var1, MapStore var2);
    }
}

