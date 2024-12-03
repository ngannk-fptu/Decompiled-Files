/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.list;

import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections.OrderedIterator;

public class TreeList
extends AbstractList {
    private AVLNode root;
    private int size;

    public TreeList() {
    }

    public TreeList(Collection coll) {
        this.addAll(coll);
    }

    public Object get(int index) {
        this.checkInterval(index, 0, this.size() - 1);
        return this.root.get(index).getValue();
    }

    public int size() {
        return this.size;
    }

    public Iterator iterator() {
        return this.listIterator(0);
    }

    public ListIterator listIterator() {
        return this.listIterator(0);
    }

    public ListIterator listIterator(int fromIndex) {
        this.checkInterval(fromIndex, 0, this.size());
        return new TreeListIterator(this, fromIndex);
    }

    public int indexOf(Object object) {
        if (this.root == null) {
            return -1;
        }
        return this.root.indexOf(object, this.root.relativePosition);
    }

    public boolean contains(Object object) {
        return this.indexOf(object) >= 0;
    }

    public Object[] toArray() {
        Object[] array = new Object[this.size()];
        if (this.root != null) {
            this.root.toArray(array, this.root.relativePosition);
        }
        return array;
    }

    public void add(int index, Object obj) {
        ++this.modCount;
        this.checkInterval(index, 0, this.size());
        this.root = this.root == null ? new AVLNode(index, obj, null, null) : this.root.insert(index, obj);
        ++this.size;
    }

    public Object set(int index, Object obj) {
        this.checkInterval(index, 0, this.size() - 1);
        AVLNode node = this.root.get(index);
        Object result = node.value;
        node.setValue(obj);
        return result;
    }

    public Object remove(int index) {
        ++this.modCount;
        this.checkInterval(index, 0, this.size() - 1);
        Object result = this.get(index);
        this.root = this.root.remove(index);
        --this.size;
        return result;
    }

    public void clear() {
        ++this.modCount;
        this.root = null;
        this.size = 0;
    }

    private void checkInterval(int index, int startIndex, int endIndex) {
        if (index < startIndex || index > endIndex) {
            throw new IndexOutOfBoundsException("Invalid index:" + index + ", size=" + this.size());
        }
    }

    static class TreeListIterator
    implements ListIterator,
    OrderedIterator {
        protected final TreeList parent;
        protected AVLNode next;
        protected int nextIndex;
        protected AVLNode current;
        protected int currentIndex;
        protected int expectedModCount;

        protected TreeListIterator(TreeList parent, int fromIndex) throws IndexOutOfBoundsException {
            this.parent = parent;
            this.expectedModCount = parent.modCount;
            this.next = parent.root == null ? null : parent.root.get(fromIndex);
            this.nextIndex = fromIndex;
            this.currentIndex = -1;
        }

        protected void checkModCount() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        public boolean hasNext() {
            return this.nextIndex < this.parent.size();
        }

        public Object next() {
            this.checkModCount();
            if (!this.hasNext()) {
                throw new NoSuchElementException("No element at index " + this.nextIndex + ".");
            }
            if (this.next == null) {
                this.next = this.parent.root.get(this.nextIndex);
            }
            Object value = this.next.getValue();
            this.current = this.next;
            this.currentIndex = this.nextIndex++;
            this.next = this.next.next();
            return value;
        }

        public boolean hasPrevious() {
            return this.nextIndex > 0;
        }

        public Object previous() {
            this.checkModCount();
            if (!this.hasPrevious()) {
                throw new NoSuchElementException("Already at start of list.");
            }
            this.next = this.next == null ? this.parent.root.get(this.nextIndex - 1) : this.next.previous();
            Object value = this.next.getValue();
            this.current = this.next;
            this.currentIndex = --this.nextIndex;
            return value;
        }

        public int nextIndex() {
            return this.nextIndex;
        }

        public int previousIndex() {
            return this.nextIndex() - 1;
        }

        public void remove() {
            this.checkModCount();
            if (this.currentIndex == -1) {
                throw new IllegalStateException();
            }
            this.parent.remove(this.currentIndex);
            if (this.nextIndex != this.currentIndex) {
                --this.nextIndex;
            }
            this.next = null;
            this.current = null;
            this.currentIndex = -1;
            ++this.expectedModCount;
        }

        public void set(Object obj) {
            this.checkModCount();
            if (this.current == null) {
                throw new IllegalStateException();
            }
            this.current.setValue(obj);
        }

        public void add(Object obj) {
            this.checkModCount();
            this.parent.add(this.nextIndex, obj);
            this.current = null;
            this.currentIndex = -1;
            ++this.nextIndex;
            ++this.expectedModCount;
        }
    }

    static class AVLNode {
        private AVLNode left;
        private boolean leftIsPrevious;
        private AVLNode right;
        private boolean rightIsNext;
        private int height;
        private int relativePosition;
        private Object value;

        private AVLNode(int relativePosition, Object obj, AVLNode rightFollower, AVLNode leftFollower) {
            this.relativePosition = relativePosition;
            this.value = obj;
            this.rightIsNext = true;
            this.leftIsPrevious = true;
            this.right = rightFollower;
            this.left = leftFollower;
        }

        Object getValue() {
            return this.value;
        }

        void setValue(Object obj) {
            this.value = obj;
        }

        AVLNode get(int index) {
            AVLNode nextNode;
            int indexRelativeToMe = index - this.relativePosition;
            if (indexRelativeToMe == 0) {
                return this;
            }
            AVLNode aVLNode = nextNode = indexRelativeToMe < 0 ? this.getLeftSubTree() : this.getRightSubTree();
            if (nextNode == null) {
                return null;
            }
            return nextNode.get(indexRelativeToMe);
        }

        int indexOf(Object object, int index) {
            int result;
            if (this.getLeftSubTree() != null && (result = this.left.indexOf(object, index + this.left.relativePosition)) != -1) {
                return result;
            }
            if (this.value == null ? this.value == object : this.value.equals(object)) {
                return index;
            }
            if (this.getRightSubTree() != null) {
                return this.right.indexOf(object, index + this.right.relativePosition);
            }
            return -1;
        }

        void toArray(Object[] array, int index) {
            array[index] = this.value;
            if (this.getLeftSubTree() != null) {
                this.left.toArray(array, index + this.left.relativePosition);
            }
            if (this.getRightSubTree() != null) {
                this.right.toArray(array, index + this.right.relativePosition);
            }
        }

        AVLNode next() {
            if (this.rightIsNext || this.right == null) {
                return this.right;
            }
            return this.right.min();
        }

        AVLNode previous() {
            if (this.leftIsPrevious || this.left == null) {
                return this.left;
            }
            return this.left.max();
        }

        AVLNode insert(int index, Object obj) {
            int indexRelativeToMe = index - this.relativePosition;
            if (indexRelativeToMe <= 0) {
                return this.insertOnLeft(indexRelativeToMe, obj);
            }
            return this.insertOnRight(indexRelativeToMe, obj);
        }

        private AVLNode insertOnLeft(int indexRelativeToMe, Object obj) {
            AVLNode ret = this;
            if (this.getLeftSubTree() == null) {
                this.setLeft(new AVLNode(-1, obj, this, this.left), null);
            } else {
                this.setLeft(this.left.insert(indexRelativeToMe, obj), null);
            }
            if (this.relativePosition >= 0) {
                ++this.relativePosition;
            }
            ret = this.balance();
            this.recalcHeight();
            return ret;
        }

        private AVLNode insertOnRight(int indexRelativeToMe, Object obj) {
            AVLNode ret = this;
            if (this.getRightSubTree() == null) {
                this.setRight(new AVLNode(1, obj, this.right, this), null);
            } else {
                this.setRight(this.right.insert(indexRelativeToMe, obj), null);
            }
            if (this.relativePosition < 0) {
                --this.relativePosition;
            }
            ret = this.balance();
            this.recalcHeight();
            return ret;
        }

        private AVLNode getLeftSubTree() {
            return this.leftIsPrevious ? null : this.left;
        }

        private AVLNode getRightSubTree() {
            return this.rightIsNext ? null : this.right;
        }

        private AVLNode max() {
            return this.getRightSubTree() == null ? this : this.right.max();
        }

        private AVLNode min() {
            return this.getLeftSubTree() == null ? this : this.left.min();
        }

        AVLNode remove(int index) {
            int indexRelativeToMe = index - this.relativePosition;
            if (indexRelativeToMe == 0) {
                return this.removeSelf();
            }
            if (indexRelativeToMe > 0) {
                this.setRight(this.right.remove(indexRelativeToMe), this.right.right);
                if (this.relativePosition < 0) {
                    ++this.relativePosition;
                }
            } else {
                this.setLeft(this.left.remove(indexRelativeToMe), this.left.left);
                if (this.relativePosition > 0) {
                    --this.relativePosition;
                }
            }
            this.recalcHeight();
            return this.balance();
        }

        private AVLNode removeMax() {
            if (this.getRightSubTree() == null) {
                return this.removeSelf();
            }
            this.setRight(this.right.removeMax(), this.right.right);
            if (this.relativePosition < 0) {
                ++this.relativePosition;
            }
            this.recalcHeight();
            return this.balance();
        }

        private AVLNode removeMin() {
            if (this.getLeftSubTree() == null) {
                return this.removeSelf();
            }
            this.setLeft(this.left.removeMin(), this.left.left);
            if (this.relativePosition > 0) {
                --this.relativePosition;
            }
            this.recalcHeight();
            return this.balance();
        }

        private AVLNode removeSelf() {
            if (this.getRightSubTree() == null && this.getLeftSubTree() == null) {
                return null;
            }
            if (this.getRightSubTree() == null) {
                if (this.relativePosition > 0) {
                    this.left.relativePosition = this.left.relativePosition + (this.relativePosition + (this.relativePosition > 0 ? 0 : 1));
                }
                this.left.max().setRight(null, this.right);
                return this.left;
            }
            if (this.getLeftSubTree() == null) {
                this.right.relativePosition = this.right.relativePosition + (this.relativePosition - (this.relativePosition < 0 ? 0 : 1));
                this.right.min().setLeft(null, this.left);
                return this.right;
            }
            if (this.heightRightMinusLeft() > 0) {
                AVLNode rightMin = this.right.min();
                this.value = rightMin.value;
                if (this.leftIsPrevious) {
                    this.left = rightMin.left;
                }
                this.right = this.right.removeMin();
                if (this.relativePosition < 0) {
                    ++this.relativePosition;
                }
            } else {
                AVLNode leftMax = this.left.max();
                this.value = leftMax.value;
                if (this.rightIsNext) {
                    this.right = leftMax.right;
                }
                AVLNode leftPrevious = this.left.left;
                this.left = this.left.removeMax();
                if (this.left == null) {
                    this.left = leftPrevious;
                    this.leftIsPrevious = true;
                }
                if (this.relativePosition > 0) {
                    --this.relativePosition;
                }
            }
            this.recalcHeight();
            return this;
        }

        private AVLNode balance() {
            switch (this.heightRightMinusLeft()) {
                case -1: 
                case 0: 
                case 1: {
                    return this;
                }
                case -2: {
                    if (this.left.heightRightMinusLeft() > 0) {
                        this.setLeft(this.left.rotateLeft(), null);
                    }
                    return this.rotateRight();
                }
                case 2: {
                    if (this.right.heightRightMinusLeft() < 0) {
                        this.setRight(this.right.rotateRight(), null);
                    }
                    return this.rotateLeft();
                }
            }
            throw new RuntimeException("tree inconsistent!");
        }

        private int getOffset(AVLNode node) {
            if (node == null) {
                return 0;
            }
            return node.relativePosition;
        }

        private int setOffset(AVLNode node, int newOffest) {
            if (node == null) {
                return 0;
            }
            int oldOffset = this.getOffset(node);
            node.relativePosition = newOffest;
            return oldOffset;
        }

        private void recalcHeight() {
            this.height = Math.max(this.getLeftSubTree() == null ? -1 : this.getLeftSubTree().height, this.getRightSubTree() == null ? -1 : this.getRightSubTree().height) + 1;
        }

        private int getHeight(AVLNode node) {
            return node == null ? -1 : node.height;
        }

        private int heightRightMinusLeft() {
            return this.getHeight(this.getRightSubTree()) - this.getHeight(this.getLeftSubTree());
        }

        private AVLNode rotateLeft() {
            AVLNode newTop = this.right;
            AVLNode movedNode = this.getRightSubTree().getLeftSubTree();
            int newTopPosition = this.relativePosition + this.getOffset(newTop);
            int myNewPosition = -newTop.relativePosition;
            int movedPosition = this.getOffset(newTop) + this.getOffset(movedNode);
            this.setRight(movedNode, newTop);
            newTop.setLeft(this, null);
            this.setOffset(newTop, newTopPosition);
            this.setOffset(this, myNewPosition);
            this.setOffset(movedNode, movedPosition);
            return newTop;
        }

        private AVLNode rotateRight() {
            AVLNode newTop = this.left;
            AVLNode movedNode = this.getLeftSubTree().getRightSubTree();
            int newTopPosition = this.relativePosition + this.getOffset(newTop);
            int myNewPosition = -newTop.relativePosition;
            int movedPosition = this.getOffset(newTop) + this.getOffset(movedNode);
            this.setLeft(movedNode, newTop);
            newTop.setRight(this, null);
            this.setOffset(newTop, newTopPosition);
            this.setOffset(this, myNewPosition);
            this.setOffset(movedNode, movedPosition);
            return newTop;
        }

        private void setLeft(AVLNode node, AVLNode previous) {
            this.leftIsPrevious = node == null;
            this.left = this.leftIsPrevious ? previous : node;
            this.recalcHeight();
        }

        private void setRight(AVLNode node, AVLNode next) {
            this.rightIsNext = node == null;
            this.right = this.rightIsNext ? next : node;
            this.recalcHeight();
        }

        public String toString() {
            return "AVLNode(" + this.relativePosition + "," + (this.left != null) + "," + this.value + "," + (this.getRightSubTree() != null) + ", faedelung " + this.rightIsNext + " )";
        }
    }
}

