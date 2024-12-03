/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongAddAndGetCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.AddAndGetOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import java.security.Permission;

public class AddAndGetMessageTask
extends AbstractCPMessageTask<CPAtomicLongAddAndGetCodec.RequestParameters> {
    public AddAndGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPAtomicLongAddAndGetCodec.RequestParameters)this.parameters).groupId, new AddAndGetOp(((CPAtomicLongAddAndGetCodec.RequestParameters)this.parameters).name, ((CPAtomicLongAddAndGetCodec.RequestParameters)this.parameters).delta));
    }

    @Override
    public String getServiceName() {
        return "hz:raft:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((CPAtomicLongAddAndGetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPAtomicLongAddAndGetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "addAndGet";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPAtomicLongAddAndGetCodec.RequestParameters)this.parameters).delta};
    }

    @Override
    protected CPAtomicLongAddAndGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPAtomicLongAddAndGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPAtomicLongAddAndGetCodec.encodeResponse((Long)response);
    }
}

