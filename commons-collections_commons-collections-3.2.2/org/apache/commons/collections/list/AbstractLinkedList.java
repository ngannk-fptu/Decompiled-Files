/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections.OrderedIterator;

public abstract class AbstractLinkedList
implements List {
    protected transient Node header;
    protected transient int size;
    protected transient int modCount;

    protected AbstractLinkedList() {
    }

    protected AbstractLinkedList(Collection coll) {
        this.init();
        this.addAll(coll);
    }

    protected void init() {
        this.header = this.createHeaderNode();
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public Object get(int index) {
        Node node = this.getNode(index, false);
        return node.getValue();
    }

    public Iterator iterator() {
        return this.listIterator();
    }

    public ListIterator listIterator() {
        return new LinkedListIterator(this, 0);
    }

    public ListIterator listIterator(int fromIndex) {
        return new LinkedListIterator(this, fromIndex);
    }

    public int indexOf(Object value) {
        int i = 0;
        Node node = this.header.next;
        while (node != this.header) {
            if (this.isEqualValue(node.getValue(), value)) {
                return i;
            }
            ++i;
            node = node.next;
        }
        return -1;
    }

    public int lastIndexOf(Object value) {
        int i = this.size - 1;
        Node node = this.header.previous;
        while (node != this.header) {
            if (this.isEqualValue(node.getValue(), value)) {
                return i;
            }
            --i;
            node = node.previous;
        }
        return -1;
    }

    public boolean contains(Object value) {
        return this.indexOf(value) != -1;
    }

    public boolean containsAll(Collection coll) {
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            if (this.contains(it.next())) continue;
            return false;
        }
        return true;
    }

    public Object[] toArray() {
        return this.toArray(new Object[this.size]);
    }

    public Object[] toArray(Object[] array) {
        if (array.length < this.size) {
            Class<?> componentType = array.getClass().getComponentType();
            array = (Object[])Array.newInstance(componentType, this.size);
        }
        int i = 0;
        Node node = this.header.next;
        while (node != this.header) {
            array[i] = node.getValue();
            node = node.next;
            ++i;
        }
        if (array.length > this.size) {
            array[this.size] = null;
        }
        return array;
    }

    public List subList(int fromIndexInclusive, int toIndexExclusive) {
        return new LinkedSubList(this, fromIndexInclusive, toIndexExclusive);
    }

    public boolean add(Object value) {
        this.addLast(value);
        return true;
    }

    public void add(int index, Object value) {
        Node node = this.getNode(index, true);
        this.addNodeBefore(node, value);
    }

    public boolean addAll(Collection coll) {
        return this.addAll(this.size, coll);
    }

    public boolean addAll(int index, Collection coll) {
        Node node = this.getNode(index, true);
        Iterator itr = coll.iterator();
        while (itr.hasNext()) {
            Object value = itr.next();
            this.addNodeBefore(node, value);
        }
        return true;
    }

    public Object remove(int index) {
        Node node = this.getNode(index, false);
        Object oldValue = node.getValue();
        this.removeNode(node);
        return oldValue;
    }

    public boolean remove(Object value) {
        Node node = this.header.next;
        while (node != this.header) {
            if (this.isEqualValue(node.getValue(), value)) {
                this.removeNode(node);
                return true;
            }
            node = node.next;
        }
        return false;
    }

    public boolean removeAll(Collection coll) {
        boolean modified = false;
        Iterator it = this.iterator();
        while (it.hasNext()) {
            if (!coll.contains(it.next())) continue;
            it.remove();
            modified = true;
        }
        return modified;
    }

    public boolean retainAll(Collection coll) {
        boolean modified = false;
        Iterator it = this.iterator();
        while (it.hasNext()) {
            if (coll.contains(it.next())) continue;
            it.remove();
            modified = true;
        }
        return modified;
    }

    public Object set(int index, Object value) {
        Node node = this.getNode(index, false);
        Object oldValue = node.getValue();
        this.updateNode(node, value);
        return oldValue;
    }

    public void clear() {
        this.removeAllNodes();
    }

    public Object getFirst() {
        Node node = this.header.next;
        if (node == this.header) {
            throw new NoSuchElementException();
        }
        return node.getValue();
    }

    public Object getLast() {
        Node node = this.header.previous;
        if (node == this.header) {
            throw new NoSuchElementException();
        }
        return node.getValue();
    }

    public boolean addFirst(Object o) {
        this.addNodeAfter(this.header, o);
        return true;
    }

    public boolean addLast(Object o) {
        this.addNodeBefore(this.header, o);
        return true;
    }

    public Object removeFirst() {
        Node node = this.header.next;
        if (node == this.header) {
            throw new NoSuchElementException();
        }
        Object oldValue = node.getValue();
        this.removeNode(node);
        return oldValue;
    }

    public Object removeLast() {
        Node node = this.header.previous;
        if (node == this.header) {
            throw new NoSuchElementException();
        }
        Object oldValue = node.getValue();
        this.removeNode(node);
        return oldValue;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof List)) {
            return false;
        }
        List other = (List)obj;
        if (other.size() != this.size()) {
            return false;
        }
        ListIterator it1 = this.listIterator();
        ListIterator it2 = other.listIterator();
        while (it1.hasNext() && it2.hasNext()) {
            Object o1 = it1.next();
            Object o2 = it2.next();
            if (o1 != null ? o1.equals(o2) : o2 == null) continue;
            return false;
        }
        return !it1.hasNext() && !it2.hasNext();
    }

    public int hashCode() {
        int hashCode = 1;
        Iterator it = this.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
        }
        return hashCode;
    }

    public String toString() {
        if (this.size() == 0) {
            return "[]";
        }
        StringBuffer buf = new StringBuffer(16 * this.size());
        buf.append("[");
        Iterator it = this.iterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Object value = it.next();
            buf.append((Object)(value == this ? "(this Collection)" : value));
            hasNext = it.hasNext();
            if (!hasNext) continue;
            buf.append(", ");
        }
        buf.append("]");
        return buf.toString();
    }

    protected boolean isEqualValue(Object value1, Object value2) {
        return value1 == value2 || value1 != null && value1.equals(value2);
    }

    protected void updateNode(Node node, Object value) {
        node.setValue(value);
    }

    protected Node createHeaderNode() {
        return new Node();
    }

    protected Node createNode(Object value) {
        return new Node(value);
    }

    protected void addNodeBefore(Node node, Object value) {
        Node newNode = this.createNode(value);
        this.addNode(newNode, node);
    }

    protected void addNodeAfter(Node node, Object value) {
        Node newNode = this.createNode(value);
        this.addNode(newNode, node.next);
    }

    protected void addNode(Node nodeToInsert, Node insertBeforeNode) {
        nodeToInsert.next = insertBeforeNode;
        nodeToInsert.previous = insertBeforeNode.previous;
        insertBeforeNode.previous.next = nodeToInsert;
        insertBeforeNode.previous = nodeToInsert;
        ++this.size;
        ++this.modCount;
    }

    protected void removeNode(Node node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
        --this.size;
        ++this.modCount;
    }

    protected void removeAllNodes() {
        this.header.next = this.header;
        this.header.previous = this.header;
        this.size = 0;
        ++this.modCount;
    }

    protected Node getNode(int index, boolean endMarkerAllowed) throws IndexOutOfBoundsException {
        Node node;
        if (index < 0) {
            throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") less than zero.");
        }
        if (!endMarkerAllowed && index == this.size) {
            throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") is the size of the list.");
        }
        if (index > this.size) {
            throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") greater than the size of the " + "list (" + this.size + ").");
        }
        if (index < this.size / 2) {
            node = this.header.next;
            for (int currentIndex = 0; currentIndex < index; ++currentIndex) {
                node = node.next;
            }
        } else {
            node = this.header;
            for (int currentIndex = this.size; currentIndex > index; --currentIndex) {
                node = node.previous;
            }
        }
        return node;
    }

    protected Iterator createSubListIterator(LinkedSubList subList) {
        return this.createSubListListIterator(subList, 0);
    }

    protected ListIterator createSubListListIterator(LinkedSubList subList, int fromIndex) {
        return new LinkedSubListIterator(subList, fromIndex);
    }

    protected void doWriteObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeInt(this.size());
        Iterator itr = this.iterator();
        while (itr.hasNext()) {
            outputStream.writeObject(itr.next());
        }
    }

    protected void doReadObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        this.init();
        int size = inputStream.readInt();
        for (int i = 0; i < size; ++i) {
            this.add(inputStream.readObject());
        }
    }

    protected static class LinkedSubList
    extends AbstractList {
        AbstractLinkedList parent;
        int offset;
        int size;
        int expectedModCount;

        protected LinkedSubList(AbstractLinkedList parent, int fromIndex, int toIndex) {
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
            }
            if (toIndex > parent.size()) {
                throw new IndexOutOfBoundsException("toIndex = " + toIndex);
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
            }
            this.parent = parent;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.expectedModCount = parent.modCount;
        }

        public int size() {
            this.checkModCount();
            return this.size;
        }

        public Object get(int index) {
            this.rangeCheck(index, this.size);
            this.checkModCount();
            return this.parent.get(index + this.offset);
        }

        public void add(int index, Object obj) {
            this.rangeCheck(index, this.size + 1);
            this.checkModCount();
            this.parent.add(index + this.offset, obj);
            this.expectedModCount = this.parent.modCount;
            ++this.size;
            ++this.modCount;
        }

        public Object remove(int index) {
            this.rangeCheck(index, this.size);
            this.checkModCount();
            Object result = this.parent.remove(index + this.offset);
            this.expectedModCount = this.parent.modCount;
            --this.size;
            ++this.modCount;
            return result;
        }

        public boolean addAll(Collection coll) {
            return this.addAll(this.size, coll);
        }

        public boolean addAll(int index, Collection coll) {
            this.rangeCheck(index, this.size + 1);
            int cSize = coll.size();
            if (cSize == 0) {
                return false;
            }
            this.checkModCount();
            this.parent.addAll(this.offset + index, coll);
            this.expectedModCount = this.parent.modCount;
            this.size += cSize;
            ++this.modCount;
            return true;
        }

        public Object set(int index, Object obj) {
            this.rangeCheck(index, this.size);
            this.checkModCount();
            return this.parent.set(index + this.offset, obj);
        }

        public void clear() {
            this.checkModCount();
            Iterator it = this.iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }

        public Iterator iterator() {
            this.checkModCount();
            return this.parent.createSubListIterator(this);
        }

        public ListIterator listIterator(int index) {
            this.rangeCheck(index, this.size + 1);
            this.checkModCount();
            return this.parent.createSubListListIterator(this, index);
        }

        public List subList(int fromIndexInclusive, int toIndexExclusive) {
            return new LinkedSubList(this.parent, fromIndexInclusive + this.offset, toIndexExclusive + this.offset);
        }

        protected void rangeCheck(int index, int beyond) {
            if (index < 0 || index >= beyond) {
                throw new IndexOutOfBoundsException("Index '" + index + "' out of bounds for size '" + this.size + "'");
            }
        }

        protected void checkModCount() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    protected static class LinkedSubListIterator
    extends LinkedListIterator {
        protected final LinkedSubList sub;

        protected LinkedSubListIterator(LinkedSubList sub, int startIndex) {
            super(sub.parent, startIndex + sub.offset);
            this.sub = sub;
        }

        public boolean hasNext() {
            return this.nextIndex() < this.sub.size;
        }

        public boolean hasPrevious() {
            return this.previousIndex() >= 0;
        }

        public int nextIndex() {
            return super.nextIndex() - this.sub.offset;
        }

        public void add(Object obj) {
            super.add(obj);
            this.sub.expectedModCount = this.parent.modCount;
            ++this.sub.size;
        }

        public void remove() {
            super.remove();
            this.sub.expectedModCount = this.parent.modCount;
            --this.sub.size;
        }
    }

    protected static class LinkedListIterator
    implements ListIterator,
    OrderedIterator {
        protected final AbstractLinkedList parent;
        protected Node next;
        protected int nextIndex;
        protected Node current;
        protected int expectedModCount;

        protected LinkedListIterator(AbstractLinkedList parent, int fromIndex) throws IndexOutOfBoundsException {
            this.parent = parent;
            this.expectedModCount = parent.modCount;
            this.next = parent.getNode(fromIndex, true);
            this.nextIndex = fromIndex;
        }

        protected void checkModCount() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        protected Node getLastNodeReturned() throws IllegalStateException {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            return this.current;
        }

        public boolean hasNext() {
            return this.next != this.parent.header;
        }

        public Object next() {
            this.checkModCount();
            if (!this.hasNext()) {
                throw new NoSuchElementException("No element at index " + this.nextIndex + ".");
            }
            Object value = this.next.getValue();
            this.current = this.next;
            this.next = this.next.next;
            ++this.nextIndex;
            return value;
        }

        public boolean hasPrevious() {
            return this.next.previous != this.parent.header;
        }

        public Object previous() {
            this.checkModCount();
            if (!this.hasPrevious()) {
                throw new NoSuchElementException("Already at start of list.");
            }
            this.next = this.next.previous;
            Object value = this.next.getValue();
            this.current = this.next;
            --this.nextIndex;
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
            if (this.current == this.next) {
                this.next = this.next.next;
                this.parent.removeNode(this.getLastNodeReturned());
            } else {
                this.parent.removeNode(this.getLastNodeReturned());
                --this.nextIndex;
            }
            this.current = null;
            ++this.expectedModCount;
        }

        public void set(Object obj) {
            this.checkModCount();
            this.getLastNodeReturned().setValue(obj);
        }

        public void add(Object obj) {
            this.checkModCount();
            this.parent.addNodeBefore(this.next, obj);
            this.current = null;
            ++this.nextIndex;
            ++this.expectedModCount;
        }
    }

    protected static class Node {
        protected Node previous;
        protected Node next;
        protected Object value;

        protected Node() {
            this.previous = this;
            this.next = this;
        }

        protected Node(Object value) {
            this.value = value;
        }

        protected Node(Node previous, Node next, Object value) {
            this.previous = previous;
            this.next = next;
            this.value = value;
        }

        protected Object getValue() {
            return this.value;
        }

        protected void setValue(Object value) {
            this.value = value;
        }

        protected Node getPreviousNode() {
            return this.previous;
        }

        protected void setPreviousNode(Node previous) {
            this.previous = previous;
        }

        protected Node getNextNode() {
            return this.next;
        }

        protected void setNextNode(Node next) {
            this.next = next;
        }
    }
}

