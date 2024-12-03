/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.flakeidgen.impl;

import com.hazelcast.flakeidgen.impl.FlakeIdGeneratorDataSerializerHook;
import com.hazelcast.flakeidgen.impl.FlakeIdGeneratorProxy;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

class NewIdBatchOperation
extends Operation
implements IdentifiedDataSerializable {
    private String flakeIdGenName;
    private int batchSize;

    NewIdBatchOperation() {
    }

    NewIdBatchOperation(String genName, int batchSize) {
        this.flakeIdGenName = genName;
        this.batchSize = batchSize;
    }

    @Override
    public void run() throws Exception {
        FlakeIdGeneratorProxy proxy = (FlakeIdGeneratorProxy)this.getNodeEngine().getProxyService().getDistributedObject(this.getServiceName(), this.flakeIdGenName);
        final FlakeIdGeneratorProxy.IdBatchAndWaitTime result = proxy.newIdBaseLocal(this.batchSize);
        if (result.waitTimeMillis == 0L) {
            this.sendResponse(result.idBatch.base());
        } else {
            this.getNodeEngine().getExecutionService().schedule(new Runnable(){

                @Override
                public void run() {
                    NewIdBatchOperation.this.sendResponse(result.idBatch.base());
                }
            }, result.waitTimeMillis, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:flakeIdGeneratorService";
    }

    @Override
    public int getFactoryId() {
        return FlakeIdGeneratorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.flakeIdGenName = in.readUTF();
        this.batchSize = in.readInt();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.flakeIdGenName);
        out.writeInt(this.batchSize);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", flakeIdGenName=").append(this.flakeIdGenName);
        sb.append(", batchSize=").append(this.batchSize);
    }
}

