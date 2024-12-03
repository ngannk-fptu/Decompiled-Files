/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapContainsValueCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.operations.MultiMapOperationFactory;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.Map;

public class MultiMapContainsValueMessageTask
extends AbstractAllPartitionsMessageTask<MultiMapContainsValueCodec.RequestParameters> {
    public MultiMapContainsValueMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        return new MultiMapOperationFactory(((MultiMapContainsValueCodec.RequestParameters)this.parameters).name, MultiMapOperationFactory.OperationFactoryType.CONTAINS, null, ((MultiMapContainsValueCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        boolean found = false;
        for (Object obj : map.values()) {
            if (!Boolean.TRUE.equals(obj)) continue;
            found = true;
        }
        return found;
    }

    @Override
    protected MultiMapContainsValueCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapContainsValueCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapContainsValueCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapContainsValueCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapContainsValueCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "containsValue";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MultiMapContainsValueCodec.RequestParameters)this.parameters).value};
    }
}

