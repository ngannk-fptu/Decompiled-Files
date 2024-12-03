/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.tree;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.AddressableHeap;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LogarithmicTime;

public class BinaryTreeAddressableHeap<K, V>
implements AddressableHeap<K, V>,
Serializable {
    private static final long serialVersionUID = 1L;
    private final Comparator<? super K> comparator;
    private long size;
    private Node root;

    public BinaryTreeAddressableHeap() {
        this(null);
    }

    public BinaryTreeAddressableHeap(Comparator<? super K> comparator) {
        this.comparator = comparator;
        this.size = 0L;
        this.root = null;
    }

    @Override
    @LogarithmicTime
    public AddressableHeap.Handle<K, V> insert(K key) {
        return this.insert(key, null);
    }

    @Override
    @LogarithmicTime
    public AddressableHeap.Handle<K, V> insert(K key, V value) {
        if (key == null) {
            throw new NullPointerException("Null keys not permitted");
        }
        Node n = new Node(key, value);
        if (this.size == 0L) {
            this.root = n;
            this.size = 1L;
            return n;
        }
        if (this.size == 1L) {
            int c = this.comparator == null ? ((Comparable)key).compareTo(this.root.key) : this.comparator.compare(key, this.root.key);
            if (c < 0) {
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
        Node p = this.findParentNode(this.size + 1L);
        if (p.o_c == null) {
            p.o_c = n;
        } else {
            p.o_c.y_s = n;
        }
        n.y_s = p;
        ++this.size;
        this.fixup(n);
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
    @LogarithmicTime
    public AddressableHeap.Handle<K, V> deleteMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        Node oldRoot = this.root;
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
        Node lastNodeParent = this.findParentNode(this.size);
        Node lastNode = lastNodeParent.o_c;
        if (lastNode.y_s != lastNodeParent) {
            Node tmp = lastNode;
            lastNode = tmp.y_s;
            tmp.y_s = lastNodeParent;
        } else {
            lastNodeParent.o_c = null;
        }
        lastNode.y_s = null;
        --this.size;
        if (this.root.o_c.y_s == this.root) {
            this.root.o_c.y_s = lastNode;
        } else {
            this.root.o_c.y_s.y_s = lastNode;
        }
        lastNode.o_c = this.root.o_c;
        this.root = lastNode;
        this.fixdown(this.root);
        oldRoot.o_c = null;
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

    private void fixup(Node n) {
        if (this.comparator == null) {
            Node p = this.getParent(n);
            while (p != null && ((Comparable)n.key).compareTo(p.key) < 0) {
                Node pp = this.getParent(p);
                this.swap(n, p, pp);
                p = pp;
            }
        } else {
            Node p = this.getParent(n);
            while (p != null && this.comparator.compare(n.key, p.key) < 0) {
                Node pp = this.getParent(p);
                this.swap(n, p, pp);
                p = pp;
            }
        }
    }

    private void fixdown(Node n) {
        if (this.comparator == null) {
            Node p = this.getParent(n);
            while (n.o_c != null) {
                Node child = n.o_c;
                if (child.y_s != n && ((Comparable)child.y_s.key).compareTo(child.key) < 0) {
                    child = child.y_s;
                }
                if (((Comparable)n.key).compareTo(child.key) > 0) {
                    this.swap(child, n, p);
                    p = child;
                    continue;
                }
                break;
            }
        } else {
            Node p = this.getParent(n);
            while (n.o_c != null) {
                Node child = n.o_c;
                if (child.y_s != n && this.comparator.compare(child.y_s.key, child.key) < 0) {
                    child = child.y_s;
                }
                if (this.comparator.compare(n.key, child.key) > 0) {
                    this.swap(child, n, p);
                    p = child;
                    continue;
                }
                break;
            }
        }
    }

    private Node getParent(Node n) {
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

    private Node findParentNode(long node) {
        long[] s = new long[]{node};
        BitSet bits = BitSet.valueOf(s);
        Node cur = this.root;
        for (int i = bits.length() - 2; i > 0; --i) {
            cur = bits.get(i) ? cur.o_c.y_s : cur.o_c;
        }
        return cur;
    }

    private void swap(Node n, Node root) {
        Node nLeftChild = n.o_c;
        if (root.o_c == n) {
            if (n.y_s == root) {
                n.o_c = root;
                root.y_s = n;
            } else {
                root.y_s = n.y_s;
                root.y_s.y_s = n;
                n.o_c = root;
            }
        } else {
            root.o_c.y_s = root;
            n.o_c = root.o_c;
            root.y_s = n;
        }
        n.y_s = null;
        root.o_c = nLeftChild;
        if (nLeftChild != null) {
            if (nLeftChild.y_s == n) {
                nLeftChild.y_s = root;
            } else {
                nLeftChild.y_s.y_s = root;
            }
        }
        this.root = n;
    }

    private void swap(Node n, Node p, Node pp) {
        if (pp == null) {
            this.swap(n, p);
            return;
        }
        Node nLeftChild = n.o_c;
        if (pp.o_c == p) {
            if (p.o_c == n) {
                if (n.y_s == p) {
                    pp.o_c = n;
                    n.y_s = p.y_s;
                    n.o_c = p;
                    p.y_s = n;
                } else {
                    n.y_s.y_s = n;
                    Node tmp = n.y_s;
                    n.y_s = p.y_s;
                    p.y_s = tmp;
                    pp.o_c = n;
                    n.o_c = p;
                }
            } else {
                Node tmp = p.o_c;
                n.y_s = p.y_s;
                pp.o_c = n;
                n.o_c = tmp;
                tmp.y_s = p;
                p.y_s = n;
            }
        } else if (p.o_c == n) {
            if (n.y_s == p) {
                n.y_s = pp;
                pp.o_c.y_s = n;
                n.o_c = p;
                p.y_s = n;
            } else {
                pp.o_c.y_s = n;
                p.y_s = n.y_s;
                n.y_s = pp;
                n.o_c = p;
                p.y_s.y_s = n;
            }
        } else {
            pp.o_c.y_s = n;
            n.y_s = pp;
            n.o_c = p.o_c;
            n.o_c.y_s = p;
            p.y_s = n;
        }
        p.o_c = nLeftChild;
        if (nLeftChild != null) {
            if (nLeftChild.y_s == n) {
                nLeftChild.y_s = p;
            } else {
                nLeftChild.y_s.y_s = p;
            }
        }
    }

    private class Node
    implements AddressableHeap.Handle<K, V>,
    Serializable {
        private static final long serialVersionUID = 1L;
        K key;
        V value;
        Node o_c;
        Node y_s;

        Node(K key, V value) {
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
        @LogarithmicTime
        public void decreaseKey(K newKey) {
            if (this != BinaryTreeAddressableHeap.this.root && this.y_s == null) {
                throw new IllegalArgumentException("Invalid handle!");
            }
            int c = BinaryTreeAddressableHeap.this.comparator == null ? ((Comparable)newKey).compareTo(this.key) : BinaryTreeAddressableHeap.this.comparator.compare(newKey, this.key);
            if (c > 0) {
                throw new IllegalArgumentException("Keys can only be decreased!");
            }
            this.key = newKey;
            if (c == 0 || BinaryTreeAddressableHeap.this.root == this) {
                return;
            }
            BinaryTreeAddressableHeap.this.fixup(this);
        }

        @Override
        @LogarithmicTime
        public void delete() {
            if (this != BinaryTreeAddressableHeap.this.root && this.y_s == null) {
                throw new IllegalArgumentException("Invalid handle!");
            }
            Node p = BinaryTreeAddressableHeap.this.getParent(this);
            while (p != null) {
                Node pp = BinaryTreeAddressableHeap.this.getParent(p);
                BinaryTreeAddressableHeap.this.swap(this, p, pp);
                p = pp;
            }
            BinaryTreeAddressableHeap.this.deleteMin();
            this.o_c = null;
            this.y_s = null;
        }
    }
}

