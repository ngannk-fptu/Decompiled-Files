/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicRefApplyCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.ApplyOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicReferencePermission;
import java.security.Permission;

public class ApplyMessageTask
extends AbstractCPMessageTask<CPAtomicRefApplyCodec.RequestParameters> {
    public ApplyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        ApplyOp.ReturnValueType returnValueType = ApplyOp.ReturnValueType.fromValue(((CPAtomicRefApplyCodec.RequestParameters)this.parameters).returnValueType);
        this.invoke(((CPAtomicRefApplyCodec.RequestParameters)this.parameters).groupId, new ApplyOp(((CPAtomicRefApplyCodec.RequestParameters)this.parameters).name, ((CPAtomicRefApplyCodec.RequestParameters)this.parameters).function, returnValueType, ((CPAtomicRefApplyCodec.RequestParameters)this.parameters).alter));
    }

    @Override
    protected CPAtomicRefApplyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPAtomicRefApplyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPAtomicRefApplyCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:raft:atomicRefService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((CPAtomicRefApplyCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPAtomicRefApplyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        if (((CPAtomicRefApplyCodec.RequestParameters)this.parameters).alter) {
            if (((CPAtomicRefApplyCodec.RequestParameters)this.parameters).returnValueType == ApplyOp.ReturnValueType.RETURN_OLD_VALUE.value()) {
                return "getAndAlter";
            }
            if (((CPAtomicRefApplyCodec.RequestParameters)this.parameters).returnValueType == ApplyOp.ReturnValueType.RETURN_NEW_VALUE.value()) {
                return "alterAndGet";
            }
            return "alter";
        }
        return "apply";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPAtomicRefApplyCodec.RequestParameters)this.parameters).function};
    }
}

