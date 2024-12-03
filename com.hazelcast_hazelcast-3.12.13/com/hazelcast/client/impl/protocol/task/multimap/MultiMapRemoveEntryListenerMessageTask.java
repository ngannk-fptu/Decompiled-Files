/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapRemoveEntryListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractRemoveListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import java.security.Permission;

public class MultiMapRemoveEntryListenerMessageTask
extends AbstractRemoveListenerMessageTask<MultiMapRemoveEntryListenerCodec.RequestParameters> {
    public MultiMapRemoveEntryListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean deRegisterListener() {
        MultiMapService service = (MultiMapService)this.getService("hz:impl:multiMapService");
        return service.removeListener(((MultiMapRemoveEntryListenerCodec.RequestParameters)this.parameters).name, ((MultiMapRemoveEntryListenerCodec.RequestParameters)this.parameters).registrationId);
    }

    @Override
    protected String getRegistrationId() {
        return ((MultiMapRemoveEntryListenerCodec.RequestParameters)this.parameters).registrationId;
    }

    @Override
    protected MultiMapRemoveEntryListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapRemoveEntryListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapRemoveEntryListenerCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapRemoveEntryListenerCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapRemoveEntryListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "removeEntryListener";
    }
}

