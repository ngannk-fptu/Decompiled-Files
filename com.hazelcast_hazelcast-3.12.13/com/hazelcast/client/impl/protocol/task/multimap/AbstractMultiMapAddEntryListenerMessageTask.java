/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.MapEvent;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.DataAwareEntryEvent;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MultiMapPermission;
import java.security.Permission;

public abstract class AbstractMultiMapAddEntryListenerMessageTask<P>
extends AbstractCallableMessageTask<P>
implements ListenerMessageTask {
    public AbstractMultiMapAddEntryListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        MultiMapService service = (MultiMapService)this.getService("hz:impl:multiMapService");
        MultiMapListener listener = new MultiMapListener();
        String name = this.getDistributedObjectName();
        Data key = this.getKey();
        boolean includeValue = this.shouldIncludeValue();
        String registrationId = service.addListener(name, listener, key, includeValue, this.isLocalOnly());
        this.endpoint.addListenerDestroyAction("hz:impl:multiMapService", name, registrationId);
        return registrationId;
    }

    protected abstract boolean shouldIncludeValue();

    protected abstract boolean isLocalOnly();

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(this.getDistributedObjectName(), "listen");
    }

    @Override
    public String getMethodName() {
        return "addEntryListener";
    }

    public Data getKey() {
        return null;
    }

    protected abstract ClientMessage encodeEvent(Data var1, Data var2, Data var3, int var4, String var5, int var6);

    private class MultiMapListener
    extends EntryAdapter<Object, Object> {
        private MultiMapListener() {
        }

        @Override
        public void onEntryEvent(EntryEvent event) {
            if (AbstractMultiMapAddEntryListenerMessageTask.this.endpoint.isAlive()) {
                if (!(event instanceof DataAwareEntryEvent)) {
                    throw new IllegalArgumentException("Expecting: DataAwareEntryEvent, Found: " + event.getClass().getSimpleName());
                }
                DataAwareEntryEvent dataAwareEntryEvent = (DataAwareEntryEvent)event;
                Data key = dataAwareEntryEvent.getKeyData();
                Data value = dataAwareEntryEvent.getNewValueData();
                Data oldValue = dataAwareEntryEvent.getOldValueData();
                EntryEventType type = event.getEventType();
                String uuid = event.getMember().getUuid();
                AbstractMultiMapAddEntryListenerMessageTask.this.sendClientMessage(key, AbstractMultiMapAddEntryListenerMessageTask.this.encodeEvent(key, value, oldValue, type.getType(), uuid, 1));
            }
        }

        @Override
        public void onMapEvent(MapEvent event) {
            if (AbstractMultiMapAddEntryListenerMessageTask.this.endpoint.isAlive()) {
                EntryEventType type = event.getEventType();
                String uuid = event.getMember().getUuid();
                AbstractMultiMapAddEntryListenerMessageTask.this.sendClientMessage(null, AbstractMultiMapAddEntryListenerMessageTask.this.encodeEvent(null, null, null, type.getType(), uuid, event.getNumberOfEntriesAffected()));
            }
        }
    }
}

