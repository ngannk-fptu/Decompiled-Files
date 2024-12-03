/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueAddListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.collection.impl.common.DataAwareItemEvent;
import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.QueuePermission;
import java.security.Permission;

public class QueueAddListenerMessageTask
extends AbstractCallableMessageTask<QueueAddListenerCodec.RequestParameters>
implements ListenerMessageTask {
    public QueueAddListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        QueueService service = (QueueService)this.getService("hz:impl:queueService");
        Object partitionKey = this.serializationService.toData(((QueueAddListenerCodec.RequestParameters)this.parameters).name);
        ItemListener listener = new ItemListener((Data)partitionKey){
            final /* synthetic */ Data val$partitionKey;
            {
                this.val$partitionKey = data;
            }

            public void itemAdded(ItemEvent item) {
                this.send(item);
            }

            public void itemRemoved(ItemEvent item) {
                this.send(item);
            }

            private void send(ItemEvent event) {
                if (QueueAddListenerMessageTask.this.endpoint.isAlive()) {
                    if (!(event instanceof DataAwareItemEvent)) {
                        throw new IllegalArgumentException("Expecting: DataAwareItemEvent, Found: " + event.getClass().getSimpleName());
                    }
                    DataAwareItemEvent dataAwareItemEvent = (DataAwareItemEvent)event;
                    Data item = dataAwareItemEvent.getItemData();
                    ClientMessage clientMessage = QueueAddListenerCodec.encodeItemEvent(item, event.getMember().getUuid(), event.getEventType().getType());
                    QueueAddListenerMessageTask.this.sendClientMessage(this.val$partitionKey, clientMessage);
                }
            }
        };
        String registrationId = service.addItemListener(((QueueAddListenerCodec.RequestParameters)this.parameters).name, listener, ((QueueAddListenerCodec.RequestParameters)this.parameters).includeValue, ((QueueAddListenerCodec.RequestParameters)this.parameters).localOnly);
        this.endpoint.addListenerDestroyAction("hz:impl:queueService", ((QueueAddListenerCodec.RequestParameters)this.parameters).name, registrationId);
        return registrationId;
    }

    @Override
    protected QueueAddListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueAddListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return QueueAddListenerCodec.encodeResponse((String)response);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, ((QueueAddListenerCodec.RequestParameters)this.parameters).includeValue};
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueAddListenerCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getMethodName() {
        return "addItemListener";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueueAddListenerCodec.RequestParameters)this.parameters).name;
    }
}

