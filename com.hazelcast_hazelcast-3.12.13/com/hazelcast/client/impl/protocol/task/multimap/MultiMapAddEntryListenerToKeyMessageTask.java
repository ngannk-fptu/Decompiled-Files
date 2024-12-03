/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapAddEntryListenerToKeyCodec;
import com.hazelcast.client.impl.protocol.task.multimap.AbstractMultiMapAddEntryListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;

public class MultiMapAddEntryListenerToKeyMessageTask
extends AbstractMultiMapAddEntryListenerMessageTask<MultiMapAddEntryListenerToKeyCodec.RequestParameters> {
    public MultiMapAddEntryListenerToKeyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean shouldIncludeValue() {
        return ((MultiMapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).includeValue;
    }

    @Override
    protected boolean isLocalOnly() {
        return ((MultiMapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).localOnly;
    }

    @Override
    protected ClientMessage encodeEvent(Data key, Data value, Data oldValue, int type, String uuid, int numberOfEntriesAffected) {
        return MultiMapAddEntryListenerToKeyCodec.encodeEntryEvent(key, value, oldValue, null, type, uuid, numberOfEntriesAffected);
    }

    @Override
    protected MultiMapAddEntryListenerToKeyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapAddEntryListenerToKeyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapAddEntryListenerToKeyCodec.encodeResponse((String)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, ((MultiMapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).key, ((MultiMapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).includeValue};
    }

    @Override
    public Data getKey() {
        return ((MultiMapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).key;
    }
}

