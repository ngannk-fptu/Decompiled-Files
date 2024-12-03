/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.journal;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.journal.CacheEventJournalReadResultSetImpl;
import com.hazelcast.cache.impl.journal.InternalEventJournalCacheEvent;
import com.hazelcast.cache.journal.EventJournalCacheEvent;
import com.hazelcast.internal.journal.EventJournal;
import com.hazelcast.internal.journal.EventJournalReadOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.util.function.Function;
import com.hazelcast.util.function.Predicate;
import java.io.IOException;

public class CacheEventJournalReadOperation<K, V, T>
extends EventJournalReadOperation<T, InternalEventJournalCacheEvent> {
    protected Predicate<? super EventJournalCacheEvent<K, V>> predicate;
    protected Function<? super EventJournalCacheEvent<K, V>, ? extends T> projection;

    public CacheEventJournalReadOperation() {
    }

    public CacheEventJournalReadOperation(String cacheName, long startSequence, int minSize, int maxSize, Predicate<? super EventJournalCacheEvent<K, V>> predicate, Function<? super EventJournalCacheEvent<K, V>, ? extends T> projection) {
        super(cacheName, startSequence, minSize, maxSize);
        this.predicate = predicate;
        this.projection = projection;
    }

    @Override
    protected EventJournal<InternalEventJournalCacheEvent> getJournal() {
        CacheService service = (CacheService)this.getService();
        return service.getEventJournal();
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 57;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
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

    @Override
    protected ReadResultSetImpl<InternalEventJournalCacheEvent, T> createResultSet() {
        return new CacheEventJournalReadResultSetImpl(this.minSize, this.maxSize, this.getNodeEngine().getSerializationService(), this.predicate, this.projection);
    }
}

