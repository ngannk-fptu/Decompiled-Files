/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientRemovePartitionLostListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractRemoveListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class RemovePartitionLostListenerMessageTask
extends AbstractRemoveListenerMessageTask<ClientRemovePartitionLostListenerCodec.RequestParameters> {
    public RemovePartitionLostListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean deRegisterListener() {
        InternalPartitionService service = (InternalPartitionService)this.getService("hz:core:partitionService");
        return service.removePartitionLostListener(((ClientRemovePartitionLostListenerCodec.RequestParameters)this.parameters).registrationId);
    }

    @Override
    protected String getRegistrationId() {
        return ((ClientRemovePartitionLostListenerCodec.RequestParameters)this.parameters).registrationId;
    }

    @Override
    protected ClientRemovePartitionLostListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientRemovePartitionLostListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientRemovePartitionLostListenerCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:core:partitionService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return "removePartitionLostListener";
    }
}

