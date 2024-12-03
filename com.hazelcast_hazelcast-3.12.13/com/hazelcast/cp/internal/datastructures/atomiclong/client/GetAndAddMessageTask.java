/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongGetAndAddCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.GetAndAddOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import java.security.Permission;

public class GetAndAddMessageTask
extends AbstractCPMessageTask<CPAtomicLongGetAndAddCodec.RequestParameters> {
    public GetAndAddMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPAtomicLongGetAndAddCodec.RequestParameters)this.parameters).groupId, new GetAndAddOp(((CPAtomicLongGetAndAddCodec.RequestParameters)this.parameters).name, ((CPAtomicLongGetAndAddCodec.RequestParameters)this.parameters).delta));
    }

    @Override
    protected CPAtomicLongGetAndAddCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPAtomicLongGetAndAddCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPAtomicLongGetAndAddCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((CPAtomicLongGetAndAddCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPAtomicLongGetAndAddCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getAndAdd";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPAtomicLongGetAndAddCodec.RequestParameters)this.parameters).delta};
    }
}

