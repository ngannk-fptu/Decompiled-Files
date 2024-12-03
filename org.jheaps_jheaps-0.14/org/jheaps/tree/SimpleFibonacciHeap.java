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

public class SimpleFibonacciHeap<K, V>
implements MergeableAddressableHeap<K, V>,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final int AUX_CONSOLIDATE_ARRAY_SIZE = 91;
    private final Comparator<? super K> comparator;
    private Node<K, V> root = null;
    private long size;
    private Node<K, V>[] aux;
    protected SimpleFibonacciHeap<K, V> other;

    @ConstantTime
    public SimpleFibonacciHeap() {
        this(null);
    }

    @ConstantTime
    public SimpleFibonacciHeap(Comparator<? super K> comparator) {
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
        if (this.root == null) {
            this.root = n;
        } else if (this.comparator == null) {
            if (((Comparable)n.key).compareTo(this.root.key) < 0) {
                this.root = this.link(this.root, n);
            } else {
                this.link(n, this.root);
            }
        } else if (this.comparator.compare(n.key, this.root.key) < 0) {
            this.root = this.link(this.root, n);
        } else {
            this.link(n, this.root);
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
    @ConstantTime(amortized=true)
    public AddressableHeap.Handle<K, V> findMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        return this.root;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public AddressableHeap.Handle<K, V> deleteMin() {
        if (this.comparator == null) {
            return this.comparableDeleteMin();
        }
        return this.comparatorDeleteMin();
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
    }

    @Override
    @ConstantTime(amortized=true)
    public void meld(MergeableAddressableHeap<K, V> other) {
        SimpleFibonacciHeap h = (SimpleFibonacciHeap)other;
        if (this.comparator != null ? h.comparator == null || !h.comparator.equals(this.comparator) : h.comparator != null) {
            throw new IllegalArgumentException("Cannot meld heaps using different comparators!");
        }
        if (h.other != h) {
            throw new IllegalStateException("A heap cannot be used after a meld.");
        }
        if (this.root == null) {
            this.root = h.root;
        } else if (h.root != null) {
            if (this.comparator == null) {
                if (((Comparable)h.root.key).compareTo(this.root.key) < 0) {
                    this.root = this.link(this.root, h.root);
                } else {
                    this.link(h.root, this.root);
                }
            } else if (this.comparator.compare(h.root.key, this.root.key) < 0) {
                this.root = this.link(this.root, h.root);
            } else {
                this.link(h.root, this.root);
            }
        }
        this.size += h.size;
        h.size = 0L;
        h.root = null;
        h.other = this;
    }

    private void comparableDecreaseKey(Node<K, V> n, K newKey) {
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
            this.root.mark = false;
            this.cascadingRankChange(y);
            if (((Comparable)n.key).compareTo(this.root.key) < 0) {
                this.root = this.link(this.root, n);
            } else {
                this.link(n, this.root);
            }
        }
    }

    private void comparatorDecreaseKey(Node<K, V> n, K newKey) {
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
            this.root.mark = false;
            this.cascadingRankChange(y);
            if (this.comparator.compare(n.key, this.root.key) < 0) {
                this.root = this.link(this.root, n);
            } else {
                this.link(n, this.root);
            }
        }
    }

    private void forceDecreaseKeyToMinimum(Node<K, V> n) {
        Node y = n.parent;
        if (y != null) {
            this.cut(n, y);
            this.root.mark = false;
            this.cascadingRankChange(y);
            this.root = this.link(this.root, n);
        }
    }

    private AddressableHeap.Handle<K, V> comparableDeleteMin() {
        int i;
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        Node<K, V> z = this.root;
        Node x = this.root.child;
        z.child = null;
        z.next = null;
        z.prev = null;
        if (x == null) {
            this.root = null;
            this.size = 0L;
            return z;
        }
        int maxDegree = -1;
        while (x != null) {
            Node<K, V> y;
            Node nextX = x.next == x ? null : x.next;
            x.parent = null;
            x.prev.next = x.next;
            x.next.prev = x.prev;
            x.next = x;
            x.prev = x;
            int d = x.rank;
            while ((y = this.aux[d]) != null) {
                if (((Comparable)y.key).compareTo(x.key) < 0) {
                    Node tmp = x;
                    x = y;
                    y = tmp;
                }
                this.link(y, x);
                ++x.rank;
                this.aux[d] = null;
                ++d;
            }
            this.aux[d] = x;
            if (d > maxDegree) {
                maxDegree = d;
            }
            x = nextX;
        }
        for (i = 0; i <= maxDegree && this.aux[i] == null; ++i) {
        }
        this.root = this.aux[i];
        this.aux[i] = null;
        ++i;
        while (i <= maxDegree) {
            Node<K, V> n = this.aux[i];
            if (n != null) {
                if (((Comparable)n.key).compareTo(this.root.key) < 0) {
                    this.root = this.link(this.root, n);
                } else {
                    this.link(n, this.root);
                }
                this.aux[i] = null;
            }
            ++i;
        }
        --this.size;
        return z;
    }

    private AddressableHeap.Handle<K, V> comparatorDeleteMin() {
        int i;
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        Node<K, V> z = this.root;
        Node x = this.root.child;
        z.child = null;
        z.next = null;
        z.prev = null;
        if (x == null) {
            this.root = null;
            this.size = 0L;
            return z;
        }
        int maxDegree = -1;
        while (x != null) {
            Node<K, V> y;
            Node nextX = x.next == x ? null : x.next;
            x.parent = null;
            x.prev.next = x.next;
            x.next.prev = x.prev;
            x.next = x;
            x.prev = x;
            int d = x.rank;
            while ((y = this.aux[d]) != null) {
                if (this.comparator.compare(y.key, x.key) < 0) {
                    Node tmp = x;
                    x = y;
                    y = tmp;
                }
                this.link(y, x);
                ++x.rank;
                this.aux[d] = null;
                ++d;
            }
            this.aux[d] = x;
            if (d > maxDegree) {
                maxDegree = d;
            }
            x = nextX;
        }
        for (i = 0; i <= maxDegree && this.aux[i] == null; ++i) {
        }
        this.root = this.aux[i];
        this.aux[i] = null;
        ++i;
        while (i <= maxDegree) {
            Node<K, V> n = this.aux[i];
            if (n != null) {
                if (this.comparator.compare(n.key, this.root.key) < 0) {
                    this.root = this.link(this.root, n);
                } else {
                    this.link(n, this.root);
                }
                this.aux[i] = null;
            }
            ++i;
        }
        --this.size;
        return z;
    }

    private Node<K, V> link(Node<K, V> y, Node<K, V> x) {
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
        return x;
    }

    private void cut(Node<K, V> x, Node<K, V> y) {
        y.child = x.next;
        if (y.child == x) {
            y.child = null;
        }
        x.prev.next = x.next;
        x.next.prev = x.prev;
        x.next = x;
        x.prev = x;
        x.parent = null;
        x.mark = false;
    }

    private void cascadingRankChange(Node<K, V> y) {
        while (y.mark) {
            y.mark = false;
            if (y.rank > 0) {
                --y.rank;
            }
            y = y.parent;
        }
        y.mark = true;
        if (y.rank > 0) {
            --y.rank;
        }
    }

    static class Node<K, V>
    implements AddressableHeap.Handle<K, V>,
    Serializable {
        private static final long serialVersionUID = 1L;
        SimpleFibonacciHeap<K, V> heap;
        K key;
        V value;
        Node<K, V> parent;
        Node<K, V> child;
        Node<K, V> next;
        Node<K, V> prev;
        int rank;
        boolean mark;

        Node(SimpleFibonacciHeap<K, V> heap, K key, V value) {
            this.heap = heap;
            this.key = key;
            this.value = value;
            this.parent = null;
            this.child = null;
            this.next = this;
            this.prev = this;
            this.rank = 0;
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
            SimpleFibonacciHeap<K, V> h = this.getOwner();
            if (((SimpleFibonacciHeap)h).comparator == null) {
                ((SimpleFibonacciHeap)h).comparableDecreaseKey(this, newKey);
            } else {
                ((SimpleFibonacciHeap)h).comparatorDecreaseKey(this, newKey);
            }
        }

        @Override
        @LogarithmicTime(amortized=true)
        public void delete() {
            if (this.next == null) {
                throw new IllegalArgumentException("Invalid handle!");
            }
            SimpleFibonacciHeap<K, V> h = this.getOwner();
            ((SimpleFibonacciHeap)h).forceDecreaseKeyToMinimum(this);
            h.deleteMin();
        }

        SimpleFibonacciHeap<K, V> getOwner() {
            if (this.heap.other != this.heap) {
                SimpleFibonacciHeap<K, V> root = this.heap;
                while (root != root.other) {
                    root = root.other;
                }
                SimpleFibonacciHeap<K, V> cur = this.heap;
                while (cur.other != root) {
                    SimpleFibonacciHeap next = cur.other;
                    cur.other = root;
                    cur = next;
                }
                this.heap = root;
            }
            return this.heap;
        }
    }
}

