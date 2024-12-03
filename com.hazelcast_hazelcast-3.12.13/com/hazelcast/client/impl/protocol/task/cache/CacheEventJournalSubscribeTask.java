/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.journal.CacheEventJournalSubscribeOperation;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheEventJournalSubscribeCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.journal.EventJournalInitialSubscriberState;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class CacheEventJournalSubscribeTask
extends AbstractCacheMessageTask<CacheEventJournalSubscribeCodec.RequestParameters> {
    public CacheEventJournalSubscribeTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CacheEventJournalSubscribeOperation(((CacheEventJournalSubscribeCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected CacheEventJournalSubscribeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheEventJournalSubscribeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        EventJournalInitialSubscriberState state = (EventJournalInitialSubscriberState)response;
        return CacheEventJournalSubscribeCodec.encodeResponse(state.getOldestSequence(), state.getNewestSequence());
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheEventJournalSubscribeCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheEventJournalSubscribeCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "subscribeToEventJournal";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{this.getPartitionId()};
    }
}

