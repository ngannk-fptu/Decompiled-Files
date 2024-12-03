/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryView;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidator;
import com.hazelcast.internal.util.ToHeapDataConverter;
import com.hazelcast.map.impl.EntryViews;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.event.MapEventPublisher;
import com.hazelcast.map.impl.mapstore.MapDataStore;
import com.hazelcast.map.impl.nearcache.MapNearCacheManager;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.ServiceNamespaceAware;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.wan.impl.CallerProvenance;
import java.util.List;

public abstract class MapOperation
extends AbstractNamedOperation
implements IdentifiedDataSerializable,
ServiceNamespaceAware {
    protected transient MapService mapService;
    protected transient MapContainer mapContainer;
    protected transient MapServiceContext mapServiceContext;
    protected transient MapEventPublisher mapEventPublisher;
    protected transient RecordStore recordStore;
    protected transient boolean createRecordStoreOnDemand = true;
    protected boolean disableWanReplicationEvent;

    public MapOperation() {
    }

    public MapOperation(String name) {
        this.name = name;
    }

    public void setMapService(MapService mapService) {
        this.mapService = mapService;
    }

    public void setMapContainer(MapContainer mapContainer) {
        this.mapContainer = mapContainer;
    }

    protected final CallerProvenance getCallerProvenance() {
        return this.disableWanReplicationEvent ? CallerProvenance.WAN : CallerProvenance.NOT_WAN;
    }

    @Override
    public void beforeRun() throws Exception {
        super.beforeRun();
        this.mapService = (MapService)this.getService();
        this.mapServiceContext = this.mapService.getMapServiceContext();
        this.mapEventPublisher = this.mapServiceContext.getMapEventPublisher();
        this.innerBeforeRun();
    }

    public void innerBeforeRun() throws Exception {
        this.recordStore = this.getRecordStoreOrNull();
        this.mapContainer = this.recordStore == null ? this.mapServiceContext.getMapContainer(this.name) : this.recordStore.getMapContainer();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    public boolean isPostProcessing(RecordStore recordStore) {
        MapDataStore<Data, Object> mapDataStore = recordStore.getMapDataStore();
        return mapDataStore.isPostProcessingMapStore() || this.mapServiceContext.hasInterceptor(this.name);
    }

    public void setThreadId(long threadId) {
        throw new UnsupportedOperationException();
    }

    public long getThreadId() {
        throw new UnsupportedOperationException();
    }

    protected final void invalidateNearCache(List<Data> keys) {
        if (!this.mapContainer.hasInvalidationListener() || CollectionUtil.isEmpty(keys)) {
            return;
        }
        Invalidator invalidator = this.getNearCacheInvalidator();
        for (Data key : keys) {
            invalidator.invalidateKey(key, this.name, this.getCallerUuid());
        }
    }

    public final void invalidateNearCache(Data key) {
        if (!this.mapContainer.hasInvalidationListener() || key == null) {
            return;
        }
        Invalidator invalidator = this.getNearCacheInvalidator();
        invalidator.invalidateKey(key, this.name, this.getCallerUuid());
    }

    protected final void invalidateAllKeysInNearCaches() {
        if (this.mapContainer.hasInvalidationListener()) {
            int partitionId = this.getPartitionId();
            Invalidator invalidator = this.getNearCacheInvalidator();
            if (partitionId == this.getNodeEngine().getPartitionService().getPartitionId(this.name)) {
                invalidator.invalidateAllKeys(this.name, this.getCallerUuid());
            }
            invalidator.resetPartitionMetaData(this.name, this.getPartitionId());
        }
    }

    private Invalidator getNearCacheInvalidator() {
        MapNearCacheManager mapNearCacheManager = this.mapServiceContext.getMapNearCacheManager();
        return mapNearCacheManager.getInvalidator();
    }

    protected void evict(Data excludedKey) {
        assert (this.recordStore != null) : "Record-store cannot be null";
        this.recordStore.evictEntries(excludedKey);
    }

    private RecordStore getRecordStoreOrNull() {
        int partitionId = this.getPartitionId();
        if (partitionId == -1) {
            return null;
        }
        PartitionContainer partitionContainer = this.mapServiceContext.getPartitionContainer(partitionId);
        if (this.createRecordStoreOnDemand) {
            return partitionContainer.getRecordStore(this.name);
        }
        return partitionContainer.getExistingRecordStore(this.name);
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public ObjectNamespace getServiceNamespace() {
        MapContainer container = this.mapContainer;
        if (container == null) {
            MapService service = (MapService)this.getService();
            container = service.getMapServiceContext().getMapContainer(this.name);
        }
        return container.getObjectNamespace();
    }

    protected final boolean canThisOpGenerateWANEvent() {
        return !this.disableWanReplicationEvent;
    }

    protected final void publishWanUpdate(Data dataKey, Object value) {
        this.publishWanUpdateInternal(dataKey, value, false);
    }

    private void publishWanUpdateInternal(Data dataKey, Object value, boolean hasLoadProvenance) {
        if (!this.canPublishWANEvent()) {
            return;
        }
        Object record = this.recordStore.getRecord(dataKey);
        if (record == null) {
            return;
        }
        Data dataValue = ToHeapDataConverter.toHeapData(this.mapServiceContext.toData(value));
        EntryView<Data, Data> entryView = EntryViews.createSimpleEntryView(ToHeapDataConverter.toHeapData(dataKey), dataValue, record);
        this.mapEventPublisher.publishWanUpdate(this.name, entryView, hasLoadProvenance);
    }

    protected final void publishLoadAsWanUpdate(Data dataKey, Object value) {
        this.publishWanUpdateInternal(dataKey, value, true);
    }

    protected final void publishWanRemove(Data dataKey) {
        if (!this.canPublishWANEvent()) {
            return;
        }
        this.mapEventPublisher.publishWanRemove(this.name, ToHeapDataConverter.toHeapData(dataKey));
    }

    private boolean canPublishWANEvent() {
        return this.mapContainer.isWanReplicationEnabled() && this.canThisOpGenerateWANEvent();
    }
}

