/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.cache.impl.journal;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.journal.DeserializingEventJournalCacheEvent;
import com.hazelcast.cache.impl.journal.InternalEventJournalCacheEvent;
import com.hazelcast.cache.journal.EventJournalCacheEvent;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.projection.Projection;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.function.Function;
import com.hazelcast.util.function.Predicate;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class CacheEventJournalReadResultSetImpl<K, V, T>
extends ReadResultSetImpl<InternalEventJournalCacheEvent, T> {
    public CacheEventJournalReadResultSetImpl() {
    }

    CacheEventJournalReadResultSetImpl(int minSize, int maxSize, SerializationService serializationService, final Predicate<? super EventJournalCacheEvent<K, V>> predicate, Function<? super EventJournalCacheEvent<K, V>, ? extends T> projection) {
        super(minSize, maxSize, serializationService, predicate == null ? null : new Predicate<InternalEventJournalCacheEvent>(){

            @Override
            @SuppressFBWarnings(value={"BC_UNCONFIRMED_CAST"})
            public boolean test(InternalEventJournalCacheEvent e) {
                return predicate.test((DeserializingEventJournalCacheEvent)e);
            }
        }, projection == null ? null : new ProjectionAdapter(projection));
    }

    @Override
    public void addItem(long seq, Object item) {
        InternalEventJournalCacheEvent e = (InternalEventJournalCacheEvent)item;
        DeserializingEventJournalCacheEvent deserialisingEvent = new DeserializingEventJournalCacheEvent(this.serializationService, e);
        super.addItem(seq, deserialisingEvent);
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 60;
    }

    @SerializableByConvention
    private static class ProjectionAdapter<K, V, T>
    extends Projection<InternalEventJournalCacheEvent, T> {
        private final Function<? super EventJournalCacheEvent<K, V>, ? extends T> projection;

        ProjectionAdapter(Function<? super EventJournalCacheEvent<K, V>, ? extends T> projection) {
            this.projection = projection;
        }

        @Override
        @SuppressFBWarnings(value={"BC_UNCONFIRMED_CAST"})
        public T transform(InternalEventJournalCacheEvent input) {
            return this.projection.apply((DeserializingEventJournalCacheEvent)input);
        }
    }
}

