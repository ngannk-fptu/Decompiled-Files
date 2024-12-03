/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapAddEntryListenerWithPredicateCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapAddEntryListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.EventListenerFilter;
import com.hazelcast.map.impl.query.QueryEventFilter;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.EventFilter;

public class MapAddEntryListenerWithPredicateMessageTask
extends AbstractMapAddEntryListenerMessageTask<MapAddEntryListenerWithPredicateCodec.RequestParameters> {
    public MapAddEntryListenerWithPredicateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected EventFilter getEventFilter() {
        Predicate predicate = (Predicate)this.serializationService.toObject(((MapAddEntryListenerWithPredicateCodec.RequestParameters)this.parameters).predicate);
        QueryEventFilter eventFilter = new QueryEventFilter(((MapAddEntryListenerWithPredicateCodec.RequestParameters)this.parameters).includeValue, null, predicate);
        return new EventListenerFilter(((MapAddEntryListenerWithPredicateCodec.RequestParameters)this.parameters).listenerFlags, eventFilter);
    }

    @Override
    protected boolean isLocalOnly() {
        return ((MapAddEntryListenerWithPredicateCodec.RequestParameters)this.parameters).localOnly;
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapAddEntryListenerWithPredicateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    protected MapAddEntryListenerWithPredicateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapAddEntryListenerWithPredicateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapAddEntryListenerWithPredicateCodec.encodeResponse((String)response);
    }

    @Override
    protected ClientMessage encodeEvent(Data keyData, Data newValueData, Data oldValueData, Data meringValueData, int type, String uuid, int numberOfAffectedEntries) {
        return MapAddEntryListenerWithPredicateCodec.encodeEntryEvent(keyData, newValueData, oldValueData, meringValueData, type, uuid, numberOfAffectedEntries);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, ((MapAddEntryListenerWithPredicateCodec.RequestParameters)this.parameters).predicate, ((MapAddEntryListenerWithPredicateCodec.RequestParameters)this.parameters).includeValue};
    }
}

