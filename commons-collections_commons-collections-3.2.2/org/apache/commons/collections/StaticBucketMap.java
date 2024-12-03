/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections.KeyValue;

public final class StaticBucketMap
implements Map {
    private static final int DEFAULT_BUCKETS = 255;
    private Node[] m_buckets;
    private Lock[] m_locks;

    public StaticBucketMap() {
        this(255);
    }

    public StaticBucketMap(int numBuckets) {
        int size = Math.max(17, numBuckets);
        if (size % 2 == 0) {
            --size;
        }
        this.m_buckets = new Node[size];
        this.m_locks = new Lock[size];
        for (int i = 0; i < size; ++i) {
            this.m_locks[i] = new Lock();
        }
    }

    private final int getHash(Object key) {
        if (key == null) {
            return 0;
        }
        int hash = key.hashCode();
        hash += ~(hash << 15);
        hash ^= hash >>> 10;
        hash += hash << 3;
        hash ^= hash >>> 6;
        hash += ~(hash << 11);
        hash ^= hash >>> 16;
        return (hash %= this.m_buckets.length) < 0 ? hash * -1 : hash;
    }

    public Set keySet() {
        return new KeySet();
    }

    public int size() {
        int cnt = 0;
        for (int i = 0; i < this.m_buckets.length; ++i) {
            cnt += this.m_locks[i].size;
        }
        return cnt;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object put(Object key, Object value) {
        int hash = this.getHash(key);
        Lock lock = this.m_locks[hash];
        synchronized (lock) {
            Node n = this.m_buckets[hash];
            if (n == null) {
                n = new Node();
                n.key = key;
                n.value = value;
                this.m_buckets[hash] = n;
                ++this.m_locks[hash].size;
                return null;
            }
            Node next = n;
            while (next != null) {
                n = next;
                if (n.key == key || n.key != null && n.key.equals(key)) {
                    Object returnVal = n.value;
                    n.value = value;
                    return returnVal;
                }
                next = next.next;
            }
            Node newNode = new Node();
            newNode.key = key;
            newNode.value = value;
            n.next = newNode;
            ++this.m_locks[hash].size;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object get(Object key) {
        int hash = this.getHash(key);
        Lock lock = this.m_locks[hash];
        synchronized (lock) {
            Node n = this.m_buckets[hash];
            while (n != null) {
                if (n.key == key || n.key != null && n.key.equals(key)) {
                    return n.value;
                }
                n = n.next;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsKey(Object key) {
        int hash = this.getHash(key);
        Lock lock = this.m_locks[hash];
        synchronized (lock) {
            Node n = this.m_buckets[hash];
            while (n != null) {
                if (n.key == key || n.key != null && n.key.equals(key)) {
                    return true;
                }
                n = n.next;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsValue(Object value) {
        for (int i = 0; i < this.m_buckets.length; ++i) {
            Lock lock = this.m_locks[i];
            synchronized (lock) {
                Node n = this.m_buckets[i];
                while (n != null) {
                    if (n.value == value || n.value != null && n.value.equals(value)) {
                        return true;
                    }
                    n = n.next;
                }
                continue;
            }
        }
        return false;
    }

    public Collection values() {
        return new Values();
    }

    public Set entrySet() {
        return new EntrySet();
    }

    public void putAll(Map other) {
        Iterator i = other.keySet().iterator();
        while (i.hasNext()) {
            Object key = i.next();
            this.put(key, other.get(key));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object remove(Object key) {
        int hash = this.getHash(key);
        Lock lock = this.m_locks[hash];
        synchronized (lock) {
            Node n = this.m_buckets[hash];
            Node prev = null;
            while (n != null) {
                if (n.key == key || n.key != null && n.key.equals(key)) {
                    if (null == prev) {
                        this.m_buckets[hash] = n.next;
                    } else {
                        prev.next = n.next;
                    }
                    --this.m_locks[hash].size;
                    return n.value;
                }
                prev = n;
                n = n.next;
            }
        }
        return null;
    }

    public final boolean isEmpty() {
        return this.size() == 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void clear() {
        for (int i = 0; i < this.m_buckets.length; ++i) {
            Lock lock;
            Lock lock2 = lock = this.m_locks[i];
            synchronized (lock2) {
                this.m_buckets[i] = null;
                lock.size = 0;
                continue;
            }
        }
    }

    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        Map other = (Map)obj;
        return this.entrySet().equals(other.entrySet());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final int hashCode() {
        int hashCode = 0;
        for (int i = 0; i < this.m_buckets.length; ++i) {
            Lock lock = this.m_locks[i];
            synchronized (lock) {
                Node n = this.m_buckets[i];
                while (n != null) {
                    hashCode += n.hashCode();
                    n = n.next;
                }
                continue;
            }
        }
        return hashCode;
    }

    public void atomic(Runnable r) {
        if (r == null) {
            throw new NullPointerException();
        }
        this.atomic(r, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void atomic(Runnable r, int bucket) {
        if (bucket >= this.m_buckets.length) {
            r.run();
            return;
        }
        Lock lock = this.m_locks[bucket];
        synchronized (lock) {
            this.atomic(r, bucket + 1);
        }
    }

    private class Values
    extends AbstractCollection {
        private Values() {
        }

        public int size() {
            return StaticBucketMap.this.size();
        }

        public void clear() {
            StaticBucketMap.this.clear();
        }

        public Iterator iterator() {
            return new ValueIterator();
        }
    }

    private class KeySet
    extends AbstractSet {
        private KeySet() {
        }

        public int size() {
            return StaticBucketMap.this.size();
        }

        public void clear() {
            StaticBucketMap.this.clear();
        }

        public Iterator iterator() {
            return new KeyIterator();
        }

        public boolean contains(Object o) {
            return StaticBucketMap.this.containsKey(o);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean remove(Object o) {
            int hash = StaticBucketMap.this.getHash(o);
            Lock lock = StaticBucketMap.this.m_locks[hash];
            synchronized (lock) {
                Node n = StaticBucketMap.this.m_buckets[hash];
                while (n != null) {
                    Object k = n.getKey();
                    if (k == o || k != null && k.equals(o)) {
                        StaticBucketMap.this.remove(k);
                        return true;
                    }
                    n = n.next;
                }
            }
            return false;
        }
    }

    private class EntrySet
    extends AbstractSet {
        private EntrySet() {
        }

        public int size() {
            return StaticBucketMap.this.size();
        }

        public void clear() {
            StaticBucketMap.this.clear();
        }

        public Iterator iterator() {
            return new EntryIterator();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean contains(Object o) {
            Map.Entry entry = (Map.Entry)o;
            int hash = StaticBucketMap.this.getHash(entry.getKey());
            Lock lock = StaticBucketMap.this.m_locks[hash];
            synchronized (lock) {
                Node n = StaticBucketMap.this.m_buckets[hash];
                while (n != null) {
                    if (n.equals(entry)) {
                        return true;
                    }
                    n = n.next;
                }
            }
            return false;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry)obj;
            int hash = StaticBucketMap.this.getHash(entry.getKey());
            Lock lock = StaticBucketMap.this.m_locks[hash];
            synchronized (lock) {
                Node n = StaticBucketMap.this.m_buckets[hash];
                while (n != null) {
                    if (n.equals(entry)) {
                        StaticBucketMap.this.remove(n.getKey());
                        return true;
                    }
                    n = n.next;
                }
            }
            return false;
        }
    }

    private class KeyIterator
    extends EntryIterator {
        private KeyIterator() {
        }

        public Object next() {
            return this.nextEntry().getKey();
        }
    }

    private class ValueIterator
    extends EntryIterator {
        private ValueIterator() {
        }

        public Object next() {
            return this.nextEntry().getValue();
        }
    }

    private class EntryIterator
    implements Iterator {
        private ArrayList current = new ArrayList();
        private int bucket;
        private Map.Entry last;

        private EntryIterator() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean hasNext() {
            if (this.current.size() > 0) {
                return true;
            }
            while (this.bucket < StaticBucketMap.this.m_buckets.length) {
                Lock lock = StaticBucketMap.this.m_locks[this.bucket];
                synchronized (lock) {
                    Node n = StaticBucketMap.this.m_buckets[this.bucket];
                    while (n != null) {
                        this.current.add(n);
                        n = n.next;
                    }
                    ++this.bucket;
                    if (this.current.size() > 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        protected Map.Entry nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.last = (Map.Entry)this.current.remove(this.current.size() - 1);
            return this.last;
        }

        public Object next() {
            return this.nextEntry();
        }

        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException();
            }
            StaticBucketMap.this.remove(this.last.getKey());
            this.last = null;
        }
    }

    private static final class Lock {
        public int size;

        private Lock() {
        }
    }

    private static final class Node
    implements Map.Entry,
    KeyValue {
        protected Object key;
        protected Object value;
        protected Node next;

        private Node() {
        }

        public Object getKey() {
            return this.key;
        }

        public Object getValue() {
            return this.value;
        }

        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o == this) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o;
            return (this.key == null ? e2.getKey() == null : this.key.equals(e2.getKey())) && (this.value == null ? e2.getValue() == null : this.value.equals(e2.getValue()));
        }

        public Object setValue(Object val) {
            Object retVal = this.value;
            this.value = val;
            return retVal;
        }
    }
}

