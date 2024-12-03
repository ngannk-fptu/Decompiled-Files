/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.Member;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.DataAwareEntryEvent;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.replicatedmap.impl.ReplicatedMapEventPublishingService;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.record.ReplicatedEntryEventFilter;
import com.hazelcast.replicatedmap.impl.record.ReplicatedQueryEventFilter;
import com.hazelcast.security.permission.MapPermission;
import java.security.Permission;

public abstract class AbstractReplicatedMapAddEntryListenerMessageTask<Parameter>
extends AbstractCallableMessageTask<Parameter>
implements EntryListener<Object, Object>,
ListenerMessageTask {
    public AbstractReplicatedMapAddEntryListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        ReplicatedMapService service = (ReplicatedMapService)this.getService("hz:impl:replicatedMapService");
        ReplicatedMapEventPublishingService eventPublishingService = service.getEventPublishingService();
        Predicate predicate = this.getPredicate();
        String registrationId = predicate == null ? eventPublishingService.addEventListener(this, new ReplicatedEntryEventFilter(this.getKey()), this.getDistributedObjectName()) : eventPublishingService.addEventListener(this, new ReplicatedQueryEventFilter(this.getKey(), predicate), this.getDistributedObjectName());
        this.endpoint.addListenerDestroyAction("hz:impl:replicatedMapService", this.getDistributedObjectName(), registrationId);
        return registrationId;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public String getMethodName() {
        return "addEntryListener";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(this.getDistributedObjectName(), "listen");
    }

    public abstract Predicate getPredicate();

    public abstract Data getKey();

    protected abstract boolean isLocalOnly();

    private void handleEvent(EntryEvent<Object, Object> event) {
        if (!this.shouldSendEvent(event)) {
            return;
        }
        DataAwareEntryEvent dataAwareEntryEvent = (DataAwareEntryEvent)event;
        Data key = dataAwareEntryEvent.getKeyData();
        Data newValue = dataAwareEntryEvent.getNewValueData();
        Data oldValue = dataAwareEntryEvent.getOldValueData();
        Data mergingValue = dataAwareEntryEvent.getMergingValueData();
        ClientMessage clientMessage = this.encodeEvent(key, newValue, oldValue, mergingValue, event.getEventType().getType(), event.getMember().getUuid(), 1);
        this.sendClientMessage(key, clientMessage);
    }

    private void handleMapEvent(MapEvent event) {
        if (!this.shouldSendEvent(event)) {
            return;
        }
        ClientMessage clientMessage = this.encodeEvent(null, null, null, null, event.getEventType().getType(), event.getMember().getUuid(), event.getNumberOfEntriesAffected());
        this.sendClientMessage(null, clientMessage);
    }

    private boolean shouldSendEvent(IMapEvent event) {
        if (!this.endpoint.isAlive()) {
            return false;
        }
        Member originatedMember = event.getMember();
        return !this.isLocalOnly() || this.nodeEngine.getLocalMember().equals(originatedMember);
    }

    protected abstract ClientMessage encodeEvent(Data var1, Data var2, Data var3, Data var4, int var5, String var6, int var7);

    @Override
    public void entryAdded(EntryEvent<Object, Object> event) {
        this.handleEvent(event);
    }

    @Override
    public void entryRemoved(EntryEvent<Object, Object> event) {
        this.handleEvent(event);
    }

    @Override
    public void entryUpdated(EntryEvent<Object, Object> event) {
        this.handleEvent(event);
    }

    @Override
    public void entryEvicted(EntryEvent<Object, Object> event) {
        this.handleEvent(event);
    }

    @Override
    public void mapEvicted(MapEvent event) {
    }

    @Override
    public void mapCleared(MapEvent event) {
        this.handleMapEvent(event);
    }
}

