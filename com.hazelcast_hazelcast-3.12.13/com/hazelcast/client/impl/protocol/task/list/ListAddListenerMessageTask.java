/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListAddListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.collection.impl.collection.CollectionEventFilter;
import com.hazelcast.collection.impl.common.DataAwareItemEvent;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import java.security.Permission;

public class ListAddListenerMessageTask
extends AbstractCallableMessageTask<ListAddListenerCodec.RequestParameters>
implements ListenerMessageTask {
    public ListAddListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        Object partitionKey = this.serializationService.toData(((ListAddListenerCodec.RequestParameters)this.parameters).name);
        ItemListener listener = this.createItemListener(this.endpoint, (Data)partitionKey);
        EventService eventService = this.clientEngine.getEventService();
        CollectionEventFilter filter = new CollectionEventFilter(((ListAddListenerCodec.RequestParameters)this.parameters).includeValue);
        EventRegistration registration = ((ListAddListenerCodec.RequestParameters)this.parameters).localOnly ? eventService.registerLocalListener(this.getServiceName(), ((ListAddListenerCodec.RequestParameters)this.parameters).name, filter, listener) : eventService.registerListener(this.getServiceName(), ((ListAddListenerCodec.RequestParameters)this.parameters).name, filter, listener);
        String registrationId = registration.getId();
        this.endpoint.addListenerDestroyAction(this.getServiceName(), ((ListAddListenerCodec.RequestParameters)this.parameters).name, registrationId);
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
                    ClientMessage clientMessage = ListAddListenerCodec.encodeItemEvent(item, event.getMember().getUuid(), event.getEventType().getType());
                    ListAddListenerMessageTask.this.sendClientMessage(partitionKey, clientMessage);
                }
            }
        };
    }

    @Override
    protected ListAddListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListAddListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListAddListenerCodec.encodeResponse((String)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, ((ListAddListenerCodec.RequestParameters)this.parameters).includeValue};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListAddListenerCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getMethodName() {
        return "addItemListener";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListAddListenerCodec.RequestParameters)this.parameters).name;
    }
}

