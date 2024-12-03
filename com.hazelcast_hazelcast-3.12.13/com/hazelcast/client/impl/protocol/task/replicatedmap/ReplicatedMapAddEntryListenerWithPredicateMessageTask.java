/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapAddEntryListenerWithPredicateCodec;
import com.hazelcast.client.impl.protocol.task.replicatedmap.AbstractReplicatedMapAddEntryListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;

public class ReplicatedMapAddEntryListenerWithPredicateMessageTask
extends AbstractReplicatedMapAddEntryListenerMessageTask<ReplicatedMapAddEntryListenerWithPredicateCodec.RequestParameters> {
    public ReplicatedMapAddEntryListenerWithPredicateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    public Predicate getPredicate() {
        return (Predicate)this.serializationService.toObject(((ReplicatedMapAddEntryListenerWithPredicateCodec.RequestParameters)this.parameters).predicate);
    }

    @Override
    public Data getKey() {
        return null;
    }

    @Override
    protected boolean isLocalOnly() {
        return ((ReplicatedMapAddEntryListenerWithPredicateCodec.RequestParameters)this.parameters).localOnly;
    }

    @Override
    protected ClientMessage encodeEvent(Data key, Data newValue, Data oldValue, Data mergingValue, int type, String uuid, int numberOfAffectedEntries) {
        return ReplicatedMapAddEntryListenerWithPredicateCodec.encodeEntryEvent(key, newValue, oldValue, mergingValue, type, uuid, numberOfAffectedEntries);
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapAddEntryListenerWithPredicateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    protected ReplicatedMapAddEntryListenerWithPredicateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapAddEntryListenerWithPredicateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ReplicatedMapAddEntryListenerWithPredicateCodec.encodeResponse((String)response);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, ((ReplicatedMapAddEntryListenerWithPredicateCodec.RequestParameters)this.parameters).predicate};
    }
}

