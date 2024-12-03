/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.executorservice.durable;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DurableExecutorIsShutdownCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.durableexecutor.impl.DistributedDurableExecutorService;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class DurableExecutorIsShutdownMessageTask
extends AbstractCallableMessageTask<DurableExecutorIsShutdownCodec.RequestParameters> {
    public DurableExecutorIsShutdownMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        DistributedDurableExecutorService service = (DistributedDurableExecutorService)this.getService("hz:impl:durableExecutorService");
        return service.isShutdown(((DurableExecutorIsShutdownCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected DurableExecutorIsShutdownCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DurableExecutorIsShutdownCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DurableExecutorIsShutdownCodec.encodeResponse((Boolean)response);
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
        return ((DurableExecutorIsShutdownCodec.RequestParameters)this.parameters).name;
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

