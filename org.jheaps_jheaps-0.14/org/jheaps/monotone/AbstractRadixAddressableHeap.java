/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.monotone;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.AddressableHeap;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LogarithmicTime;

abstract class AbstractRadixAddressableHeap<K, V>
implements AddressableHeap<K, V>,
Serializable {
    private static final long serialVersionUID = 1L;
    protected static final int EMPTY = -1;
    protected Node[] buckets;
    protected long size;
    protected K lastDeletedKey;
    protected Node currentMin;
    protected K minKey;
    protected K maxKey;

    AbstractRadixAddressableHeap() {
    }

    @Override
    @ConstantTime
    public AddressableHeap.Handle<K, V> findMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        return this.currentMin;
    }

    @Override
    @ConstantTime
    public AddressableHeap.Handle<K, V> insert(K key) {
        return this.insert(key, null);
    }

    @Override
    @ConstantTime
    public AddressableHeap.Handle<K, V> insert(K key, V value) {
        int b;
        if (key == null) {
            throw new IllegalArgumentException("Null keys not permitted");
        }
        if (this.compare(key, this.maxKey) > 0) {
            throw new IllegalArgumentException("Key is more than the maximum allowed key");
        }
        if (this.compare(key, this.lastDeletedKey) < 0) {
            throw new IllegalArgumentException("Invalid key. Monotone heap.");
        }
        Node p = new Node(key, value);
        p.bucket = b = this.computeBucket(key, this.lastDeletedKey);
        if (this.buckets[b] == null) {
            this.buckets[b] = p;
        } else {
            this.buckets[b].prev = p;
            p.next = this.buckets[b];
            this.buckets[b] = p;
        }
        if (this.currentMin == null || this.compare(key, this.currentMin.key) < 0) {
            this.currentMin = p;
        }
        ++this.size;
        return p;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public AddressableHeap.Handle<K, V> deleteMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        Node result = this.currentMin;
        this.lastDeletedKey = this.currentMin.key;
        if (this.currentMin.bucket == 0) {
            Node head = this.buckets[this.currentMin.bucket];
            if (this.currentMin.next != null) {
                this.currentMin.next.prev = this.currentMin.prev;
            }
            if (this.currentMin.prev != null) {
                this.currentMin.prev.next = this.currentMin.next;
            }
            if (head == this.currentMin) {
                this.currentMin.prev = null;
                this.buckets[this.currentMin.bucket] = this.currentMin.next;
            }
            this.currentMin.next = null;
            this.currentMin.prev = null;
            this.currentMin.bucket = -1;
            this.currentMin = this.buckets[0];
            if (--this.size > 0L) {
                this.findAndCacheMinimum(0);
            }
        } else {
            Node newMin = null;
            int currentMinBucket = this.currentMin.bucket;
            Node val = this.buckets[currentMinBucket];
            while (val != null) {
                this.buckets[currentMinBucket] = val.next;
                if (this.buckets[currentMinBucket] != null) {
                    this.buckets[currentMinBucket].prev = null;
                }
                val.next = null;
                val.prev = null;
                val.bucket = -1;
                if (val != this.currentMin) {
                    int b = this.computeBucket(val.key, this.lastDeletedKey);
                    assert (b < currentMinBucket);
                    val.next = this.buckets[b];
                    if (this.buckets[b] != null) {
                        this.buckets[b].prev = val;
                    }
                    this.buckets[b] = val;
                    val.bucket = b;
                    if (newMin == null || this.compare(val.key, newMin.key) < 0) {
                        newMin = val;
                    }
                }
                val = this.buckets[currentMinBucket];
            }
            this.currentMin = newMin;
            if (--this.size > 0L) {
                this.findAndCacheMinimum(currentMinBucket + 1);
            }
        }
        return result;
    }

    @Override
    @ConstantTime
    public boolean isEmpty() {
        return this.size == 0L;
    }

    @Override
    @ConstantTime
    public long size() {
        return this.size;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.buckets.length; ++i) {
            this.buckets[i] = null;
        }
        this.size = 0L;
        this.lastDeletedKey = this.minKey;
        this.currentMin = null;
    }

    @Override
    public Comparator<? super K> comparator() {
        return null;
    }

    protected abstract int compare(K var1, K var2);

    protected int computeBucket(K key, K minKey) {
        return 1 + Math.min(this.msd(key, minKey), this.buckets.length - 2);
    }

    protected abstract int msd(K var1, K var2);

    private void findAndCacheMinimum(int firstBucket) {
        if (this.currentMin == null) {
            int currentMinBucket = -1;
            for (int i = firstBucket; i < this.buckets.length; ++i) {
                if (this.buckets[i] == null) continue;
                currentMinBucket = i;
                break;
            }
            if (currentMinBucket >= 0) {
                Node val = this.buckets[currentMinBucket];
                while (val != null) {
                    if (this.currentMin == null || this.compare(val.key, this.currentMin.key) < 0) {
                        this.currentMin = val;
                    }
                    val = val.next;
                }
            }
        }
    }

    protected class Node
    implements AddressableHeap.Handle<K, V>,
    Serializable {
        private static final long serialVersionUID = 1L;
        K key;
        V value;
        Node next;
        Node prev;
        int bucket;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
            this.prev = null;
            this.bucket = -1;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public void decreaseKey(K newKey) {
            int newBucket;
            if (AbstractRadixAddressableHeap.this.size == 0L) {
                throw new IllegalArgumentException("Invalid handle!");
            }
            if (this.bucket == -1) {
                throw new IllegalArgumentException("Invalid handle!");
            }
            if (AbstractRadixAddressableHeap.this.compare(newKey, AbstractRadixAddressableHeap.this.lastDeletedKey) < 0) {
                throw new IllegalArgumentException("Invalid key. Monotone heap.");
            }
            int c = AbstractRadixAddressableHeap.this.compare(newKey, this.key);
            if (c > 0) {
                throw new IllegalArgumentException("Keys can only be decreased!");
            }
            this.key = newKey;
            if (c == 0) {
                return;
            }
            if (this == AbstractRadixAddressableHeap.this.currentMin || AbstractRadixAddressableHeap.this.compare(this.key, AbstractRadixAddressableHeap.this.currentMin.key) < 0) {
                AbstractRadixAddressableHeap.this.currentMin = this;
            }
            if ((newBucket = AbstractRadixAddressableHeap.this.computeBucket(this.key, AbstractRadixAddressableHeap.this.lastDeletedKey)) == this.bucket) {
                return;
            }
            Node head = AbstractRadixAddressableHeap.this.buckets[this.bucket];
            if (this.next != null) {
                this.next.prev = this.prev;
            }
            if (this.prev != null) {
                this.prev.next = this.next;
            }
            if (head == this) {
                this.prev = null;
                AbstractRadixAddressableHeap.this.buckets[this.bucket] = this.next;
            }
            if (AbstractRadixAddressableHeap.this.buckets[newBucket] == null) {
                AbstractRadixAddressableHeap.this.buckets[newBucket] = this;
                this.next = null;
            } else {
                AbstractRadixAddressableHeap.this.buckets[newBucket].prev = this;
                this.next = AbstractRadixAddressableHeap.this.buckets[newBucket];
                AbstractRadixAddressableHeap.this.buckets[newBucket] = this;
            }
            this.prev = null;
            this.bucket = newBucket;
        }

        @Override
        public void delete() {
            if (AbstractRadixAddressableHeap.this.size == 0L || this.bucket == -1) {
                throw new IllegalArgumentException("Invalid handle!");
            }
            if (this == AbstractRadixAddressableHeap.this.currentMin) {
                AbstractRadixAddressableHeap.this.deleteMin();
                return;
            }
            Node head = AbstractRadixAddressableHeap.this.buckets[this.bucket];
            if (this.next != null) {
                this.next.prev = this.prev;
            }
            if (this.prev != null) {
                this.prev.next = this.next;
            }
            if (head == this) {
                AbstractRadixAddressableHeap.this.buckets[this.bucket] = this.next;
            }
            this.prev = null;
            this.next = null;
            this.bucket = -1;
            --AbstractRadixAddressableHeap.this.size;
        }
    }
}

