/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.util.AbstractDecoratedMap;
import com.twelvemonkeys.util.ExpiringMap;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class TimeoutMap<K, V>
extends AbstractDecoratedMap<K, V>
implements ExpiringMap<K, V>,
Serializable,
Cloneable {
    protected long expiryTime = 60000L;
    private volatile long nextExpiryTime = Long.MAX_VALUE;

    public TimeoutMap() {
    }

    public TimeoutMap(Map<? extends K, ? extends V> map) {
        super(map);
    }

    public TimeoutMap(long l) {
        this();
        this.expiryTime = l;
    }

    public TimeoutMap(Map<K, Map.Entry<K, V>> map, Map<? extends K, ? extends V> map2, long l) {
        super(map, map2);
        this.expiryTime = l;
    }

    public long getExpiryTime() {
        return this.expiryTime;
    }

    public void setExpiryTime(long l) {
        long l2 = this.expiryTime;
        this.expiryTime = l;
        if (this.expiryTime < l2) {
            this.nextExpiryTime = 0L;
            this.removeExpiredEntries();
        }
    }

    @Override
    public int size() {
        this.removeExpiredEntries();
        return this.entries.size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() <= 0;
    }

    @Override
    public boolean containsKey(Object object) {
        this.removeExpiredEntries();
        return this.entries.containsKey(object);
    }

    @Override
    public V get(Object object) {
        TimedEntry timedEntry = (TimedEntry)this.entries.get(object);
        if (timedEntry == null) {
            return null;
        }
        if (timedEntry.isExpired()) {
            this.entries.remove(object);
            this.processRemoved((Map.Entry)timedEntry);
            return null;
        }
        return timedEntry.getValue();
    }

    @Override
    public V put(K k, V v) {
        Object object;
        Map.Entry entry = (TimedEntry)this.entries.get(k);
        if (entry == null) {
            object = null;
            entry = this.createEntry((Object)k, (Object)v);
            this.entries.put(k, entry);
        } else {
            object = ((TimedEntry)entry).mValue;
            ((TimedEntry)entry).setValue(v);
            ((AbstractDecoratedMap.BasicEntry)entry).recordAccess(this);
        }
        this.removeExpiredEntries();
        ++this.modCount;
        return (V)object;
    }

    @Override
    public V remove(Object object) {
        TimedEntry timedEntry = (TimedEntry)this.entries.remove(object);
        return timedEntry != null ? (V)timedEntry.getValue() : null;
    }

    @Override
    public void clear() {
        this.entries.clear();
        this.init();
    }

    TimedEntry createEntry(K k, V v) {
        return new TimedEntry(k, v);
    }

    protected void removeExpiredEntries() {
        long l = System.currentTimeMillis();
        if (l > this.nextExpiryTime) {
            this.removeExpiredEntriesSynced(l);
        }
    }

    private synchronized void removeExpiredEntriesSynced(long l) {
        if (l > this.nextExpiryTime) {
            long l2;
            this.nextExpiryTime = l2 = Long.MAX_VALUE;
            EntryIterator entryIterator = new EntryIterator();
            while (entryIterator.hasNext()) {
                TimedEntry timedEntry = (TimedEntry)entryIterator.next();
                long l3 = timedEntry.expires();
                if (l3 >= l2) continue;
                l2 = l3;
            }
            this.nextExpiryTime = l2;
        }
    }

    @Override
    public Collection<V> values() {
        this.removeExpiredEntries();
        return super.values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        this.removeExpiredEntries();
        return super.entrySet();
    }

    @Override
    public Set<K> keySet() {
        this.removeExpiredEntries();
        return super.keySet();
    }

    @Override
    protected Iterator<K> newKeyIterator() {
        return new KeyIterator();
    }

    @Override
    protected Iterator<V> newValueIterator() {
        return new ValueIterator();
    }

    @Override
    protected Iterator<Map.Entry<K, V>> newEntryIterator() {
        return new EntryIterator();
    }

    @Override
    public void processRemoved(Map.Entry entry) {
    }

    private class TimedEntry
    extends AbstractDecoratedMap.BasicEntry<K, V> {
        private long mTimestamp;

        TimedEntry(K k, V v) {
            super(k, v);
            this.updateTimestamp();
        }

        @Override
        public V setValue(V v) {
            this.updateTimestamp();
            return super.setValue(v);
        }

        private void updateTimestamp() {
            this.mTimestamp = System.currentTimeMillis();
            long l = this.expires();
            if (l < TimeoutMap.this.nextExpiryTime) {
                TimeoutMap.this.nextExpiryTime = l;
            }
        }

        final boolean isExpired() {
            return this.isExpiredBy(System.currentTimeMillis());
        }

        final boolean isExpiredBy(long l) {
            return l > this.expires();
        }

        final long expires() {
            return this.mTimestamp + TimeoutMap.this.expiryTime;
        }
    }

    private class EntryIterator
    extends TimeoutMapIterator<Map.Entry<K, V>> {
        private EntryIterator() {
        }

        @Override
        public Map.Entry<K, V> next() {
            return this.nextEntry();
        }
    }

    private class ValueIterator
    extends TimeoutMapIterator<V> {
        private ValueIterator() {
        }

        @Override
        public V next() {
            return this.nextEntry().mValue;
        }
    }

    private class KeyIterator
    extends TimeoutMapIterator<K> {
        private KeyIterator() {
        }

        @Override
        public K next() {
            return this.nextEntry().mKey;
        }
    }

    private abstract class TimeoutMapIterator<E>
    implements Iterator<E> {
        Iterator<Map.Entry<K, Map.Entry<K, V>>> mIterator;
        AbstractDecoratedMap.BasicEntry<K, V> mNext;
        long mNow;

        private TimeoutMapIterator() {
            this.mIterator = TimeoutMap.this.entries.entrySet().iterator();
            this.mNow = System.currentTimeMillis();
        }

        @Override
        public void remove() {
            this.mNext = null;
            this.mIterator.remove();
        }

        @Override
        public boolean hasNext() {
            if (this.mNext != null) {
                return true;
            }
            while (this.mNext == null && this.mIterator.hasNext()) {
                Map.Entry entry = this.mIterator.next();
                TimedEntry timedEntry = (TimedEntry)entry.getValue();
                if (timedEntry.isExpiredBy(this.mNow)) {
                    this.mIterator.remove();
                    TimeoutMap.this.processRemoved((Map.Entry)timedEntry);
                    continue;
                }
                this.mNext = timedEntry;
                return true;
            }
            return false;
        }

        AbstractDecoratedMap.BasicEntry<K, V> nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            AbstractDecoratedMap.BasicEntry basicEntry = this.mNext;
            this.mNext = null;
            return basicEntry;
        }
    }
}

