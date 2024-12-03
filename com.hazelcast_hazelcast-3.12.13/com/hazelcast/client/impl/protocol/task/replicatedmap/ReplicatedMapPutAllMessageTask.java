/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapPutAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapEntries;
import com.hazelcast.replicatedmap.impl.operation.PutAllOperationFactory;
import com.hazelcast.security.permission.ReplicatedMapPermission;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.MapUtil;
import java.security.Permission;
import java.util.Map;

public class ReplicatedMapPutAllMessageTask
extends AbstractAllPartitionsMessageTask<ReplicatedMapPutAllCodec.RequestParameters> {
    public ReplicatedMapPutAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        return new PutAllOperationFactory(((ReplicatedMapPutAllCodec.RequestParameters)this.parameters).name, new ReplicatedMapEntries(((ReplicatedMapPutAllCodec.RequestParameters)this.parameters).entries));
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        for (Map.Entry<Integer, Object> entry : map.entrySet()) {
            Object result = this.serializationService.toObject(entry.getValue());
            if (!(result instanceof Throwable)) continue;
            throw ExceptionUtil.rethrow((Throwable)result);
        }
        return null;
    }

    @Override
    protected ReplicatedMapPutAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapPutAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ReplicatedMapPutAllCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapPutAllCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "putAll";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ReplicatedMapPermission(((ReplicatedMapPutAllCodec.RequestParameters)this.parameters).name, "put");
    }

    @Override
    public Object[] getParameters() {
        Map<Data, Data> map = MapUtil.createHashMap(((ReplicatedMapPutAllCodec.RequestParameters)this.parameters).entries.size());
        for (Map.Entry<Data, Data> entry : ((ReplicatedMapPutAllCodec.RequestParameters)this.parameters).entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return new Object[]{map};
    }
}

