/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.map.impl.journal;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.journal.DeserializingEventJournalMapEvent;
import com.hazelcast.map.impl.journal.InternalEventJournalMapEvent;
import com.hazelcast.map.journal.EventJournalMapEvent;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.projection.Projection;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.function.Function;
import com.hazelcast.util.function.Predicate;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class MapEventJournalReadResultSetImpl<K, V, T>
extends ReadResultSetImpl<InternalEventJournalMapEvent, T> {
    public MapEventJournalReadResultSetImpl() {
    }

    MapEventJournalReadResultSetImpl(int minSize, int maxSize, SerializationService serializationService, final Predicate<? super EventJournalMapEvent<K, V>> predicate, Function<? super EventJournalMapEvent<K, V>, ? extends T> projection) {
        super(minSize, maxSize, serializationService, predicate == null ? null : new Predicate<InternalEventJournalMapEvent>(){

            @Override
            @SuppressFBWarnings(value={"BC_UNCONFIRMED_CAST"})
            public boolean test(InternalEventJournalMapEvent e) {
                return predicate.test((DeserializingEventJournalMapEvent)e);
            }
        }, projection == null ? null : new ProjectionAdapter(projection));
    }

    @Override
    public void addItem(long seq, Object item) {
        InternalEventJournalMapEvent e = (InternalEventJournalMapEvent)item;
        DeserializingEventJournalMapEvent deserialisingEvent = new DeserializingEventJournalMapEvent(this.serializationService, e);
        super.addItem(seq, deserialisingEvent);
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 145;
    }

    @SerializableByConvention
    private static class ProjectionAdapter<K, V, T>
    extends Projection<InternalEventJournalMapEvent, T> {
        private final Function<? super EventJournalMapEvent<K, V>, ? extends T> projection;

        ProjectionAdapter(Function<? super EventJournalMapEvent<K, V>, ? extends T> projection) {
            this.projection = projection;
        }

        @Override
        @SuppressFBWarnings(value={"BC_UNCONFIRMED_CAST"})
        public T transform(InternalEventJournalMapEvent e) {
            return this.projection.apply((DeserializingEventJournalMapEvent)e);
        }
    }
}

