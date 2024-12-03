/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapAddEntryListenerCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapAddEntryListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.EntryEventFilter;
import com.hazelcast.map.impl.EventListenerFilter;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventFilter;

public class MapAddEntryListenerMessageTask
extends AbstractMapAddEntryListenerMessageTask<MapAddEntryListenerCodec.RequestParameters> {
    public MapAddEntryListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected EventFilter getEventFilter() {
        EntryEventFilter eventFilter = new EntryEventFilter(((MapAddEntryListenerCodec.RequestParameters)this.parameters).includeValue, null);
        return new EventListenerFilter(((MapAddEntryListenerCodec.RequestParameters)this.parameters).listenerFlags, eventFilter);
    }

    @Override
    protected boolean isLocalOnly() {
        return ((MapAddEntryListenerCodec.RequestParameters)this.parameters).localOnly;
    }

    @Override
    protected ClientMessage encodeEvent(Data keyData, Data newValueData, Data oldValueData, Data meringValueData, int type, String uuid, int numberOfAffectedEntries) {
        return MapAddEntryListenerCodec.encodeEntryEvent(keyData, newValueData, oldValueData, meringValueData, type, uuid, numberOfAffectedEntries);
    }

    @Override
    protected MapAddEntryListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapAddEntryListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapAddEntryListenerCodec.encodeResponse((String)response);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, ((MapAddEntryListenerCodec.RequestParameters)this.parameters).includeValue};
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapAddEntryListenerCodec.RequestParameters)this.parameters).name;
    }
}

