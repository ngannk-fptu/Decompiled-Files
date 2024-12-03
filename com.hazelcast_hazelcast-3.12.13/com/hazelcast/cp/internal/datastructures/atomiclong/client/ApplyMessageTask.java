/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongApplyCodec;
import com.hazelcast.core.IFunction;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.ApplyOp;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import java.security.Permission;

public class ApplyMessageTask
extends AbstractCPMessageTask<CPAtomicLongApplyCodec.RequestParameters> {
    public ApplyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        IFunction function = (IFunction)this.serializationService.toObject(((CPAtomicLongApplyCodec.RequestParameters)this.parameters).function);
        this.query(((CPAtomicLongApplyCodec.RequestParameters)this.parameters).groupId, new ApplyOp(((CPAtomicLongApplyCodec.RequestParameters)this.parameters).name, function), QueryPolicy.LINEARIZABLE);
    }

    @Override
    protected CPAtomicLongApplyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPAtomicLongApplyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPAtomicLongApplyCodec.encodeResponse(this.nodeEngine.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:raft:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((CPAtomicLongApplyCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPAtomicLongApplyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "apply";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPAtomicLongApplyCodec.RequestParameters)this.parameters).function};
    }
}

