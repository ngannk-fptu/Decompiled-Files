/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.flakeidgen.impl.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.FlakeIdGeneratorNewIdBatchCodec;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.flakeidgen.impl.FlakeIdGeneratorProxy;
import com.hazelcast.flakeidgen.impl.IdBatch;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.FlakeIdGeneratorPermission;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class NewIdBatchMessageTask
extends AbstractMessageTask<FlakeIdGeneratorNewIdBatchCodec.RequestParameters>
implements BlockingMessageTask {
    public NewIdBatchMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected FlakeIdGeneratorNewIdBatchCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return FlakeIdGeneratorNewIdBatchCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        IdBatch idBatch = (IdBatch)response;
        return FlakeIdGeneratorNewIdBatchCodec.encodeResponse(idBatch.base(), idBatch.increment(), idBatch.batchSize());
    }

    @Override
    protected void processMessage() {
        FlakeIdGeneratorProxy proxy = (FlakeIdGeneratorProxy)this.nodeEngine.getProxyService().getDistributedObject(this.getServiceName(), ((FlakeIdGeneratorNewIdBatchCodec.RequestParameters)this.parameters).name);
        final FlakeIdGeneratorProxy.IdBatchAndWaitTime result = proxy.newIdBatch(((FlakeIdGeneratorNewIdBatchCodec.RequestParameters)this.parameters).batchSize);
        if (result.waitTimeMillis == 0L) {
            this.sendResponse(result.idBatch);
        } else {
            this.nodeEngine.getExecutionService().schedule(new Runnable(){

                @Override
                public void run() {
                    NewIdBatchMessageTask.this.sendResponse(result.idBatch);
                }
            }, result.waitTimeMillis, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:flakeIdGeneratorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new FlakeIdGeneratorPermission(((FlakeIdGeneratorNewIdBatchCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((FlakeIdGeneratorNewIdBatchCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "newIdBatch";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

