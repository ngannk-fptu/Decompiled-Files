/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl.operations;

import com.hazelcast.durableexecutor.impl.DistributedDurableExecutorService;
import com.hazelcast.durableexecutor.impl.DurableExecutorContainer;
import com.hazelcast.durableexecutor.impl.DurableExecutorDataSerializerHook;
import com.hazelcast.durableexecutor.impl.DurableExecutorPartitionContainer;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.AbstractNamedOperation;

abstract class AbstractDurableExecutorOperation
extends AbstractNamedOperation
implements IdentifiedDataSerializable {
    private transient DurableExecutorContainer executorContainer;

    AbstractDurableExecutorOperation() {
    }

    AbstractDurableExecutorOperation(String name) {
        super(name);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:durableExecutorService";
    }

    public DurableExecutorContainer getExecutorContainer() {
        if (this.executorContainer == null) {
            DistributedDurableExecutorService service = (DistributedDurableExecutorService)this.getService();
            DurableExecutorPartitionContainer partitionContainer = service.getPartitionContainer(this.getPartitionId());
            this.executorContainer = partitionContainer.getOrCreateContainer(this.name);
        }
        return this.executorContainer;
    }

    public int getSyncBackupCount() {
        return this.executorContainer.getDurability();
    }

    public int getAsyncBackupCount() {
        return 0;
    }

    public boolean shouldBackup() {
        return true;
    }

    @Override
    public int getFactoryId() {
        return DurableExecutorDataSerializerHook.F_ID;
    }
}

