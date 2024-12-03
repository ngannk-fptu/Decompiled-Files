/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapEventJournalReadCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.journal.MapEventJournalReadOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.function.Function;
import com.hazelcast.util.function.Predicate;
import java.security.Permission;
import java.util.ArrayList;

public class MapEventJournalReadTask<K, V, T>
extends AbstractMapPartitionMessageTask<MapEventJournalReadCodec.RequestParameters> {
    public MapEventJournalReadTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Function projection = (Function)this.serializationService.toObject(((MapEventJournalReadCodec.RequestParameters)this.parameters).projection);
        Predicate predicate = (Predicate)this.serializationService.toObject(((MapEventJournalReadCodec.RequestParameters)this.parameters).predicate);
        return new MapEventJournalReadOperation(((MapEventJournalReadCodec.RequestParameters)this.parameters).name, ((MapEventJournalReadCodec.RequestParameters)this.parameters).startSequence, ((MapEventJournalReadCodec.RequestParameters)this.parameters).minSize, ((MapEventJournalReadCodec.RequestParameters)this.parameters).maxSize, predicate, projection);
    }

    @Override
    protected MapEventJournalReadCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapEventJournalReadCodec.decodeRequest(clientMessage);
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
        return MapEventJournalReadCodec.encodeResponse(resultSet.readCount(), items, seqs, resultSet.getNextSequenceToReadFrom());
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapEventJournalReadCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapEventJournalReadCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "readFromEventJournal";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapEventJournalReadCodec.RequestParameters)this.parameters).startSequence, ((MapEventJournalReadCodec.RequestParameters)this.parameters).maxSize, this.getPartitionId(), ((MapEventJournalReadCodec.RequestParameters)this.parameters).predicate, ((MapEventJournalReadCodec.RequestParameters)this.parameters).projection};
    }
}

