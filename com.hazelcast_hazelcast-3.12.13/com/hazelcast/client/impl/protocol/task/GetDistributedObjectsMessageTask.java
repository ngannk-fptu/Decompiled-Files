/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.client.DistributedObjectInfo;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientGetDistributedObjectsCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectUtil;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetDistributedObjectsMessageTask
extends AbstractCallableMessageTask<ClientGetDistributedObjectsCodec.RequestParameters> {
    public GetDistributedObjectsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        Collection<DistributedObject> distributedObjects = this.clientEngine.getProxyService().getAllDistributedObjects();
        ArrayList<DistributedObjectInfo> coll = new ArrayList<DistributedObjectInfo>(distributedObjects.size());
        for (DistributedObject distributedObject : distributedObjects) {
            String name = DistributedObjectUtil.getName(distributedObject);
            coll.add(new DistributedObjectInfo(distributedObject.getServiceName(), name));
        }
        return coll;
    }

    @Override
    protected ClientGetDistributedObjectsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientGetDistributedObjectsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientGetDistributedObjectsCodec.encodeResponse((List)response);
    }

    @Override
    public String getServiceName() {
        return "hz:core:proxyService";
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
        return "getDistributedObjects";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

