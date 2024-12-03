/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.nearcache;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.IFunction;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.internal.nearcache.impl.DefaultNearCacheManager;
import com.hazelcast.internal.nearcache.impl.invalidation.BatchInvalidator;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidator;
import com.hazelcast.internal.nearcache.impl.invalidation.MinimalPartitionService;
import com.hazelcast.internal.nearcache.impl.invalidation.NonStopInvalidator;
import com.hazelcast.internal.nearcache.impl.invalidation.RepairingHandler;
import com.hazelcast.internal.nearcache.impl.invalidation.RepairingTask;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.EventListenerFilter;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.nearcache.MemberMinimalPartitionService;
import com.hazelcast.map.impl.nearcache.invalidation.MemberMapInvalidationMetaDataFetcher;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;

public class MapNearCacheManager
extends DefaultNearCacheManager {
    private static final InvalidationAcceptorFilter INVALIDATION_ACCEPTOR = new InvalidationAcceptorFilter();
    protected final int partitionCount;
    protected final NodeEngine nodeEngine;
    protected final MapServiceContext mapServiceContext;
    protected final MinimalPartitionService partitionService;
    protected final Invalidator invalidator;
    protected final RepairingTask repairingTask;

    public MapNearCacheManager(MapServiceContext mapServiceContext) {
        super(mapServiceContext.getNodeEngine().getSerializationService(), mapServiceContext.getNodeEngine().getExecutionService().getGlobalTaskScheduler(), null, mapServiceContext.getNodeEngine().getProperties());
        this.nodeEngine = mapServiceContext.getNodeEngine();
        this.mapServiceContext = mapServiceContext;
        this.partitionService = new MemberMinimalPartitionService(this.nodeEngine.getPartitionService());
        this.partitionCount = this.partitionService.getPartitionCount();
        this.invalidator = this.createInvalidator();
        this.repairingTask = this.createRepairingInvalidationTask();
    }

    private Invalidator createInvalidator() {
        boolean batchingEnabled;
        HazelcastProperties hazelcastProperties = this.nodeEngine.getProperties();
        int batchSize = hazelcastProperties.getInteger(GroupProperty.MAP_INVALIDATION_MESSAGE_BATCH_SIZE);
        int batchFrequencySeconds = hazelcastProperties.getInteger(GroupProperty.MAP_INVALIDATION_MESSAGE_BATCH_FREQUENCY_SECONDS);
        boolean bl = batchingEnabled = hazelcastProperties.getBoolean(GroupProperty.MAP_INVALIDATION_MESSAGE_BATCH_ENABLED) && batchSize > 1;
        if (batchingEnabled) {
            return new BatchInvalidator("hz:impl:mapService", batchSize, batchFrequencySeconds, INVALIDATION_ACCEPTOR, this.nodeEngine);
        }
        return new NonStopInvalidator("hz:impl:mapService", INVALIDATION_ACCEPTOR, this.nodeEngine);
    }

    private RepairingTask createRepairingInvalidationTask() {
        ExecutionService executionService = this.nodeEngine.getExecutionService();
        ClusterService clusterService = this.nodeEngine.getClusterService();
        OperationService operationService = this.nodeEngine.getOperationService();
        HazelcastProperties properties = this.nodeEngine.getProperties();
        ILogger metadataFetcherLogger = this.nodeEngine.getLogger(MemberMapInvalidationMetaDataFetcher.class);
        MemberMapInvalidationMetaDataFetcher invalidationMetaDataFetcher = new MemberMapInvalidationMetaDataFetcher(clusterService, operationService, metadataFetcherLogger);
        ILogger repairingTaskLogger = this.nodeEngine.getLogger(RepairingTask.class);
        String localUuid = this.nodeEngine.getLocalMember().getUuid();
        return new RepairingTask(properties, invalidationMetaDataFetcher, executionService.getGlobalTaskScheduler(), this.serializationService, this.partitionService, localUuid, repairingTaskLogger);
    }

    public void reset() {
        this.clearAllNearCaches();
        this.invalidator.reset();
    }

    public void shutdown() {
        this.destroyAllNearCaches();
        this.invalidator.shutdown();
    }

    @Override
    public boolean destroyNearCache(String mapName) {
        this.invalidator.destroy(mapName, this.nodeEngine.getLocalMember().getUuid());
        return super.destroyNearCache(mapName);
    }

    public Invalidator getInvalidator() {
        return this.invalidator;
    }

    public RepairingHandler newRepairingHandler(String name, NearCache nearCache) {
        return this.repairingTask.registerAndGetHandler(name, nearCache);
    }

    public void deregisterRepairingHandler(String name) {
        this.repairingTask.deregisterHandler(name);
    }

    public RepairingTask getRepairingTask() {
        return this.repairingTask;
    }

    @SerializableByConvention
    private static class InvalidationAcceptorFilter
    implements IFunction<EventRegistration, Boolean> {
        private InvalidationAcceptorFilter() {
        }

        @Override
        public Boolean apply(EventRegistration eventRegistration) {
            EventFilter filter = eventRegistration.getFilter();
            return filter instanceof EventListenerFilter && filter.eval(EntryEventType.INVALIDATION.getType());
        }
    }
}

