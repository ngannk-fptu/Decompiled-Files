/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.tree;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.AddressableHeap;
import org.jheaps.AddressableHeapFactory;
import org.jheaps.DoubleEndedAddressableHeap;
import org.jheaps.MergeableAddressableHeap;
import org.jheaps.MergeableDoubleEndedAddressableHeap;

public class ReflectedHeap<K, V>
implements MergeableDoubleEndedAddressableHeap<K, V>,
Serializable {
    private static final long serialVersionUID = -5428954082047233961L;
    private final Comparator<? super K> comparator;
    private final AddressableHeap<K, HandleMap<K, V>> minHeap;
    private final AddressableHeap<K, HandleMap<K, V>> maxHeap;
    private ReflectedHandle<K, V> free;
    private long size;
    private ReflectedHeap<K, V> other;

    public ReflectedHeap(AddressableHeapFactory<K, ?> heapFactory) {
        this(heapFactory, null);
    }

    public ReflectedHeap(AddressableHeapFactory<K, ?> heapFactory, Comparator<? super K> comparator) {
        if (heapFactory == null) {
            throw new NullPointerException("Underlying heap factory cannot be null");
        }
        this.comparator = comparator;
        this.minHeap = heapFactory.get(comparator);
        this.maxHeap = heapFactory.get(Collections.reverseOrder(comparator));
        this.free = null;
        this.size = 0L;
        this.other = this;
    }

    @Override
    public Comparator<? super K> comparator() {
        return this.comparator;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0L;
    }

    @Override
    public long size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0L;
        this.free = null;
        this.minHeap.clear();
        this.maxHeap.clear();
    }

    @Override
    public DoubleEndedAddressableHeap.Handle<K, V> insert(K key, V value) {
        if (key == null) {
            throw new NullPointerException("Null keys not permitted");
        }
        if (this.other != this) {
            throw new IllegalStateException("A heap cannot be used after a meld");
        }
        if (this.size % 2L == 0L) {
            this.free = new ReflectedHandle<K, V>(this, key, value);
            ++this.size;
            return this.free;
        }
        ReflectedHandle<K, V> newHandle = new ReflectedHandle<K, V>(this, key, value);
        this.insertPair(newHandle, this.free);
        this.free = null;
        ++this.size;
        return newHandle;
    }

    @Override
    public DoubleEndedAddressableHeap.Handle<K, V> insert(K key) {
        return this.insert((Object)key, (Object)null);
    }

    @Override
    public DoubleEndedAddressableHeap.Handle<K, V> findMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        if (this.size == 1L) {
            return this.free;
        }
        if (this.size % 2L == 0L) {
            return this.minHeap.findMin().getValue().outer;
        }
        AddressableHeap.Handle<K, HandleMap<K, V>> minInnerHandle = this.minHeap.findMin();
        int c = this.comparator == null ? ((Comparable)minInnerHandle.getKey()).compareTo(this.free.key) : this.comparator.compare(minInnerHandle.getKey(), this.free.key);
        if (c < 0) {
            return minInnerHandle.getValue().outer;
        }
        return this.free;
    }

    @Override
    public DoubleEndedAddressableHeap.Handle<K, V> findMax() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        if (this.size == 1L) {
            return this.free;
        }
        if (this.size % 2L == 0L) {
            return this.maxHeap.findMin().getValue().outer;
        }
        AddressableHeap.Handle<K, HandleMap<K, V>> maxInnerHandle = this.maxHeap.findMin();
        int c = this.comparator == null ? ((Comparable)maxInnerHandle.getKey()).compareTo(this.free.key) : this.comparator.compare(maxInnerHandle.getKey(), this.free.key);
        if (c > 0) {
            return maxInnerHandle.getValue().outer;
        }
        return this.free;
    }

    @Override
    public DoubleEndedAddressableHeap.Handle<K, V> deleteMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        if (this.size == 1L) {
            ReflectedHandle<K, V> min = this.free;
            this.free = null;
            --this.size;
            return min;
        }
        if (this.size % 2L == 0L) {
            AddressableHeap.Handle<K, HandleMap<K, V>> minInner = this.minHeap.deleteMin();
            ReflectedHandle minOuter = minInner.getValue().outer;
            minOuter.inner = null;
            minOuter.minNotMax = false;
            AddressableHeap.Handle maxInner = minInner.getValue().otherInner;
            ReflectedHandle maxOuter = maxInner.getValue().outer;
            maxInner.delete();
            maxOuter.inner = null;
            maxOuter.minNotMax = false;
            this.free = maxOuter;
            --this.size;
            return minOuter;
        }
        AddressableHeap.Handle<K, HandleMap<K, V>> minInner = this.minHeap.findMin();
        int c = this.comparator == null ? ((Comparable)minInner.getKey()).compareTo(this.free.key) : this.comparator.compare(minInner.getKey(), this.free.key);
        if (c >= 0) {
            ReflectedHandle<K, V> min = this.free;
            this.free = null;
            --this.size;
            return min;
        }
        minInner.delete();
        ReflectedHandle minOuter = minInner.getValue().outer;
        minOuter.inner = null;
        minOuter.minNotMax = false;
        AddressableHeap.Handle maxInner = minInner.getValue().otherInner;
        ReflectedHandle maxOuter = maxInner.getValue().outer;
        maxInner.delete();
        maxOuter.inner = null;
        maxOuter.minNotMax = false;
        this.insertPair(maxOuter, this.free);
        this.free = null;
        --this.size;
        return minOuter;
    }

    @Override
    public DoubleEndedAddressableHeap.Handle<K, V> deleteMax() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        if (this.size == 1L) {
            ReflectedHandle<K, V> max = this.free;
            this.free = null;
            --this.size;
            return max;
        }
        if (this.size % 2L == 0L) {
            AddressableHeap.Handle<K, HandleMap<K, V>> maxInner = this.maxHeap.deleteMin();
            ReflectedHandle maxOuter = maxInner.getValue().outer;
            maxOuter.inner = null;
            maxOuter.minNotMax = false;
            AddressableHeap.Handle minInner = maxInner.getValue().otherInner;
            ReflectedHandle minOuter = minInner.getValue().outer;
            minInner.delete();
            minOuter.inner = null;
            minOuter.minNotMax = false;
            this.free = minOuter;
            --this.size;
            return maxOuter;
        }
        AddressableHeap.Handle<K, HandleMap<K, V>> maxInner = this.maxHeap.findMin();
        int c = this.comparator == null ? ((Comparable)maxInner.getKey()).compareTo(this.free.key) : this.comparator.compare(maxInner.getKey(), this.free.key);
        if (c < 0) {
            ReflectedHandle<K, V> max = this.free;
            this.free = null;
            --this.size;
            return max;
        }
        maxInner.delete();
        ReflectedHandle maxOuter = maxInner.getValue().outer;
        maxOuter.inner = null;
        maxOuter.minNotMax = false;
        AddressableHeap.Handle minInner = maxInner.getValue().otherInner;
        ReflectedHandle minOuter = minInner.getValue().outer;
        minInner.delete();
        minOuter.inner = null;
        minOuter.minNotMax = false;
        this.insertPair(minOuter, this.free);
        this.free = null;
        --this.size;
        return maxOuter;
    }

    @Override
    public void meld(MergeableDoubleEndedAddressableHeap<K, V> other) {
        ReflectedHeap h = (ReflectedHeap)other;
        if (this.comparator != null ? h.comparator == null || !h.comparator.equals(this.comparator) : h.comparator != null) {
            throw new IllegalArgumentException("Cannot meld heaps using different comparators!");
        }
        if (h.other != h) {
            throw new IllegalStateException("A heap cannot be used after a meld.");
        }
        if (!(this.minHeap instanceof MergeableAddressableHeap)) {
            throw new IllegalArgumentException("Underlying heaps are not meldable.");
        }
        MergeableAddressableHeap minAsMergeableHeap = (MergeableAddressableHeap)this.minHeap;
        MergeableAddressableHeap hMinAsMergeableHeap = (MergeableAddressableHeap)h.minHeap;
        minAsMergeableHeap.meld(hMinAsMergeableHeap);
        MergeableAddressableHeap maxAsMergeableHeap = (MergeableAddressableHeap)this.maxHeap;
        MergeableAddressableHeap hMaxAsMergeableHeap = (MergeableAddressableHeap)h.maxHeap;
        maxAsMergeableHeap.meld(hMaxAsMergeableHeap);
        if (this.free == null) {
            if (h.free != null) {
                this.free = h.free;
                h.free = null;
            }
        } else if (h.free != null) {
            this.insertPair(this.free, h.free);
            h.free = null;
            this.free = null;
        }
        this.size += h.size;
        h.size = 0L;
        h.other = this;
    }

    private void insertPair(ReflectedHandle<K, V> handle1, ReflectedHandle<K, V> handle2) {
        AddressableHeap.Handle innerHandle2;
        AddressableHeap.Handle innerHandle1;
        int c = this.comparator == null ? ((Comparable)handle1.key).compareTo(handle2.key) : this.comparator.compare(handle1.key, handle2.key);
        if (c <= 0) {
            innerHandle1 = this.minHeap.insert(handle1.key);
            handle1.minNotMax = true;
            innerHandle2 = this.maxHeap.insert(handle2.key);
            handle2.minNotMax = false;
        } else {
            innerHandle1 = this.maxHeap.insert(handle1.key);
            handle1.minNotMax = false;
            innerHandle2 = this.minHeap.insert(handle2.key);
            handle2.minNotMax = true;
        }
        handle1.inner = innerHandle1;
        handle2.inner = innerHandle2;
        innerHandle1.setValue(new HandleMap(handle1, innerHandle2));
        innerHandle2.setValue(new HandleMap(handle2, innerHandle1));
    }

    private void delete(ReflectedHandle<K, V> n) {
        if (n.inner == null && this.free != n) {
            throw new IllegalArgumentException("Invalid handle!");
        }
        if (this.free == n) {
            this.free = null;
        } else {
            AddressableHeap.Handle nInner = n.inner;
            ReflectedHandle nOuter = nInner.getValue().outer;
            nInner.delete();
            nOuter.inner = null;
            nOuter.minNotMax = false;
            AddressableHeap.Handle otherInner = nInner.getValue().otherInner;
            ReflectedHandle otherOuter = otherInner.getValue().outer;
            otherInner.delete();
            otherOuter.inner = null;
            otherOuter.minNotMax = false;
            if (this.free == null) {
                this.free = otherOuter;
            } else {
                this.insertPair(otherOuter, this.free);
                this.free = null;
            }
        }
        --this.size;
    }

    private void decreaseKey(ReflectedHandle<K, V> n, K newKey) {
        if (n.inner == null && this.free != n) {
            throw new IllegalArgumentException("Invalid handle!");
        }
        int c = this.comparator == null ? ((Comparable)newKey).compareTo(n.key) : this.comparator.compare(newKey, n.key);
        if (c > 0) {
            throw new IllegalArgumentException("Keys can only be decreased!");
        }
        n.key = newKey;
        if (c == 0 || this.free == n) {
            return;
        }
        AddressableHeap.Handle nInner = n.inner;
        if (n.minNotMax) {
            n.inner.decreaseKey(newKey);
        } else {
            nInner.delete();
            ReflectedHandle nOuter = nInner.getValue().outer;
            nOuter.inner = null;
            nOuter.minNotMax = false;
            AddressableHeap.Handle minInner = nInner.getValue().otherInner;
            ReflectedHandle minOuter = minInner.getValue().outer;
            minInner.delete();
            minOuter.inner = null;
            minOuter.minNotMax = false;
            nOuter.key = newKey;
            this.insertPair(nOuter, minOuter);
        }
    }

    private void increaseKey(ReflectedHandle<K, V> n, K newKey) {
        if (n.inner == null && this.free != n) {
            throw new IllegalArgumentException("Invalid handle!");
        }
        int c = this.comparator == null ? ((Comparable)newKey).compareTo(n.key) : this.comparator.compare(newKey, n.key);
        if (c < 0) {
            throw new IllegalArgumentException("Keys can only be increased!");
        }
        n.key = newKey;
        if (c == 0 || this.free == n) {
            return;
        }
        AddressableHeap.Handle nInner = n.inner;
        if (!n.minNotMax) {
            n.inner.decreaseKey(newKey);
        } else {
            nInner.delete();
            ReflectedHandle nOuter = nInner.getValue().outer;
            nOuter.inner = null;
            nOuter.minNotMax = false;
            AddressableHeap.Handle maxInner = nInner.getValue().otherInner;
            ReflectedHandle maxOuter = maxInner.getValue().outer;
            maxInner.delete();
            maxOuter.inner = null;
            maxOuter.minNotMax = false;
            nOuter.key = newKey;
            this.insertPair(nOuter, maxOuter);
        }
    }

    private static class HandleMap<K, V>
    implements Serializable {
        private static final long serialVersionUID = 1L;
        ReflectedHandle<K, V> outer;
        AddressableHeap.Handle<K, HandleMap<K, V>> otherInner;

        public HandleMap(ReflectedHandle<K, V> outer, AddressableHeap.Handle<K, HandleMap<K, V>> otherInner) {
            this.outer = outer;
            this.otherInner = otherInner;
        }
    }

    private static class ReflectedHandle<K, V>
    implements DoubleEndedAddressableHeap.Handle<K, V>,
    Serializable {
        private static final long serialVersionUID = 3179286196684064903L;
        ReflectedHeap<K, V> heap;
        K key;
        V value;
        boolean minNotMax;
        AddressableHeap.Handle<K, HandleMap<K, V>> inner;

        public ReflectedHandle(ReflectedHeap<K, V> heap, K key, V value) {
            this.heap = heap;
            this.key = key;
            this.value = value;
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
            ((ReflectedHeap)this.getOwner()).decreaseKey(this, newKey);
        }

        @Override
        public void delete() {
            ((ReflectedHeap)this.getOwner()).delete(this);
        }

        @Override
        public void increaseKey(K newKey) {
            ((ReflectedHeap)this.getOwner()).increaseKey(this, newKey);
        }

        ReflectedHeap<K, V> getOwner() {
            if (((ReflectedHeap)this.heap).other != this.heap) {
                ReflectedHeap root = this.heap;
                while (root != root.other) {
                    root = root.other;
                }
                ReflectedHeap cur = this.heap;
                while (cur.other != root) {
                    ReflectedHeap next = cur.other;
                    cur.other = root;
                    cur = next;
                }
                this.heap = root;
            }
            return this.heap;
        }
    }
}

