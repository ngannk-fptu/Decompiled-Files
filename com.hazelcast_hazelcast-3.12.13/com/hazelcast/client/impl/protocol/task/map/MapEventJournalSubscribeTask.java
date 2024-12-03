/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapEventJournalSubscribeCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.journal.EventJournalInitialSubscriberState;
import com.hazelcast.map.impl.journal.MapEventJournalSubscribeOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MapEventJournalSubscribeTask
extends AbstractMapPartitionMessageTask<MapEventJournalSubscribeCodec.RequestParameters> {
    public MapEventJournalSubscribeTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new MapEventJournalSubscribeOperation(((MapEventJournalSubscribeCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected MapEventJournalSubscribeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapEventJournalSubscribeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        EventJournalInitialSubscriberState state = (EventJournalInitialSubscriberState)response;
        return MapEventJournalSubscribeCodec.encodeResponse(state.getOldestSequence(), state.getNewestSequence());
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapEventJournalSubscribeCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapEventJournalSubscribeCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "subscribeToEventJournal";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{this.getPartitionId()};
    }
}

