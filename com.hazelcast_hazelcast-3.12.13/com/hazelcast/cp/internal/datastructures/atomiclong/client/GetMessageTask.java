/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongGetCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.GetAndAddOp;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import java.security.Permission;

public class GetMessageTask
extends AbstractCPMessageTask<CPAtomicLongGetCodec.RequestParameters> {
    public GetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.query(((CPAtomicLongGetCodec.RequestParameters)this.parameters).groupId, new GetAndAddOp(((CPAtomicLongGetCodec.RequestParameters)this.parameters).name, 0L), QueryPolicy.LINEARIZABLE);
    }

    @Override
    protected CPAtomicLongGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPAtomicLongGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPAtomicLongGetCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((CPAtomicLongGetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPAtomicLongGetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "get";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

