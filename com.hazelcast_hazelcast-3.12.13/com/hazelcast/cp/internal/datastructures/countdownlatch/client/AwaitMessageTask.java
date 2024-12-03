/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPCountDownLatchAwaitCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.AwaitOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CountDownLatchPermission;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class AwaitMessageTask
extends AbstractCPMessageTask<CPCountDownLatchAwaitCodec.RequestParameters> {
    public AwaitMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPCountDownLatchAwaitCodec.RequestParameters)this.parameters).groupId, new AwaitOp(((CPCountDownLatchAwaitCodec.RequestParameters)this.parameters).name, ((CPCountDownLatchAwaitCodec.RequestParameters)this.parameters).invocationUid, ((CPCountDownLatchAwaitCodec.RequestParameters)this.parameters).timeoutMs));
    }

    @Override
    protected CPCountDownLatchAwaitCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPCountDownLatchAwaitCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPCountDownLatchAwaitCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:countDownLatchService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new CountDownLatchPermission(((CPCountDownLatchAwaitCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPCountDownLatchAwaitCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "await";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPCountDownLatchAwaitCodec.RequestParameters)this.parameters).timeoutMs, TimeUnit.MILLISECONDS};
    }
}

