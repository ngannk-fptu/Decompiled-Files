/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapAddEntryListenerToKeyWithPredicateCodec;
import com.hazelcast.client.impl.protocol.task.replicatedmap.AbstractReplicatedMapAddEntryListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;

public class ReplicatedMapAddEntryListenerToKeyWithPredicateMessageTask
extends AbstractReplicatedMapAddEntryListenerMessageTask<ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.RequestParameters> {
    public ReplicatedMapAddEntryListenerToKeyWithPredicateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    public Predicate getPredicate() {
        return (Predicate)this.serializationService.toObject(((ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).predicate);
    }

    @Override
    public Data getKey() {
        return ((ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).key;
    }

    @Override
    protected boolean isLocalOnly() {
        return ((ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).localOnly;
    }

    @Override
    protected ClientMessage encodeEvent(Data key, Data newValue, Data oldValue, Data mergingValue, int type, String uuid, int numberOfAffectedEntries) {
        return ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.encodeEntryEvent(key, newValue, oldValue, mergingValue, type, uuid, numberOfAffectedEntries);
    }

    @Override
    protected ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.encodeResponse((String)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, ((ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).predicate, ((ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.RequestParameters)this.parameters).key};
    }
}

