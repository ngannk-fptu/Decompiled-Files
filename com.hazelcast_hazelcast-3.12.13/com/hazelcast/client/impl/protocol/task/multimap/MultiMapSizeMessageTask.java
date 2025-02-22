/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapSizeCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.operations.MultiMapOperationFactory;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.util.MapUtil;
import java.security.Permission;
import java.util.Map;

public class MultiMapSizeMessageTask
extends AbstractAllPartitionsMessageTask<MultiMapSizeCodec.RequestParameters> {
    public MultiMapSizeMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        return new MultiMapOperationFactory(((MultiMapSizeCodec.RequestParameters)this.parameters).name, MultiMapOperationFactory.OperationFactoryType.SIZE);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        long total = 0L;
        for (Object obj : map.values()) {
            total += (long)((Integer)obj).intValue();
        }
        return MapUtil.toIntSize(total);
    }

    @Override
    protected MultiMapSizeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapSizeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapSizeCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapSizeCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapSizeCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "size";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

