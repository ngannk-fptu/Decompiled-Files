/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapAddEntryListenerToKeyCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapAddEntryListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.EntryEventFilter;
import com.hazelcast.map.impl.EventListenerFilter;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventFilter;

public class MapAddEntryListenerToKeyMessageTask
extends AbstractMapAddEntryListenerMessageTask<MapAddEntryListenerToKeyCodec.RequestParameters> {
    public MapAddEntryListenerToKeyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected MapAddEntryListenerToKeyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapAddEntryListenerToKeyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapAddEntryListenerToKeyCodec.encodeResponse((String)response);
    }

    @Override
    protected ClientMessage encodeEvent(Data keyData, Data newValueData, Data oldValueData, Data meringValueData, int type, String uuid, int numberOfAffectedEntries) {
        return MapAddEntryListenerToKeyCodec.encodeEntryEvent(keyData, newValueData, oldValueData, meringValueData, type, uuid, numberOfAffectedEntries);
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    protected EventFilter getEventFilter() {
        EntryEventFilter eventFilter = new EntryEventFilter(((MapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).includeValue, ((MapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).key);
        return new EventListenerFilter(((MapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).listenerFlags, eventFilter);
    }

    @Override
    protected boolean isLocalOnly() {
        return ((MapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).localOnly;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, ((MapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).key, ((MapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).includeValue};
    }
}

