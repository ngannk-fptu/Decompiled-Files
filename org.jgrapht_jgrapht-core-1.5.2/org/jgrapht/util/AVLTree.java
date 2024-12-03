/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AVLTree<T>
implements Iterable<T> {
    private TreeNode<T> virtualRoot = new TreeNode<Object>(null);
    private int modCount = 0;

    public AVLTree() {
    }

    private AVLTree(TreeNode<T> root) {
        this.makeRoot(root);
    }

    public TreeNode<T> addMax(T value) {
        TreeNode<T> newMax = new TreeNode<T>(value);
        this.addMaxNode(newMax);
        return newMax;
    }

    public void addMaxNode(TreeNode<T> newMax) {
        this.registerModification();
        if (this.isEmpty()) {
            this.virtualRoot.left = newMax;
            newMax.parent = this.virtualRoot;
        } else {
            TreeNode<T> max = this.getMax();
            max.setRightChild(newMax);
            this.balance(max);
        }
    }

    public TreeNode<T> addMin(T value) {
        TreeNode<T> newMin = new TreeNode<T>(value);
        this.addMinNode(newMin);
        return newMin;
    }

    public void addMinNode(TreeNode<T> newMin) {
        this.registerModification();
        if (this.isEmpty()) {
            this.virtualRoot.left = newMin;
            newMin.parent = this.virtualRoot;
        } else {
            TreeNode<T> min = this.getMin();
            min.setLeftChild(newMin);
            this.balance(min);
        }
    }

    public AVLTree<T> splitAfter(TreeNode<T> node) {
        this.registerModification();
        TreeNode parent = node.parent;
        boolean nextMove = node.isLeftChild();
        TreeNode<T> left = node.left;
        TreeNode right = node.right;
        node.parent.substituteChild(node, null);
        node.reset();
        if (left != null) {
            left.parent = null;
        }
        if (right != null) {
            right.parent = null;
        }
        if (left == null) {
            left = node;
        } else {
            TreeNode<T> t = left;
            while (t.right != null) {
                t = t.right;
            }
            t.setRightChild(node);
            while (t != left) {
                TreeNode<T> p = t.parent;
                p.substituteChild(t, this.balanceNode(t));
                t = p;
            }
            left = this.balanceNode(left);
        }
        return this.split(left, right, parent, nextMove);
    }

    public AVLTree<T> splitBefore(TreeNode<T> node) {
        this.registerModification();
        TreeNode<T> predecessor = this.predecessor(node);
        if (predecessor == null) {
            AVLTree<T> tree = new AVLTree<T>();
            this.swap(tree);
            return tree;
        }
        return this.splitAfter(predecessor);
    }

    public void mergeAfter(AVLTree<T> tree) {
        this.registerModification();
        if (tree.isEmpty()) {
            return;
        }
        if (tree.getSize() == 1) {
            this.addMaxNode(tree.removeMin());
            return;
        }
        TreeNode<T> junctionNode = tree.removeMin();
        TreeNode<T> treeRoot = tree.getRoot();
        tree.clear();
        this.makeRoot(this.merge(junctionNode, this.getRoot(), treeRoot));
    }

    public void mergeBefore(AVLTree<T> tree) {
        this.registerModification();
        tree.mergeAfter(this);
        this.swap(tree);
    }

    public TreeNode<T> removeMin() {
        this.registerModification();
        if (this.isEmpty()) {
            return null;
        }
        TreeNode<T> min = this.getMin();
        if (min.parent == this.virtualRoot) {
            this.makeRoot(min.right);
        } else {
            min.parent.setLeftChild(min.right);
        }
        this.balance(min.parent);
        return min;
    }

    public TreeNode<T> removeMax() {
        this.registerModification();
        if (this.isEmpty()) {
            return null;
        }
        TreeNode<T> max = this.getMax();
        if (max.parent == this.virtualRoot) {
            this.makeRoot(max.left);
        } else {
            max.parent.setRightChild(max.left);
        }
        this.balance(max.parent);
        return max;
    }

    public TreeNode<T> getRoot() {
        return this.virtualRoot.left;
    }

    public TreeNode<T> successor(TreeNode<T> node) {
        return node.successor;
    }

    public TreeNode<T> predecessor(TreeNode<T> node) {
        return node.predecessor;
    }

    public TreeNode<T> getMin() {
        return this.getRoot() == null ? null : this.getRoot().getSubtreeMin();
    }

    public TreeNode<T> getMax() {
        return this.getRoot() == null ? null : this.getRoot().getSubtreeMax();
    }

    public boolean isEmpty() {
        return this.getRoot() == null;
    }

    public void clear() {
        this.registerModification();
        this.virtualRoot.left = null;
    }

    public int getSize() {
        return this.virtualRoot.left == null ? 0 : this.virtualRoot.left.subtreeSize;
    }

    private void makeRoot(TreeNode<T> node) {
        this.virtualRoot.left = node;
        if (node != null) {
            node.subtreeMax.successor = null;
            node.subtreeMin.predecessor = null;
            node.parent = this.virtualRoot;
        }
    }

    private AVLTree<T> split(TreeNode<T> left, TreeNode<T> right, TreeNode<T> p, boolean leftMove) {
        while (p != this.virtualRoot) {
            boolean nextMove = p.isLeftChild();
            TreeNode nextP = p.parent;
            p.parent.substituteChild(p, null);
            p.parent = null;
            if (leftMove) {
                right = this.merge(p, right, p.right);
            } else {
                left = this.merge(p, p.left, left);
            }
            p = nextP;
            leftMove = nextMove;
        }
        this.makeRoot(left);
        return new AVLTree<T>(right);
    }

    private TreeNode<T> merge(TreeNode<T> junctionNode, TreeNode<T> left, TreeNode<T> right) {
        if (left == null && right == null) {
            junctionNode.reset();
            return junctionNode;
        }
        if (left == null) {
            right.setLeftChild(this.merge(junctionNode, left, right.left));
            return this.balanceNode(right);
        }
        if (right == null) {
            left.setRightChild(this.merge(junctionNode, left.right, right));
            return this.balanceNode(left);
        }
        if (left.getHeight() > right.getHeight() + 1) {
            left.setRightChild(this.merge(junctionNode, left.right, right));
            return this.balanceNode(left);
        }
        if (right.getHeight() > left.getHeight() + 1) {
            right.setLeftChild(this.merge(junctionNode, left, right.left));
            return this.balanceNode(right);
        }
        junctionNode.setLeftChild(left);
        junctionNode.setRightChild(right);
        return this.balanceNode(junctionNode);
    }

    private void swap(AVLTree<T> tree) {
        TreeNode t = this.virtualRoot.left;
        this.makeRoot(tree.virtualRoot.left);
        tree.makeRoot(t);
    }

    private TreeNode<T> rotateRight(TreeNode<T> node) {
        TreeNode<T> left = node.left;
        left.parent = null;
        node.setLeftChild(left.right);
        left.setRightChild(node);
        node.updateHeightAndSubtreeSize();
        left.updateHeightAndSubtreeSize();
        return left;
    }

    private TreeNode<T> rotateLeft(TreeNode<T> node) {
        TreeNode<T> right = node.right;
        right.parent = null;
        node.setRightChild(right.left);
        right.setLeftChild(node);
        node.updateHeightAndSubtreeSize();
        right.updateHeightAndSubtreeSize();
        return right;
    }

    private void balance(TreeNode<T> node) {
        this.balance(node, this.virtualRoot);
    }

    private void balance(TreeNode<T> node, TreeNode<T> stop) {
        if (node == stop) {
            return;
        }
        TreeNode<T> p = node.parent;
        if (p == this.virtualRoot) {
            this.makeRoot(this.balanceNode(node));
        } else {
            p.substituteChild(node, this.balanceNode(node));
        }
        this.balance(p, stop);
    }

    private TreeNode<T> balanceNode(TreeNode<T> node) {
        node.updateHeightAndSubtreeSize();
        if (node.isLeftDoubleHeavy()) {
            if (node.left.isRightHeavy()) {
                node.setLeftChild(this.rotateLeft(node.left));
            }
            this.rotateRight(node);
            return node.parent;
        }
        if (node.isRightDoubleHeavy()) {
            if (node.right.isLeftHeavy()) {
                node.setRightChild(this.rotateRight(node.right));
            }
            this.rotateLeft(node);
            return node.parent;
        }
        return node;
    }

    private void registerModification() {
        ++this.modCount;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        Iterator<TreeNode<T>> i = this.nodeIterator();
        while (i.hasNext()) {
            TreeNode<T> node = i.next();
            builder.append(node.toString()).append("\n");
        }
        return builder.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new TreeValuesIterator();
    }

    public Iterator<TreeNode<T>> nodeIterator() {
        return new TreeNodeIterator();
    }

    public static class TreeNode<T> {
        T value;
        TreeNode<T> parent;
        TreeNode<T> left;
        TreeNode<T> right;
        TreeNode<T> successor;
        TreeNode<T> predecessor;
        TreeNode<T> subtreeMin;
        TreeNode<T> subtreeMax;
        int height;
        int subtreeSize;

        TreeNode(T value) {
            this.value = value;
            this.reset();
        }

        public T getValue() {
            return this.value;
        }

        public TreeNode<T> getRoot() {
            TreeNode<T> current = this;
            while (current.parent != null) {
                current = current.parent;
            }
            return current.left;
        }

        public TreeNode<T> getSubtreeMin() {
            return this.subtreeMin;
        }

        public TreeNode<T> getSubtreeMax() {
            return this.subtreeMax;
        }

        public TreeNode<T> getTreeMin() {
            return this.getRoot().getSubtreeMin();
        }

        public TreeNode<T> getTreeMax() {
            return this.getRoot().getSubtreeMax();
        }

        public TreeNode<T> getParent() {
            return this.parent;
        }

        public TreeNode<T> getLeft() {
            return this.left;
        }

        public TreeNode<T> getRight() {
            return this.right;
        }

        int getHeight() {
            return this.height;
        }

        int getSubtreeSize() {
            return this.subtreeSize;
        }

        void reset() {
            this.height = 1;
            this.subtreeSize = 1;
            this.subtreeMin = this;
            this.subtreeMax = this;
            this.successor = null;
            this.predecessor = null;
            this.parent = null;
            this.right = null;
            this.left = null;
        }

        int getRightHeight() {
            return this.right == null ? 0 : this.right.height;
        }

        int getLeftHeight() {
            return this.left == null ? 0 : this.left.height;
        }

        int getLeftSubtreeSize() {
            return this.left == null ? 0 : this.left.subtreeSize;
        }

        int getRightSubtreeSize() {
            return this.right == null ? 0 : this.right.subtreeSize;
        }

        void updateHeightAndSubtreeSize() {
            this.height = Math.max(this.getLeftHeight(), this.getRightHeight()) + 1;
            this.subtreeSize = this.getLeftSubtreeSize() + this.getRightSubtreeSize() + 1;
        }

        boolean isLeftDoubleHeavy() {
            return this.getLeftHeight() > this.getRightHeight() + 1;
        }

        boolean isRightDoubleHeavy() {
            return this.getRightHeight() > this.getLeftHeight() + 1;
        }

        boolean isLeftHeavy() {
            return this.getLeftHeight() > this.getRightHeight();
        }

        boolean isRightHeavy() {
            return this.getRightHeight() > this.getLeftHeight();
        }

        boolean isLeftChild() {
            return this == this.parent.left;
        }

        boolean isRightChild() {
            return this == this.parent.right;
        }

        public TreeNode<T> getSuccessor() {
            return this.successor;
        }

        public TreeNode<T> getPredecessor() {
            return this.predecessor;
        }

        void setSuccessor(TreeNode<T> node) {
            this.successor = node;
            if (node != null) {
                node.predecessor = this;
            }
        }

        void setPredecessor(TreeNode<T> node) {
            this.predecessor = node;
            if (node != null) {
                node.successor = this;
            }
        }

        void setLeftChild(TreeNode<T> node) {
            this.left = node;
            if (node != null) {
                node.parent = this;
                this.setPredecessor(node.subtreeMax);
                this.subtreeMin = node.subtreeMin;
            } else {
                this.subtreeMin = this;
                this.predecessor = null;
            }
        }

        void setRightChild(TreeNode<T> node) {
            this.right = node;
            if (node != null) {
                node.parent = this;
                this.setSuccessor(node.subtreeMin);
                this.subtreeMax = node.subtreeMax;
            } else {
                this.successor = null;
                this.subtreeMax = this;
            }
        }

        void substituteChild(TreeNode<T> prevChild, TreeNode<T> newChild) {
            assert (this.left == prevChild || this.right == prevChild);
            assert (this.left != prevChild || this.right != prevChild);
            if (this.left == prevChild) {
                this.setLeftChild(newChild);
            } else {
                this.setRightChild(newChild);
            }
        }

        public String toString() {
            return String.format("{%s}: [parent = %s, left = %s, right = %s], [subtreeMin = %s, subtreeMax = %s], [predecessor = %s, successor = %s], [height = %d, subtreeSize = %d]", this.value, this.parent == null ? "null" : this.parent.value, this.left == null ? "null" : this.left.value, this.right == null ? "null" : this.right.value, this.subtreeMin == null ? "null" : this.subtreeMin.value, this.subtreeMax == null ? "null" : this.subtreeMax.value, this.predecessor == null ? "null" : this.predecessor.value, this.successor == null ? "null" : this.successor.value, this.height, this.subtreeSize);
        }
    }

    private class TreeValuesIterator
    implements Iterator<T> {
        private TreeNodeIterator iterator;

        public TreeValuesIterator() {
            this.iterator = new TreeNodeIterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public T next() {
            return ((TreeNode)this.iterator.next()).getValue();
        }
    }

    private class TreeNodeIterator
    implements Iterator<TreeNode<T>> {
        private TreeNode<T> nextNode;
        private final int expectedModCount;

        public TreeNodeIterator() {
            this.nextNode = AVLTree.this.getMin();
            this.expectedModCount = AVLTree.this.modCount;
        }

        @Override
        public boolean hasNext() {
            this.checkForComodification();
            return this.nextNode != null;
        }

        @Override
        public TreeNode<T> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            TreeNode result = this.nextNode;
            this.nextNode = AVLTree.this.successor(this.nextNode);
            return result;
        }

        private void checkForComodification() {
            if (this.expectedModCount != AVLTree.this.modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}

