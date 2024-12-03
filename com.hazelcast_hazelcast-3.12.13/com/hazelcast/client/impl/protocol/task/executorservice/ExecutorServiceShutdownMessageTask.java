/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.executorservice;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceShutdownCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.executor.impl.DistributedExecutorService;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class ExecutorServiceShutdownMessageTask
extends AbstractCallableMessageTask<ExecutorServiceShutdownCodec.RequestParameters> {
    public ExecutorServiceShutdownMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        DistributedExecutorService service = (DistributedExecutorService)this.getService("hz:impl:executorService");
        service.shutdownExecutor(((ExecutorServiceShutdownCodec.RequestParameters)this.parameters).name);
        return null;
    }

    @Override
    protected ExecutorServiceShutdownCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ExecutorServiceShutdownCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ExecutorServiceShutdownCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:executorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return null;
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

