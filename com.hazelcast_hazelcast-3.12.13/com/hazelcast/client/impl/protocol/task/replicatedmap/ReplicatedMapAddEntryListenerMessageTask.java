/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapAddEntryListenerCodec;
import com.hazelcast.client.impl.protocol.task.replicatedmap.AbstractReplicatedMapAddEntryListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;

public class ReplicatedMapAddEntryListenerMessageTask
extends AbstractReplicatedMapAddEntryListenerMessageTask<ReplicatedMapAddEntryListenerCodec.RequestParameters> {
    public ReplicatedMapAddEntryListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    public Predicate getPredicate() {
        return null;
    }

    @Override
    public Data getKey() {
        return null;
    }

    @Override
    protected boolean isLocalOnly() {
        return ((ReplicatedMapAddEntryListenerCodec.RequestParameters)this.parameters).localOnly;
    }

    @Override
    protected ClientMessage encodeEvent(Data key, Data newValue, Data oldValue, Data mergingValue, int type, String uuid, int numberOfAffectedEntries) {
        return ReplicatedMapAddEntryListenerCodec.encodeEntryEvent(key, newValue, oldValue, mergingValue, type, uuid, numberOfAffectedEntries);
    }

    @Override
    protected ReplicatedMapAddEntryListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapAddEntryListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ReplicatedMapAddEntryListenerCodec.encodeResponse((String)response);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null};
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapAddEntryListenerCodec.RequestParameters)this.parameters).name;
    }
}

