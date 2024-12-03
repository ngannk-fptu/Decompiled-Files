/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.LockDataSerializerHook;
import com.hazelcast.concurrent.lock.LockServiceImpl;
import com.hazelcast.concurrent.lock.LockStoreContainer;
import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ServiceNamespace;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class LockReplicationOperation
extends Operation
implements IdentifiedDataSerializable,
Versioned {
    private final Collection<LockStoreImpl> locks = new LinkedList<LockStoreImpl>();

    public LockReplicationOperation() {
    }

    public LockReplicationOperation(LockStoreContainer container, int partitionId, int replicaIndex) {
        this(container, partitionId, replicaIndex, container.getAllNamespaces(replicaIndex));
    }

    public LockReplicationOperation(LockStoreContainer container, int partitionId, int replicaIndex, Collection<ServiceNamespace> namespaces) {
        this.setPartitionId(partitionId).setReplicaIndex(replicaIndex);
        for (ServiceNamespace namespace : namespaces) {
            LockStoreImpl ls = container.getLockStore((ObjectNamespace)namespace);
            if (ls == null || ls.getTotalBackupCount() < replicaIndex) continue;
            this.locks.add(ls);
        }
    }

    @Override
    public void run() {
        LockServiceImpl lockService = (LockServiceImpl)this.getService();
        LockStoreContainer container = lockService.getLockContainer(this.getPartitionId());
        for (LockStoreImpl ls : this.locks) {
            container.put(ls);
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    public boolean isEmpty() {
        return this.locks.isEmpty();
    }

    @Override
    public int getFactoryId() {
        return LockDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        int len = this.locks.size();
        out.writeInt(len);
        if (len > 0) {
            for (LockStoreImpl ls : this.locks) {
                ls.writeData(out);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int len = in.readInt();
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                LockStoreImpl ls = new LockStoreImpl();
                ls.readData(in);
                this.locks.add(ls);
            }
        }
    }
}

