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

public class FibonacciHeap<K, V>
implements MergeableAddressableHeap<K, V>,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final int AUX_CONSOLIDATE_ARRAY_SIZE = 91;
    private final Comparator<? super K> comparator;
    private Node<K, V> minRoot = null;
    private int roots = 0;
    private long size;
    private Node<K, V>[] aux;
    protected FibonacciHeap<K, V> other;

    @ConstantTime
    public FibonacciHeap() {
        this(null);
    }

    @ConstantTime
    public FibonacciHeap(Comparator<? super K> comparator) {
        this.comparator = comparator;
        this.size = 0L;
        this.aux = (Node[])Array.newInstance(Node.class, 91);
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
        this.addToRootList(n);
        ++this.size;
        return n;
    }

    @Override
    @ConstantTime(amortized=true)
    public AddressableHeap.Handle<K, V> insert(K key) {
        return this.insert(key, null);
    }

    @Override
    @ConstantTime(amortized=true)
    public AddressableHeap.Handle<K, V> findMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        return this.minRoot;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public AddressableHeap.Handle<K, V> deleteMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        Node<K, V> z = this.minRoot;
        Node x = z.child;
        while (x != null) {
            Node nextX = x.next == x ? null : x.next;
            x.parent = null;
            x.prev.next = x.next;
            x.next.prev = x.prev;
            x.next = this.minRoot.next;
            x.prev = this.minRoot;
            this.minRoot.next = x;
            x.next.prev = x;
            ++this.roots;
            x = nextX;
        }
        z.degree = 0;
        z.child = null;
        z.prev.next = z.next;
        z.next.prev = z.prev;
        --this.roots;
        --this.size;
        if (z == z.next) {
            this.minRoot = null;
        } else {
            this.minRoot = z.next;
            this.consolidate();
        }
        z.next = null;
        z.prev = null;
        return z;
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
        this.minRoot = null;
        this.roots = 0;
        this.size = 0L;
    }

    @Override
    @ConstantTime(amortized=true)
    public void meld(MergeableAddressableHeap<K, V> other) {
        FibonacciHeap h = (FibonacciHeap)other;
        if (this.comparator != null ? h.comparator == null || !h.comparator.equals(this.comparator) : h.comparator != null) {
            throw new IllegalArgumentException("Cannot meld heaps using different comparators!");
        }
        if (h.other != h) {
            throw new IllegalStateException("A heap cannot be used after a meld.");
        }
        if (this.size == 0L) {
            this.minRoot = h.minRoot;
        } else if (h.size != 0L) {
            Node h22;
            Node<K, V> h11 = this.minRoot;
            Node h12 = h11.next;
            Node<K, V> h21 = h.minRoot;
            h11.next = h22 = h21.next;
            h22.prev = h11;
            h21.next = h12;
            h12.prev = h21;
            if (this.comparator == null && ((Comparable)h.minRoot.key).compareTo(this.minRoot.key) < 0 || this.comparator != null && this.comparator.compare(h.minRoot.key, this.minRoot.key) < 0) {
                this.minRoot = h.minRoot;
            }
        }
        this.roots += h.roots;
        this.size += h.size;
        h.size = 0L;
        h.minRoot = null;
        h.roots = 0;
        h.other = this;
    }

    private void decreaseKey(Node<K, V> n, K newKey) {
        int c = ((Comparable)newKey).compareTo(n.key);
        if (c > 0) {
            throw new IllegalArgumentException("Keys can only be decreased!");
        }
        n.key = newKey;
        if (c == 0) {
            return;
        }
        if (n.next == null) {
            throw new IllegalArgumentException("Invalid handle!");
        }
        Node y = n.parent;
        if (y != null && ((Comparable)n.key).compareTo(y.key) < 0) {
            this.cut(n, y);
            this.cascadingCut(y);
        }
        if (((Comparable)n.key).compareTo(this.minRoot.key) < 0) {
            this.minRoot = n;
        }
    }

    private void decreaseKeyWithComparator(Node<K, V> n, K newKey) {
        int c = this.comparator.compare(newKey, n.key);
        if (c > 0) {
            throw new IllegalArgumentException("Keys can only be decreased!");
        }
        n.key = newKey;
        if (c == 0) {
            return;
        }
        if (n.next == null) {
            throw new IllegalArgumentException("Invalid handle!");
        }
        Node y = n.parent;
        if (y != null && this.comparator.compare(n.key, y.key) < 0) {
            this.cut(n, y);
            this.cascadingCut(y);
        }
        if (this.comparator.compare(n.key, this.minRoot.key) < 0) {
            this.minRoot = n;
        }
    }

    private void forceDecreaseKeyToMinimum(Node<K, V> n) {
        Node y = n.parent;
        if (y != null) {
            this.cut(n, y);
            this.cascadingCut(y);
        }
        this.minRoot = n;
    }

    private void consolidate() {
        int maxDegree = -1;
        Node<K, V> x = this.minRoot;
        for (int numRoots = this.roots; numRoots > 0; --numRoots) {
            Node<K, V> y;
            Node nextX = x.next;
            int d = x.degree;
            while ((y = this.aux[d]) != null) {
                int c = this.comparator == null ? ((Comparable)y.key).compareTo(x.key) : this.comparator.compare(y.key, x.key);
                if (c < 0) {
                    Node<K, V> tmp = x;
                    x = y;
                    y = tmp;
                }
                this.link(y, x);
                this.aux[d] = null;
                ++d;
            }
            this.aux[d] = x;
            if (d > maxDegree) {
                maxDegree = d;
            }
            x = nextX;
        }
        this.minRoot = null;
        this.roots = 0;
        for (int i = 0; i <= maxDegree; ++i) {
            if (this.aux[i] == null) continue;
            this.addToRootList(this.aux[i]);
            this.aux[i] = null;
        }
    }

    private void link(Node<K, V> y, Node<K, V> x) {
        y.prev.next = y.next;
        y.next.prev = y.prev;
        --this.roots;
        y.mark = false;
        ++x.degree;
        y.parent = x;
        Node child = x.child;
        if (child == null) {
            x.child = y;
            y.next = y;
            y.prev = y;
        } else {
            y.prev = child;
            y.next = child.next;
            child.next = y;
            y.next.prev = y;
        }
    }

    private void cut(Node<K, V> x, Node<K, V> y) {
        x.prev.next = x.next;
        x.next.prev = x.prev;
        --y.degree;
        if (y.degree == 0) {
            y.child = null;
        } else if (y.child == x) {
            y.child = x.next;
        }
        x.parent = null;
        this.addToRootList(x);
        x.mark = false;
    }

    private void cascadingCut(Node<K, V> y) {
        Node z;
        while ((z = y.parent) != null) {
            if (!y.mark) {
                y.mark = true;
                break;
            }
            this.cut(y, z);
            y = z;
        }
    }

    private void addToRootList(Node<K, V> n) {
        if (this.minRoot == null) {
            n.next = n;
            n.prev = n;
            this.minRoot = n;
            this.roots = 1;
        } else {
            n.next = this.minRoot.next;
            n.prev = this.minRoot;
            this.minRoot.next.prev = n;
            this.minRoot.next = n;
            int c = this.comparator == null ? ((Comparable)n.key).compareTo(this.minRoot.key) : this.comparator.compare(n.key, this.minRoot.key);
            if (c < 0) {
                this.minRoot = n;
            }
            ++this.roots;
        }
    }

    static class Node<K, V>
    implements AddressableHeap.Handle<K, V>,
    Serializable {
        private static final long serialVersionUID = 1L;
        FibonacciHeap<K, V> heap;
        K key;
        V value;
        Node<K, V> parent;
        Node<K, V> child;
        Node<K, V> next;
        Node<K, V> prev;
        int degree;
        boolean mark;

        Node(FibonacciHeap<K, V> heap, K key, V value) {
            this.heap = heap;
            this.key = key;
            this.value = value;
            this.parent = null;
            this.child = null;
            this.next = null;
            this.prev = null;
            this.degree = 0;
            this.mark = false;
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
            FibonacciHeap<K, V> h = this.getOwner();
            if (((FibonacciHeap)h).comparator == null) {
                ((FibonacciHeap)h).decreaseKey(this, newKey);
            } else {
                ((FibonacciHeap)h).decreaseKeyWithComparator(this, newKey);
            }
        }

        @Override
        @LogarithmicTime(amortized=true)
        public void delete() {
            if (this.next == null) {
                throw new IllegalArgumentException("Invalid handle!");
            }
            FibonacciHeap<K, V> h = this.getOwner();
            ((FibonacciHeap)h).forceDecreaseKeyToMinimum(this);
            h.deleteMin();
        }

        FibonacciHeap<K, V> getOwner() {
            if (this.heap.other != this.heap) {
                FibonacciHeap<K, V> root = this.heap;
                while (root != root.other) {
                    root = root.other;
                }
                FibonacciHeap<K, V> cur = this.heap;
                while (cur.other != root) {
                    FibonacciHeap next = cur.other;
                    cur.other = root;
                    cur = next;
                }
                this.heap = root;
            }
            return this.heap;
        }
    }
}

