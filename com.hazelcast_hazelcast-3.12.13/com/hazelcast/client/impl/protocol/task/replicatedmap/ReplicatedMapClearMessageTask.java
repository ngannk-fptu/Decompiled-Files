/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapClearCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.replicatedmap.impl.ReplicatedMapEventPublishingService;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.ClearOperationFactory;
import com.hazelcast.security.permission.ReplicatedMapPermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.Map;

public class ReplicatedMapClearMessageTask
extends AbstractAllPartitionsMessageTask<ReplicatedMapClearCodec.RequestParameters> {
    public ReplicatedMapClearMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        return new ClearOperationFactory(((ReplicatedMapClearCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        int deletedEntrySize = 0;
        for (Object deletedEntryPerPartition : map.values()) {
            deletedEntrySize += ((Integer)deletedEntryPerPartition).intValue();
        }
        ReplicatedMapService service = (ReplicatedMapService)this.getService(this.getServiceName());
        ReplicatedMapEventPublishingService eventPublishingService = service.getEventPublishingService();
        eventPublishingService.fireMapClearedEvent(deletedEntrySize, this.getDistributedObjectName());
        return null;
    }

    @Override
    protected ReplicatedMapClearCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapClearCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ReplicatedMapClearCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapClearCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "clear";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ReplicatedMapPermission(((ReplicatedMapClearCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

