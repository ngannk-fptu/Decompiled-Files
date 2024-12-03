/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.tree;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.Heap;
import org.jheaps.MergeableHeap;
import org.jheaps.annotations.ConstantTime;

public class BinaryTreeSoftHeap<K>
implements Heap<K>,
MergeableHeap<K>,
Serializable {
    private static final long serialVersionUID = 1L;
    private final Comparator<? super K> comparator;
    private static final long[] TARGET_SIZE = new long[]{1L, 2L, 3L, 5L, 8L, 12L, 18L, 27L, 41L, 62L, 93L, 140L, 210L, 315L, 473L, 710L, 1065L, 1598L, 2397L, 3596L, 5394L, 8091L, 12137L, 18206L, 27309L, 40964L, 61446L, 92169L, 138254L, 207381L, 311072L, 466608L, 699912L, 1049868L, 1574802L, 2362203L, 3543305L, 5314958L, 7972437L, 11958656L, 17937984L, 26906976L, 40360464L, 60540696L, 90811044L, 136216566L, 204324849L, 306487274L, 459730911L, 689596367L, 1034394551L, 1551591827L, 2327387741L, 3491081612L, 5236622418L, 7854933627L, 11782400441L, 17673600662L, 26510400993L, 39765601490L, 59648402235L, 89472603353L, 134208905030L};
    private final int rankLimit;
    final RootList<K> rootList;
    private long size;

    public BinaryTreeSoftHeap(double errorRate) {
        this(errorRate, null);
    }

    public BinaryTreeSoftHeap(double errorRate, Comparator<? super K> comparator) {
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
        this.rootList.head = null;
        this.rootList.tail = null;
        this.size = 0L;
    }

    @Override
    public void meld(MergeableHeap<K> other) {
        BinaryTreeSoftHeap h = (BinaryTreeSoftHeap)other;
        if (this.comparator != null ? h.comparator == null || !h.comparator.equals(this.comparator) : h.comparator != null) {
            throw new IllegalArgumentException("Cannot meld heaps using different comparators!");
        }
        if (this.rankLimit != h.rankLimit) {
            throw new IllegalArgumentException("Cannot meld heaps with different error rates!");
        }
        this.mergeInto(h.rootList.head, h.rootList.tail);
        this.size += h.size;
        h.size = 0L;
        h.rootList.head = null;
        h.rootList.tail = null;
    }

    @Override
    public void insert(K key) {
        SoftHandle<K> n = new SoftHandle<K>(key);
        TreeNode<K> treeNode = new TreeNode<K>(n);
        RootListNode<K> rootListNode = new RootListNode<K>(treeNode);
        this.mergeInto(rootListNode, rootListNode);
        ++this.size;
    }

    @Override
    public K findMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        return this.rootList.head.suffixMin.root.cHead.key;
    }

    @Override
    public K deleteMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        RootListNode minRootListNode = this.rootList.head.suffixMin;
        TreeNode root = minRootListNode.root;
        SoftHandle result = root.cHead;
        root.cHead = result.next;
        --root.cSize;
        if (root.cSize <= this.targetSize(root.rank) / 2L) {
            if (root.left != null || root.right != null) {
                this.sift(root);
                this.updateSuffixMin(minRootListNode);
            } else if (root.cSize == 0L) {
                RootListNode minRootPrevListNode = minRootListNode.prev;
                if (minRootPrevListNode != null) {
                    minRootPrevListNode.next = minRootListNode.next;
                } else {
                    this.rootList.head = minRootListNode.next;
                }
                if (minRootListNode.next != null) {
                    minRootListNode.next.prev = minRootPrevListNode;
                } else {
                    this.rootList.tail = minRootPrevListNode;
                }
                minRootListNode.prev = null;
                minRootListNode.next = null;
                this.updateSuffixMin(minRootPrevListNode);
            }
        }
        result.next = null;
        --this.size;
        return result.key;
    }

    private long targetSize(int rank) {
        return rank <= this.rankLimit ? 1L : TARGET_SIZE[rank - this.rankLimit];
    }

    private void sift(TreeNode<K> x) {
        ArrayDeque stack = new ArrayDeque();
        stack.push(x);
        while (!stack.isEmpty()) {
            x = (TreeNode)stack.peek();
            TreeNode xLeft = x.left;
            TreeNode xRight = x.right;
            if (xLeft == null && xRight == null || x.cSize >= this.targetSize(x.rank)) {
                stack.pop();
                continue;
            }
            if (xLeft == null || xRight != null && (this.comparator == null && ((Comparable)xLeft.cKey).compareTo(xRight.cKey) > 0 || this.comparator != null && this.comparator.compare(xLeft.cKey, xRight.cKey) > 0)) {
                x.left = xRight;
                x.right = xLeft;
                xLeft = x.left;
            }
            xLeft.cTail.next = x.cHead;
            x.cHead = xLeft.cHead;
            if (x.cTail == null) {
                x.cTail = xLeft.cTail;
            }
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

    private TreeNode<K> combine(TreeNode<K> x, TreeNode<K> y) {
        TreeNode z = new TreeNode();
        z.left = x;
        z.right = y;
        z.rank = x.rank + 1;
        this.sift(z);
        return z;
    }

    private void updateSuffixMin(RootListNode<K> t) {
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

    private void mergeInto(RootListNode<K> head, RootListNode<K> tail) {
        RootListNode resultTail;
        RootListNode resultHead;
        if (this.rootList.head == null) {
            this.rootList.head = head;
            this.rootList.tail = tail;
            return;
        }
        RootListNode resultTailPrev = null;
        RootListNode cur1 = this.rootList.head;
        RootListNode<K> cur2 = head;
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

    static class SoftHandle<K>
    implements Serializable {
        private static final long serialVersionUID = 1L;
        K key;
        SoftHandle<K> next;

        SoftHandle(K key) {
            this.key = key;
            this.next = null;
        }
    }

    static class TreeNode<K>
    implements Serializable {
        private static final long serialVersionUID = 1L;
        int rank = 0;
        TreeNode<K> left = null;
        TreeNode<K> right = null;
        SoftHandle<K> cHead;
        SoftHandle<K> cTail;
        long cSize;
        K cKey;

        TreeNode() {
            this(null);
        }

        TreeNode(SoftHandle<K> n) {
            this.cHead = n;
            this.cTail = n;
            if (n != null) {
                this.cSize = 1L;
                this.cKey = n.key;
            } else {
                this.cSize = 0L;
                this.cKey = null;
            }
        }
    }

    static class RootListNode<K>
    implements Serializable {
        private static final long serialVersionUID = 1L;
        RootListNode<K> next;
        RootListNode<K> prev;
        RootListNode<K> suffixMin;
        TreeNode<K> root;

        RootListNode(TreeNode<K> tree) {
            this.root = tree;
            this.suffixMin = this;
            this.next = null;
            this.prev = null;
        }
    }

    static class RootList<K>
    implements Serializable {
        private static final long serialVersionUID = 1L;
        RootListNode<K> head = null;
        RootListNode<K> tail = null;

        RootList() {
        }
    }
}

