/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.executorservice;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceIsShutdownCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.executor.impl.DistributedExecutorService;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class ExecutorServiceIsShutdownMessageTask
extends AbstractCallableMessageTask<ExecutorServiceIsShutdownCodec.RequestParameters> {
    public ExecutorServiceIsShutdownMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        DistributedExecutorService service = (DistributedExecutorService)this.getService("hz:impl:executorService");
        return service.isShutdown(((ExecutorServiceIsShutdownCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected ExecutorServiceIsShutdownCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ExecutorServiceIsShutdownCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ExecutorServiceIsShutdownCodec.encodeResponse((Boolean)response);
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
        return "isShutdown";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

