/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.internal.partition.impl.PartitionDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.ServiceNamespace;
import java.io.IOException;

public final class NonFragmentedServiceNamespace
implements ServiceNamespace,
IdentifiedDataSerializable {
    public static final NonFragmentedServiceNamespace INSTANCE = new NonFragmentedServiceNamespace();

    private NonFragmentedServiceNamespace() {
    }

    @Override
    public String getServiceName() {
        return "hz:default-replica-namespace";
    }

    public boolean equals(Object o) {
        return this == o || o != null && this.getClass() == o.getClass();
    }

    public int hashCode() {
        return this.getServiceName().hashCode();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
    }

    public String toString() {
        return "NonFragmentedServiceNamespace";
    }

    @Override
    public int getFactoryId() {
        return PartitionDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 20;
    }
}

