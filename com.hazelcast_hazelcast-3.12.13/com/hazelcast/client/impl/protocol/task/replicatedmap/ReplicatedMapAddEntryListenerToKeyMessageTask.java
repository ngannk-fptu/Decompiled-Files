/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapAddEntryListenerToKeyCodec;
import com.hazelcast.client.impl.protocol.task.replicatedmap.AbstractReplicatedMapAddEntryListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;

public class ReplicatedMapAddEntryListenerToKeyMessageTask
extends AbstractReplicatedMapAddEntryListenerMessageTask<ReplicatedMapAddEntryListenerToKeyCodec.RequestParameters> {
    public ReplicatedMapAddEntryListenerToKeyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    public Predicate getPredicate() {
        return null;
    }

    @Override
    public Data getKey() {
        return ((ReplicatedMapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).key;
    }

    @Override
    protected boolean isLocalOnly() {
        return ((ReplicatedMapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).localOnly;
    }

    @Override
    protected ClientMessage encodeEvent(Data key, Data newValue, Data oldValue, Data mergingValue, int type, String uuid, int numberOfAffectedEntries) {
        return ReplicatedMapAddEntryListenerToKeyCodec.encodeEntryEvent(key, newValue, oldValue, mergingValue, type, uuid, numberOfAffectedEntries);
    }

    @Override
    protected ReplicatedMapAddEntryListenerToKeyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapAddEntryListenerToKeyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ReplicatedMapAddEntryListenerToKeyCodec.encodeResponse((String)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, ((ReplicatedMapAddEntryListenerToKeyCodec.RequestParameters)this.parameters).key};
    }
}

