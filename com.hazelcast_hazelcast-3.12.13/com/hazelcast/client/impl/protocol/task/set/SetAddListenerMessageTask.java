/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.set;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SetAddListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.collection.impl.collection.CollectionEventFilter;
import com.hazelcast.collection.impl.common.DataAwareItemEvent;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import java.security.Permission;

public class SetAddListenerMessageTask
extends AbstractCallableMessageTask<SetAddListenerCodec.RequestParameters>
implements ListenerMessageTask {
    public SetAddListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        Object partitionKey = this.serializationService.toData(((SetAddListenerCodec.RequestParameters)this.parameters).name);
        ItemListener listener = this.createItemListener(this.endpoint, (Data)partitionKey);
        EventService eventService = this.clientEngine.getEventService();
        CollectionEventFilter filter = new CollectionEventFilter(((SetAddListenerCodec.RequestParameters)this.parameters).includeValue);
        EventRegistration registration = ((SetAddListenerCodec.RequestParameters)this.parameters).localOnly ? eventService.registerLocalListener(this.getServiceName(), ((SetAddListenerCodec.RequestParameters)this.parameters).name, filter, listener) : eventService.registerListener(this.getServiceName(), ((SetAddListenerCodec.RequestParameters)this.parameters).name, filter, listener);
        String registrationId = registration.getId();
        this.endpoint.addListenerDestroyAction(this.getServiceName(), ((SetAddListenerCodec.RequestParameters)this.parameters).name, registrationId);
        return registrationId;
    }

    private ItemListener createItemListener(final ClientEndpoint endpoint, final Data partitionKey) {
        return new ItemListener(){

            public void itemAdded(ItemEvent item) {
                this.send(item);
            }

            public void itemRemoved(ItemEvent item) {
                this.send(item);
            }

            private void send(ItemEvent event) {
                if (endpoint.isAlive()) {
                    if (!(event instanceof DataAwareItemEvent)) {
                        throw new IllegalArgumentException("Expecting: DataAwareItemEvent, Found: " + event.getClass().getSimpleName());
                    }
                    DataAwareItemEvent dataAwareItemEvent = (DataAwareItemEvent)event;
                    Data item = dataAwareItemEvent.getItemData();
                    ClientMessage clientMessage = SetAddListenerCodec.encodeItemEvent(item, event.getMember().getUuid(), event.getEventType().getType());
                    SetAddListenerMessageTask.this.sendClientMessage(partitionKey, clientMessage);
                }
            }
        };
    }

    @Override
    protected SetAddListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SetAddListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SetAddListenerCodec.encodeResponse((String)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, ((SetAddListenerCodec.RequestParameters)this.parameters).includeValue};
    }

    @Override
    public Permission getRequiredPermission() {
        return new SetPermission(((SetAddListenerCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getMethodName() {
        return "addItemListener";
    }

    @Override
    public String getDistributedObjectName() {
        return ((SetAddListenerCodec.RequestParameters)this.parameters).name;
    }
}

