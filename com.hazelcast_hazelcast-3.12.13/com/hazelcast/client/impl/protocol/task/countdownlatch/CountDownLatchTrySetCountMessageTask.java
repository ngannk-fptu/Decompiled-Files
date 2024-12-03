/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.countdownlatch;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CountDownLatchTrySetCountCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.countdownlatch.operations.SetCountOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CountDownLatchPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class CountDownLatchTrySetCountMessageTask
extends AbstractPartitionMessageTask<CountDownLatchTrySetCountCodec.RequestParameters> {
    public CountDownLatchTrySetCountMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new SetCountOperation(((CountDownLatchTrySetCountCodec.RequestParameters)this.parameters).name, ((CountDownLatchTrySetCountCodec.RequestParameters)this.parameters).count);
    }

    @Override
    protected CountDownLatchTrySetCountCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CountDownLatchTrySetCountCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CountDownLatchTrySetCountCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:countDownLatchService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new CountDownLatchPermission(((CountDownLatchTrySetCountCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CountDownLatchTrySetCountCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "trySetCount";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CountDownLatchTrySetCountCodec.RequestParameters)this.parameters).count};
    }
}

