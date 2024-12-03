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
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.ringbuffer.impl.ArrayRingbuffer;
import com.hazelcast.ringbuffer.impl.Ringbuffer;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.RingbufferDataSerializerHook;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.ringbuffer.impl.operations.MergeBackupOperation;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.ServiceNamespaceAware;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.RingbufferMergeData;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;

public class MergeOperation
extends Operation
implements IdentifiedDataSerializable,
BackupAwareOperation,
ServiceNamespaceAware {
    private ObjectNamespace namespace;
    private SplitBrainMergePolicy<RingbufferMergeData, SplitBrainMergeTypes.RingbufferMergeTypes> mergePolicy;
    private Ringbuffer<Object> mergingRingbuffer;
    private transient Ringbuffer<Object> resultRingbuffer;
    private transient RingbufferConfig config;
    private transient RingbufferService ringbufferService;
    private transient SerializationService serializationService;

    public MergeOperation() {
    }

    public MergeOperation(ObjectNamespace namespace, SplitBrainMergePolicy<RingbufferMergeData, SplitBrainMergeTypes.RingbufferMergeTypes> mergePolicy, Ringbuffer<Object> mergingRingbuffer) {
        this.namespace = namespace;
        this.mergePolicy = mergePolicy;
        this.mergingRingbuffer = mergingRingbuffer;
    }

    @Override
    public void beforeRun() throws Exception {
        this.ringbufferService = (RingbufferService)this.getService();
        this.config = this.getRingbufferConfig(this.ringbufferService, this.namespace);
        this.serializationService = this.getNodeEngine().getSerializationService();
    }

    @Override
    public void run() throws Exception {
        RingbufferContainer<Object, Object> existingContainer = this.ringbufferService.getContainerOrNull(this.getPartitionId(), this.namespace);
        SplitBrainMergeTypes.RingbufferMergeTypes mergingValue = MergingValueFactory.createMergingValue(this.serializationService, this.mergingRingbuffer);
        this.serializationService.getManagedContext().initialize(this.mergePolicy);
        this.resultRingbuffer = this.merge(existingContainer, mergingValue);
    }

    private Ringbuffer<Object> merge(RingbufferContainer<Object, Object> existingContainer, SplitBrainMergeTypes.RingbufferMergeTypes mergingValue) {
        SplitBrainMergeTypes.RingbufferMergeTypes existingValue = this.createMergingValueOrNull(existingContainer);
        RingbufferMergeData resultData = this.mergePolicy.merge(mergingValue, existingValue);
        if (resultData == null) {
            this.ringbufferService.destroyDistributedObject(this.namespace.getObjectName());
            return null;
        }
        if (existingContainer == null) {
            RingbufferConfig config = this.getRingbufferConfig(this.ringbufferService, this.namespace);
            existingContainer = this.ringbufferService.getOrCreateContainer(this.getPartitionId(), this.namespace, config);
        }
        this.setRingbufferData(resultData, existingContainer);
        return existingContainer.getRingbuffer();
    }

    private SplitBrainMergeTypes.RingbufferMergeTypes createMergingValueOrNull(RingbufferContainer<Object, Object> existingContainer) {
        return existingContainer == null || existingContainer.getRingbuffer().isEmpty() ? null : MergingValueFactory.createMergingValue(this.serializationService, existingContainer.getRingbuffer());
    }

    private void setRingbufferData(RingbufferMergeData fromMergeData, RingbufferContainer<Object, Object> toContainer) {
        boolean storeEnabled = toContainer.getStore().isEnabled();
        Data[] storeItems = storeEnabled ? new Data[fromMergeData.size()] : null;
        toContainer.setHeadSequence(fromMergeData.getHeadSequence());
        toContainer.setTailSequence(fromMergeData.getTailSequence());
        for (long seq = fromMergeData.getHeadSequence(); seq <= fromMergeData.getTailSequence(); ++seq) {
            Object resultValue = fromMergeData.read(seq);
            toContainer.set(seq, resultValue);
            if (!storeEnabled) continue;
            storeItems[(int)(seq - fromMergeData.getHeadSequence())] = this.serializationService.toData(resultValue);
        }
        if (storeEnabled) {
            toContainer.getStore().storeAll(fromMergeData.getHeadSequence(), storeItems);
        }
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public int getSyncBackupCount() {
        return this.config.getBackupCount();
    }

    @Override
    public int getAsyncBackupCount() {
        return this.config.getAsyncBackupCount();
    }

    @Override
    public Operation getBackupOperation() {
        return new MergeBackupOperation(this.namespace.getObjectName(), this.resultRingbuffer);
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
            return journal.toRingbufferConfig(journalConfig, this.namespace);
        }
        if ("hz:impl:cacheService".equals(serviceName)) {
            CacheService cacheService = (CacheService)this.getNodeEngine().getService("hz:impl:cacheService");
            CacheEventJournal journal = cacheService.getEventJournal();
            EventJournalConfig journalConfig = journal.getEventJournalConfig(ns);
            return journal.toRingbufferConfig(journalConfig, this.namespace);
        }
        throw new IllegalArgumentException("Unsupported ringbuffer service name: " + serviceName);
    }

    @Override
    public ServiceNamespace getServiceNamespace() {
        return this.namespace;
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
        return 11;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.namespace);
        out.writeObject(this.mergePolicy);
        out.writeLong(this.mergingRingbuffer.tailSequence());
        out.writeLong(this.mergingRingbuffer.headSequence());
        out.writeInt((int)this.mergingRingbuffer.getCapacity());
        for (Object t : this.mergingRingbuffer) {
            IOUtil.writeObject(out, t);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.namespace = (ObjectNamespace)in.readObject();
        this.mergePolicy = (SplitBrainMergePolicy)in.readObject();
        long tailSequence = in.readLong();
        long headSequence = in.readLong();
        int capacity = in.readInt();
        this.mergingRingbuffer = new ArrayRingbuffer<Object>(capacity);
        this.mergingRingbuffer.setTailSequence(tailSequence);
        this.mergingRingbuffer.setHeadSequence(headSequence);
        for (long seq = headSequence; seq <= tailSequence; ++seq) {
            this.mergingRingbuffer.set(seq, IOUtil.readObject(in));
        }
    }
}

