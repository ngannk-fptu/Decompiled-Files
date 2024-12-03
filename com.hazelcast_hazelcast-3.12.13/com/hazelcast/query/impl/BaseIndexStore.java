/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.CompositeValue;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.IndexStore;
import com.hazelcast.query.impl.MultiResultSet;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.SingleResultSet;
import com.hazelcast.query.impl.TypeConverters;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class BaseIndexStore
implements IndexStore {
    static final float LOAD_FACTOR = 0.75f;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = this.lock.writeLock();
    private final CopyFunctor<Data, QueryableEntry> resultCopyFunctor;

    BaseIndexStore(IndexCopyBehavior copyOn) {
        this.resultCopyFunctor = copyOn == IndexCopyBehavior.COPY_ON_WRITE || copyOn == IndexCopyBehavior.NEVER ? new PassThroughFunctor() : new CopyInputFunctor();
    }

    abstract Comparable canonicalizeScalarForStorage(Comparable var1);

    void takeWriteLock() {
        this.writeLock.lock();
    }

    void releaseWriteLock() {
        this.writeLock.unlock();
    }

    void takeReadLock() {
        this.readLock.lock();
    }

    void releaseReadLock() {
        this.readLock.unlock();
    }

    final void copyToMultiResultSet(MultiResultSet resultSet, Map<Data, QueryableEntry> records) {
        resultSet.addResultSet(this.resultCopyFunctor.invoke(records));
    }

    final Set<QueryableEntry> toSingleResultSet(Map<Data, QueryableEntry> records) {
        return new SingleResultSet(this.resultCopyFunctor.invoke(records));
    }

    @Override
    public void destroy() {
    }

    Comparable sanitizeValue(Object input) {
        if (input instanceof CompositeValue) {
            CompositeValue compositeValue = (CompositeValue)input;
            Comparable[] components = compositeValue.getComponents();
            for (int i = 0; i < components.length; ++i) {
                components[i] = this.sanitizeScalar(components[i]);
            }
            return compositeValue;
        }
        return this.sanitizeScalar(input);
    }

    private Comparable sanitizeScalar(Object input) {
        if (input == null || input instanceof Comparable) {
            Comparable value = (Comparable)input;
            if (value == null) {
                value = AbstractIndex.NULL;
            } else if (value.getClass().isEnum()) {
                value = TypeConverters.ENUM_CONVERTER.convert(value);
            }
            return this.canonicalizeScalarForStorage(value);
        }
        throw new IllegalArgumentException("It is not allowed to use a type that is not Comparable: " + input.getClass());
    }

    private static class CopyInputFunctor
    implements CopyFunctor<Data, QueryableEntry> {
        private CopyInputFunctor() {
        }

        @Override
        public Map<Data, QueryableEntry> invoke(Map<Data, QueryableEntry> map) {
            if (map != null && !map.isEmpty()) {
                return new HashMap<Data, QueryableEntry>(map);
            }
            return map;
        }
    }

    private static class PassThroughFunctor
    implements CopyFunctor<Data, QueryableEntry> {
        private PassThroughFunctor() {
        }

        @Override
        public Map<Data, QueryableEntry> invoke(Map<Data, QueryableEntry> map) {
            return map;
        }
    }

    static interface IndexFunctor<A, B> {
        public Object invoke(A var1, B var2);
    }

    static interface CopyFunctor<A, B> {
        public Map<A, B> invoke(Map<A, B> var1);
    }
}

