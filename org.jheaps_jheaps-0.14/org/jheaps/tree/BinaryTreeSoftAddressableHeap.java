/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.tree;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.AddressableHeap;
import org.jheaps.MergeableAddressableHeap;
import org.jheaps.annotations.ConstantTime;

public class BinaryTreeSoftAddressableHeap<K, V>
implements MergeableAddressableHeap<K, V>,
Serializable {
    private static final long serialVersionUID = 1L;
    private final Comparator<? super K> comparator;
    private static final long[] TARGET_SIZE = new long[]{1L, 2L, 3L, 5L, 8L, 12L, 18L, 27L, 41L, 62L, 93L, 140L, 210L, 315L, 473L, 710L, 1065L, 1598L, 2397L, 3596L, 5394L, 8091L, 12137L, 18206L, 27309L, 40964L, 61446L, 92169L, 138254L, 207381L, 311072L, 466608L, 699912L, 1049868L, 1574802L, 2362203L, 3543305L, 5314958L, 7972437L, 11958656L, 17937984L, 26906976L, 40360464L, 60540696L, 90811044L, 136216566L, 204324849L, 306487274L, 459730911L, 689596367L, 1034394551L, 1551591827L, 2327387741L, 3491081612L, 5236622418L, 7854933627L, 11782400441L, 17673600662L, 26510400993L, 39765601490L, 59648402235L, 89472603353L, 134208905030L};
    private final int rankLimit;
    final RootList<K, V> rootList;
    private long size;
    private BinaryTreeSoftAddressableHeap<K, V> other;

    public BinaryTreeSoftAddressableHeap(double errorRate) {
        this(errorRate, null);
    }

    public BinaryTreeSoftAddressableHeap(double errorRate, Comparator<? super K> comparator) {
        if (Double.compare(errorRate, 0.0) <= 0) {
            throw new IllegalArgumentException("Error rate must be positive");
        }
        if (Double.compare(errorRate, 1.0) >= 0) {
            throw new IllegalArgumentException("Error rate must be less than one");
        }
        this.rankLimit = (int)Math.ceil(Math.log(1.0 / errorRate) / Math.log(2.0)) + 5;
        this.rootList = new RootList();
        this.comparator = comparator;
        this.size = 0L;
        this.other = this;
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
        this.rootList.head = null;
        this.rootList.tail = null;
        this.size = 0L;
    }

    @Override
    public void meld(MergeableAddressableHeap<K, V> other) {
        BinaryTreeSoftAddressableHeap h = (BinaryTreeSoftAddressableHeap)other;
        if (this.comparator != null ? h.comparator == null || !h.comparator.equals(this.comparator) : h.comparator != null) {
            throw new IllegalArgumentException("Cannot meld heaps using different comparators!");
        }
        if (this.rankLimit != h.rankLimit) {
            throw new IllegalArgumentException("Cannot meld heaps with different error rates!");
        }
        if (h.other != h) {
            throw new IllegalStateException("A heap cannot be used after a meld.");
        }
        this.mergeInto(h.rootList.head, h.rootList.tail);
        this.size += h.size;
        h.size = 0L;
        h.rootList.head = null;
        h.rootList.tail = null;
        h.other = this;
    }

    @Override
    public AddressableHeap.Handle<K, V> insert(K key, V value) {
        if (this.other != this) {
            throw new IllegalStateException("A heap cannot be used after a meld");
        }
        if (key == null) {
            throw new NullPointerException("Null keys not permitted");
        }
        SoftHandle<K, V> n = new SoftHandle<K, V>(this, key, value);
        TreeNode<K, V> treeNode = new TreeNode<K, V>(n);
        RootListNode<K, V> rootListNode = new RootListNode<K, V>(treeNode);
        this.mergeInto(rootListNode, rootListNode);
        ++this.size;
        return n;
    }

    @Override
    public AddressableHeap.Handle<K, V> insert(K key) {
        return this.insert(key, null);
    }

    @Override
    public SoftHandle<K, V> findMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        return this.rootList.head.suffixMin.root.cHead;
    }

    @Override
    public AddressableHeap.Handle<K, V> deleteMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        RootListNode minRootListNode = this.rootList.head.suffixMin;
        TreeNode root = minRootListNode.root;
        SoftHandle result = root.cHead;
        if (result.next != null) {
            result.next.prev = null;
            result.next.tree = root;
        }
        root.cHead = result.next;
        --root.cSize;
        if (root.cHead == null || root.cSize <= this.targetSize(root.rank) / 2L) {
            if (root.left != null || root.right != null) {
                this.sift(root);
                this.updateSuffixMin(minRootListNode);
            } else if (root.cHead == null) {
                RootListNode minRootPrevListNode = minRootListNode.prev;
                this.delete(minRootListNode);
                this.updateSuffixMin(minRootPrevListNode);
            }
        }
        result.next = null;
        result.prev = null;
        result.tree = null;
        --this.size;
        return result;
    }

    private long targetSize(int rank) {
        return rank <= this.rankLimit ? 1L : TARGET_SIZE[rank - this.rankLimit];
    }

    private void sift(TreeNode<K, V> x) {
        ArrayDeque stack = new ArrayDeque();
        stack.push(x);
        while (!stack.isEmpty()) {
            x = (TreeNode)stack.peek();
            TreeNode xLeft = x.left;
            TreeNode xRight = x.right;
            if (xLeft == null && xRight == null || x.cHead != null && x.cSize >= this.targetSize(x.rank)) {
                stack.pop();
                continue;
            }
            if (xLeft == null || xRight != null && (this.comparator == null && ((Comparable)xLeft.cKey).compareTo(xRight.cKey) > 0 || this.comparator != null && this.comparator.compare(xLeft.cKey, xRight.cKey) > 0)) {
                x.left = xRight;
                x.right = xLeft;
                xLeft = x.left;
            }
            xLeft.cTail.next = x.cHead;
            if (x.cHead != null) {
                x.cHead.prev = xLeft.cTail;
            }
            x.cHead = xLeft.cHead;
            if (x.cTail == null) {
                x.cTail = xLeft.cTail;
            }
            x.cHead.tree = x;
            x.cSize += xLeft.cSize;
            x.cKey = xLeft.cKey;
            xLeft.cKey = null;
            xLeft.cHead = null;
            xLeft.cTail = null;
            xLeft.cSize = 0L;
            if (xLeft.left != null || xLeft.right != null) {
                stack.push(xLeft);
                continue;
            }
            x.left = null;
        }
    }

    private TreeNode<K, V> combine(TreeNode<K, V> x, TreeNode<K, V> y) {
        TreeNode z = new TreeNode();
        z.left = x;
        x.parent = z;
        z.right = y;
        y.parent = z;
        z.rank = x.rank + 1;
        this.sift(z);
        return z;
    }

    private void updateSuffixMin(RootListNode<K, V> t) {
        if (this.comparator == null) {
            while (t != null) {
                if (t.next == null) {
                    t.suffixMin = t;
                } else {
                    RootListNode nextSuffixMin = t.next.suffixMin;
                    t.suffixMin = ((Comparable)t.root.cKey).compareTo(nextSuffixMin.root.cKey) <= 0 ? t : nextSuffixMin;
                }
                t = t.prev;
            }
        } else {
            while (t != null) {
                if (t.next == null) {
                    t.suffixMin = t;
                } else {
                    RootListNode nextSuffixMin = t.next.suffixMin;
                    t.suffixMin = this.comparator.compare(t.root.cKey, nextSuffixMin.root.cKey) <= 0 ? t : nextSuffixMin;
                }
                t = t.prev;
            }
        }
    }

    private void mergeInto(RootListNode<K, V> head, RootListNode<K, V> tail) {
        RootListNode resultTail;
        RootListNode resultHead;
        if (this.rootList.head == null) {
            this.rootList.head = head;
            this.rootList.tail = tail;
            return;
        }
        RootListNode resultTailPrev = null;
        RootListNode cur1 = this.rootList.head;
        RootListNode<K, V> cur2 = head;
        if (cur1.root.rank <= cur2.root.rank) {
            resultHead = cur1;
            resultTail = cur1;
            RootListNode cur1next = cur1.next;
            cur1.next = null;
            cur1 = cur1next;
            if (cur1next != null) {
                cur1next.prev = null;
            }
        } else {
            resultHead = cur2;
            resultTail = cur2;
            RootListNode cur2next = cur2.next;
            cur2.next = null;
            cur2 = cur2next;
            if (cur2next != null) {
                cur2next.prev = null;
            }
        }
        block10: while (true) {
            int rank2;
            int rank1;
            int resultRank = resultTail.root.rank;
            if (cur1 != null) {
                rank1 = cur1.root.rank;
            } else {
                if (cur2 == null || cur2.root.rank > resultRank) break;
                rank1 = Integer.MAX_VALUE;
            }
            if (cur2 != null) {
                rank2 = cur2.root.rank;
            } else {
                if (cur1 == null || cur1.root.rank > resultRank) break;
                rank2 = Integer.MAX_VALUE;
            }
            if (rank1 <= rank2) {
                switch (Integer.compare(rank1, resultRank)) {
                    case 0: {
                        resultTail.root = this.combine(cur1.root, resultTail.root);
                        resultTail.root.parent = resultTail;
                        RootListNode cur1next = cur1.next;
                        cur1.next = null;
                        if (cur1next != null) {
                            cur1next.prev = null;
                        }
                        cur1 = cur1next;
                        continue block10;
                    }
                    case -1: {
                        RootListNode cur1next = cur1.next;
                        cur1.next = resultTail;
                        resultTail.prev = cur1;
                        cur1.prev = resultTailPrev;
                        if (resultTailPrev != null) {
                            resultTailPrev.next = cur1;
                        } else {
                            resultHead = cur1;
                        }
                        resultTailPrev = cur1;
                        if (cur1next != null) {
                            cur1next.prev = null;
                        }
                        cur1 = cur1next;
                        continue block10;
                    }
                    case 1: {
                        resultTail.next = cur1;
                        cur1.prev = resultTail;
                        resultTailPrev = resultTail;
                        resultTail = cur1;
                        cur1 = cur1.next;
                        resultTail.next = null;
                        if (cur1 == null) continue block10;
                        cur1.prev = null;
                        continue block10;
                    }
                }
                continue;
            }
            switch (Integer.compare(rank2, resultRank)) {
                case 0: {
                    resultTail.root = this.combine(cur2.root, resultTail.root);
                    resultTail.root.parent = resultTail;
                    RootListNode cur2next = cur2.next;
                    cur2.next = null;
                    if (cur2next != null) {
                        cur2next.prev = null;
                    }
                    cur2 = cur2next;
                    break;
                }
                case -1: {
                    RootListNode cur2next = cur2.next;
                    cur2.next = resultTail;
                    resultTail.prev = cur2;
                    cur2.prev = resultTailPrev;
                    if (resultTailPrev != null) {
                        resultTailPrev.next = cur2;
                    } else {
                        resultHead = cur2;
                    }
                    resultTailPrev = cur2;
                    if (cur2next != null) {
                        cur2next.prev = null;
                    }
                    cur2 = cur2next;
                    break;
                }
                case 1: {
                    resultTail.next = cur2;
                    cur2.prev = resultTail;
                    resultTailPrev = resultTail;
                    resultTail = cur2;
                    cur2 = cur2.next;
                    resultTail.next = null;
                    if (cur2 == null) break;
                    cur2.prev = null;
                    break;
                }
            }
        }
        RootListNode updateSuffixFix = resultTail;
        if (cur1 != null) {
            cur1.prev = resultTail;
            resultTail.next = cur1;
            resultTail = this.rootList.tail;
        }
        if (cur2 != null) {
            cur2.prev = resultTail;
            resultTail.next = cur2;
            resultTail = tail;
        }
        this.updateSuffixMin(updateSuffixFix);
        this.rootList.head = resultHead;
        this.rootList.tail = resultTail;
    }

    private void delete(RootListNode<K, V> n) {
        RootListNode nPrev = n.prev;
        if (nPrev != null) {
            nPrev.next = n.next;
        } else {
            this.rootList.head = n.next;
        }
        if (n.next != null) {
            n.next.prev = nPrev;
        } else {
            this.rootList.tail = nPrev;
        }
        n.prev = null;
        n.next = null;
    }

    private void delete(SoftHandle<K, V> n) {
        if (n.tree == null) {
            throw new IllegalArgumentException("Invalid handle!");
        }
        TreeNode tree = n.tree;
        if (tree.cHead != n) {
            if (n.next != null) {
                n.next.prev = n.prev;
            }
            n.prev.next = n.next;
        } else {
            SoftHandle nNext = n.next;
            tree.cHead = nNext;
            if (nNext != null) {
                nNext.prev = null;
                nNext.tree = tree;
            } else {
                this.sift(tree);
                if (tree.cHead == null) {
                    if (tree.parent instanceof TreeNode) {
                        TreeNode p = (TreeNode)tree.parent;
                        if (p.left == tree) {
                            p.left = null;
                        } else {
                            p.right = null;
                        }
                    } else {
                        this.delete((RootListNode)tree.parent);
                    }
                }
            }
        }
        n.tree = null;
        n.prev = null;
        n.next = null;
        --this.size;
    }

    static class SoftHandle<K, V>
    implements AddressableHeap.Handle<K, V>,
    Serializable {
        private static final long serialVersionUID = 1L;
        BinaryTreeSoftAddressableHeap<K, V> heap;
        K key;
        V value;
        SoftHandle<K, V> next;
        SoftHandle<K, V> prev;
        TreeNode<K, V> tree;

        SoftHandle(BinaryTreeSoftAddressableHeap<K, V> heap, K key, V value) {
            this.heap = heap;
            this.key = key;
            this.value = value;
            this.next = null;
            this.prev = null;
            this.tree = null;
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
            throw new UnsupportedOperationException("Not supported in a soft heap");
        }

        @Override
        public void delete() {
            ((BinaryTreeSoftAddressableHeap)this.getOwner()).delete(this);
        }

        BinaryTreeSoftAddressableHeap<K, V> getOwner() {
            if (((BinaryTreeSoftAddressableHeap)this.heap).other != this.heap) {
                BinaryTreeSoftAddressableHeap root = this.heap;
                while (root != root.other) {
                    root = root.other;
                }
                BinaryTreeSoftAddressableHeap cur = this.heap;
                while (cur.other != root) {
                    BinaryTreeSoftAddressableHeap nextOne = cur.other;
                    cur.other = root;
                    cur = nextOne;
                }
                this.heap = root;
            }
            return this.heap;
        }
    }

    static class TreeNode<K, V>
    implements Serializable {
        private static final long serialVersionUID = 1L;
        int rank = 0;
        Object parent = null;
        TreeNode<K, V> left = null;
        TreeNode<K, V> right = null;
        SoftHandle<K, V> cHead;
        SoftHandle<K, V> cTail;
        long cSize;
        K cKey;

        TreeNode() {
            this(null);
        }

        TreeNode(SoftHandle<K, V> n) {
            this.cHead = n;
            this.cTail = n;
            if (n != null) {
                this.cSize = 1L;
                this.cKey = n.key;
                n.tree = this;
            } else {
                this.cSize = 0L;
                this.cKey = null;
            }
        }
    }

    static class RootListNode<K, V>
    implements Serializable {
        private static final long serialVersionUID = 1L;
        RootListNode<K, V> next;
        RootListNode<K, V> prev;
        RootListNode<K, V> suffixMin;
        TreeNode<K, V> root;

        RootListNode(TreeNode<K, V> tree) {
            this.root = tree;
            tree.parent = this;
            this.suffixMin = this;
            this.next = null;
            this.prev = null;
        }
    }

    static class RootList<K, V>
    implements Serializable {
        private static final long serialVersionUID = 1L;
        RootListNode<K, V> head = null;
        RootListNode<K, V> tail = null;

        RootList() {
        }
    }
}

