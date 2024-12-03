/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import org.codehaus.groovy.util.AbstractConcurrentMapBase;

public abstract class AbstractConcurrentMap<K, V>
extends AbstractConcurrentMapBase {
    public AbstractConcurrentMap(Object segmentInfo) {
        super(segmentInfo);
    }

    @Override
    public Segment segmentFor(int hash) {
        return (Segment)super.segmentFor(hash);
    }

    public V get(K key) {
        int hash = AbstractConcurrentMap.hash(key);
        return this.segmentFor(hash).get(key, hash);
    }

    public Entry<K, V> getOrPut(K key, V value) {
        int hash = AbstractConcurrentMap.hash(key);
        return this.segmentFor(hash).getOrPut(key, hash, value);
    }

    public void put(K key, V value) {
        int hash = AbstractConcurrentMap.hash(key);
        this.segmentFor(hash).put(key, hash, value);
    }

    public void remove(K key) {
        int hash = AbstractConcurrentMap.hash(key);
        this.segmentFor(hash).remove(key, hash);
    }

    public static interface Entry<K, V>
    extends AbstractConcurrentMapBase.Entry<V> {
        public boolean isEqual(K var1, int var2);
    }

    public static abstract class Segment<K, V>
    extends AbstractConcurrentMapBase.Segment {
        protected Segment(int initialCapacity) {
            super(initialCapacity);
        }

        public final V get(K key, int hash) {
            Object[] tab = this.table;
            Object o = tab[hash & tab.length - 1];
            if (o != null) {
                if (o instanceof Entry) {
                    Entry e = (Entry)o;
                    if (e.isEqual(key, hash)) {
                        return e.getValue();
                    }
                } else {
                    Object[] arr = (Object[])o;
                    for (int i = 0; i < arr.length; ++i) {
                        Entry e = (Entry)arr[i];
                        if (e == null || !e.isEqual(key, hash)) continue;
                        return e.getValue();
                    }
                }
            }
            return null;
        }

        public final Entry<K, V> getOrPut(K key, int hash, V value) {
            Object[] tab = this.table;
            Object o = tab[hash & tab.length - 1];
            if (o != null) {
                if (o instanceof Entry) {
                    Entry e = (Entry)o;
                    if (e.isEqual(key, hash)) {
                        return e;
                    }
                } else {
                    Object[] arr = (Object[])o;
                    for (int i = 0; i < arr.length; ++i) {
                        Entry e = (Entry)arr[i];
                        if (e == null || !e.isEqual(key, hash)) continue;
                        return e;
                    }
                }
            }
            return this.put(key, hash, value);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public final Entry put(K key, int hash, V value) {
            this.lock();
            try {
                Entry<K, V> e;
                this.rehashIfThresholdExceeded();
                Object[] tab = this.table;
                int index = hash & tab.length - 1;
                Object o = tab[index];
                if (o != null) {
                    if (o instanceof Entry) {
                        Entry e2 = (Entry)o;
                        if (e2.isEqual(key, hash)) {
                            e2.setValue(value);
                            Entry entry = e2;
                            return entry;
                        }
                        Entry<K, V> ee = this.createEntry(key, hash, value);
                        Object[] arr = new Object[]{ee, e2};
                        tab[index] = arr;
                        ++this.count;
                        Entry<K, V> entry = ee;
                        return entry;
                    }
                    Object[] arr = (Object[])o;
                    for (int i = 0; i < arr.length; ++i) {
                        Entry e3 = (Entry)arr[i];
                        if (e3 == null || !e3.isEqual(key, hash)) continue;
                        e3.setValue(value);
                        Entry entry = e3;
                        return entry;
                    }
                    Entry<K, V> ee = this.createEntry(key, hash, value);
                    for (int i = 0; i < arr.length; ++i) {
                        Entry e4 = (Entry)arr[i];
                        if (e4 != null) continue;
                        arr[i] = ee;
                        ++this.count;
                        Entry<K, V> entry = ee;
                        return entry;
                    }
                    Object[] newArr = new Object[arr.length + 1];
                    newArr[0] = ee;
                    System.arraycopy(arr, 0, newArr, 1, arr.length);
                    tab[index] = newArr;
                    ++this.count;
                    Entry<K, V> entry = ee;
                    return entry;
                }
                tab[index] = e = this.createEntry(key, hash, value);
                ++this.count;
                Entry<K, V> entry = e;
                return entry;
            }
            finally {
                this.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void remove(K key, int hash) {
            block6: {
                this.lock();
                try {
                    int c = this.count - 1;
                    Object[] tab = this.table;
                    int index = hash & tab.length - 1;
                    Object o = tab[index];
                    if (o == null) break block6;
                    if (o instanceof Entry) {
                        if (((Entry)o).isEqual(key, hash)) {
                            tab[index] = null;
                            this.count = c;
                        }
                        break block6;
                    }
                    Object[] arr = (Object[])o;
                    for (int i = 0; i < arr.length; ++i) {
                        Entry e = (Entry)arr[i];
                        if (e == null || !e.isEqual(key, hash)) continue;
                        arr[i] = null;
                        this.count = c;
                        break;
                    }
                }
                finally {
                    this.unlock();
                }
            }
        }

        protected abstract Entry<K, V> createEntry(K var1, int var2, V var3);
    }
}

