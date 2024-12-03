/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.util.AbstractSequentialList;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.jgrapht.alg.util.Pair;

public class DoublyLinkedList<E>
extends AbstractSequentialList<E>
implements Deque<E> {
    private ListNodeImpl<E> head = null;
    private int size;

    private ListNodeImpl<E> tail() {
        return this.head.prev;
    }

    @Override
    public boolean isEmpty() {
        return this.head == null;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        if (!this.isEmpty()) {
            ListNodeImpl next;
            ListNodeImpl<Object> node = this.head;
            do {
                next = node.next;
                boolean removed = this.removeListNode(node);
                assert (removed);
            } while ((node = next) != this.head);
            this.head = null;
            assert (this.size == 0);
        }
    }

    private void addListNode(ListNodeImpl<E> node) {
        if (node.list != null) {
            String list = node.list == this ? "this" : "other";
            throw new IllegalArgumentException("Node <" + node + "> already contained in " + list + " list");
        }
        node.list = this;
        ++this.size;
        ++this.modCount;
    }

    private void moveAllListNodes(DoublyLinkedList<E> list) {
        DoublyLinkedList<E> doublyLinkedList = list;
        Objects.requireNonNull(doublyLinkedList);
        ListNodeIteratorImpl it = doublyLinkedList.new ListNodeIteratorImpl(0);
        while (it.hasNext()) {
            ListNode node = it.nextNode();
            assert (((ListNodeImpl)node).list == list);
            ((ListNodeImpl)node).list = this;
        }
        this.size += list.size;
        list.size = 0;
        ++this.modCount;
        ++list.modCount;
    }

    private boolean removeListNode(ListNodeImpl<E> node) {
        if (node.list == this) {
            node.list = null;
            node.next = null;
            node.prev = null;
            --this.size;
            ++this.modCount;
            return true;
        }
        return false;
    }

    private void link(ListNodeImpl<E> predecessor, ListNodeImpl<E> successor) {
        predecessor.next = successor;
        successor.prev = predecessor;
    }

    private void linkBefore(ListNodeImpl<E> node, ListNodeImpl<E> successor) {
        this.addListNode(node);
        this.link(successor.prev, node);
        this.link(node, successor);
    }

    private void linkLast(ListNodeImpl<E> node) {
        if (this.isEmpty()) {
            this.addListNode(node);
            this.link(node, node);
            this.head = node;
        } else {
            this.linkBefore(node, this.head);
        }
    }

    private void linkListIntoThisBefore(int index, DoublyLinkedList<E> list) {
        int previousSize = this.size;
        this.moveAllListNodes(list);
        if (previousSize == 0) {
            this.head = list.head;
        } else {
            ListNodeImpl<E> refNode = index == previousSize ? this.head : this.getNodeAt(index);
            ListNodeImpl<E> listTail = list.tail();
            this.link(refNode.prev, list.head);
            this.link(listTail, refNode);
            if (index == 0) {
                this.head = list.head;
            }
        }
        list.head = null;
    }

    private boolean unlink(ListNodeImpl<E> node) {
        ListNodeImpl prev = node.prev;
        ListNodeImpl next = node.next;
        if (this.removeListNode(node)) {
            if (this.size == 0) {
                this.head = null;
            } else {
                this.link(prev, next);
                if (this.head == node) {
                    this.head = next;
                }
            }
            return true;
        }
        return false;
    }

    public void addNode(int index, ListNode<E> node) {
        ListNodeImpl nodeImpl = (ListNodeImpl)node;
        if (index == this.size) {
            this.linkLast(nodeImpl);
        } else {
            ListNodeImpl<E> successor = index == 0 ? this.head : this.getNodeAt(index);
            this.linkBefore(nodeImpl, successor);
            if (this.head == successor) {
                this.head = nodeImpl;
            }
        }
    }

    public void addNodeFirst(ListNode<E> node) {
        this.addNode(0, node);
    }

    public void addNodeLast(ListNode<E> node) {
        this.addNode(this.size, node);
    }

    public void addNodeBefore(ListNode<E> node, ListNode<E> successor) {
        ListNodeImpl successorImpl = (ListNodeImpl)successor;
        ListNodeImpl nodeImpl = (ListNodeImpl)node;
        if (successorImpl.list != this) {
            throw new IllegalArgumentException("Node <" + successorImpl + "> not in this list");
        }
        this.linkBefore(nodeImpl, successorImpl);
        if (this.head == successorImpl) {
            this.head = nodeImpl;
        }
    }

    public ListNode<E> getFirstNode() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.head;
    }

    public ListNode<E> getLastNode() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.tail();
    }

    public ListNode<E> getNode(int index) {
        return this.getNodeAt(index);
    }

    private ListNodeImpl<E> getNodeAt(int index) {
        ListNodeImpl<Object> node;
        if (index < 0 || this.size <= index) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        if (index < this.size / 2) {
            node = this.head;
            for (int i = 0; i < index; ++i) {
                node = node.next;
            }
        } else {
            node = this.tail();
            for (int i = this.size - 1; index < i; --i) {
                node = node.prev;
            }
        }
        return node;
    }

    public int indexOfNode(ListNode<E> node) {
        if (!this.containsNode(node)) {
            return -1;
        }
        ListNodeImpl<Object> current = this.head;
        for (int i = 0; i < this.size; ++i) {
            if (current == node) {
                return i;
            }
            current = current.next;
        }
        throw new IllegalStateException("Node contained in list not found: " + node);
    }

    public boolean containsNode(ListNode<E> node) {
        return ((ListNodeImpl)node).list == this;
    }

    public boolean removeNode(ListNode<E> node) {
        return this.unlink((ListNodeImpl)node);
    }

    public ListNode<E> nodeOf(Object element) {
        return this.searchNode(() -> this.head, n -> n.next, element).getFirst();
    }

    public ListNode<E> lastNodeOf(Object element) {
        return this.searchNode(this::tail, n -> n.prev, element).getFirst();
    }

    private Pair<ListNodeImpl<E>, Integer> searchNode(Supplier<ListNodeImpl<E>> first, UnaryOperator<ListNodeImpl<E>> next, Object element) {
        if (!this.isEmpty()) {
            ListNodeImpl firstNode;
            int index = 0;
            ListNodeImpl node = firstNode = first.get();
            do {
                if (Objects.equals(node.value, element)) {
                    return Pair.of(node, index);
                }
                ++index;
            } while ((node = (ListNodeImpl)next.apply(node)) != firstNode);
        }
        return Pair.of(null, -1);
    }

    public ListNode<E> addElementFirst(E element) {
        ListNodeImpl<E> node = new ListNodeImpl<E>(element);
        this.addNode(0, node);
        return node;
    }

    public ListNode<E> addElementLast(E element) {
        ListNodeImpl<E> node = new ListNodeImpl<E>(element);
        this.addNode(this.size, node);
        return node;
    }

    public ListNode<E> addElementBeforeNode(ListNode<E> successor, E element) {
        ListNodeImpl<E> node = new ListNodeImpl<E>(element);
        this.addNodeBefore(node, successor);
        return node;
    }

    @Override
    public void add(int index, E element) {
        if (index == this.size) {
            this.addElementLast(element);
        } else {
            this.addElementBeforeNode(this.getNode(index), element);
        }
    }

    @Override
    public E get(int index) {
        return (E)this.getNodeAt((int)index).value;
    }

    @Override
    public E remove(int index) {
        ListNode<E> node = this.getNode(index);
        this.removeNode(node);
        return node.getValue();
    }

    @Override
    public void addFirst(E e) {
        this.addElementFirst(e);
    }

    @Override
    public void addLast(E e) {
        this.addElementLast(e);
    }

    @Override
    public boolean offerFirst(E e) {
        this.addElementFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        this.addElementLast(e);
        return true;
    }

    @Override
    public E removeFirst() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        ListNodeImpl<E> node = this.head;
        this.removeNode(node);
        return (E)node.getValue();
    }

    @Override
    public E removeLast() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        ListNodeImpl<E> node = this.tail();
        this.removeNode(node);
        return (E)node.getValue();
    }

    @Override
    public E pollFirst() {
        if (this.isEmpty()) {
            return null;
        }
        ListNodeImpl<E> node = this.head;
        this.removeNode(node);
        return (E)node.getValue();
    }

    @Override
    public E pollLast() {
        if (this.isEmpty()) {
            return null;
        }
        ListNodeImpl<E> node = this.tail();
        this.removeNode(node);
        return (E)node.getValue();
    }

    @Override
    public E getFirst() {
        return this.getFirstNode().getValue();
    }

    @Override
    public E getLast() {
        return this.getLastNode().getValue();
    }

    @Override
    public E peekFirst() {
        return this.isEmpty() ? null : (E)this.getFirst();
    }

    @Override
    public E peekLast() {
        return this.isEmpty() ? null : (E)this.getLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        ListNode<E> node = this.nodeOf(o);
        if (node != null) {
            this.removeNode(node);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        ListNode<E> node = this.lastNodeOf(o);
        if (node != null) {
            this.removeNode(node);
            return true;
        }
        return false;
    }

    @Override
    public boolean offer(E e) {
        return this.offerLast(e);
    }

    @Override
    public E remove() {
        return this.removeFirst();
    }

    @Override
    public E poll() {
        return this.pollFirst();
    }

    @Override
    public E element() {
        return this.getFirst();
    }

    @Override
    public E peek() {
        return this.peekFirst();
    }

    @Override
    public void push(E e) {
        this.addFirst(e);
    }

    @Override
    public E pop() {
        return this.removeFirst();
    }

    public void invert() {
        ListNodeImpl next;
        if (this.size < 2) {
            return;
        }
        ListNodeImpl<E> newHead = this.tail();
        ListNodeImpl<Object> current = this.head;
        do {
            next = current.next;
            current.next = current.prev;
            current.prev = next;
        } while ((current = next) != this.head);
        this.head = newHead;
        ++this.modCount;
    }

    public void moveFrom(int index, DoublyLinkedList<E> movedList) {
        this.linkListIntoThisBefore(index, movedList);
    }

    public void append(DoublyLinkedList<E> movedList) {
        this.moveFrom(this.size, movedList);
    }

    public void prepend(DoublyLinkedList<E> movedList) {
        this.moveFrom(0, movedList);
    }

    public NodeIterator<E> circularIterator(E firstElement) {
        ListNodeImpl startNode = (ListNodeImpl)this.nodeOf(firstElement);
        if (startNode == null) {
            throw new NoSuchElementException();
        }
        return new ListNodeIteratorImpl(0, startNode);
    }

    public NodeIterator<E> reverseCircularIterator(E firstElement) {
        ListNodeImpl startNode = (ListNodeImpl)this.nodeOf(firstElement);
        if (startNode == null) {
            throw new NoSuchElementException();
        }
        return DoublyLinkedList.reverseIterator(new ListNodeIteratorImpl(this.size, startNode.next));
    }

    @Override
    public NodeIterator<E> descendingIterator() {
        return DoublyLinkedList.reverseIterator(this.listIterator(this.size));
    }

    @Override
    public NodeIterator<E> iterator() {
        return this.listIterator();
    }

    @Override
    public ListNodeIterator<E> listIterator() {
        return this.listIterator(0);
    }

    @Override
    public ListNodeIterator<E> listIterator(int index) {
        return new ListNodeIteratorImpl(index);
    }

    public ListNodeIterator<E> listIterator(E element) {
        Pair<ListNodeImpl<E>, Integer> startPair = this.searchNode(() -> this.head, n -> n.next, element);
        ListNodeImpl<E> startNode = startPair.getFirst();
        int startIndex = startPair.getSecond();
        if (startNode == null) {
            throw new NoSuchElementException();
        }
        return new ListNodeIteratorImpl(startIndex, startNode);
    }

    private static <E> NodeIterator<E> reverseIterator(final ListNodeIterator<E> listIterator) {
        return new NodeIterator<E>(){

            @Override
            public boolean hasNext() {
                return listIterator.hasPrevious();
            }

            @Override
            public ListNode<E> nextNode() {
                return listIterator.previousNode();
            }

            @Override
            public void remove() {
                listIterator.remove();
            }
        };
    }

    static /* synthetic */ int access$000(DoublyLinkedList x0) {
        return x0.modCount;
    }

    private static class ListNodeImpl<V>
    implements ListNode<V> {
        private final V value;
        private DoublyLinkedList<V> list = null;
        private ListNodeImpl<V> next = null;
        private ListNodeImpl<V> prev = null;

        ListNodeImpl(V value) {
            this.value = value;
        }

        public String toString() {
            if (this.list == null) {
                return " - " + this.value + " - ";
            }
            return this.prev.value + " -> " + this.value + " -> " + this.next.value;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public ListNodeImpl<V> getNext() {
            return this.next;
        }

        @Override
        public ListNodeImpl<V> getPrev() {
            return this.prev;
        }
    }

    private class ListNodeIteratorImpl
    implements ListNodeIterator<E> {
        private int nextIndex;
        private ListNodeImpl<E> next;
        private ListNodeImpl<E> last = null;
        private int expectedModCount = DoublyLinkedList.access$000(DoublyLinkedList.this);

        private ListNodeIteratorImpl(int startIndex) {
            this.nextIndex = startIndex;
            this.next = startIndex == DoublyLinkedList.this.size ? (DoublyLinkedList.this.isEmpty() ? null : DoublyLinkedList.this.head) : DoublyLinkedList.this.getNodeAt(startIndex);
        }

        private ListNodeIteratorImpl(int startIndex, ListNodeImpl<E> startNode) {
            this.nextIndex = startIndex;
            this.next = startNode;
        }

        @Override
        public boolean hasNext() {
            return this.nextIndex < DoublyLinkedList.this.size;
        }

        @Override
        public boolean hasPrevious() {
            return this.nextIndex > 0;
        }

        @Override
        public int nextIndex() {
            return this.nextIndex;
        }

        @Override
        public int previousIndex() {
            return this.nextIndex - 1;
        }

        @Override
        public ListNodeImpl<E> nextNode() {
            this.checkForComodification();
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.last = this.next;
            this.next = this.next.next;
            ++this.nextIndex;
            return this.last;
        }

        @Override
        public ListNode<E> previousNode() {
            this.checkForComodification();
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.next = this.next.prev;
            this.last = this.next;
            --this.nextIndex;
            return this.last;
        }

        @Override
        public void add(E e) {
            this.checkForComodification();
            if (this.nextIndex == DoublyLinkedList.this.size) {
                DoublyLinkedList.this.addElementLast(e);
                if (DoublyLinkedList.this.size == 1) {
                    this.next = DoublyLinkedList.this.head;
                }
            } else {
                DoublyLinkedList.this.addElementBeforeNode(this.next, e);
            }
            this.last = null;
            ++this.nextIndex;
            ++this.expectedModCount;
        }

        @Override
        public void set(E e) {
            if (this.last == null) {
                throw new IllegalStateException();
            }
            this.checkForComodification();
            ListNodeImpl nextNode = this.last.next;
            boolean wasLast = this.last == DoublyLinkedList.this.tail();
            DoublyLinkedList.this.removeNode(this.last);
            this.last = wasLast ? (ListNodeImpl)DoublyLinkedList.this.addElementLast(e) : (ListNodeImpl)DoublyLinkedList.this.addElementBeforeNode(nextNode, e);
            this.expectedModCount += 2;
        }

        @Override
        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException();
            }
            this.checkForComodification();
            ListNodeImpl lastsNext = this.last.next;
            DoublyLinkedList.this.removeNode(this.last);
            if (this.next == this.last) {
                this.next = lastsNext;
            } else {
                --this.nextIndex;
            }
            this.last = null;
            ++this.expectedModCount;
        }

        private void checkForComodification() {
            if (this.expectedModCount != DoublyLinkedList.this.modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    public static interface ListNode<V> {
        public V getValue();

        public ListNode<V> getNext();

        public ListNode<V> getPrev();
    }

    public static interface ListNodeIterator<E>
    extends ListIterator<E>,
    NodeIterator<E> {
        @Override
        default public E next() {
            return this.nextNode().getValue();
        }

        @Override
        default public E previous() {
            return this.previousNode().getValue();
        }

        public ListNode<E> previousNode();
    }

    public static interface NodeIterator<E>
    extends Iterator<E> {
        @Override
        default public E next() {
            return this.nextNode().getValue();
        }

        public ListNode<E> nextNode();
    }
}

