/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

import com.sun.jersey.core.util.KeyComparator;
import com.sun.jersey.impl.ImplMessages;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class KeyComparatorHashMap<K, V>
extends AbstractMap<K, V>
implements Map<K, V>,
Cloneable,
Serializable {
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final int MAXIMUM_CAPACITY = 0x40000000;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    transient Entry<K, V>[] table;
    transient int size;
    int threshold;
    final float loadFactor;
    volatile transient int modCount;
    final KeyComparator<K> keyComparator;
    static final Object NULL_KEY = new Object();
    private transient Set<Map.Entry<K, V>> entrySet = null;

    public int getDEFAULT_INITIAL_CAPACITY() {
        return 16;
    }

    public KeyComparatorHashMap(int initialCapacity, float loadFactor, KeyComparator<K> keyComparator) {
        int capacity;
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(ImplMessages.ILLEGAL_INITIAL_CAPACITY(initialCapacity));
        }
        if (initialCapacity > 0x40000000) {
            initialCapacity = 0x40000000;
        }
        if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException(ImplMessages.ILLEGAL_LOAD_FACTOR(Float.valueOf(loadFactor)));
        }
        for (capacity = 1; capacity < initialCapacity; capacity <<= 1) {
        }
        this.loadFactor = loadFactor;
        this.threshold = (int)((float)capacity * loadFactor);
        this.table = new Entry[capacity];
        this.init();
        this.keyComparator = keyComparator;
    }

    public KeyComparatorHashMap(int initialCapacity, KeyComparator<K> keyComparator) {
        this(initialCapacity, 0.75f, keyComparator);
    }

    public KeyComparatorHashMap(KeyComparator<K> keyComparator) {
        this.loadFactor = 0.75f;
        this.threshold = 12;
        this.table = new Entry[16];
        this.init();
        this.keyComparator = keyComparator;
    }

    public KeyComparatorHashMap(Map<? extends K, ? extends V> m, KeyComparator<K> keyComparator) {
        this(Math.max((int)((float)m.size() / 0.75f) + 1, 16), 0.75f, keyComparator);
        this.putAllForCreate(m);
    }

    public int getModCount() {
        return this.modCount;
    }

    void init() {
    }

    static <T> T maskNull(T key) {
        return (T)(key == null ? NULL_KEY : key);
    }

    static <T> boolean isNull(T key) {
        return key == NULL_KEY;
    }

    static <T> T unmaskNull(T key) {
        return key == NULL_KEY ? null : (T)key;
    }

    static int hash(Object x) {
        int h = x.hashCode();
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }

    static boolean eq(Object x, Object y) {
        return x == y || x.equals(y);
    }

    static int indexFor(int h, int length) {
        return h & length - 1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    int keyComparatorHash(K k) {
        return KeyComparatorHashMap.isNull(k) ? this.hash(k.hashCode()) : this.hash(this.keyComparator.hash(k));
    }

    int hash(int h) {
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }

    boolean keyComparatorEq(K x, K y) {
        if (KeyComparatorHashMap.isNull(x)) {
            return x == y;
        }
        if (KeyComparatorHashMap.isNull(y)) {
            return x == y;
        }
        return x == y || this.keyComparator.equals(x, y);
    }

    @Override
    public V get(Object key) {
        Object k = KeyComparatorHashMap.maskNull(key);
        int hash = this.keyComparatorHash(k);
        int i = KeyComparatorHashMap.indexFor(hash, this.table.length);
        Entry<K, V> e = this.table[i];
        while (e != null) {
            if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        Object k = KeyComparatorHashMap.maskNull(key);
        int hash = this.keyComparatorHash(k);
        int i = KeyComparatorHashMap.indexFor(hash, this.table.length);
        Entry<K, V> e = this.table[i];
        while (e != null) {
            if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
                return true;
            }
            e = e.next;
        }
        return false;
    }

    Entry<K, V> getEntry(K key) {
        K k = KeyComparatorHashMap.maskNull(key);
        int hash = this.keyComparatorHash(k);
        int i = KeyComparatorHashMap.indexFor(hash, this.table.length);
        Entry<K, V> e = this.table[i];
        while (!(e == null || e.hash == hash && this.keyComparatorEq(k, e.key))) {
            e = e.next;
        }
        return e;
    }

    @Override
    public V put(K key, V value) {
        K k = KeyComparatorHashMap.maskNull(key);
        int hash = this.keyComparatorHash(k);
        int i = KeyComparatorHashMap.indexFor(hash, this.table.length);
        Entry<K, V> e = this.table[i];
        while (e != null) {
            if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
                Object oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
            e = e.next;
        }
        ++this.modCount;
        this.addEntry(hash, k, value, i);
        return null;
    }

    private void putForCreate(K key, V value) {
        K k = KeyComparatorHashMap.maskNull(key);
        int hash = this.keyComparatorHash(k);
        int i = KeyComparatorHashMap.indexFor(hash, this.table.length);
        Entry<K, V> e = this.table[i];
        while (e != null) {
            if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
                e.value = value;
                return;
            }
            e = e.next;
        }
        this.createEntry(hash, k, value, i);
    }

    void putAllForCreate(Map<? extends K, ? extends V> m) {
        for (Map.Entry<K, V> e : m.entrySet()) {
            this.putForCreate(e.getKey(), e.getValue());
        }
    }

    void resize(int newCapacity) {
        Entry<K, V>[] oldTable = this.table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == 0x40000000) {
            this.threshold = Integer.MAX_VALUE;
            return;
        }
        Entry[] newTable = new Entry[newCapacity];
        this.transfer(newTable);
        this.table = newTable;
        this.threshold = (int)((float)newCapacity * this.loadFactor);
    }

    void transfer(Entry<K, V>[] newTable) {
        Entry<K, V>[] src = this.table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; ++j) {
            Entry next;
            Entry<K, V> e = src[j];
            if (e == null) continue;
            src[j] = null;
            do {
                next = e.next;
                int i = KeyComparatorHashMap.indexFor(e.hash, newCapacity);
                e.next = newTable[i];
                newTable[i] = e;
            } while ((e = next) != null);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0) {
            return;
        }
        if (numKeysToBeAdded > this.threshold) {
            int newCapacity;
            int targetCapacity = (int)((float)numKeysToBeAdded / this.loadFactor + 1.0f);
            if (targetCapacity > 0x40000000) {
                targetCapacity = 0x40000000;
            }
            for (newCapacity = this.table.length; newCapacity < targetCapacity; newCapacity <<= 1) {
            }
            if (newCapacity > this.table.length) {
                this.resize(newCapacity);
            }
        }
        for (Map.Entry<K, V> e : m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        Entry<K, V> e = this.removeEntryForKey(key);
        return e == null ? null : (V)e.value;
    }

    Entry<K, V> removeEntryForKey(Object key) {
        Entry<K, V> prev;
        Object k = KeyComparatorHashMap.maskNull(key);
        int hash = this.keyComparatorHash(k);
        int i = KeyComparatorHashMap.indexFor(hash, this.table.length);
        Entry<K, V> e = prev = this.table[i];
        while (e != null) {
            Entry next = e.next;
            if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
                ++this.modCount;
                --this.size;
                if (prev == e) {
                    this.table[i] = next;
                } else {
                    prev.next = next;
                }
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }
        return e;
    }

    Entry<K, V> removeMapping(Object o) {
        Entry<K, V> prev;
        if (!(o instanceof Map.Entry)) {
            return null;
        }
        Map.Entry entry = (Map.Entry)o;
        Object k = KeyComparatorHashMap.maskNull(entry.getKey());
        int hash = this.keyComparatorHash(k);
        int i = KeyComparatorHashMap.indexFor(hash, this.table.length);
        Entry<K, V> e = prev = this.table[i];
        while (e != null) {
            Entry next = e.next;
            if (e.hash == hash && e.equals(entry)) {
                ++this.modCount;
                --this.size;
                if (prev == e) {
                    this.table[i] = next;
                } else {
                    prev.next = next;
                }
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }
        return e;
    }

    @Override
    public void clear() {
        ++this.modCount;
        Entry<K, V>[] tab = this.table;
        for (int i = 0; i < tab.length; ++i) {
            tab[i] = null;
        }
        this.size = 0;
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            return this.containsNullValue();
        }
        Entry<K, V>[] tab = this.table;
        for (int i = 0; i < tab.length; ++i) {
            Entry<K, V> e = tab[i];
            while (e != null) {
                if (value.equals(e.value)) {
                    return true;
                }
                e = e.next;
            }
        }
        return false;
    }

    private boolean containsNullValue() {
        Entry<K, V>[] tab = this.table;
        for (int i = 0; i < tab.length; ++i) {
            Entry<K, V> e = tab[i];
            while (e != null) {
                if (e.value == null) {
                    return true;
                }
                e = e.next;
            }
        }
        return false;
    }

    @Override
    public Object clone() {
        KeyComparatorHashMap result = null;
        try {
            result = (KeyComparatorHashMap)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        result.table = new Entry[this.table.length];
        result.entrySet = null;
        result.modCount = 0;
        result.size = 0;
        result.init();
        result.putAllForCreate(this);
        return result;
    }

    void addEntry(int hash, K key, V value, int bucketIndex) {
        Entry<K, V> e = this.table[bucketIndex];
        this.table[bucketIndex] = new Entry<K, V>(hash, key, value, e);
        if (this.size++ >= this.threshold) {
            this.resize(2 * this.table.length);
        }
    }

    void createEntry(int hash, K key, V value, int bucketIndex) {
        Entry<K, V> e = this.table[bucketIndex];
        this.table[bucketIndex] = new Entry<K, V>(hash, key, value, e);
        ++this.size;
    }

    Iterator<K> newKeyIterator() {
        return new KeyIterator();
    }

    Iterator<V> newValueIterator() {
        return new ValueIterator();
    }

    Iterator<Map.Entry<K, V>> newEntryIterator() {
        return new EntryIterator();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        EntrySet es = this.entrySet;
        return es != null ? es : (this.entrySet = new EntrySet());
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        Iterator<Map.Entry<K, V>> i = this.entrySet().iterator();
        s.defaultWriteObject();
        s.writeInt(this.table.length);
        s.writeInt(this.size);
        while (i.hasNext()) {
            Map.Entry<K, V> e = i.next();
            s.writeObject(e.getKey());
            s.writeObject(e.getValue());
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int numBuckets = s.readInt();
        this.table = new Entry[numBuckets];
        this.init();
        int size = s.readInt();
        for (int i = 0; i < size; ++i) {
            Object key = s.readObject();
            Object value = s.readObject();
            this.putForCreate(key, value);
        }
    }

    int capacity() {
        return this.table.length;
    }

    float loadFactor() {
        return this.loadFactor;
    }

    private class EntrySet
    extends AbstractSet {
        private EntrySet() {
        }

        @Override
        public Iterator iterator() {
            return KeyComparatorHashMap.this.newEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            Entry candidate = KeyComparatorHashMap.this.getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }

        @Override
        public boolean remove(Object o) {
            return KeyComparatorHashMap.this.removeMapping(o) != null;
        }

        @Override
        public int size() {
            return KeyComparatorHashMap.this.size;
        }

        @Override
        public void clear() {
            KeyComparatorHashMap.this.clear();
        }
    }

    private class EntryIterator
    extends HashIterator<Map.Entry<K, V>> {
        private EntryIterator() {
        }

        @Override
        public Map.Entry<K, V> next() {
            return this.nextEntry();
        }
    }

    private class KeyIterator
    extends HashIterator<K> {
        private KeyIterator() {
        }

        @Override
        public K next() {
            return this.nextEntry().getKey();
        }
    }

    private class ValueIterator
    extends HashIterator<V> {
        private ValueIterator() {
        }

        @Override
        public V next() {
            return this.nextEntry().value;
        }
    }

    private abstract class HashIterator<E>
    implements Iterator<E> {
        Entry<K, V> next;
        int expectedModCount;
        int index;
        Entry<K, V> current;

        HashIterator() {
            this.expectedModCount = KeyComparatorHashMap.this.modCount;
            Entry<K, V>[] t = KeyComparatorHashMap.this.table;
            int i = t.length;
            Entry n = null;
            if (KeyComparatorHashMap.this.size != 0) {
                while (i > 0 && (n = t[--i]) == null) {
                }
            }
            this.next = n;
            this.index = i;
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        Entry<K, V> nextEntry() {
            if (KeyComparatorHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            Entry e = this.next;
            if (e == null) {
                throw new NoSuchElementException();
            }
            Entry n = e.next;
            Entry<K, V>[] t = KeyComparatorHashMap.this.table;
            int i = this.index;
            while (n == null && i > 0) {
                n = t[--i];
            }
            this.index = i;
            this.next = n;
            this.current = e;
            return this.current;
        }

        @Override
        public void remove() {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            if (KeyComparatorHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            Object k = this.current.key;
            this.current = null;
            KeyComparatorHashMap.this.removeEntryForKey(k);
            this.expectedModCount = KeyComparatorHashMap.this.modCount;
        }
    }

    static class Entry<K, V>
    implements Map.Entry<K, V> {
        final K key;
        V value;
        final int hash;
        Entry<K, V> next;

        Entry(int h, K k, V v, Entry<K, V> n) {
            this.value = v;
            this.next = n;
            this.key = k;
            this.hash = h;
        }

        @Override
        public K getKey() {
            return KeyComparatorHashMap.unmaskNull(this.key);
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V newValue) {
            V oldValue = this.value;
            this.value = newValue;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            Object v2;
            V v1;
            Object k2;
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            K k1 = this.getKey();
            return (k1 == (k2 = e.getKey()) || k1 != null && k1.equals(k2)) && ((v1 = this.getValue()) == (v2 = e.getValue()) || v1 != null && v1.equals(v2));
        }

        @Override
        public int hashCode() {
            return (this.key == NULL_KEY ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.getKey() + "=" + this.getValue();
        }

        void recordAccess(KeyComparatorHashMap<K, V> m) {
        }

        void recordRemoval(KeyComparatorHashMap<K, V> m) {
        }
    }
}

