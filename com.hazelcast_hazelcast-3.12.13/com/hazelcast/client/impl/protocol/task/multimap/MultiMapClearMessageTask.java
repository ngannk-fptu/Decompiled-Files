/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapClearCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAllPartitionsMessageTask;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.operations.MultiMapOperationFactory;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.Map;

public class MultiMapClearMessageTask
extends AbstractAllPartitionsMessageTask<MultiMapClearCodec.RequestParameters> {
    public MultiMapClearMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        return new MultiMapOperationFactory(((MultiMapClearCodec.RequestParameters)this.parameters).name, MultiMapOperationFactory.OperationFactoryType.CLEAR);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        int totalAffectedEntries = 0;
        for (Object affectedEntries : map.values()) {
            totalAffectedEntries += ((Integer)affectedEntries).intValue();
        }
        MultiMapService service = (MultiMapService)this.getService("hz:impl:multiMapService");
        service.publishMultiMapEvent(((MultiMapClearCodec.RequestParameters)this.parameters).name, EntryEventType.CLEAR_ALL, totalAffectedEntries);
        return null;
    }

    @Override
    protected MultiMapClearCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapClearCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapClearCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapClearCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapClearCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "clear";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

