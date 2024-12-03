/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.tree;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.AddressableHeap;
import org.jheaps.MergeableAddressableHeap;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LogarithmicTime;

public class RankPairingHeap<K, V>
implements MergeableAddressableHeap<K, V>,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final int AUX_BUCKET_ARRAY_SIZE = 65;
    private final Comparator<? super K> comparator;
    private Node<K, V> minRoot = null;
    private long size;
    private Node<K, V>[] aux;
    private RankPairingHeap<K, V> other;

    @ConstantTime
    public RankPairingHeap() {
        this(null);
    }

    @ConstantTime
    public RankPairingHeap(Comparator<? super K> comparator) {
        this.comparator = comparator;
        this.size = 0L;
        this.aux = (Node[])Array.newInstance(Node.class, 65);
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
        Node<K, V> n = new Node<K, V>(this, key, value);
        if (this.minRoot == null) {
            n.r = n;
            this.minRoot = n;
        } else {
            n.r = this.minRoot.r;
            this.minRoot.r = n;
            if (this.less(n, this.minRoot)) {
                this.minRoot = n;
            }
        }
        ++this.size;
        return n;
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
        return this.minRoot;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public AddressableHeap.Handle<K, V> deleteMin() {
        Node<K, V> auxEntry;
        int rank;
        Node cur;
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        Node<K, V> oldMinRoot = this.minRoot;
        Node spine = null;
        if (this.minRoot.l != null) {
            spine = this.severSpine(this.minRoot.l);
            this.minRoot.l = null;
        }
        int maxRank = -1;
        Node<K, V> output = null;
        while (spine != null) {
            if (spine.r == spine) {
                cur = spine;
                spine = null;
            } else {
                cur = spine.r;
                spine.r = cur.r;
            }
            cur.r = null;
            rank = cur.rank;
            auxEntry = this.aux[rank];
            if (auxEntry == null) {
                this.aux[rank] = cur;
                if (rank <= maxRank) continue;
                maxRank = rank;
                continue;
            }
            this.aux[rank] = null;
            cur = this.link(cur, auxEntry);
            if (output == null) {
                cur.r = cur;
                output = cur;
                continue;
            }
            cur.r = output.r;
            output.r = cur;
            if (!this.less(cur, output)) continue;
            output = cur;
        }
        while (this.minRoot != null) {
            if (this.minRoot.r == this.minRoot) {
                cur = this.minRoot;
                this.minRoot = null;
            } else {
                cur = this.minRoot.r;
                this.minRoot.r = cur.r;
            }
            cur.r = null;
            if (cur == oldMinRoot) continue;
            rank = cur.rank;
            auxEntry = this.aux[rank];
            if (auxEntry == null) {
                this.aux[rank] = cur;
                if (rank <= maxRank) continue;
                maxRank = rank;
                continue;
            }
            this.aux[rank] = null;
            cur = this.link(cur, auxEntry);
            if (output == null) {
                cur.r = cur;
                output = cur;
                continue;
            }
            cur.r = output.r;
            output.r = cur;
            if (!this.less(cur, output)) continue;
            output = cur;
        }
        for (int i = 0; i <= maxRank; ++i) {
            Node<K, V> cur2 = this.aux[i];
            if (cur2 == null) continue;
            this.aux[i] = null;
            if (output == null) {
                cur2.r = cur2;
                output = cur2;
                continue;
            }
            cur2.r = output.r;
            output.r = cur2;
            if (!this.less(cur2, output)) continue;
            output = cur2;
        }
        this.minRoot = output;
        --this.size;
        return oldMinRoot;
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
        this.minRoot = null;
        this.size = 0L;
    }

    @Override
    @ConstantTime
    public void meld(MergeableAddressableHeap<K, V> other) {
        RankPairingHeap h = (RankPairingHeap)other;
        if (this.comparator != null ? h.comparator == null || !h.comparator.equals(this.comparator) : h.comparator != null) {
            throw new IllegalArgumentException("Cannot meld heaps using different comparators!");
        }
        if (h.other != h) {
            throw new IllegalStateException("A heap cannot be used after a meld.");
        }
        if (this.minRoot == null) {
            this.minRoot = h.minRoot;
        } else if (h.minRoot != null) {
            Node hAfterMinRoot;
            Node afterMinRoot = this.minRoot.r;
            this.minRoot.r = hAfterMinRoot = h.minRoot.r;
            h.minRoot.r = afterMinRoot;
            if (this.less(h.minRoot, this.minRoot)) {
                this.minRoot = h.minRoot;
            }
        }
        this.size += h.size;
        h.size = 0L;
        h.minRoot = null;
        h.other = this;
    }

    private void forceDecreaseKeyToMinimum(Node<K, V> n) {
        if (n.p == null) {
            this.minRoot = n;
            return;
        }
        Node u = n.p;
        this.cut(n);
        if (this.minRoot == null) {
            n.r = n;
        } else {
            n.r = this.minRoot.r;
            this.minRoot.r = n;
        }
        this.minRoot = n;
        n.rank = n.l == null ? 0 : n.l.rank + 1;
        this.restoreType1Ranks(u);
    }

    private void decreaseKey(Node<K, V> n, K newKey) {
        int c = this.comparator == null ? ((Comparable)newKey).compareTo(n.key) : this.comparator.compare(newKey, n.key);
        if (c > 0) {
            throw new IllegalArgumentException("Keys can only be decreased!");
        }
        n.key = newKey;
        if (c == 0) {
            return;
        }
        if (n.p == null && n.r == null) {
            throw new IllegalArgumentException("Invalid handle!");
        }
        if (n.p == null) {
            if (this.less(n, this.minRoot)) {
                this.minRoot = n;
            }
            return;
        }
        Node u = n.p;
        this.cut(n);
        if (this.minRoot == null) {
            n.r = n;
            this.minRoot = n;
        } else {
            n.r = this.minRoot.r;
            this.minRoot.r = n;
            if (this.less(n, this.minRoot)) {
                this.minRoot = n;
            }
        }
        n.rank = n.l == null ? 0 : n.l.rank + 1;
        this.restoreType1Ranks(u);
    }

    private boolean less(Node<K, V> x, Node<K, V> y) {
        if (this.comparator == null) {
            return ((Comparable)x.key).compareTo(y.key) < 0;
        }
        return this.comparator.compare(x.key, y.key) < 0;
    }

    private Node<K, V> severSpine(Node<K, V> x) {
        Node<K, V> cur = x;
        while (cur.r != null) {
            cur.p = null;
            cur.rank = cur.l == null ? 0 : cur.l.rank + 1;
            cur = cur.r;
        }
        cur.p = null;
        cur.rank = cur.l == null ? 0 : cur.l.rank + 1;
        cur.r = x;
        return x;
    }

    private Node<K, V> link(Node<K, V> x, Node<K, V> y) {
        assert (x.rank == y.rank);
        int c = this.comparator == null ? ((Comparable)x.key).compareTo(y.key) : this.comparator.compare(x.key, y.key);
        if (c <= 0) {
            y.r = x.l;
            if (x.l != null) {
                x.l.p = y;
            }
            x.l = y;
            y.p = x;
            ++x.rank;
            return x;
        }
        x.r = y.l;
        if (y.l != null) {
            y.l.p = x;
        }
        y.l = x;
        x.p = y;
        ++y.rank;
        return y;
    }

    private void cut(Node<K, V> x) {
        Node u = x.p;
        assert (u != null);
        Node y = x.r;
        if (u.l == x) {
            u.l = y;
        } else {
            u.r = y;
        }
        if (y != null) {
            y.p = u;
        }
        x.p = null;
        x.r = x;
    }

    private void restoreType1Ranks(Node<K, V> u) {
        while (u != null) {
            int k;
            int leftRank;
            int n = leftRank = u.l == null ? -1 : u.l.rank;
            if (u.p == null) {
                u.rank = leftRank + 1;
                break;
            }
            int rightRank = u.r == null ? -1 : u.r.rank;
            int n2 = k = leftRank == rightRank ? leftRank + 1 : Math.max(leftRank, rightRank);
            if (k >= u.rank) break;
            u.rank = k;
            u = u.p;
        }
    }

    static class Node<K, V>
    implements AddressableHeap.Handle<K, V>,
    Serializable {
        private static final long serialVersionUID = 1L;
        RankPairingHeap<K, V> heap;
        K key;
        V value;
        Node<K, V> p;
        Node<K, V> l;
        Node<K, V> r;
        int rank;

        Node(RankPairingHeap<K, V> heap, K key, V value) {
            this.heap = heap;
            this.key = key;
            this.value = value;
            this.p = null;
            this.l = null;
            this.r = null;
            this.rank = 0;
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
        @ConstantTime(amortized=true)
        public void decreaseKey(K newKey) {
            ((RankPairingHeap)this.getOwner()).decreaseKey(this, newKey);
        }

        @Override
        @LogarithmicTime(amortized=true)
        public void delete() {
            if (this.p == null && this.r == null) {
                throw new IllegalArgumentException("Invalid handle!");
            }
            RankPairingHeap<K, V> h = this.getOwner();
            ((RankPairingHeap)h).forceDecreaseKeyToMinimum(this);
            h.deleteMin();
        }

        RankPairingHeap<K, V> getOwner() {
            if (((RankPairingHeap)this.heap).other != this.heap) {
                RankPairingHeap root = this.heap;
                while (root != root.other) {
                    root = root.other;
                }
                RankPairingHeap cur = this.heap;
                while (cur.other != root) {
                    RankPairingHeap next = cur.other;
                    cur.other = root;
                    cur = next;
                }
                this.heap = root;
            }
            return this.heap;
        }
    }
}

