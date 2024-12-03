/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.util;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.jdbc.ResourceLock;
import org.postgresql.util.CanEstimateSize;
import org.postgresql.util.Gettable;

public class LruCache<Key, Value extends CanEstimateSize>
implements Gettable<Key, Value> {
    private final @Nullable EvictAction<Value> onEvict;
    private final @Nullable CreateAction<Key, Value> createAction;
    private final int maxSizeEntries;
    private final long maxSizeBytes;
    private long currentSize;
    private final Map<Key, Value> cache;
    private final ResourceLock lock = new ResourceLock();

    private void evictValue(Value value) {
        try {
            if (this.onEvict != null) {
                this.onEvict.evict(value);
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public LruCache(int maxSizeEntries, long maxSizeBytes, boolean accessOrder) {
        this(maxSizeEntries, maxSizeBytes, accessOrder, null, null);
    }

    public LruCache(int maxSizeEntries, long maxSizeBytes, boolean accessOrder, @Nullable CreateAction<Key, Value> createAction, @Nullable EvictAction<Value> onEvict) {
        this.maxSizeEntries = maxSizeEntries;
        this.maxSizeBytes = maxSizeBytes;
        this.createAction = createAction;
        this.onEvict = onEvict;
        this.cache = new LimitedMap(16, 0.75f, accessOrder);
    }

    @Override
    public @Nullable Value get(Key key) {
        try (ResourceLock ignore = this.lock.obtain();){
            CanEstimateSize canEstimateSize = (CanEstimateSize)this.cache.get(key);
            return (Value)canEstimateSize;
        }
    }

    public Value borrow(Key key) throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            CanEstimateSize value = (CanEstimateSize)this.cache.remove(key);
            if (value == null) {
                if (this.createAction == null) {
                    throw new UnsupportedOperationException("createAction == null, so can't create object");
                }
                CanEstimateSize canEstimateSize = (CanEstimateSize)this.createAction.create(key);
                return (Value)canEstimateSize;
            }
            this.currentSize -= value.getSize();
            CanEstimateSize canEstimateSize = value;
            return (Value)canEstimateSize;
        }
    }

    public void put(Key key, Value value) {
        try (ResourceLock ignore = this.lock.obtain();){
            long valueSize = value.getSize();
            if (this.maxSizeBytes == 0L || this.maxSizeEntries == 0 || valueSize * 2L > this.maxSizeBytes) {
                this.evictValue(value);
                return;
            }
            this.currentSize += valueSize;
            @Nullable CanEstimateSize prev = (CanEstimateSize)this.cache.put(key, value);
            if (prev == null) {
                return;
            }
            this.currentSize -= prev.getSize();
            if (prev != value) {
                this.evictValue(prev);
            }
        }
    }

    public void putAll(Map<Key, Value> m) {
        try (ResourceLock ignore = this.lock.obtain();){
            for (Map.Entry<Key, Value> entry : m.entrySet()) {
                this.put(entry.getKey(), (CanEstimateSize)entry.getValue());
            }
        }
    }

    private class LimitedMap
    extends LinkedHashMap<Key, Value> {
        LimitedMap(int initialCapacity, float loadFactor, boolean accessOrder) {
            super(initialCapacity, loadFactor, accessOrder);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Key, Value> eldest) {
            if (this.size() <= LruCache.this.maxSizeEntries && LruCache.this.currentSize <= LruCache.this.maxSizeBytes) {
                return false;
            }
            Iterator it = this.entrySet().iterator();
            while (it.hasNext()) {
                if (this.size() <= LruCache.this.maxSizeEntries && LruCache.this.currentSize <= LruCache.this.maxSizeBytes) {
                    return false;
                }
                Map.Entry entry = it.next();
                LruCache.this.evictValue((CanEstimateSize)entry.getValue());
                long valueSize = ((CanEstimateSize)entry.getValue()).getSize();
                if (valueSize > 0L) {
                    LruCache.this.currentSize = LruCache.this.currentSize - valueSize;
                }
                it.remove();
            }
            return false;
        }
    }

    public static interface CreateAction<Key, Value> {
        public Value create(Key var1) throws SQLException;
    }

    public static interface EvictAction<Value> {
        public void evict(Value var1) throws SQLException;
    }
}

