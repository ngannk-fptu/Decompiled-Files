/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPCountDownLatchTrySetCountCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.TrySetCountOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CountDownLatchPermission;
import java.security.Permission;

public class TrySetCountMessageTask
extends AbstractCPMessageTask<CPCountDownLatchTrySetCountCodec.RequestParameters> {
    public TrySetCountMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPCountDownLatchTrySetCountCodec.RequestParameters)this.parameters).groupId, new TrySetCountOp(((CPCountDownLatchTrySetCountCodec.RequestParameters)this.parameters).name, ((CPCountDownLatchTrySetCountCodec.RequestParameters)this.parameters).count));
    }

    @Override
    protected CPCountDownLatchTrySetCountCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPCountDownLatchTrySetCountCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPCountDownLatchTrySetCountCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:countDownLatchService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new CountDownLatchPermission(((CPCountDownLatchTrySetCountCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPCountDownLatchTrySetCountCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "trySetCount";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPCountDownLatchTrySetCountCodec.RequestParameters)this.parameters).count};
    }
}

