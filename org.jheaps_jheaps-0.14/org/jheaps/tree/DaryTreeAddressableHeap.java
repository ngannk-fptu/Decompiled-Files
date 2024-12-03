/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.tree;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.AddressableHeap;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LogarithmicTime;

public class DaryTreeAddressableHeap<K, V>
implements AddressableHeap<K, V>,
Serializable {
    private static final long serialVersionUID = 1L;
    private final Comparator<? super K> comparator;
    private long size;
    private Node root;
    private final int d;
    private final int log2d;
    private Node[] aux;

    public DaryTreeAddressableHeap(int d) {
        this(d, null);
    }

    public DaryTreeAddressableHeap(int d, Comparator<? super K> comparator) {
        this.comparator = comparator;
        this.size = 0L;
        this.root = null;
        if (d < 2 || (d & d - 1) != 0) {
            throw new IllegalArgumentException("Branching factor d should be a power of 2.");
        }
        this.d = d;
        this.log2d = this.log2(d);
        this.aux = (Node[])Array.newInstance(Node.class, d);
    }

    @Override
    @LogarithmicTime
    public AddressableHeap.Handle<K, V> insert(K key, V value) {
        if (key == null) {
            throw new NullPointerException("Null keys not permitted");
        }
        Node n = new Node(key, value, this.d);
        if (this.size == 0L) {
            this.root = n;
            this.size = 1L;
            return n;
        }
        Node p = this.findNode(this.size);
        for (int i = 0; i < this.d; ++i) {
            if (p.children[i] != null) continue;
            p.children[i] = n;
            break;
        }
        n.parent = p;
        ++this.size;
        this.fixup(n);
        return n;
    }

    @Override
    @LogarithmicTime
    public AddressableHeap.Handle<K, V> insert(K key) {
        return this.insert(key, null);
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
        } else {
            this.root.delete();
        }
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
    @ConstantTime
    public void clear() {
        this.size = 0L;
        this.root = null;
    }

    @Override
    public Comparator<? super K> comparator() {
        return this.comparator;
    }

    private Node findNode(long node) {
        int s;
        int path;
        Node next;
        if (node == 0L) {
            return this.root;
        }
        long mask = (long)this.d - 1L;
        long location = node - 1L;
        int log = this.log2(node - 1L) / this.log2d;
        Node cur = this.root;
        for (int i = log; i >= 0 && (next = cur.children[path = (int)((location & mask << (s = i * this.log2d)) >>> s)]) != null; --i) {
            cur = next;
        }
        return cur;
    }

    private int log2(long n) {
        long log = 0L;
        if ((n & 0xFFFFFFFF00000000L) != 0L) {
            n >>>= 32;
            log = 32L;
        }
        if ((n & 0xFFFFFFFFFFFF0000L) != 0L) {
            n >>>= 16;
            log += 16L;
        }
        if (n >= 256L) {
            n >>>= 8;
            log += 8L;
        }
        if (n >= 16L) {
            n >>>= 4;
            log += 4L;
        }
        if (n >= 4L) {
            n >>>= 2;
            log += 2L;
        }
        return (int)(log + (n >>> 1));
    }

    private void fixup(Node n) {
        if (this.comparator == null) {
            Node p = n.parent;
            while (p != null && ((Comparable)n.key).compareTo(p.key) < 0) {
                Node pp = p.parent;
                this.swap(n, p);
                p = pp;
            }
        } else {
            Node p = n.parent;
            while (p != null && this.comparator.compare(n.key, p.key) < 0) {
                Node pp = p.parent;
                this.swap(n, p);
                p = pp;
            }
        }
    }

    private void fixdown(Node n) {
        if (this.comparator == null) {
            while (n.children[0] != null) {
                int min = 0;
                Node child = n.children[min];
                for (int i = 1; i < this.d; ++i) {
                    Node candidate = n.children[i];
                    if (candidate == null || ((Comparable)candidate.key).compareTo(child.key) >= 0) continue;
                    min = i;
                    child = candidate;
                }
                if (((Comparable)n.key).compareTo(child.key) > 0) {
                    this.swap(child, n);
                    continue;
                }
                break;
            }
        } else {
            while (n.children[0] != null) {
                int min = 0;
                Node child = n.children[min];
                for (int i = 1; i < this.d; ++i) {
                    Node candidate = n.children[i];
                    if (candidate == null || this.comparator.compare(candidate.key, child.key) >= 0) continue;
                    min = i;
                    child = candidate;
                }
                if (this.comparator.compare(n.key, child.key) > 0) {
                    this.swap(child, n);
                    continue;
                }
                break;
            }
        }
    }

    private void swap(Node a, Node b) {
        if (a == null || b == null || a == b) {
            return;
        }
        if (a.parent == b) {
            Node tmp = a;
            a = b;
            b = tmp;
        }
        Node pa = a.parent;
        if (b.parent == a) {
            int i;
            int whichChild = -1;
            for (i = 0; i < this.d; ++i) {
                this.aux[i] = b.children[i];
                if (b == a.children[i]) {
                    b.children[i] = a;
                    a.parent = b;
                } else {
                    b.children[i] = a.children[i];
                    if (b.children[i] != null) {
                        b.children[i].parent = b;
                    }
                }
                if (pa == null || pa.children[i] != a) continue;
                whichChild = i;
            }
            b.parent = pa;
            if (pa != null) {
                pa.children[whichChild] = b;
            }
            for (i = 0; i < this.d; ++i) {
                a.children[i] = this.aux[i];
                if (a.children[i] != null) {
                    a.children[i].parent = a;
                }
                this.aux[i] = null;
            }
        } else {
            int i;
            Node pb = b.parent;
            for (i = 0; i < this.d; ++i) {
                this.aux[i] = b.children[i];
                b.children[i] = a.children[i];
                if (b.children[i] == null) continue;
                b.children[i].parent = b;
            }
            for (i = 0; i < this.d; ++i) {
                a.children[i] = this.aux[i];
                if (a.children[i] != null) {
                    a.children[i].parent = a;
                }
                this.aux[i] = null;
            }
            int aIsChild = -1;
            if (pa != null) {
                for (int i2 = 0; i2 < this.d; ++i2) {
                    if (pa.children[i2] != a) continue;
                    aIsChild = i2;
                }
            } else {
                b.parent = null;
            }
            int bIsChild = -1;
            if (pb != null) {
                for (int i3 = 0; i3 < this.d; ++i3) {
                    if (pb.children[i3] != b) continue;
                    bIsChild = i3;
                }
            } else {
                a.parent = null;
            }
            if (aIsChild >= 0) {
                pa.children[aIsChild] = b;
                b.parent = pa;
            }
            if (bIsChild >= 0) {
                pb.children[bIsChild] = a;
                a.parent = pb;
            }
        }
        if (this.root == a) {
            this.root = b;
        } else if (this.root == b) {
            this.root = a;
        }
    }

    private class Node
    implements AddressableHeap.Handle<K, V>,
    Serializable {
        private static final long serialVersionUID = 1L;
        K key;
        V value;
        Node parent;
        Node[] children;

        Node(K key, V value, int d) {
            this.key = key;
            this.value = value;
            this.parent = null;
            this.children = (Node[])Array.newInstance(Node.class, d);
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
            if (this.parent == null && DaryTreeAddressableHeap.this.root != this) {
                throw new IllegalArgumentException("Invalid handle!");
            }
            int c = DaryTreeAddressableHeap.this.comparator == null ? ((Comparable)newKey).compareTo(this.key) : DaryTreeAddressableHeap.this.comparator.compare(newKey, this.key);
            if (c > 0) {
                throw new IllegalArgumentException("Keys can only be decreased!");
            }
            this.key = newKey;
            if (c == 0 || DaryTreeAddressableHeap.this.root == this) {
                return;
            }
            DaryTreeAddressableHeap.this.fixup(this);
        }

        @Override
        @LogarithmicTime
        public void delete() {
            if (this.parent == null && DaryTreeAddressableHeap.this.root != this) {
                throw new IllegalArgumentException("Invalid handle!");
            }
            if (DaryTreeAddressableHeap.this.size == 0L) {
                throw new NoSuchElementException();
            }
            Node last = DaryTreeAddressableHeap.this.findNode(DaryTreeAddressableHeap.this.size - 1L);
            DaryTreeAddressableHeap.this.swap(this, last);
            if (this.parent != null) {
                for (int i = 0; i < DaryTreeAddressableHeap.this.d; ++i) {
                    if (this.parent.children[i] != this) continue;
                    this.parent.children[i] = null;
                }
                this.parent = null;
            }
            DaryTreeAddressableHeap.this.size--;
            if (DaryTreeAddressableHeap.this.size == 0L) {
                DaryTreeAddressableHeap.this.root = null;
            } else if (this != last) {
                DaryTreeAddressableHeap.this.fixdown(last);
            }
        }
    }
}

