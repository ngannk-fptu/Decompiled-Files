/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientAddPartitionLostListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.partition.PartitionLostEvent;
import com.hazelcast.partition.PartitionLostListener;
import com.hazelcast.spi.partition.IPartitionService;
import java.security.Permission;

public class AddPartitionLostListenerMessageTask
extends AbstractCallableMessageTask<ClientAddPartitionLostListenerCodec.RequestParameters>
implements ListenerMessageTask {
    public AddPartitionLostListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        IPartitionService partitionService = (IPartitionService)this.getService(this.getServiceName());
        PartitionLostListener listener = new PartitionLostListener(){

            @Override
            public void partitionLost(PartitionLostEvent event) {
                if (AddPartitionLostListenerMessageTask.this.endpoint.isAlive()) {
                    ClientMessage eventMessage = ClientAddPartitionLostListenerCodec.encodePartitionLostEvent(event.getPartitionId(), event.getLostBackupCount(), event.getEventSource());
                    AddPartitionLostListenerMessageTask.this.sendClientMessage(null, eventMessage);
                }
            }
        };
        String registrationId = ((ClientAddPartitionLostListenerCodec.RequestParameters)this.parameters).localOnly ? partitionService.addLocalPartitionLostListener(listener) : partitionService.addPartitionLostListener(listener);
        this.endpoint.addListenerDestroyAction(this.getServiceName(), ".partitionLost", registrationId);
        return registrationId;
    }

    @Override
    protected ClientAddPartitionLostListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientAddPartitionLostListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientAddPartitionLostListenerCodec.encodeResponse((String)response);
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
        return "addPartitionLostListener";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

