/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapAddEntryListenerToKeyWithPredicateCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapAddEntryListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.EventListenerFilter;
import com.hazelcast.map.impl.query.QueryEventFilter;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.EventFilter;

public class MapAddEntryListenerToKeyWithPredicateMessageTask
extends AbstractMapAddEntryListenerMessageTask<MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters> {
    public MapAddEntryListenerToKeyWithPredicateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected EventFilter getEventFilter() {
        Predicate predicate = (Predicate)this.serializationService.toObject(((MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).predicate);
        QueryEventFilter eventFilter = new QueryEventFilter(((MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).includeValue, ((MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).key, predicate);
        return new EventListenerFilter(((MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).listenerFlags, eventFilter);
    }

    @Override
    protected boolean isLocalOnly() {
        return ((MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).localOnly;
    }

    @Override
    protected MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapAddEntryListenerToKeyWithPredicateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapAddEntryListenerToKeyWithPredicateCodec.encodeResponse((String)response);
    }

    @Override
    protected ClientMessage encodeEvent(Data keyData, Data newValueData, Data oldValueData, Data meringValueData, int type, String uuid, int numberOfAffectedEntries) {
        return MapAddEntryListenerToKeyWithPredicateCodec.encodeEntryEvent(keyData, newValueData, oldValueData, meringValueData, type, uuid, numberOfAffectedEntries);
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, ((MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).predicate, ((MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).key, ((MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).includeValue};
    }
}

