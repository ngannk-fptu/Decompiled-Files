/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.proxyservice.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ProxyService;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import java.io.IOException;

public class InitializeDistributedObjectOperation
extends Operation
implements IdentifiedDataSerializable {
    private String serviceName;
    private String name;

    public InitializeDistributedObjectOperation() {
    }

    public InitializeDistributedObjectOperation(String serviceName, String name) {
        this.serviceName = serviceName;
        this.name = name;
    }

    @Override
    public void run() throws Exception {
        ProxyService proxyService = this.getNodeEngine().getProxyService();
        proxyService.initializeDistributedObject(this.serviceName, this.name);
    }

    @Override
    public String getServiceName() {
        return this.serviceName;
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.serviceName);
        out.writeObject(this.name);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.serviceName = in.readUTF();
        this.name = (String)in.readObject();
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 14;
    }
}

