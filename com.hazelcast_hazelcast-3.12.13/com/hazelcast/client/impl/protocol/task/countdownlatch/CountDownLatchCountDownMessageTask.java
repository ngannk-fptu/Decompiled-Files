/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.countdownlatch;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CountDownLatchCountDownCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.countdownlatch.operations.CountDownOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CountDownLatchPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class CountDownLatchCountDownMessageTask
extends AbstractPartitionMessageTask<CountDownLatchCountDownCodec.RequestParameters> {
    public CountDownLatchCountDownMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CountDownOperation(((CountDownLatchCountDownCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected CountDownLatchCountDownCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CountDownLatchCountDownCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CountDownLatchCountDownCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:countDownLatchService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new CountDownLatchPermission(((CountDownLatchCountDownCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CountDownLatchCountDownCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "countDown";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

