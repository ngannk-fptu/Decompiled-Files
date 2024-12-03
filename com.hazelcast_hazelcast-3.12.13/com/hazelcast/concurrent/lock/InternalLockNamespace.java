/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.ObjectNamespace;
import java.io.IOException;

@SerializableByConvention(value=SerializableByConvention.Reason.PUBLIC_API)
public final class InternalLockNamespace
implements ObjectNamespace {
    private String name;

    public InternalLockNamespace() {
    }

    public InternalLockNamespace(String name) {
        this.name = name;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public String getObjectName() {
        return this.name;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && this.getClass() == o.getClass();
    }

    public int hashCode() {
        return this.getServiceName().hashCode();
    }

    public String toString() {
        return "InternalLockNamespace{service='hz:impl:lockService', objectName=" + this.name + '}';
    }
}

