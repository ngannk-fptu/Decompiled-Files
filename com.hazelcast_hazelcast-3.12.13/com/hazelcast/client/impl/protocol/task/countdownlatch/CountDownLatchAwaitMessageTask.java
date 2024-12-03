/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.countdownlatch;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CountDownLatchAwaitCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.countdownlatch.operations.AwaitOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CountDownLatchPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class CountDownLatchAwaitMessageTask
extends AbstractPartitionMessageTask<CountDownLatchAwaitCodec.RequestParameters> {
    public CountDownLatchAwaitMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new AwaitOperation(((CountDownLatchAwaitCodec.RequestParameters)this.parameters).name, ((CountDownLatchAwaitCodec.RequestParameters)this.parameters).timeout);
    }

    @Override
    protected CountDownLatchAwaitCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CountDownLatchAwaitCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CountDownLatchAwaitCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:countDownLatchService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new CountDownLatchPermission(((CountDownLatchAwaitCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CountDownLatchAwaitCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "await";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CountDownLatchAwaitCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS};
    }
}

