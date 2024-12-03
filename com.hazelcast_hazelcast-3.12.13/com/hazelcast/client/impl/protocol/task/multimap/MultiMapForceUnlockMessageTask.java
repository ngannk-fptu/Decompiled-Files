/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapForceUnlockCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.operations.UnlockOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MultiMapForceUnlockMessageTask
extends AbstractPartitionMessageTask<MultiMapForceUnlockCodec.RequestParameters> {
    public MultiMapForceUnlockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        DistributedObjectNamespace namespace = new DistributedObjectNamespace("hz:impl:multiMapService", ((MultiMapForceUnlockCodec.RequestParameters)this.parameters).name);
        return new UnlockOperation((ObjectNamespace)namespace, ((MultiMapForceUnlockCodec.RequestParameters)this.parameters).key, -1L, true, ((MultiMapForceUnlockCodec.RequestParameters)this.parameters).referenceId);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapForceUnlockCodec.encodeResponse();
    }

    @Override
    protected MultiMapForceUnlockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapForceUnlockCodec.decodeRequest(clientMessage);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public String getDistributedObjectType() {
        return "hz:impl:multiMapService";
    }

    @Override
    public String getMethodName() {
        return "forceUnlock";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MultiMapForceUnlockCodec.RequestParameters)this.parameters).key};
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapForceUnlockCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapForceUnlockCodec.RequestParameters)this.parameters).name;
    }
}

