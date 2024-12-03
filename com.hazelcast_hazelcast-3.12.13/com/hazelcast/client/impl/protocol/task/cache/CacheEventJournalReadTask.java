/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.journal.CacheEventJournalReadOperation;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheEventJournalReadCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.function.Function;
import com.hazelcast.util.function.Predicate;
import java.security.Permission;
import java.util.ArrayList;

public class CacheEventJournalReadTask<K, V, T>
extends AbstractCacheMessageTask<CacheEventJournalReadCodec.RequestParameters> {
    public CacheEventJournalReadTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Function projection = (Function)this.serializationService.toObject(((CacheEventJournalReadCodec.RequestParameters)this.parameters).projection);
        Predicate predicate = (Predicate)this.serializationService.toObject(((CacheEventJournalReadCodec.RequestParameters)this.parameters).predicate);
        return new CacheEventJournalReadOperation(((CacheEventJournalReadCodec.RequestParameters)this.parameters).name, ((CacheEventJournalReadCodec.RequestParameters)this.parameters).startSequence, ((CacheEventJournalReadCodec.RequestParameters)this.parameters).minSize, ((CacheEventJournalReadCodec.RequestParameters)this.parameters).maxSize, predicate, projection);
    }

    @Override
    protected CacheEventJournalReadCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheEventJournalReadCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        ReadResultSetImpl resultSet = (ReadResultSetImpl)this.nodeEngine.getSerializationService().toObject(response);
        ArrayList<Data> items = new ArrayList<Data>(resultSet.size());
        long[] seqs = new long[resultSet.size()];
        Data[] dataItems = resultSet.getDataItems();
        for (int k = 0; k < resultSet.size(); ++k) {
            items.add(dataItems[k]);
            seqs[k] = resultSet.getSequence(k);
        }
        return CacheEventJournalReadCodec.encodeResponse(resultSet.readCount(), items, seqs, resultSet.getNextSequenceToReadFrom());
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheEventJournalReadCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheEventJournalReadCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "readFromEventJournal";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CacheEventJournalReadCodec.RequestParameters)this.parameters).startSequence, ((CacheEventJournalReadCodec.RequestParameters)this.parameters).maxSize, this.getPartitionId(), ((CacheEventJournalReadCodec.RequestParameters)this.parameters).predicate, ((CacheEventJournalReadCodec.RequestParameters)this.parameters).projection};
    }
}

