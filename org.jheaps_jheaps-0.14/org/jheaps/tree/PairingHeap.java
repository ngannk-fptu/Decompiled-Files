/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.tree;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.AddressableHeap;
import org.jheaps.MergeableAddressableHeap;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LogarithmicTime;

public class PairingHeap<K, V>
implements MergeableAddressableHeap<K, V>,
Serializable {
    private static final long serialVersionUID = 1L;
    private final Comparator<? super K> comparator;
    private Node<K, V> root = null;
    private long size;
    private PairingHeap<K, V> other;

    @ConstantTime
    public PairingHeap() {
        this(null);
    }

    @ConstantTime
    public PairingHeap(Comparator<? super K> comparator) {
        this.comparator = comparator;
        this.size = 0L;
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
    @ConstantTime(amortized=false)
    public AddressableHeap.Handle<K, V> findMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        return this.root;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public AddressableHeap.Handle<K, V> deleteMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        Node<K, V> oldRoot = this.root;
        this.root = this.combine(this.cutChildren(this.root));
        --this.size;
        return oldRoot;
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
    }

    @Override
    @LogarithmicTime(amortized=true)
    public void meld(MergeableAddressableHeap<K, V> other) {
        PairingHeap h = (PairingHeap)other;
        if (this.comparator != null ? h.comparator == null || !h.comparator.equals(this.comparator) : h.comparator != null) {
            throw new IllegalArgumentException("Cannot meld heaps using different comparators!");
        }
        if (h.other != h) {
            throw new IllegalStateException("A heap cannot be used after a meld.");
        }
        this.size += h.size;
        this.root = this.comparator == null ? this.link(this.root, h.root) : this.linkWithComparator(this.root, h.root);
        h.size = 0L;
        h.root = null;
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
        if (n.o_s == null) {
            throw new IllegalArgumentException("Invalid handle!");
        }
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
        this.root = this.comparator == null ? this.link(this.root, n) : this.linkWithComparator(this.root, n);
    }

    private void delete(Node<K, V> n) {
        if (this.root == n) {
            this.deleteMin();
            n.o_c = null;
            n.y_s = null;
            n.o_s = null;
            return;
        }
        if (n.o_s == null) {
            throw new IllegalArgumentException("Invalid handle!");
        }
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
        Node<K, V> t = this.combine(this.cutChildren(n));
        this.root = this.comparator == null ? this.link(this.root, t) : this.linkWithComparator(this.root, t);
        --this.size;
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
        PairingHeap<K, V> heap;
        K key;
        V value;
        Node<K, V> o_c;
        Node<K, V> y_s;
        Node<K, V> o_s;

        Node(PairingHeap<K, V> heap, K key, V value) {
            this.heap = heap;
            this.key = key;
            this.value = value;
            this.o_c = null;
            this.y_s = null;
            this.o_s = null;
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
        @LogarithmicTime(amortized=true)
        public void decreaseKey(K newKey) {
            ((PairingHeap)this.getOwner()).decreaseKey(this, newKey);
        }

        @Override
        @LogarithmicTime(amortized=true)
        public void delete() {
            ((PairingHeap)this.getOwner()).delete(this);
        }

        PairingHeap<K, V> getOwner() {
            if (((PairingHeap)this.heap).other != this.heap) {
                PairingHeap root = this.heap;
                while (root != root.other) {
                    root = root.other;
                }
                PairingHeap cur = this.heap;
                while (cur.other != root) {
                    PairingHeap next = cur.other;
                    cur.other = root;
                    cur = next;
                }
                this.heap = root;
            }
            return this.heap;
        }
    }
}

