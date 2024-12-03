/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference.operations;

import com.hazelcast.concurrent.atomicreference.AtomicReferenceContainer;
import com.hazelcast.concurrent.atomicreference.AtomicReferenceService;
import com.hazelcast.concurrent.atomicreference.operations.AtomicReferenceBackupAwareOperation;
import com.hazelcast.concurrent.atomicreference.operations.MergeBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;

public class MergeOperation
extends AtomicReferenceBackupAwareOperation {
    private SplitBrainMergePolicy<Object, SplitBrainMergeTypes.AtomicReferenceMergeTypes> mergePolicy;
    private Data mergingValue;
    private transient Data backupValue;

    public MergeOperation() {
    }

    public MergeOperation(String name, SplitBrainMergePolicy<Object, SplitBrainMergeTypes.AtomicReferenceMergeTypes> mergePolicy, Data mergingValue) {
        super(name);
        this.mergePolicy = mergePolicy;
        this.mergingValue = mergingValue;
    }

    @Override
    public void run() throws Exception {
        AtomicReferenceService service = (AtomicReferenceService)this.getService();
        boolean isExistingContainer = service.containsReferenceContainer(this.name);
        AtomicReferenceContainer container = isExistingContainer ? this.getReferenceContainer() : null;
        Data oldValue = isExistingContainer ? container.get() : null;
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        serializationService.getManagedContext().initialize(this.mergePolicy);
        SplitBrainMergeTypes.AtomicReferenceMergeTypes mergeValue = MergingValueFactory.createMergingValue(serializationService, this.mergingValue);
        SplitBrainMergeTypes.AtomicReferenceMergeTypes existingValue = isExistingContainer ? MergingValueFactory.createMergingValue(serializationService, oldValue) : null;
        Object newValue = serializationService.toData(this.mergePolicy.merge(mergeValue, existingValue));
        this.backupValue = this.setNewValue(service, container, oldValue, (Data)newValue);
        this.shouldBackup = this.backupValue == null && oldValue != null || this.backupValue != null && !this.backupValue.equals(oldValue);
    }

    private Data setNewValue(AtomicReferenceService service, AtomicReferenceContainer container, Data oldValue, Data newValue) {
        if (newValue == null) {
            service.destroyDistributedObject(this.name);
            return null;
        }
        if (container == null) {
            container = this.getReferenceContainer();
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
        out.writeData(this.mergingValue);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.mergePolicy = (SplitBrainMergePolicy)in.readObject();
        this.mergingValue = in.readData();
    }
}

