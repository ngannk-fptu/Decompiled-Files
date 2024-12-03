/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapRemoveEntryListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractRemoveListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.replicatedmap.impl.ReplicatedMapEventPublishingService;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.security.permission.ReplicatedMapPermission;
import java.security.Permission;

public class ReplicatedMapRemoveEntryListenerMessageTask
extends AbstractRemoveListenerMessageTask<ReplicatedMapRemoveEntryListenerCodec.RequestParameters> {
    public ReplicatedMapRemoveEntryListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean deRegisterListener() {
        ReplicatedMapService service = (ReplicatedMapService)this.getService("hz:impl:replicatedMapService");
        ReplicatedMapEventPublishingService eventPublishingService = service.getEventPublishingService();
        return eventPublishingService.removeEventListener(((ReplicatedMapRemoveEntryListenerCodec.RequestParameters)this.parameters).name, ((ReplicatedMapRemoveEntryListenerCodec.RequestParameters)this.parameters).registrationId);
    }

    @Override
    protected String getRegistrationId() {
        return ((ReplicatedMapRemoveEntryListenerCodec.RequestParameters)this.parameters).registrationId;
    }

    @Override
    protected ReplicatedMapRemoveEntryListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapRemoveEntryListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ReplicatedMapRemoveEntryListenerCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapRemoveEntryListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "removeEntryListener";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ReplicatedMapPermission(((ReplicatedMapRemoveEntryListenerCodec.RequestParameters)this.parameters).name, "listen");
    }
}

