/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.DefaultObjectNamespace;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import java.io.IOException;

public final class DistributedObjectNamespace
extends DefaultObjectNamespace
implements ObjectNamespace,
IdentifiedDataSerializable {
    public DistributedObjectNamespace() {
    }

    public DistributedObjectNamespace(String serviceName, String objectName) {
        super(serviceName, objectName);
    }

    public DistributedObjectNamespace(ObjectNamespace namespace) {
        super(namespace.getServiceName(), namespace.getObjectName());
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.service);
        out.writeUTF(this.objectName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.service = in.readUTF();
        this.objectName = in.readUTF();
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 20;
    }
}

