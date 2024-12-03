/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientAddDistributedObjectListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.ProxyService;
import java.security.Permission;
import java.util.concurrent.Callable;

public class AddDistributedObjectListenerMessageTask
extends AbstractCallableMessageTask<ClientAddDistributedObjectListenerCodec.RequestParameters>
implements DistributedObjectListener {
    public AddDistributedObjectListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        final ProxyService proxyService = this.clientEngine.getProxyService();
        final String registrationId = proxyService.addProxyListener(this);
        this.endpoint.addDestroyAction(registrationId, new Callable(){

            public Boolean call() {
                return proxyService.removeProxyListener(registrationId);
            }
        });
        return registrationId;
    }

    @Override
    protected ClientAddDistributedObjectListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientAddDistributedObjectListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientAddDistributedObjectListenerCodec.encodeResponse((String)response);
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
        return "addDistributedObjectListener";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public void distributedObjectCreated(DistributedObjectEvent event) {
        this.send(event);
    }

    @Override
    public void distributedObjectDestroyed(DistributedObjectEvent event) {
        this.send(event);
    }

    private void send(DistributedObjectEvent event) {
        if (!this.shouldSendEvent()) {
            return;
        }
        String name = (String)event.getObjectName();
        String serviceName = event.getServiceName();
        ClientMessage eventMessage = ClientAddDistributedObjectListenerCodec.encodeDistributedObjectEvent(name, serviceName, event.getEventType().name());
        this.sendClientMessage(null, eventMessage);
    }

    private boolean shouldSendEvent() {
        if (!this.endpoint.isAlive()) {
            return false;
        }
        ClusterService clusterService = this.clientEngine.getClusterService();
        boolean currentMemberIsMaster = clusterService.isMaster();
        return !((ClientAddDistributedObjectListenerCodec.RequestParameters)this.parameters).localOnly || currentMemberIsMaster;
    }
}

