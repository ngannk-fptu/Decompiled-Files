/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.disk.ods;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Stack;

public class AATreeSet<T extends Comparable>
extends AbstractSet<T>
implements SortedSet<T> {
    private static final Node<?> TERMINAL = new TerminalNode();
    private Node<T> root = this.terminal();
    private int size;
    private boolean mutated;
    private Node<T> item = this.terminal();
    private Node<T> heir = this.terminal();
    private T removed;

    @Override
    public boolean add(T o) {
        try {
            this.root = this.insert(this.root, o);
            if (this.mutated) {
                ++this.size;
            }
            boolean bl = this.mutated;
            return bl;
        }
        finally {
            this.mutated = false;
        }
    }

    @Override
    public boolean remove(Object o) {
        try {
            this.root = this.remove(this.root, (Comparable)o);
            if (this.mutated) {
                --this.size;
            }
            boolean bl = this.mutated;
            return bl;
        }
        finally {
            this.heir = this.terminal();
            this.item = this.terminal();
            this.mutated = false;
            this.removed = null;
        }
    }

    public T removeAndReturn(Object o) {
        try {
            this.root = this.remove(this.root, (Comparable)o);
            if (this.mutated) {
                --this.size;
            }
            T t = this.removed;
            return t;
        }
        finally {
            this.heir = this.terminal();
            this.item = this.terminal();
            this.mutated = false;
            this.removed = null;
        }
    }

    @Override
    public void clear() {
        this.root = this.terminal();
        this.size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new TreeIterator();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.root == this.terminal();
    }

    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return new SubSet(this, fromElement, toElement);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return new SubSet(this, null, toElement);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return new SubSet(this, fromElement, null);
    }

    @Override
    public T first() {
        Node<T> leftMost = this.root;
        while (leftMost.getLeft() != this.terminal()) {
            leftMost = leftMost.getLeft();
        }
        return (T)((Comparable)leftMost.getPayload());
    }

    @Override
    public T last() {
        Node<T> rightMost = this.root;
        while (rightMost.getRight() != this.terminal()) {
            rightMost = rightMost.getRight();
        }
        return (T)((Comparable)rightMost.getPayload());
    }

    public T find(Object probe) {
        return (T)this.find(this.root, (Comparable)probe).getPayload();
    }

    private Node<T> terminal() {
        return TERMINAL;
    }

    protected final Node<T> getRoot() {
        return this.root;
    }

    private Node<T> find(Node<T> top, T probe) {
        if (top == this.terminal()) {
            return top;
        }
        int direction = top.compareTo(probe);
        if (direction > 0) {
            return this.find(top.getLeft(), probe);
        }
        if (direction < 0) {
            return this.find(top.getRight(), probe);
        }
        return top;
    }

    private Node<T> insert(Node<T> top, T data) {
        if (top == this.terminal()) {
            this.mutated = true;
            return this.createNode(data);
        }
        int direction = top.compareTo(data);
        if (direction > 0) {
            top.setLeft(this.insert(top.getLeft(), data));
        } else if (direction < 0) {
            top.setRight(this.insert(top.getRight(), data));
        } else {
            return top;
        }
        top = AATreeSet.skew(top);
        top = AATreeSet.split(top);
        return top;
    }

    private Node<T> createNode(T data) {
        if (data instanceof Node) {
            return (Node)data;
        }
        return new TreeNode<T>(data);
    }

    private Node<T> remove(Node<T> top, T data) {
        if (top != this.terminal()) {
            int direction = top.compareTo(data);
            this.heir = top;
            if (direction > 0) {
                top.setLeft(this.remove(top.getLeft(), data));
            } else {
                this.item = top;
                top.setRight(this.remove(top.getRight(), data));
            }
            if (top == this.heir) {
                if (this.item != this.terminal() && this.item.compareTo(data) == 0) {
                    this.mutated = true;
                    this.item.swapPayload(top);
                    this.removed = (Comparable)top.getPayload();
                    top = top.getRight();
                }
            } else if (top.getLeft().getLevel() < top.getLevel() - 1 || top.getRight().getLevel() < top.getLevel() - 1) {
                if (top.getRight().getLevel() > top.decrementLevel()) {
                    top.getRight().setLevel(top.getLevel());
                }
                top = AATreeSet.skew(top);
                top.setRight(AATreeSet.skew(top.getRight()));
                top.getRight().setRight(AATreeSet.skew(top.getRight().getRight()));
                top = AATreeSet.split(top);
                top.setRight(AATreeSet.split(top.getRight()));
            }
        }
        return top;
    }

    private static <T> Node<T> skew(Node<T> top) {
        if (top.getLeft().getLevel() == top.getLevel() && top.getLevel() != 0) {
            Node<T> save = top.getLeft();
            top.setLeft(save.getRight());
            save.setRight(top);
            top = save;
        }
        return top;
    }

    private static <T> Node<T> split(Node<T> top) {
        if (top.getRight().getRight().getLevel() == top.getLevel() && top.getLevel() != 0) {
            Node<T> save = top.getRight();
            top.setRight(save.getLeft());
            save.setLeft(top);
            top = save;
            top.incrementLevel();
        }
        return top;
    }

    private class SubTreeIterator
    extends TreeIterator {
        SubTreeIterator(T start) {
            super(AATreeSet.this, start);
        }
    }

    private class TreeIterator
    implements Iterator<T> {
        private final Stack<Node<T>> path;
        private Node<T> next;

        TreeIterator() {
            this.path = new Stack();
            this.path.push(AATreeSet.this.terminal());
            Node leftMost = AATreeSet.this.root;
            while (leftMost.getLeft() != AATreeSet.this.terminal()) {
                this.path.push(leftMost);
                leftMost = leftMost.getLeft();
            }
            this.next = leftMost;
        }

        TreeIterator(T start) {
            block4: {
                this.path = new Stack();
                this.path.push(AATreeSet.this.terminal());
                Node current = AATreeSet.this.root;
                while (true) {
                    int direction;
                    if ((direction = current.compareTo(start)) > 0) {
                        if (current.getLeft() == AATreeSet.this.terminal()) {
                            this.next = current;
                            break block4;
                        }
                        this.path.push(current);
                        current = current.getLeft();
                        continue;
                    }
                    if (direction >= 0) break;
                    if (current.getRight() == AATreeSet.this.terminal()) {
                        this.next = this.path.pop();
                        break block4;
                    }
                    current = current.getRight();
                }
                this.next = current;
            }
        }

        @Override
        public boolean hasNext() {
            return this.next != AATreeSet.this.terminal();
        }

        @Override
        public T next() {
            Node current = this.next;
            this.advance();
            return (Comparable)current.getPayload();
        }

        private void advance() {
            Node successor = this.next.getRight();
            if (successor != AATreeSet.this.terminal()) {
                while (successor.getLeft() != AATreeSet.this.terminal()) {
                    this.path.push(successor);
                    successor = successor.getLeft();
                }
                this.next = successor;
            } else {
                this.next = this.path.pop();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class SubSet
    extends AbstractSet<T>
    implements SortedSet<T> {
        private final T start;
        private final T end;
        final /* synthetic */ AATreeSet this$0;

        SubSet(T start, T end) {
            this.this$0 = var1_1;
            this.start = start;
            this.end = end;
        }

        @Override
        public boolean add(T o) {
            if (this.inRange(o)) {
                return this.this$0.add(o);
            }
            throw new IllegalArgumentException();
        }

        @Override
        public boolean remove(Object o) {
            if (this.inRange((Comparable)o)) {
                return this.remove(o);
            }
            return false;
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<T> iterator() {
            if (this.end == null) {
                return new SubTreeIterator(this.this$0, this.start, this.end);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            return !this.iterator().hasNext();
        }

        @Override
        public Comparator<? super T> comparator() {
            return null;
        }

        @Override
        public SortedSet<T> subSet(T fromElement, T toElement) {
            if (this.inRangeInclusive(fromElement) && this.inRangeInclusive(toElement)) {
                return new SubSet(this.this$0, fromElement, toElement);
            }
            throw new IllegalArgumentException();
        }

        @Override
        public SortedSet<T> headSet(T toElement) {
            if (this.inRangeInclusive(toElement)) {
                return new SubSet(this.this$0, this.start, toElement);
            }
            throw new IllegalArgumentException();
        }

        @Override
        public SortedSet<T> tailSet(T fromElement) {
            if (this.inRangeInclusive(fromElement)) {
                return new SubSet(this.this$0, fromElement, this.end);
            }
            throw new IllegalArgumentException();
        }

        @Override
        public T first() {
            if (this.start == null) {
                return this.this$0.first();
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public T last() {
            if (this.end == null) {
                return this.this$0.last();
            }
            throw new UnsupportedOperationException();
        }

        private boolean inRange(T value) {
            return !(this.start != null && this.start.compareTo(value) > 0 || this.end != null && this.end.compareTo(value) <= 0);
        }

        private boolean inRangeInclusive(T value) {
            return !(this.start != null && this.start.compareTo(value) > 0 || this.end != null && this.end.compareTo(value) < 0);
        }
    }

    private static final class TerminalNode<E>
    extends AbstractTreeNode<E> {
        TerminalNode() {
            super(0);
            super.setLeft(this);
            super.setRight(this);
        }

        @Override
        public int compareTo(E data) {
            return 0;
        }

        @Override
        public void setLeft(Node<E> right) {
            if (right != TERMINAL) {
                throw new AssertionError();
            }
        }

        @Override
        public void setRight(Node<E> left) {
            if (left != TERMINAL) {
                throw new AssertionError();
            }
        }

        @Override
        public void setLevel(int value) {
            throw new AssertionError();
        }

        @Override
        public int decrementLevel() {
            throw new AssertionError();
        }

        @Override
        public int incrementLevel() {
            throw new AssertionError();
        }

        @Override
        public void swapPayload(Node<E> payload) {
            throw new AssertionError();
        }

        @Override
        public E getPayload() {
            return null;
        }
    }

    private static final class TreeNode<E extends Comparable>
    extends AbstractTreeNode<E> {
        private E payload;

        public TreeNode(E payload) {
            this.payload = payload;
        }

        @Override
        public int compareTo(E data) {
            return this.payload.compareTo(data);
        }

        @Override
        public void swapPayload(Node<E> node) {
            if (!(node instanceof TreeNode)) {
                throw new IllegalArgumentException();
            }
            TreeNode treeNode = (TreeNode)node;
            E temp = treeNode.payload;
            treeNode.payload = this.payload;
            this.payload = temp;
        }

        @Override
        public E getPayload() {
            return this.payload;
        }
    }

    public static abstract class AbstractTreeNode<E>
    implements Node<E> {
        private Node<E> left = TERMINAL;
        private Node<E> right = TERMINAL;
        private int level;

        public AbstractTreeNode() {
            this(1);
        }

        private AbstractTreeNode(int level) {
            this.level = level;
        }

        @Override
        public void setLeft(Node<E> node) {
            this.left = node;
        }

        @Override
        public void setRight(Node<E> node) {
            this.right = node;
        }

        @Override
        public Node<E> getLeft() {
            return this.left;
        }

        @Override
        public Node<E> getRight() {
            return this.right;
        }

        @Override
        public int getLevel() {
            return this.level;
        }

        @Override
        public void setLevel(int value) {
            this.level = value;
        }

        @Override
        public int decrementLevel() {
            return --this.level;
        }

        @Override
        public int incrementLevel() {
            return ++this.level;
        }
    }

    public static interface Node<E> {
        public int compareTo(E var1);

        public void setLeft(Node<E> var1);

        public void setRight(Node<E> var1);

        public Node<E> getLeft();

        public Node<E> getRight();

        public int getLevel();

        public void setLevel(int var1);

        public int decrementLevel();

        public int incrementLevel();

        public void swapPayload(Node<E> var1);

        public E getPayload();
    }
}

