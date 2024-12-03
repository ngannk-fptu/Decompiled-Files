/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool.impl;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.pool.impl.CursorableLinkedList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class CursorableSubList<E>
extends CursorableLinkedList<E>
implements List<E> {
    protected CursorableLinkedList<E> _list = null;
    protected CursorableLinkedList.Listable<E> _pre = null;
    protected CursorableLinkedList.Listable<E> _post = null;

    CursorableSubList(CursorableLinkedList<E> list, int from, int to) {
        if (0 > from || list.size() < to) {
            throw new IndexOutOfBoundsException();
        }
        if (from > to) {
            throw new IllegalArgumentException();
        }
        this._list = list;
        if (from < list.size()) {
            this._head.setNext(this._list.getListableAt(from));
            this._pre = null == this._head.next() ? null : this._head.next().prev();
        } else {
            this._pre = this._list.getListableAt(from - 1);
        }
        if (from == to) {
            this._head.setNext(null);
            this._head.setPrev(null);
            this._post = to < list.size() ? this._list.getListableAt(to) : null;
        } else {
            this._head.setPrev(this._list.getListableAt(to - 1));
            this._post = this._head.prev().next();
        }
        this._size = to - from;
        this._modCount = this._list._modCount;
    }

    @Override
    public void clear() {
        this.checkForComod();
        Iterator<E> it = this.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    @Override
    public Iterator<E> iterator() {
        this.checkForComod();
        return super.iterator();
    }

    @Override
    public int size() {
        this.checkForComod();
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        this.checkForComod();
        return super.isEmpty();
    }

    @Override
    public Object[] toArray() {
        this.checkForComod();
        return super.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        this.checkForComod();
        return super.toArray(a);
    }

    @Override
    public boolean contains(Object o) {
        this.checkForComod();
        return super.contains(o);
    }

    @Override
    public boolean remove(Object o) {
        this.checkForComod();
        return super.remove(o);
    }

    @Override
    public E removeFirst() {
        this.checkForComod();
        return super.removeFirst();
    }

    @Override
    public E removeLast() {
        this.checkForComod();
        return super.removeLast();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        this.checkForComod();
        return super.addAll(c);
    }

    @Override
    public boolean add(E o) {
        this.checkForComod();
        return super.add(o);
    }

    @Override
    public boolean addFirst(E o) {
        this.checkForComod();
        return super.addFirst(o);
    }

    @Override
    public boolean addLast(E o) {
        this.checkForComod();
        return super.addLast(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        this.checkForComod();
        return super.removeAll(c);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        this.checkForComod();
        return super.containsAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        this.checkForComod();
        return super.addAll(index, c);
    }

    @Override
    public int hashCode() {
        this.checkForComod();
        return super.hashCode();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        this.checkForComod();
        return super.retainAll(c);
    }

    @Override
    public E set(int index, E element) {
        this.checkForComod();
        return super.set(index, element);
    }

    @Override
    public boolean equals(Object o) {
        this.checkForComod();
        return super.equals(o);
    }

    @Override
    public E get(int index) {
        this.checkForComod();
        return super.get(index);
    }

    @Override
    public E getFirst() {
        this.checkForComod();
        return super.getFirst();
    }

    @Override
    public E getLast() {
        this.checkForComod();
        return super.getLast();
    }

    @Override
    public void add(int index, E element) {
        this.checkForComod();
        super.add(index, element);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        this.checkForComod();
        return super.listIterator(index);
    }

    @Override
    public E remove(int index) {
        this.checkForComod();
        return super.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        this.checkForComod();
        return super.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        this.checkForComod();
        return super.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        this.checkForComod();
        return super.listIterator();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        this.checkForComod();
        return super.subList(fromIndex, toIndex);
    }

    @Override
    protected CursorableLinkedList.Listable<E> insertListable(CursorableLinkedList.Listable<E> before, CursorableLinkedList.Listable<E> after, E value) {
        ++this._modCount;
        ++this._size;
        CursorableLinkedList.Listable<E> elt = this._list.insertListable(null == before ? this._pre : before, null == after ? this._post : after, value);
        if (null == this._head.next()) {
            this._head.setNext(elt);
            this._head.setPrev(elt);
        }
        if (before == this._head.prev()) {
            this._head.setPrev(elt);
        }
        if (after == this._head.next()) {
            this._head.setNext(elt);
        }
        this.broadcastListableInserted(elt);
        return elt;
    }

    @Override
    protected void removeListable(CursorableLinkedList.Listable<E> elt) {
        ++this._modCount;
        --this._size;
        if (this._head.next() == elt && this._head.prev() == elt) {
            this._head.setNext(null);
            this._head.setPrev(null);
        }
        if (this._head.next() == elt) {
            this._head.setNext(elt.next());
        }
        if (this._head.prev() == elt) {
            this._head.setPrev(elt.prev());
        }
        this._list.removeListable(elt);
        this.broadcastListableRemoved(elt);
    }

    protected void checkForComod() throws ConcurrentModificationException {
        if (this._modCount != this._list._modCount) {
            throw new ConcurrentModificationException();
        }
    }
}

