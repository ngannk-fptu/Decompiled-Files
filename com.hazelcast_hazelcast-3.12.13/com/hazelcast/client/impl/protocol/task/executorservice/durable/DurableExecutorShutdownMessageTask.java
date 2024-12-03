/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.executorservice.durable;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DurableExecutorShutdownCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.durableexecutor.impl.DistributedDurableExecutorService;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class DurableExecutorShutdownMessageTask
extends AbstractCallableMessageTask<DurableExecutorShutdownCodec.RequestParameters> {
    public DurableExecutorShutdownMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        DistributedDurableExecutorService service = (DistributedDurableExecutorService)this.getService("hz:impl:durableExecutorService");
        service.shutdownExecutor(((DurableExecutorShutdownCodec.RequestParameters)this.parameters).name);
        return null;
    }

    @Override
    protected DurableExecutorShutdownCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DurableExecutorShutdownCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DurableExecutorShutdownCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:durableExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((DurableExecutorShutdownCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "shutdown";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

