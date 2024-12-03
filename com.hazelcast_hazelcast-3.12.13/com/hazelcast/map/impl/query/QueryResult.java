/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.QueryResultSizeExceededException;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.projection.Projection;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.IterationType;
import com.hazelcast.util.SortingUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class QueryResult
implements Result<QueryResult>,
Iterable<QueryResultRow> {
    private List rows = new ArrayList();
    private Collection<Integer> partitionIds;
    private IterationType iterationType;
    private final transient SerializationService serializationService;
    private final transient long resultLimit;
    private final transient boolean orderAndLimitExpected;
    private final transient Projection projection;
    private transient long resultSize;

    public QueryResult() {
        this.serializationService = null;
        this.orderAndLimitExpected = false;
        this.resultLimit = Long.MAX_VALUE;
        this.projection = null;
    }

    public QueryResult(IterationType iterationType, Projection projection, SerializationService serializationService, long resultLimit, boolean orderAndLimitExpected) {
        this.iterationType = iterationType;
        this.projection = projection;
        this.serializationService = serializationService;
        this.resultLimit = resultLimit;
        this.orderAndLimitExpected = orderAndLimitExpected;
    }

    IterationType getIterationType() {
        return this.iterationType;
    }

    @Override
    public Iterator<QueryResultRow> iterator() {
        return this.rows.iterator();
    }

    public int size() {
        return this.rows.size();
    }

    public boolean isEmpty() {
        return this.rows.isEmpty();
    }

    public void addRow(QueryResultRow row) {
        this.rows.add(row);
    }

    @Override
    public void add(QueryableEntry entry) {
        if (++this.resultSize > this.resultLimit) {
            throw new QueryResultSizeExceededException();
        }
        this.rows.add(this.orderAndLimitExpected ? entry : this.convertEntryToRow(entry));
    }

    @Override
    public QueryResult createSubResult() {
        return new QueryResult(this.iterationType, this.projection, this.serializationService, this.resultLimit, this.orderAndLimitExpected);
    }

    @Override
    public void orderAndLimit(PagingPredicate pagingPredicate, Map.Entry<Integer, Map.Entry> nearestAnchorEntry) {
        this.rows = SortingUtil.getSortedSubList(this.rows, pagingPredicate, nearestAnchorEntry);
    }

    @Override
    public void completeConstruction(Collection<Integer> partitionIds) {
        this.setPartitionIds(partitionIds);
        if (this.orderAndLimitExpected) {
            ListIterator<QueryResultRow> iterator = this.rows.listIterator();
            while (iterator.hasNext()) {
                iterator.set(this.convertEntryToRow((QueryableEntry)iterator.next()));
            }
        }
    }

    private Data getValueData(QueryableEntry entry) {
        if (this.projection != null) {
            return this.serializationService.toData(this.projection.transform(entry));
        }
        return entry.getValueData();
    }

    @Override
    public Collection<Integer> getPartitionIds() {
        return this.partitionIds;
    }

    @Override
    public void combine(QueryResult result) {
        Collection<Integer> otherPartitionIds = result.getPartitionIds();
        if (otherPartitionIds == null) {
            return;
        }
        if (this.partitionIds == null) {
            this.partitionIds = new ArrayList<Integer>(otherPartitionIds.size());
        }
        this.partitionIds.addAll(otherPartitionIds);
        this.rows.addAll(result.rows);
    }

    @Override
    public void onCombineFinished() {
    }

    @Override
    public void setPartitionIds(Collection<Integer> partitionIds) {
        this.partitionIds = new ArrayList<Integer>(partitionIds);
    }

    public List<QueryResultRow> getRows() {
        return this.rows;
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 10;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        int partitionSize = this.partitionIds == null ? 0 : this.partitionIds.size();
        out.writeInt(partitionSize);
        if (partitionSize > 0) {
            for (Integer partitionId : this.partitionIds) {
                out.writeInt(partitionId);
            }
        }
        out.writeByte(this.iterationType.getId());
        int resultSize = this.rows.size();
        out.writeInt(resultSize);
        if (resultSize > 0) {
            for (QueryResultRow row : this.rows) {
                row.writeData(out);
            }
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int partitionSize = in.readInt();
        if (partitionSize > 0) {
            this.partitionIds = new ArrayList<Integer>(partitionSize);
            for (int i = 0; i < partitionSize; ++i) {
                this.partitionIds.add(in.readInt());
            }
        }
        this.iterationType = IterationType.getById(in.readByte());
        int resultSize = in.readInt();
        if (resultSize > 0) {
            for (int i = 0; i < resultSize; ++i) {
                QueryResultRow row = new QueryResultRow();
                row.readData(in);
                this.rows.add(row);
            }
        }
    }

    private QueryResultRow convertEntryToRow(QueryableEntry entry) {
        Data key = null;
        Data value = null;
        switch (this.iterationType) {
            case KEY: {
                key = entry.getKeyData();
                break;
            }
            case VALUE: {
                value = this.getValueData(entry);
                break;
            }
            case ENTRY: {
                key = entry.getKeyData();
                value = entry.getValueData();
                break;
            }
            default: {
                throw new IllegalStateException("Unknown iterationType:" + (Object)((Object)this.iterationType));
            }
        }
        return new QueryResultRow(key, value);
    }
}

