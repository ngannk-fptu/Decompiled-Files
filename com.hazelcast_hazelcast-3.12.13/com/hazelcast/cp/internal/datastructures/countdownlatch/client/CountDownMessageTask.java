/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPCountDownLatchCountDownCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.CountDownOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CountDownLatchPermission;
import java.security.Permission;

public class CountDownMessageTask
extends AbstractCPMessageTask<CPCountDownLatchCountDownCodec.RequestParameters> {
    public CountDownMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPCountDownLatchCountDownCodec.RequestParameters)this.parameters).groupId, new CountDownOp(((CPCountDownLatchCountDownCodec.RequestParameters)this.parameters).name, ((CPCountDownLatchCountDownCodec.RequestParameters)this.parameters).invocationUid, ((CPCountDownLatchCountDownCodec.RequestParameters)this.parameters).expectedRound));
    }

    @Override
    protected CPCountDownLatchCountDownCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPCountDownLatchCountDownCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPCountDownLatchCountDownCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:raft:countDownLatchService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new CountDownLatchPermission(((CPCountDownLatchCountDownCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPCountDownLatchCountDownCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "countDown";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

