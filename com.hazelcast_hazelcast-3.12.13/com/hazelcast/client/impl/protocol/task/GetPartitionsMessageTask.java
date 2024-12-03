/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientGetPartitionsCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.PartitionTableView;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import java.security.Permission;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GetPartitionsMessageTask
extends AbstractCallableMessageTask<ClientGetPartitionsCodec.RequestParameters> {
    public GetPartitionsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        InternalPartitionService service = (InternalPartitionService)this.getService("hz:core:partitionService");
        service.firstArrangement();
        PartitionTableView partitionTableView = service.createPartitionTableView();
        int partitionStateVersion = partitionTableView.getVersion();
        Collection<Map.Entry<Address, List<Integer>>> partitions = this.clientEngine.getPartitionListenerService().getPartitions(partitionTableView);
        return ClientGetPartitionsCodec.encodeResponse(partitions, partitionStateVersion);
    }

    @Override
    protected ClientGetPartitionsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientGetPartitionsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return (ClientMessage)response;
    }

    @Override
    public String getServiceName() {
        return "hz:core:partitionService";
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }
}

