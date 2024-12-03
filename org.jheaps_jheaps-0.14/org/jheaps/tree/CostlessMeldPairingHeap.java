/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.tree;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.AddressableHeap;
import org.jheaps.MergeableAddressableHeap;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LogLogTime;
import org.jheaps.annotations.LogarithmicTime;

public class CostlessMeldPairingHeap<K, V>
implements MergeableAddressableHeap<K, V>,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_DECREASE_POOL_SIZE = 65;
    private final Comparator<? super K> comparator;
    private Node<K, V> root;
    private long size;
    private Node<K, V>[] decreasePool = (Node[])Array.newInstance(Node.class, 65);
    private byte decreasePoolSize = 0;
    private byte decreasePoolMinPos = 0;
    private transient Comparator<Node<K, V>> decreasePoolComparator;
    private CostlessMeldPairingHeap<K, V> other;

    @ConstantTime
    public CostlessMeldPairingHeap() {
        this(null);
    }

    @ConstantTime
    public CostlessMeldPairingHeap(Comparator<? super K> comparator) {
        this.comparator = comparator;
        this.decreasePoolComparator = null;
        this.other = this;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public AddressableHeap.Handle<K, V> insert(K key, V value) {
        if (this.other != this) {
            throw new IllegalStateException("A heap cannot be used after a meld");
        }
        if (key == null) {
            throw new NullPointerException("Null keys not permitted");
        }
        Node<K, V> n = new Node<K, V>(this, key, value);
        this.root = this.comparator == null ? this.link(this.root, n) : this.linkWithComparator(this.root, n);
        ++this.size;
        return n;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public AddressableHeap.Handle<K, V> insert(K key) {
        return this.insert(key, null);
    }

    @Override
    @ConstantTime
    public AddressableHeap.Handle<K, V> findMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        if (this.decreasePoolMinPos >= this.decreasePoolSize) {
            return this.root;
        }
        Node<K, V> poolMin = this.decreasePool[this.decreasePoolMinPos];
        int c = this.comparator == null ? ((Comparable)this.root.key).compareTo(poolMin.key) : this.comparator.compare(this.root.key, poolMin.key);
        if (c <= 0) {
            return this.root;
        }
        return poolMin;
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
    @ConstantTime
    public void clear() {
        this.root = null;
        this.size = 0L;
        this.decreasePool = (Node[])Array.newInstance(Node.class, 65);
        this.decreasePoolSize = 0;
        this.decreasePoolMinPos = 0;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public AddressableHeap.Handle<K, V> deleteMin() {
        Node<K, V> min;
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        if (this.decreasePoolMinPos >= this.decreasePoolSize) {
            min = this.root;
            this.root = this.combine(this.cutChildren(this.root));
        } else {
            Node<K, V> poolMin = this.decreasePool[this.decreasePoolMinPos];
            int c = this.comparator == null ? ((Comparable)this.root.key).compareTo(poolMin.key) : this.comparator.compare(this.root.key, poolMin.key);
            if (c <= 0) {
                min = this.root;
                Node<K, V> childrenTree = this.combine(this.cutChildren(this.root));
                this.root = null;
                if (childrenTree != null) {
                    this.addPool(childrenTree, false);
                }
                this.consolidate();
            } else {
                min = poolMin;
                Node<K, V> childrenTree = this.combine(this.cutChildren(poolMin));
                if (childrenTree != null) {
                    this.decreasePool[this.decreasePoolMinPos] = childrenTree;
                    childrenTree.poolIndex = this.decreasePoolMinPos;
                } else {
                    this.decreasePool[this.decreasePoolMinPos] = this.decreasePool[this.decreasePoolSize - 1];
                    this.decreasePool[this.decreasePoolMinPos].poolIndex = this.decreasePoolMinPos;
                    this.decreasePool[this.decreasePoolSize - 1] = null;
                    this.decreasePoolSize = (byte)(this.decreasePoolSize - 1);
                }
                poolMin.poolIndex = (byte)-1;
                this.consolidate();
            }
        }
        --this.size;
        return min;
    }

    @Override
    @ConstantTime(amortized=true)
    public void meld(MergeableAddressableHeap<K, V> other) {
        CostlessMeldPairingHeap h = (CostlessMeldPairingHeap)other;
        if (this.comparator != null ? h.comparator == null || !h.comparator.equals(this.comparator) : h.comparator != null) {
            throw new IllegalArgumentException("Cannot meld heaps using different comparators!");
        }
        if (h.other != h) {
            throw new IllegalStateException("A heap cannot be used after a meld.");
        }
        if (this.size < h.size) {
            this.consolidate();
            this.root = this.comparator == null ? this.link(h.root, this.root) : this.linkWithComparator(h.root, this.root);
            this.decreasePoolSize = h.decreasePoolSize;
            h.decreasePoolSize = 0;
            this.decreasePoolMinPos = h.decreasePoolMinPos;
            h.decreasePoolMinPos = 0;
            Node<K, V>[] tmp = this.decreasePool;
            this.decreasePool = h.decreasePool;
            h.decreasePool = tmp;
        } else {
            h.consolidate();
            this.root = this.comparator == null ? this.link(h.root, this.root) : this.linkWithComparator(h.root, this.root);
        }
        this.size += h.size;
        h.root = null;
        h.size = 0L;
        h.other = this;
    }

    private void decreaseKey(Node<K, V> n, K newKey) {
        int c = this.comparator == null ? ((Comparable)newKey).compareTo(n.key) : this.comparator.compare(newKey, n.key);
        if (c > 0) {
            throw new IllegalArgumentException("Keys can only be decreased!");
        }
        n.key = newKey;
        if (c == 0 || this.root == n) {
            return;
        }
        if (n.o_s == null && n.poolIndex == -1) {
            throw new IllegalArgumentException("Invalid handle!");
        }
        if (n.o_s == null) {
            Node<K, V> poolMin = this.decreasePool[this.decreasePoolMinPos];
            c = this.comparator == null ? ((Comparable)newKey).compareTo(poolMin.key) : this.comparator.compare(newKey, poolMin.key);
            if (c < 0) {
                this.decreasePoolMinPos = n.poolIndex;
            }
            return;
        }
        Node<K, V> oldestChild = this.cutOldestChild(n);
        if (oldestChild != null) {
            this.linkInPlace(oldestChild, n);
        } else {
            this.cutFromParent(n);
        }
        this.addPool(n, true);
        double sizeAsDouble = this.size;
        if (this.decreasePoolSize >= Math.getExponent(sizeAsDouble) + 1) {
            this.consolidate();
        }
    }

    private void delete(Node<K, V> n) {
        double sizeAsDouble;
        if (n != this.root && n.o_s == null && n.poolIndex == -1) {
            throw new IllegalArgumentException("Invalid handle!");
        }
        if (n.o_s != null) {
            Node<K, V> oldestChild = this.cutOldestChild(n);
            if (oldestChild != null) {
                this.linkInPlace(oldestChild, n);
            } else {
                this.cutFromParent(n);
            }
        }
        Node<K, V> childrenTree = this.combine(this.cutChildren(n));
        boolean checkConsolidate = false;
        if (childrenTree != null) {
            checkConsolidate = true;
            this.addPool(childrenTree, true);
        }
        --this.size;
        if (n == this.root) {
            this.root = null;
            this.consolidate();
            checkConsolidate = false;
        } else if (n.poolIndex != -1) {
            byte curIndex = n.poolIndex;
            this.decreasePool[curIndex] = this.decreasePool[this.decreasePoolSize - 1];
            this.decreasePool[curIndex].poolIndex = curIndex;
            this.decreasePool[this.decreasePoolSize - 1] = null;
            this.decreasePoolSize = (byte)(this.decreasePoolSize - 1);
            n.poolIndex = (byte)-1;
            if (curIndex == this.decreasePoolMinPos) {
                this.consolidate();
                checkConsolidate = false;
            } else {
                if (this.decreasePoolMinPos == this.decreasePoolSize) {
                    this.decreasePoolMinPos = curIndex;
                }
                checkConsolidate = true;
            }
        }
        if (checkConsolidate && this.decreasePoolSize >= Math.getExponent(sizeAsDouble = (double)this.size) + 1) {
            this.consolidate();
        }
    }

    private void consolidate() {
        int i;
        if (this.decreasePoolSize == 0) {
            return;
        }
        if (this.decreasePoolComparator == null) {
            this.decreasePoolComparator = this.comparator == null ? new Comparator<Node<K, V>>(){

                @Override
                public int compare(Node<K, V> o1, Node<K, V> o2) {
                    return ((Comparable)o1.key).compareTo(o2.key);
                }
            } : new Comparator<Node<K, V>>(){

                @Override
                public int compare(Node<K, V> o1, Node<K, V> o2) {
                    return CostlessMeldPairingHeap.this.comparator.compare(o1.key, o2.key);
                }
            };
        }
        Arrays.sort(this.decreasePool, 0, this.decreasePoolSize, this.decreasePoolComparator);
        Node<K, V> s = this.decreasePool[i];
        s.poolIndex = (byte)-1;
        for (i = this.decreasePoolSize - 1; i > 0; --i) {
            Node<K, V> f = this.decreasePool[i - 1];
            f.poolIndex = (byte)-1;
            this.decreasePool[i] = null;
            s.y_s = f.o_c;
            s.o_s = f;
            if (f.o_c != null) {
                f.o_c.o_s = s;
            }
            f.o_c = s;
            s = f;
        }
        this.decreasePool[0] = null;
        this.decreasePoolSize = 0;
        this.decreasePoolMinPos = 0;
        this.root = this.comparator == null ? this.link(this.root, s) : this.linkWithComparator(this.root, s);
    }

    private void addPool(Node<K, V> n, boolean updateMinimum) {
        this.decreasePool[this.decreasePoolSize] = n;
        n.poolIndex = this.decreasePoolSize;
        this.decreasePoolSize = (byte)(this.decreasePoolSize + 1);
        if (updateMinimum && this.decreasePoolSize > 1) {
            Node<K, V> poolMin = this.decreasePool[this.decreasePoolMinPos];
            int c = this.comparator == null ? ((Comparable)n.key).compareTo(poolMin.key) : this.comparator.compare(n.key, poolMin.key);
            if (c < 0) {
                this.decreasePoolMinPos = n.poolIndex;
            }
        }
    }

    private Node<K, V> combine(Node<K, V> l) {
        Node n_it;
        Node<K, V> p_it;
        if (l == null) {
            return null;
        }
        assert (l.o_s == null);
        Node<K, V> pairs = null;
        Node<K, V> it = l;
        if (this.comparator == null) {
            while (it != null) {
                p_it = it;
                it = it.y_s;
                if (it == null) {
                    p_it.y_s = pairs;
                    p_it.o_s = null;
                    pairs = p_it;
                    continue;
                }
                n_it = it.y_s;
                p_it.y_s = null;
                p_it.o_s = null;
                it.y_s = null;
                it.o_s = null;
                p_it = this.link(p_it, it);
                p_it.y_s = pairs;
                pairs = p_it;
                it = n_it;
            }
        } else {
            while (it != null) {
                p_it = it;
                it = it.y_s;
                if (it == null) {
                    p_it.y_s = pairs;
                    p_it.o_s = null;
                    pairs = p_it;
                    continue;
                }
                n_it = it.y_s;
                p_it.y_s = null;
                p_it.o_s = null;
                it.y_s = null;
                it.o_s = null;
                p_it = this.linkWithComparator(p_it, it);
                p_it.y_s = pairs;
                pairs = p_it;
                it = n_it;
            }
        }
        it = pairs;
        Node<K, V> f = null;
        if (this.comparator == null) {
            while (it != null) {
                Node nextIt = it.y_s;
                it.y_s = null;
                f = this.link(f, it);
                it = nextIt;
            }
        } else {
            while (it != null) {
                Node nextIt = it.y_s;
                it.y_s = null;
                f = this.linkWithComparator(f, it);
                it = nextIt;
            }
        }
        return f;
    }

    private Node<K, V> cutChildren(Node<K, V> n) {
        Node child = n.o_c;
        n.o_c = null;
        if (child != null) {
            child.o_s = null;
        }
        return child;
    }

    private Node<K, V> cutOldestChild(Node<K, V> n) {
        Node oldestChild = n.o_c;
        if (oldestChild != null) {
            if (oldestChild.y_s != null) {
                oldestChild.y_s.o_s = n;
            }
            n.o_c = oldestChild.y_s;
            oldestChild.y_s = null;
            oldestChild.o_s = null;
        }
        return oldestChild;
    }

    private void cutFromParent(Node<K, V> n) {
        if (n.o_s != null) {
            if (n.y_s != null) {
                n.y_s.o_s = n.o_s;
            }
            if (n.o_s.o_c == n) {
                n.o_s.o_c = n.y_s;
            } else {
                n.o_s.y_s = n.y_s;
            }
            n.y_s = null;
            n.o_s = null;
        }
    }

    private void linkInPlace(Node<K, V> orphan, Node<K, V> n) {
        orphan.y_s = n.y_s;
        if (n.y_s != null) {
            n.y_s.o_s = orphan;
        }
        orphan.o_s = n.o_s;
        if (n.o_s != null) {
            if (n.o_s.o_c == n) {
                n.o_s.o_c = orphan;
            } else {
                n.o_s.y_s = orphan;
            }
        }
        n.o_s = null;
        n.y_s = null;
    }

    private Node<K, V> link(Node<K, V> f, Node<K, V> s) {
        if (s == null) {
            return f;
        }
        if (f == null) {
            return s;
        }
        if (((Comparable)f.key).compareTo(s.key) <= 0) {
            s.y_s = f.o_c;
            s.o_s = f;
            if (f.o_c != null) {
                f.o_c.o_s = s;
            }
            f.o_c = s;
            return f;
        }
        return this.link(s, f);
    }

    private Node<K, V> linkWithComparator(Node<K, V> f, Node<K, V> s) {
        if (s == null) {
            return f;
        }
        if (f == null) {
            return s;
        }
        if (this.comparator.compare(f.key, s.key) <= 0) {
            s.y_s = f.o_c;
            s.o_s = f;
            if (f.o_c != null) {
                f.o_c.o_s = s;
            }
            f.o_c = s;
            return f;
        }
        return this.linkWithComparator(s, f);
    }

    static class Node<K, V>
    implements AddressableHeap.Handle<K, V>,
    Serializable {
        private static final long serialVersionUID = 1L;
        static final byte NO_INDEX = -1;
        CostlessMeldPairingHeap<K, V> heap;
        K key;
        V value;
        Node<K, V> o_c;
        Node<K, V> y_s;
        Node<K, V> o_s;
        byte poolIndex;

        Node(CostlessMeldPairingHeap<K, V> heap, K key, V value) {
            this.heap = heap;
            this.key = key;
            this.value = value;
            this.o_c = null;
            this.y_s = null;
            this.o_s = null;
            this.poolIndex = (byte)-1;
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
        @LogLogTime(amortized=true)
        public void delete() {
            ((CostlessMeldPairingHeap)this.getOwner()).delete(this);
        }

        @Override
        @LogLogTime(amortized=true)
        public void decreaseKey(K newKey) {
            ((CostlessMeldPairingHeap)this.getOwner()).decreaseKey(this, newKey);
        }

        CostlessMeldPairingHeap<K, V> getOwner() {
            if (((CostlessMeldPairingHeap)this.heap).other != this.heap) {
                CostlessMeldPairingHeap root = this.heap;
                while (root != root.other) {
                    root = root.other;
                }
                CostlessMeldPairingHeap cur = this.heap;
                while (cur.other != root) {
                    CostlessMeldPairingHeap next = cur.other;
                    cur.other = root;
                    cur = next;
                }
                this.heap = root;
            }
            return this.heap;
        }
    }
}

