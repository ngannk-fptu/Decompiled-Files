/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.dag;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.AddressableHeap;
import org.jheaps.MergeableAddressableHeap;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LogarithmicTime;

public class HollowHeap<K, V>
implements MergeableAddressableHeap<K, V>,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final int AUX_BUCKET_ARRAY_SIZE = 128;
    private final Comparator<? super K> comparator;
    private HollowNode<K, V> root = null;
    private long size;
    private long nodes;
    private HollowNode<K, V>[] aux;
    private HollowHeap<K, V> other;

    @ConstantTime
    public HollowHeap() {
        this(null);
    }

    @ConstantTime
    public HollowHeap(Comparator<? super K> comparator) {
        this.comparator = comparator;
        this.size = 0L;
        this.nodes = 0L;
        this.aux = (HollowNode[])Array.newInstance(HollowNode.class, 128);
        this.other = this;
    }

    @Override
    @ConstantTime(amortized=true)
    public AddressableHeap.Handle<K, V> insert(K key, V value) {
        if (this.other != this) {
            throw new IllegalStateException("A heap cannot be used after a meld");
        }
        if (key == null) {
            throw new NullPointerException("Null keys not permitted");
        }
        Item<K, V> e = new Item<K, V>(key, value);
        HollowNode u = new HollowNode(this, key);
        u.item = e;
        ((Item)e).node = u;
        ++this.nodes;
        this.root = this.root == null ? u : this.link(this.root, u);
        ++this.size;
        return e;
    }

    @Override
    @ConstantTime(amortized=true)
    public AddressableHeap.Handle<K, V> insert(K key) {
        return this.insert(key, null);
    }

    @Override
    @ConstantTime(amortized=false)
    public AddressableHeap.Handle<K, V> findMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        return this.root.item;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public AddressableHeap.Handle<K, V> deleteMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        Item item = this.root.item;
        item.delete();
        return item;
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
    public Comparator<? super K> comparator() {
        return this.comparator;
    }

    @Override
    @ConstantTime(amortized=false)
    public void clear() {
        this.root = null;
        this.size = 0L;
        this.nodes = 0L;
    }

    @Override
    @ConstantTime
    public void meld(MergeableAddressableHeap<K, V> other) {
        HollowHeap h = (HollowHeap)other;
        if (this.comparator != null ? h.comparator == null || !h.comparator.equals(this.comparator) : h.comparator != null) {
            throw new IllegalArgumentException("Cannot meld heaps using different comparators!");
        }
        if (h.other != h) {
            throw new IllegalStateException("A heap cannot be used after a meld.");
        }
        if (this.root == null) {
            this.root = h.root;
        } else if (h.root != null) {
            this.root = this.link(this.root, h.root);
        }
        this.size += h.size;
        this.nodes += h.nodes;
        h.size = 0L;
        h.nodes = 0L;
        h.root = null;
        h.other = this;
    }

    private void decreaseKey(Item<K, V> e, K newKey) {
        assert (((Item)e).node != null);
        int c = this.comparator == null ? ((Comparable)newKey).compareTo(e.getKey()) : this.comparator.compare(newKey, e.getKey());
        if (c > 0) {
            throw new IllegalArgumentException("Keys can only be decreased!");
        }
        HollowNode u = ((Item)e).node;
        if (c == 0 || u == this.root) {
            ((Item)e).key = newKey;
            u.key = newKey;
            return;
        }
        HollowNode v = new HollowNode(this, newKey);
        ++this.nodes;
        u.item = null;
        v.item = e;
        ((Item)e).node = v;
        ((Item)e).key = newKey;
        if (u.rank > 2) {
            v.rank = u.rank - 2;
        }
        v.child = u;
        u.sp = v;
        this.root = this.link(this.root, v);
    }

    private void delete(Item<K, V> e) {
        assert (((Item)e).node != null);
        assert (((Item)e).node.item == e);
        ((Item)e).node.item = null;
        ((Item)e).node = null;
        --this.size;
        if (this.root.item != null) {
            return;
        }
        int maxRank = -1;
        while (this.root != null) {
            HollowNode<K, V> v = this.root;
            this.root = this.root.next;
            HollowNode w = v.child;
            while (w != null) {
                HollowNode u = w;
                w = w.next;
                u.next = null;
                if (u.item == null) {
                    if (u.sp == null) {
                        u.next = this.root;
                        this.root = u;
                        continue;
                    }
                    if (u.sp == v) {
                        u.sp = null;
                        continue;
                    }
                    u.sp = null;
                    u.next = null;
                    continue;
                }
                maxRank = Math.max(maxRank, this.doRankedLinks(u));
            }
            --this.nodes;
            v.next = null;
            v.child = null;
            v.sp = null;
            v.item = null;
        }
        this.doUnrankedLinks(maxRank);
    }

    private int doRankedLinks(HollowNode<K, V> u) {
        while (this.aux[u.rank] != null) {
            u = this.link(u, this.aux[u.rank]);
            this.aux[u.rank] = null;
            ++u.rank;
        }
        this.aux[u.rank] = u;
        return u.rank;
    }

    private void doUnrankedLinks(int maxRank) {
        assert (this.root == null);
        for (int i = 0; i <= maxRank; ++i) {
            HollowNode<K, V> u = this.aux[i];
            if (u == null) continue;
            this.root = this.root == null ? u : this.link(this.root, u);
            this.aux[i] = null;
        }
    }

    private HollowNode<K, V> link(HollowNode<K, V> v, HollowNode<K, V> w) {
        int c = this.comparator == null ? ((Comparable)v.key).compareTo(w.key) : this.comparator.compare(v.key, w.key);
        if (c > 0) {
            v.next = w.child;
            w.child = v;
            return w;
        }
        w.next = v.child;
        v.child = w;
        return v;
    }

    static class HollowNode<K, V>
    implements Serializable {
        private static final long serialVersionUID = 1L;
        HollowHeap<K, V> heap;
        K key;
        HollowNode<K, V> child;
        HollowNode<K, V> next;
        HollowNode<K, V> sp;
        int rank;
        Item<K, V> item;

        HollowNode(HollowHeap<K, V> heap, K key) {
            this.heap = heap;
            this.key = key;
            this.item = null;
            this.child = null;
            this.next = null;
            this.sp = null;
            this.rank = 0;
        }

        HollowHeap<K, V> getOwner() {
            if (((HollowHeap)this.heap).other != this.heap) {
                HollowHeap root = this.heap;
                while (root != root.other) {
                    root = root.other;
                }
                HollowHeap cur = this.heap;
                while (cur.other != root) {
                    HollowHeap next = cur.other;
                    cur.other = root;
                    cur = next;
                }
                this.heap = root;
            }
            return this.heap;
        }
    }

    static class Item<K, V>
    implements AddressableHeap.Handle<K, V>,
    Serializable {
        private static final long serialVersionUID = 1L;
        private HollowNode<K, V> node;
        private K key;
        private V value;

        public Item(K key, V value) {
            this.key = key;
            this.value = value;
            this.node = null;
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
            this.checkInvalid();
            ((HollowHeap)this.getOwner()).decreaseKey(this, newKey);
        }

        @Override
        public void delete() {
            this.checkInvalid();
            ((HollowHeap)this.getOwner()).delete(this);
        }

        HollowHeap<K, V> getOwner() {
            return this.node.getOwner();
        }

        private void checkInvalid() {
            if (this.node == null) {
                throw new IllegalArgumentException("Invalid handle!");
            }
        }
    }
}

