/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.tree;

import java.util.Comparator;
import java.util.LinkedList;
import org.jheaps.tree.SkewHeap;

public class LeftistHeap<K, V>
extends SkewHeap<K, V> {
    private static final long serialVersionUID = -5948402731186806608L;

    public LeftistHeap() {
        this(null);
    }

    public LeftistHeap(Comparator<? super K> comparator) {
        super(comparator);
    }

    @Override
    protected SkewHeap.Node<K, V> createNode(K key, V value) {
        return new LeftistNode<K, V>(this, key, value);
    }

    protected void swapChildren(SkewHeap.Node<K, V> n) {
        SkewHeap.Node right;
        SkewHeap.Node left = n.o_c;
        if (left != null && (right = left.y_s) != n) {
            n.o_c = right;
            right.y_s = left;
            left.y_s = n;
        }
    }

    @Override
    protected SkewHeap.Node<K, V> union(SkewHeap.Node<K, V> root1, SkewHeap.Node<K, V> root2) {
        SkewHeap.Node<K, V> newRoot;
        if (root1 == null) {
            return root2;
        }
        if (root2 == null) {
            return root1;
        }
        LinkedList<LeftistNode> path = new LinkedList<LeftistNode>();
        int c = ((Comparable)root1.key).compareTo(root2.key);
        if (c <= 0) {
            newRoot = root1;
            root1 = this.unlinkRightChild(root1);
        } else {
            newRoot = root2;
            root2 = this.unlinkRightChild(root2);
        }
        SkewHeap.Node<K, V> cur = newRoot;
        path.push((LeftistNode)cur);
        while (root1 != null && root2 != null) {
            c = ((Comparable)root1.key).compareTo(root2.key);
            if (c <= 0) {
                if (cur.o_c == null) {
                    cur.o_c = root1;
                } else {
                    cur.o_c.y_s = root1;
                }
                root1.y_s = cur;
                cur = root1;
                path.push((LeftistNode)cur);
                root1 = this.unlinkRightChild(root1);
                continue;
            }
            if (cur.o_c == null) {
                cur.o_c = root2;
            } else {
                cur.o_c.y_s = root2;
            }
            root2.y_s = cur;
            cur = root2;
            path.push((LeftistNode)cur);
            root2 = this.unlinkRightChild(root2);
        }
        if (root1 != null) {
            if (cur.o_c == null) {
                cur.o_c = root1;
            } else {
                cur.o_c.y_s = root1;
            }
            root1.y_s = cur;
        }
        if (root2 != null) {
            if (cur.o_c == null) {
                cur.o_c = root2;
            } else {
                cur.o_c.y_s = root2;
            }
            root2.y_s = cur;
        }
        while (!path.isEmpty()) {
            LeftistNode n = (LeftistNode)path.pop();
            if (n.o_c != null) {
                LeftistNode nLeft = (LeftistNode)n.o_c;
                int nplLeft = nLeft.npl;
                int nplRight = -1;
                if (nLeft.y_s != n) {
                    LeftistNode nRight = (LeftistNode)nLeft.y_s;
                    nplRight = nRight.npl;
                }
                n.npl = 1 + Math.min(nplLeft, nplRight);
                if (nplLeft >= nplRight) continue;
                this.swapChildren(n);
                continue;
            }
            n.npl = 0;
        }
        return newRoot;
    }

    @Override
    protected SkewHeap.Node<K, V> unionWithComparator(SkewHeap.Node<K, V> root1, SkewHeap.Node<K, V> root2) {
        SkewHeap.Node<K, V> newRoot;
        if (root1 == null) {
            return root2;
        }
        if (root2 == null) {
            return root1;
        }
        LinkedList<LeftistNode> path = new LinkedList<LeftistNode>();
        int c = this.comparator.compare(root1.key, root2.key);
        if (c <= 0) {
            newRoot = root1;
            root1 = this.unlinkRightChild(root1);
        } else {
            newRoot = root2;
            root2 = this.unlinkRightChild(root2);
        }
        SkewHeap.Node<K, V> cur = newRoot;
        path.push((LeftistNode)cur);
        while (root1 != null && root2 != null) {
            c = this.comparator.compare(root1.key, root2.key);
            if (c <= 0) {
                if (cur.o_c == null) {
                    cur.o_c = root1;
                } else {
                    cur.o_c.y_s = root1;
                }
                root1.y_s = cur;
                cur = root1;
                path.push((LeftistNode)cur);
                root1 = this.unlinkRightChild(root1);
                continue;
            }
            if (cur.o_c == null) {
                cur.o_c = root2;
            } else {
                cur.o_c.y_s = root2;
            }
            root2.y_s = cur;
            cur = root2;
            path.push((LeftistNode)cur);
            root2 = this.unlinkRightChild(root2);
        }
        if (root1 != null) {
            if (cur.o_c == null) {
                cur.o_c = root1;
            } else {
                cur.o_c.y_s = root1;
            }
            root1.y_s = cur;
        }
        if (root2 != null) {
            if (cur.o_c == null) {
                cur.o_c = root2;
            } else {
                cur.o_c.y_s = root2;
            }
            root2.y_s = cur;
        }
        while (!path.isEmpty()) {
            LeftistNode n = (LeftistNode)path.pop();
            if (n.o_c != null) {
                LeftistNode nLeft = (LeftistNode)n.o_c;
                int nplLeft = nLeft.npl;
                int nplRight = -1;
                if (nLeft.y_s != n) {
                    LeftistNode nRight = (LeftistNode)nLeft.y_s;
                    nplRight = nRight.npl;
                }
                n.npl = 1 + Math.min(nplLeft, nplRight);
                if (nplLeft >= nplRight) continue;
                this.swapChildren(n);
                continue;
            }
            n.npl = 0;
        }
        return newRoot;
    }

    static class LeftistNode<K, V>
    extends SkewHeap.Node<K, V> {
        private static final long serialVersionUID = 1L;
        int npl = 0;

        LeftistNode(LeftistHeap<K, V> heap, K key, V value) {
            super(heap, key, value);
        }
    }
}

