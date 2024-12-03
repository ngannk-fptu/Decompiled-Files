/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPCountDownLatchGetRoundCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.GetCountOp;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CountDownLatchPermission;
import java.security.Permission;

public class GetCountMessageTask
extends AbstractCPMessageTask<CPCountDownLatchGetRoundCodec.RequestParameters> {
    public GetCountMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.query(((CPCountDownLatchGetRoundCodec.RequestParameters)this.parameters).groupId, new GetCountOp(((CPCountDownLatchGetRoundCodec.RequestParameters)this.parameters).name), QueryPolicy.LINEARIZABLE);
    }

    @Override
    protected CPCountDownLatchGetRoundCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPCountDownLatchGetRoundCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPCountDownLatchGetRoundCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:countDownLatchService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new CountDownLatchPermission(((CPCountDownLatchGetRoundCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPCountDownLatchGetRoundCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getCount";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

