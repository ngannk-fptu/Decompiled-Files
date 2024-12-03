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

public class SkewHeap<K, V>
implements MergeableAddressableHeap<K, V>,
Serializable {
    private static final long serialVersionUID = 1L;
    protected final Comparator<? super K> comparator;
    protected long size;
    protected Node<K, V> root;
    protected SkewHeap<K, V> other;

    public SkewHeap() {
        this(null);
    }

    public SkewHeap(Comparator<? super K> comparator) {
        this.comparator = comparator;
        this.size = 0L;
        this.root = null;
        this.other = this;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public AddressableHeap.Handle<K, V> insert(K key) {
        return this.insert(key, null);
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
        Node<K, V> n = this.createNode(key, value);
        if (this.size == 0L) {
            this.root = n;
            this.size = 1L;
            return n;
        }
        if (this.size == 1L) {
            int c = this.comparator == null ? ((Comparable)key).compareTo(this.root.key) : this.comparator.compare(key, this.root.key);
            if (c <= 0) {
                n.o_c = this.root;
                this.root.y_s = n;
                this.root = n;
            } else {
                this.root.o_c = n;
                n.y_s = this.root;
            }
            this.size = 2L;
            return n;
        }
        this.root = this.comparator == null ? this.union(this.root, n) : this.unionWithComparator(this.root, n);
        ++this.size;
        return n;
    }

    @Override
    @ConstantTime
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
        if (this.size == 1L) {
            this.root = null;
            this.size = 0L;
            return oldRoot;
        }
        if (this.size == 2L) {
            this.root = this.root.o_c;
            this.root.o_c = null;
            this.root.y_s = null;
            this.size = 1L;
            oldRoot.o_c = null;
            return oldRoot;
        }
        this.root = this.unlinkAndUnionChildren(this.root);
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
    @ConstantTime
    public void clear() {
        this.root = null;
        this.size = 0L;
    }

    @Override
    public void meld(MergeableAddressableHeap<K, V> other) {
        SkewHeap h = (SkewHeap)other;
        if (this.comparator != null ? h.comparator == null || !h.comparator.equals(this.comparator) : h.comparator != null) {
            throw new IllegalArgumentException("Cannot meld heaps using different comparators!");
        }
        if (h.other != h) {
            throw new IllegalStateException("A heap cannot be used after a meld.");
        }
        this.size += h.size;
        this.root = this.comparator == null ? this.union(this.root, h.root) : this.unionWithComparator(this.root, h.root);
        h.size = 0L;
        h.root = null;
        h.other = this;
    }

    private void decreaseKey(Node<K, V> n, K newKey) {
        int c = this.comparator == null ? ((Comparable)newKey).compareTo(n.key) : this.comparator.compare(newKey, n.key);
        if (c > 0) {
            throw new IllegalArgumentException("Keys can only be decreased!");
        }
        if (c == 0 || this.root == n) {
            n.key = newKey;
            return;
        }
        this.delete(n);
        n.key = newKey;
        this.root = this.comparator == null ? this.union(this.root, n) : this.unionWithComparator(this.root, n);
        ++this.size;
    }

    protected Node<K, V> createNode(K key, V value) {
        return new Node<K, V>(this, key, value);
    }

    protected void delete(Node<K, V> n) {
        if (n == this.root) {
            this.deleteMin();
            return;
        }
        if (n.y_s == null) {
            throw new IllegalArgumentException("Invalid handle!");
        }
        Node<K, V> childTree = this.unlinkAndUnionChildren(n);
        Node<K, V> p = this.getParent(n);
        if (childTree == null) {
            if (p.o_c == n) {
                p.o_c = n.y_s == p ? null : n.y_s;
            } else {
                p.o_c.y_s = p;
            }
        } else if (p.o_c == n) {
            childTree.y_s = n.y_s;
            p.o_c = childTree;
        } else {
            p.o_c.y_s = childTree;
            childTree.y_s = p;
        }
        --this.size;
        n.o_c = null;
        n.y_s = null;
    }

    protected Node<K, V> unlinkAndUnionChildren(Node<K, V> n) {
        Node child1 = n.o_c;
        if (child1 == null) {
            return null;
        }
        n.o_c = null;
        Node child2 = child1.y_s;
        if (child2 == n) {
            child2 = null;
        } else {
            child2.y_s = null;
        }
        child1.y_s = null;
        if (this.comparator == null) {
            return this.union(child1, child2);
        }
        return this.unionWithComparator(child1, child2);
    }

    protected Node<K, V> getParent(Node<K, V> n) {
        if (n.y_s == null) {
            return null;
        }
        Node c = n.y_s;
        if (c.o_c == n) {
            return c;
        }
        Node p1 = c.y_s;
        if (p1 != null && p1.o_c == n) {
            return p1;
        }
        return c;
    }

    protected Node<K, V> unlinkRightChild(Node<K, V> n) {
        Node left = n.o_c;
        if (left == null || left.y_s == n) {
            return null;
        }
        Node right = left.y_s;
        left.y_s = n;
        right.y_s = null;
        return right;
    }

    protected Node<K, V> union(Node<K, V> root1, Node<K, V> root2) {
        Node<K, V> newRoot;
        if (root1 == null) {
            return root2;
        }
        if (root2 == null) {
            return root1;
        }
        int c = ((Comparable)root1.key).compareTo(root2.key);
        if (c <= 0) {
            newRoot = root1;
            root1 = this.unlinkRightChild(root1);
        } else {
            newRoot = root2;
            root2 = this.unlinkRightChild(root2);
        }
        Node<K, V> cur = newRoot;
        while (root1 != null && root2 != null) {
            c = ((Comparable)root1.key).compareTo(root2.key);
            if (c <= 0) {
                root1.y_s = cur.o_c == null ? cur : cur.o_c;
                cur.o_c = root1;
                cur = root1;
                root1 = this.unlinkRightChild(root1);
                continue;
            }
            root2.y_s = cur.o_c == null ? cur : cur.o_c;
            cur.o_c = root2;
            cur = root2;
            root2 = this.unlinkRightChild(root2);
        }
        while (root1 != null) {
            root1.y_s = cur.o_c == null ? cur : cur.o_c;
            cur.o_c = root1;
            cur = root1;
            root1 = this.unlinkRightChild(root1);
        }
        while (root2 != null) {
            root2.y_s = cur.o_c == null ? cur : cur.o_c;
            cur.o_c = root2;
            cur = root2;
            root2 = this.unlinkRightChild(root2);
        }
        return newRoot;
    }

    protected Node<K, V> unionWithComparator(Node<K, V> root1, Node<K, V> root2) {
        Node<K, V> newRoot;
        if (root1 == null) {
            return root2;
        }
        if (root2 == null) {
            return root1;
        }
        int c = this.comparator.compare(root1.key, root2.key);
        if (c <= 0) {
            newRoot = root1;
            root1 = this.unlinkRightChild(root1);
        } else {
            newRoot = root2;
            root2 = this.unlinkRightChild(root2);
        }
        Node<K, V> cur = newRoot;
        while (root1 != null && root2 != null) {
            c = this.comparator.compare(root1.key, root2.key);
            if (c <= 0) {
                root1.y_s = cur.o_c == null ? cur : cur.o_c;
                cur.o_c = root1;
                cur = root1;
                root1 = this.unlinkRightChild(root1);
                continue;
            }
            root2.y_s = cur.o_c == null ? cur : cur.o_c;
            cur.o_c = root2;
            cur = root2;
            root2 = this.unlinkRightChild(root2);
        }
        while (root1 != null) {
            root1.y_s = cur.o_c == null ? cur : cur.o_c;
            cur.o_c = root1;
            cur = root1;
            root1 = this.unlinkRightChild(root1);
        }
        while (root2 != null) {
            root2.y_s = cur.o_c == null ? cur : cur.o_c;
            cur.o_c = root2;
            cur = root2;
            root2 = this.unlinkRightChild(root2);
        }
        return newRoot;
    }

    static class Node<K, V>
    implements AddressableHeap.Handle<K, V>,
    Serializable {
        private static final long serialVersionUID = 1L;
        SkewHeap<K, V> heap;
        K key;
        V value;
        Node<K, V> o_c;
        Node<K, V> y_s;

        Node(SkewHeap<K, V> heap, K key, V value) {
            this.heap = heap;
            this.key = key;
            this.value = value;
            this.o_c = null;
            this.y_s = null;
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
            ((SkewHeap)this.getOwner()).decreaseKey(this, newKey);
        }

        @Override
        public void delete() {
            this.getOwner().delete(this);
        }

        SkewHeap<K, V> getOwner() {
            if (this.heap.other != this.heap) {
                SkewHeap<K, V> root = this.heap;
                while (root != root.other) {
                    root = root.other;
                }
                SkewHeap<K, V> cur = this.heap;
                while (cur.other != root) {
                    SkewHeap next = cur.other;
                    cur.other = root;
                    cur = next;
                }
                this.heap = root;
            }
            return this.heap;
        }
    }
}

