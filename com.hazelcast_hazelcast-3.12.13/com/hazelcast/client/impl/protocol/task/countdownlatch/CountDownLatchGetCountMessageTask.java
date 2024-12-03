/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.countdownlatch;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CountDownLatchGetCountCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.countdownlatch.operations.GetCountOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CountDownLatchPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class CountDownLatchGetCountMessageTask
extends AbstractPartitionMessageTask<CountDownLatchGetCountCodec.RequestParameters> {
    public CountDownLatchGetCountMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new GetCountOperation(((CountDownLatchGetCountCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected CountDownLatchGetCountCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CountDownLatchGetCountCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CountDownLatchGetCountCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:countDownLatchService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new CountDownLatchPermission(((CountDownLatchGetCountCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CountDownLatchGetCountCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getCount";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

