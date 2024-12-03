/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapAddEntryListenerWithPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapAddNearCacheEntryListenerCodec;
import com.hazelcast.client.impl.protocol.task.replicatedmap.AbstractReplicatedMapAddEntryListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;

public class ReplicatedMapAddNearCacheListenerMessageTask
extends AbstractReplicatedMapAddEntryListenerMessageTask<ReplicatedMapAddNearCacheEntryListenerCodec.RequestParameters> {
    public ReplicatedMapAddNearCacheListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
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
        return ((ReplicatedMapAddNearCacheEntryListenerCodec.RequestParameters)this.parameters).localOnly;
    }

    @Override
    protected ClientMessage encodeEvent(Data key, Data newValue, Data oldValue, Data mergingValue, int type, String uuid, int numberOfAffectedEntries) {
        return ReplicatedMapAddNearCacheEntryListenerCodec.encodeEntryEvent(key, newValue, oldValue, mergingValue, type, uuid, numberOfAffectedEntries);
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapAddNearCacheEntryListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    protected ReplicatedMapAddNearCacheEntryListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapAddNearCacheEntryListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ReplicatedMapAddEntryListenerWithPredicateCodec.encodeResponse((String)response);
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

