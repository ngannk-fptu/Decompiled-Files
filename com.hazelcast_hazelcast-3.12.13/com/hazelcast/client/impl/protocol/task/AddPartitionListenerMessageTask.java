/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.ClientPartitionListenerService;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientAddPartitionListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.Connection;
import com.hazelcast.util.UuidUtil;
import java.security.Permission;
import java.util.concurrent.Callable;

public class AddPartitionListenerMessageTask
extends AbstractCallableMessageTask<ClientAddPartitionListenerCodec.RequestParameters> {
    public AddPartitionListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        InternalPartitionService internalPartitionService = (InternalPartitionService)this.getService("hz:core:partitionService");
        internalPartitionService.firstArrangement();
        final ClientPartitionListenerService service = this.clientEngine.getPartitionListenerService();
        service.registerPartitionListener(this.endpoint, this.clientMessage.getCorrelationId());
        this.endpoint.addDestroyAction(UuidUtil.newUnsecureUUID().toString(), new Callable<Boolean>(){

            @Override
            public Boolean call() throws Exception {
                service.deregisterPartitionListener(AddPartitionListenerMessageTask.this.endpoint);
                return Boolean.TRUE;
            }
        });
        return true;
    }

    @Override
    protected ClientAddPartitionListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientAddPartitionListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientAddPartitionListenerCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:core:clusterService";
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

