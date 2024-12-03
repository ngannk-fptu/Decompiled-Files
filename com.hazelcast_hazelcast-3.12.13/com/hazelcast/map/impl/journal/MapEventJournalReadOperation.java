/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.journal;

import com.hazelcast.internal.journal.EventJournal;
import com.hazelcast.internal.journal.EventJournalReadOperation;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.journal.InternalEventJournalMapEvent;
import com.hazelcast.map.impl.journal.MapEventJournalReadResultSetImpl;
import com.hazelcast.map.journal.EventJournalMapEvent;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.util.function.Function;
import com.hazelcast.util.function.Predicate;
import java.io.IOException;

public class MapEventJournalReadOperation<K, V, T>
extends EventJournalReadOperation<T, InternalEventJournalMapEvent> {
    protected Predicate<? super EventJournalMapEvent<K, V>> predicate;
    protected Function<? super EventJournalMapEvent<K, V>, ? extends T> projection;

    public MapEventJournalReadOperation() {
    }

    public MapEventJournalReadOperation(String mapName, long startSequence, int minSize, int maxSize, Predicate<? super EventJournalMapEvent<K, V>> predicate, Function<? super EventJournalMapEvent<K, V>, ? extends T> projection) {
        super(mapName, startSequence, minSize, maxSize);
        this.predicate = predicate;
        this.projection = projection;
    }

    @Override
    protected ReadResultSetImpl<InternalEventJournalMapEvent, T> createResultSet() {
        return new MapEventJournalReadResultSetImpl(this.minSize, this.maxSize, this.getNodeEngine().getSerializationService(), this.predicate, this.projection);
    }

    @Override
    protected EventJournal<InternalEventJournalMapEvent> getJournal() {
        MapService service = (MapService)this.getService();
        return service.getMapServiceContext().getEventJournal();
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 142;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.predicate);
        out.writeObject(this.projection);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.predicate = (Predicate)in.readObject();
        this.projection = (Function)in.readObject();
    }
}

