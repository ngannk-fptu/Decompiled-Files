/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.MapEvent;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.DataAwareEntryEvent;
import com.hazelcast.map.impl.MapListenerAdapter;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.EventFilter;
import java.security.Permission;

public abstract class AbstractMapAddEntryListenerMessageTask<Parameter>
extends AbstractCallableMessageTask<Parameter>
implements ListenerMessageTask {
    public AbstractMapAddEntryListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        Object listener = this.newMapListener();
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        String name = this.getDistributedObjectName();
        EventFilter eventFilter = this.getEventFilter();
        String registrationId = this.isLocalOnly() ? mapServiceContext.addLocalEventListener(listener, eventFilter, name) : mapServiceContext.addEventListener(listener, eventFilter, name);
        this.endpoint.addListenerDestroyAction("hz:impl:mapService", name, registrationId);
        return registrationId;
    }

    protected Object newMapListener() {
        return new ClientMapListener();
    }

    protected abstract EventFilter getEventFilter();

    protected abstract boolean isLocalOnly();

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public String getMethodName() {
        return "addEntryListener";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(this.getDistributedObjectName(), "listen");
    }

    protected abstract ClientMessage encodeEvent(Data var1, Data var2, Data var3, Data var4, int var5, String var6, int var7);

    private class ClientMapListener
    extends MapListenerAdapter<Object, Object> {
        private ClientMapListener() {
        }

        @Override
        public void onEntryEvent(EntryEvent<Object, Object> event) {
            if (!AbstractMapAddEntryListenerMessageTask.this.endpoint.isAlive()) {
                return;
            }
            if (!(event instanceof DataAwareEntryEvent)) {
                throw new IllegalArgumentException("Expecting: DataAwareEntryEvent, Found: " + event.getClass().getSimpleName());
            }
            DataAwareEntryEvent dataAwareEntryEvent = (DataAwareEntryEvent)event;
            Data keyData = dataAwareEntryEvent.getKeyData();
            Data newValueData = dataAwareEntryEvent.getNewValueData();
            Data oldValueData = dataAwareEntryEvent.getOldValueData();
            Data meringValueData = dataAwareEntryEvent.getMergingValueData();
            AbstractMapAddEntryListenerMessageTask.this.sendClientMessage(keyData, AbstractMapAddEntryListenerMessageTask.this.encodeEvent(keyData, newValueData, oldValueData, meringValueData, event.getEventType().getType(), event.getMember().getUuid(), 1));
        }

        @Override
        public void onMapEvent(MapEvent event) {
            if (!AbstractMapAddEntryListenerMessageTask.this.endpoint.isAlive()) {
                return;
            }
            EntryEventType type = event.getEventType();
            String uuid = event.getMember().getUuid();
            int numberOfEntriesAffected = event.getNumberOfEntriesAffected();
            AbstractMapAddEntryListenerMessageTask.this.sendClientMessage(null, AbstractMapAddEntryListenerMessageTask.this.encodeEvent(null, null, null, null, type.getType(), uuid, numberOfEntriesAffected));
        }
    }
}

