/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.crdt.pncounter;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.PNCounterGetConfiguredReplicaCountCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.PNCounterPermission;
import java.security.Permission;

public class PNCounterGetConfiguredReplicaCountMessageTask
extends AbstractCallableMessageTask<PNCounterGetConfiguredReplicaCountCodec.RequestParameters> {
    public PNCounterGetConfiguredReplicaCountMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected PNCounterGetConfiguredReplicaCountCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return PNCounterGetConfiguredReplicaCountCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return PNCounterGetConfiguredReplicaCountCodec.encodeResponse((Integer)response);
    }

    @Override
    protected Object call() throws Exception {
        return this.nodeEngine.getConfig().findPNCounterConfig(((PNCounterGetConfiguredReplicaCountCodec.RequestParameters)this.parameters).name).getReplicaCount();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:PNCounterService";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return new PNCounterPermission(((PNCounterGetConfiguredReplicaCountCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((PNCounterGetConfiguredReplicaCountCodec.RequestParameters)this.parameters).name;
    }
}

