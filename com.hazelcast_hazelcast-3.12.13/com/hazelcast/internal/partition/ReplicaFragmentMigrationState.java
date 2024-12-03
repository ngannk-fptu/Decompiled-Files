/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.internal.partition.impl.PartitionDataSerializerHook;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.impl.operationservice.TargetAware;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReplicaFragmentMigrationState
implements IdentifiedDataSerializable,
TargetAware {
    private Map<ServiceNamespace, long[]> namespaces;
    private Collection<Operation> migrationOperations;

    public ReplicaFragmentMigrationState() {
    }

    public ReplicaFragmentMigrationState(Map<ServiceNamespace, long[]> namespaces, Collection<Operation> migrationOperations) {
        this.namespaces = namespaces;
        this.migrationOperations = migrationOperations;
    }

    public Map<ServiceNamespace, long[]> getNamespaceVersionMap() {
        return this.namespaces;
    }

    public Collection<Operation> getMigrationOperations() {
        return this.migrationOperations;
    }

    @Override
    public int getFactoryId() {
        return PartitionDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 17;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.namespaces.size());
        for (Map.Entry<ServiceNamespace, long[]> e : this.namespaces.entrySet()) {
            out.writeObject(e.getKey());
            out.writeLongArray(e.getValue());
        }
        out.writeInt(this.migrationOperations.size());
        for (Operation operation : this.migrationOperations) {
            out.writeObject(operation);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int namespaceSize = in.readInt();
        this.namespaces = new HashMap<ServiceNamespace, long[]>(namespaceSize);
        for (int i = 0; i < namespaceSize; ++i) {
            ServiceNamespace namespace = (ServiceNamespace)in.readObject();
            long[] replicaVersions = in.readLongArray();
            this.namespaces.put(namespace, replicaVersions);
        }
        int migrationOperationSize = in.readInt();
        this.migrationOperations = new ArrayList<Operation>(migrationOperationSize);
        for (int i = 0; i < migrationOperationSize; ++i) {
            Operation migrationOperation = (Operation)in.readObject();
            this.migrationOperations.add(migrationOperation);
        }
    }

    @Override
    public void setTarget(Address address) {
        for (Operation op : this.migrationOperations) {
            if (!(op instanceof TargetAware)) continue;
            ((TargetAware)((Object)op)).setTarget(address);
        }
    }
}

