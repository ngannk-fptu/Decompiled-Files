/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongGetAndSetCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.GetAndSetOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import java.security.Permission;

public class GetAndSetMessageTask
extends AbstractCPMessageTask<CPAtomicLongGetAndSetCodec.RequestParameters> {
    public GetAndSetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPAtomicLongGetAndSetCodec.RequestParameters)this.parameters).groupId, new GetAndSetOp(((CPAtomicLongGetAndSetCodec.RequestParameters)this.parameters).name, ((CPAtomicLongGetAndSetCodec.RequestParameters)this.parameters).newValue));
    }

    @Override
    protected CPAtomicLongGetAndSetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPAtomicLongGetAndSetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPAtomicLongGetAndSetCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((CPAtomicLongGetAndSetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPAtomicLongGetAndSetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getAndSet";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPAtomicLongGetAndSetCodec.RequestParameters)this.parameters).newValue};
    }
}

