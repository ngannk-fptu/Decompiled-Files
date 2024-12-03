/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong.operations;

import com.hazelcast.concurrent.atomiclong.AtomicLongContainer;
import com.hazelcast.concurrent.atomiclong.AtomicLongService;
import com.hazelcast.concurrent.atomiclong.operations.AtomicLongBackupAwareOperation;
import com.hazelcast.concurrent.atomiclong.operations.MergeBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;

public class MergeOperation
extends AtomicLongBackupAwareOperation {
    private SplitBrainMergePolicy<Long, SplitBrainMergeTypes.AtomicLongMergeTypes> mergePolicy;
    private long mergingValue;
    private transient Long backupValue;

    public MergeOperation() {
    }

    public MergeOperation(String name, SplitBrainMergePolicy<Long, SplitBrainMergeTypes.AtomicLongMergeTypes> mergePolicy, long mergingValue) {
        super(name);
        this.mergePolicy = mergePolicy;
        this.mergingValue = mergingValue;
    }

    @Override
    public void run() throws Exception {
        AtomicLongService service = (AtomicLongService)this.getService();
        boolean isExistingContainer = service.containsAtomicLong(this.name);
        AtomicLongContainer container = isExistingContainer ? this.getLongContainer() : null;
        Long oldValue = isExistingContainer ? Long.valueOf(container.get()) : null;
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        serializationService.getManagedContext().initialize(this.mergePolicy);
        SplitBrainMergeTypes.AtomicLongMergeTypes mergeValue = MergingValueFactory.createMergingValue(serializationService, this.mergingValue);
        SplitBrainMergeTypes.AtomicLongMergeTypes existingValue = isExistingContainer ? MergingValueFactory.createMergingValue(serializationService, oldValue) : null;
        Long newValue = this.mergePolicy.merge(mergeValue, existingValue);
        this.backupValue = this.setNewValue(service, container, oldValue, newValue);
        this.shouldBackup = this.backupValue == null && oldValue != null || this.backupValue != null && !this.backupValue.equals(oldValue);
    }

    private Long setNewValue(AtomicLongService service, AtomicLongContainer container, Long oldValue, Long newValue) {
        if (newValue == null) {
            service.destroyDistributedObject(this.name);
            return null;
        }
        if (container == null) {
            container = this.getLongContainer();
            container.set(newValue);
            return newValue;
        }
        if (!newValue.equals(oldValue)) {
            container.set(newValue);
            return newValue;
        }
        return oldValue;
    }

    @Override
    public Operation getBackupOperation() {
        return new MergeBackupOperation(this.name, this.backupValue);
    }

    @Override
    public int getId() {
        return 13;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.mergePolicy);
        out.writeLong(this.mergingValue);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.mergePolicy = (SplitBrainMergePolicy)in.readObject();
        this.mergingValue = in.readLong();
    }
}

