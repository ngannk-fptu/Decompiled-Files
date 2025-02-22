/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl.operations;

import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.journal.CacheEventJournal;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.journal.MapEventJournal;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.RingbufferDataSerializerHook;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;

public class ReplicationOperation
extends Operation
implements IdentifiedDataSerializable,
Versioned {
    private Map<ObjectNamespace, RingbufferContainer> migrationData;

    public ReplicationOperation() {
    }

    public ReplicationOperation(Map<ObjectNamespace, RingbufferContainer> migrationData, int partitionId, int replicaIndex) {
        this.setPartitionId(partitionId).setReplicaIndex(replicaIndex);
        this.migrationData = migrationData;
    }

    @Override
    public void run() {
        RingbufferService service = (RingbufferService)this.getService();
        for (Map.Entry<ObjectNamespace, RingbufferContainer> entry : this.migrationData.entrySet()) {
            ObjectNamespace ns = entry.getKey();
            RingbufferContainer ringbuffer = entry.getValue();
            service.addRingbuffer(this.getPartitionId(), ringbuffer, this.getRingbufferConfig(service, ns));
        }
    }

    private RingbufferConfig getRingbufferConfig(RingbufferService service, ObjectNamespace ns) {
        String serviceName = ns.getServiceName();
        if ("hz:impl:ringbufferService".equals(serviceName)) {
            return service.getRingbufferConfig(ns.getObjectName());
        }
        if ("hz:impl:mapService".equals(serviceName)) {
            MapService mapService = (MapService)this.getNodeEngine().getService("hz:impl:mapService");
            MapEventJournal journal = mapService.getMapServiceContext().getEventJournal();
            EventJournalConfig journalConfig = journal.getEventJournalConfig(ns);
            return journal.toRingbufferConfig(journalConfig, ns);
        }
        if ("hz:impl:cacheService".equals(serviceName)) {
            CacheService cacheService = (CacheService)this.getNodeEngine().getService("hz:impl:cacheService");
            CacheEventJournal journal = cacheService.getEventJournal();
            EventJournalConfig journalConfig = journal.getEventJournalConfig(ns);
            return journal.toRingbufferConfig(journalConfig, ns);
        }
        throw new IllegalArgumentException("Unsupported ringbuffer service name " + serviceName);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:ringbufferService";
    }

    @Override
    public int getFactoryId() {
        return RingbufferDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.migrationData.size());
        for (Map.Entry<ObjectNamespace, RingbufferContainer> entry : this.migrationData.entrySet()) {
            ObjectNamespace ns = entry.getKey();
            out.writeObject(ns);
            RingbufferContainer container = entry.getValue();
            container.writeData(out);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int mapSize = in.readInt();
        this.migrationData = MapUtil.createHashMap(mapSize);
        for (int i = 0; i < mapSize; ++i) {
            ObjectNamespace namespace = (ObjectNamespace)in.readObject();
            RingbufferContainer container = new RingbufferContainer(namespace, this.getPartitionId());
            container.readData(in);
            this.migrationData.put(namespace, container);
        }
    }
}

