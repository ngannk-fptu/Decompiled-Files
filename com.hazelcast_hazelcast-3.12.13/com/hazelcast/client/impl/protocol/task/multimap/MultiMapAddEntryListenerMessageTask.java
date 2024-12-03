/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapAddEntryListenerCodec;
import com.hazelcast.client.impl.protocol.task.multimap.AbstractMultiMapAddEntryListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;

public class MultiMapAddEntryListenerMessageTask
extends AbstractMultiMapAddEntryListenerMessageTask<MultiMapAddEntryListenerCodec.RequestParameters> {
    public MultiMapAddEntryListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean shouldIncludeValue() {
        return ((MultiMapAddEntryListenerCodec.RequestParameters)this.parameters).includeValue;
    }

    @Override
    protected boolean isLocalOnly() {
        return ((MultiMapAddEntryListenerCodec.RequestParameters)this.parameters).localOnly;
    }

    @Override
    protected ClientMessage encodeEvent(Data key, Data value, Data oldValue, int type, String uuid, int numberOfEntriesAffected) {
        return MultiMapAddEntryListenerCodec.encodeEntryEvent(key, value, oldValue, null, type, uuid, numberOfEntriesAffected);
    }

    @Override
    protected MultiMapAddEntryListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapAddEntryListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapAddEntryListenerCodec.encodeResponse((String)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapAddEntryListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, ((MultiMapAddEntryListenerCodec.RequestParameters)this.parameters).includeValue};
    }
}

