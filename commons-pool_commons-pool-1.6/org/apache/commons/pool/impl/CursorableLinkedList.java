/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.pool.impl.CursorableSubList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class CursorableLinkedList<E>
implements List<E>,
Serializable {
    private static final long serialVersionUID = 8836393098519411393L;
    protected transient int _size = 0;
    protected transient Listable<E> _head = new Listable<Object>(null, null, null);
    protected transient int _modCount = 0;
    protected transient List<WeakReference<Cursor>> _cursors = new ArrayList<WeakReference<Cursor>>();

    CursorableLinkedList() {
    }

    @Override
    public boolean add(E o) {
        this.insertListable(this._head.prev(), null, o);
        return true;
    }

    @Override
    public void add(int index, E element) {
        if (index == this._size) {
            this.add(element);
        } else {
            if (index < 0 || index > this._size) {
                throw new IndexOutOfBoundsException(String.valueOf(index) + " < 0 or " + String.valueOf(index) + " > " + this._size);
            }
            Listable<E> succ = this.isEmpty() ? null : this.getListableAt(index);
            Listable<E> pred = null == succ ? null : succ.prev();
            this.insertListable(pred, succ, element);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }
        Iterator<E> it = c.iterator();
        while (it.hasNext()) {
            this.insertListable(this._head.prev(), null, it.next());
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }
        if (this._size == index || this._size == 0) {
            return this.addAll(c);
        }
        Listable<E> succ = this.getListableAt(index);
        Listable<E> pred = null == succ ? null : succ.prev();
        Iterator<E> it = c.iterator();
        while (it.hasNext()) {
            pred = this.insertListable(pred, succ, it.next());
        }
        return true;
    }

    public boolean addFirst(E o) {
        this.insertListable(null, this._head.next(), o);
        return true;
    }

    public boolean addLast(E o) {
        this.insertListable(this._head.prev(), null, o);
        return true;
    }

    @Override
    public void clear() {
        Iterator<E> it = this.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    @Override
    public boolean contains(Object o) {
        Listable<E> elt = this._head.next();
        Listable<E> past = null;
        while (null != elt && past != this._head.prev()) {
            if (null == o && null == elt.value() || o != null && o.equals(elt.value())) {
                return true;
            }
            past = elt;
            elt = past.next();
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        Iterator<?> it = c.iterator();
        while (it.hasNext()) {
            if (this.contains(it.next())) continue;
            return false;
        }
        return true;
    }

    public Cursor cursor() {
        return new Cursor(0);
    }

    public Cursor cursor(int i) {
        return new Cursor(i);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        ListIterator it = ((List)o).listIterator();
        Listable<E> elt = this._head.next();
        Listable<E> past = null;
        while (null != elt && past != this._head.prev()) {
            if (!it.hasNext() || (null == elt.value() ? null != it.next() : !elt.value().equals(it.next()))) {
                return false;
            }
            past = elt;
            elt = past.next();
        }
        return !it.hasNext();
    }

    @Override
    public E get(int index) {
        return this.getListableAt(index).value();
    }

    public E getFirst() {
        try {
            return this._head.next().value();
        }
        catch (NullPointerException e) {
            throw new NoSuchElementException();
        }
    }

    public E getLast() {
        try {
            return this._head.prev().value();
        }
        catch (NullPointerException e) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        Listable<E> elt = this._head.next();
        Listable<E> past = null;
        while (null != elt && past != this._head.prev()) {
            hash = 31 * hash + (null == elt.value() ? 0 : elt.value().hashCode());
            past = elt;
            elt = past.next();
        }
        return hash;
    }

    @Override
    public int indexOf(Object o) {
        int ndx = 0;
        if (null == o) {
            Listable<E> elt = this._head.next();
            Listable<E> past = null;
            while (null != elt && past != this._head.prev()) {
                if (null == elt.value()) {
                    return ndx;
                }
                ++ndx;
                past = elt;
                elt = past.next();
            }
        } else {
            Listable<E> elt = this._head.next();
            Listable<E> past = null;
            while (null != elt && past != this._head.prev()) {
                if (o.equals(elt.value())) {
                    return ndx;
                }
                ++ndx;
                past = elt;
                elt = past.next();
            }
        }
        return -1;
    }

    @Override
    public boolean isEmpty() {
        return 0 == this._size;
    }

    @Override
    public Iterator<E> iterator() {
        return this.listIterator(0);
    }

    @Override
    public int lastIndexOf(Object o) {
        int ndx = this._size - 1;
        if (null == o) {
            Listable<E> elt = this._head.prev();
            Listable<E> past = null;
            while (null != elt && past != this._head.next()) {
                if (null == elt.value()) {
                    return ndx;
                }
                --ndx;
                past = elt;
                elt = past.prev();
            }
        } else {
            Listable<E> elt = this._head.prev();
            Listable<E> past = null;
            while (null != elt && past != this._head.next()) {
                if (o.equals(elt.value())) {
                    return ndx;
                }
                --ndx;
                past = elt;
                elt = past.prev();
            }
        }
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return this.listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        if (index < 0 || index > this._size) {
            throw new IndexOutOfBoundsException(index + " < 0 or > " + this._size);
        }
        return new ListIter(index);
    }

    @Override
    public boolean remove(Object o) {
        Listable<E> elt = this._head.next();
        Listable<E> past = null;
        while (null != elt && past != this._head.prev()) {
            if (null == o && null == elt.value()) {
                this.removeListable(elt);
                return true;
            }
            if (o != null && o.equals(elt.value())) {
                this.removeListable(elt);
                return true;
            }
            past = elt;
            elt = past.next();
        }
        return false;
    }

    @Override
    public E remove(int index) {
        Listable<E> elt = this.getListableAt(index);
        E ret = elt.value();
        this.removeListable(elt);
        return ret;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (0 == c.size() || 0 == this._size) {
            return false;
        }
        boolean changed = false;
        Iterator<E> it = this.iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) continue;
            it.remove();
            changed = true;
        }
        return changed;
    }

    public E removeFirst() {
        if (this._head.next() != null) {
            E val = this._head.next().value();
            this.removeListable(this._head.next());
            return val;
        }
        throw new NoSuchElementException();
    }

    public E removeLast() {
        if (this._head.prev() != null) {
            E val = this._head.prev().value();
            this.removeListable(this._head.prev());
            return val;
        }
        throw new NoSuchElementException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        Iterator<E> it = this.iterator();
        while (it.hasNext()) {
            if (c.contains(it.next())) continue;
            it.remove();
            changed = true;
        }
        return changed;
    }

    @Override
    public E set(int index, E element) {
        Listable<E> elt = this.getListableAt(index);
        E val = elt.setValue(element);
        this.broadcastListableChanged(elt);
        return val;
    }

    @Override
    public int size() {
        return this._size;
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[this._size];
        int i = 0;
        Listable<E> elt = this._head.next();
        Listable<E> past = null;
        while (null != elt && past != this._head.prev()) {
            array[i++] = elt.value();
            past = elt;
            elt = past.next();
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < this._size) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), this._size);
        }
        int i = 0;
        Listable<E> elt = this._head.next();
        Listable<E> past = null;
        while (null != elt && past != this._head.prev()) {
            a[i++] = elt.value();
            past = elt;
            elt = past.next();
        }
        if (a.length > this._size) {
            a[this._size] = null;
        }
        return a;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        Listable<E> elt = this._head.next();
        Listable<E> past = null;
        while (null != elt && past != this._head.prev()) {
            if (this._head.next() != elt) {
                buf.append(", ");
            }
            buf.append(elt.value());
            past = elt;
            elt = past.next();
        }
        buf.append("]");
        return buf.toString();
    }

    @Override
    public List<E> subList(int i, int j) {
        if (i < 0 || j > this._size || i > j) {
            throw new IndexOutOfBoundsException();
        }
        if (i == 0 && j == this._size) {
            return this;
        }
        return new CursorableSubList(this, i, j);
    }

    protected Listable<E> insertListable(Listable<E> before, Listable<E> after, E value) {
        ++this._modCount;
        ++this._size;
        Listable<E> elt = new Listable<E>(before, after, value);
        if (null != before) {
            before.setNext(elt);
        } else {
            this._head.setNext(elt);
        }
        if (null != after) {
            after.setPrev(elt);
        } else {
            this._head.setPrev(elt);
        }
        this.broadcastListableInserted(elt);
        return elt;
    }

    protected void removeListable(Listable<E> elt) {
        ++this._modCount;
        --this._size;
        if (this._head.next() == elt) {
            this._head.setNext(elt.next());
        }
        if (null != elt.next()) {
            elt.next().setPrev(elt.prev());
        }
        if (this._head.prev() == elt) {
            this._head.setPrev(elt.prev());
        }
        if (null != elt.prev()) {
            elt.prev().setNext(elt.next());
        }
        this.broadcastListableRemoved(elt);
    }

    protected Listable<E> getListableAt(int index) {
        if (index < 0 || index >= this._size) {
            throw new IndexOutOfBoundsException(String.valueOf(index) + " < 0 or " + String.valueOf(index) + " >= " + this._size);
        }
        if (index <= this._size / 2) {
            Listable<E> elt = this._head.next();
            for (int i = 0; i < index; ++i) {
                elt = elt.next();
            }
            return elt;
        }
        Listable<E> elt = this._head.prev();
        for (int i = this._size - 1; i > index; --i) {
            elt = elt.prev();
        }
        return elt;
    }

    protected void registerCursor(Cursor cur) {
        Iterator<WeakReference<Cursor>> it = this._cursors.iterator();
        while (it.hasNext()) {
            WeakReference<Cursor> ref = it.next();
            if (ref.get() != null) continue;
            it.remove();
        }
        this._cursors.add(new WeakReference<Cursor>(cur));
    }

    protected void unregisterCursor(Cursor cur) {
        Iterator<WeakReference<Cursor>> it = this._cursors.iterator();
        while (it.hasNext()) {
            WeakReference<Cursor> ref = it.next();
            Cursor cursor = (Cursor)ref.get();
            if (cursor == null) {
                it.remove();
                continue;
            }
            if (cursor != cur) continue;
            ref.clear();
            it.remove();
            break;
        }
    }

    protected void invalidateCursors() {
        Iterator<WeakReference<Cursor>> it = this._cursors.iterator();
        while (it.hasNext()) {
            WeakReference<Cursor> ref = it.next();
            Cursor cursor = (Cursor)ref.get();
            if (cursor != null) {
                cursor.invalidate();
                ref.clear();
            }
            it.remove();
        }
    }

    protected void broadcastListableChanged(Listable<E> elt) {
        Iterator<WeakReference<Cursor>> it = this._cursors.iterator();
        while (it.hasNext()) {
            WeakReference<Cursor> ref = it.next();
            Cursor cursor = (Cursor)ref.get();
            if (cursor == null) {
                it.remove();
                continue;
            }
            cursor.listableChanged(elt);
        }
    }

    protected void broadcastListableRemoved(Listable<E> elt) {
        Iterator<WeakReference<Cursor>> it = this._cursors.iterator();
        while (it.hasNext()) {
            WeakReference<Cursor> ref = it.next();
            Cursor cursor = (Cursor)ref.get();
            if (cursor == null) {
                it.remove();
                continue;
            }
            cursor.listableRemoved(elt);
        }
    }

    protected void broadcastListableInserted(Listable<E> elt) {
        Iterator<WeakReference<Cursor>> it = this._cursors.iterator();
        while (it.hasNext()) {
            WeakReference<Cursor> ref = it.next();
            Cursor cursor = (Cursor)ref.get();
            if (cursor == null) {
                it.remove();
                continue;
            }
            cursor.listableInserted(elt);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this._size);
        for (Listable<E> cur = this._head.next(); cur != null; cur = cur.next()) {
            out.writeObject(cur.value());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this._size = 0;
        this._modCount = 0;
        this._cursors = new ArrayList<WeakReference<Cursor>>();
        this._head = new Listable<Object>(null, null, null);
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            this.add(in.readObject());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public class Cursor
    extends ListIter
    implements ListIterator<E> {
        boolean _valid;

        Cursor(int index) {
            super(index);
            this._valid = false;
            this._valid = true;
            CursorableLinkedList.this.registerCursor(this);
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(E o) {
            this.checkForComod();
            Listable elt = CursorableLinkedList.this.insertListable(this._cur.prev(), this._cur.next(), o);
            this._cur.setPrev(elt);
            this._cur.setNext(elt.next());
            this._lastReturned = null;
            ++this._nextIndex;
            ++this._expectedModCount;
        }

        protected void listableRemoved(Listable<E> elt) {
            if (null == CursorableLinkedList.this._head.prev()) {
                this._cur.setNext(null);
            } else if (this._cur.next() == elt) {
                this._cur.setNext(elt.next());
            }
            if (null == CursorableLinkedList.this._head.next()) {
                this._cur.setPrev(null);
            } else if (this._cur.prev() == elt) {
                this._cur.setPrev(elt.prev());
            }
            if (this._lastReturned == elt) {
                this._lastReturned = null;
            }
        }

        protected void listableInserted(Listable<E> elt) {
            if (null == this._cur.next() && null == this._cur.prev()) {
                this._cur.setNext(elt);
            } else if (this._cur.prev() == elt.prev()) {
                this._cur.setNext(elt);
            }
            if (this._cur.next() == elt.next()) {
                this._cur.setPrev(elt);
            }
            if (this._lastReturned == elt) {
                this._lastReturned = null;
            }
        }

        protected void listableChanged(Listable<E> elt) {
            if (this._lastReturned == elt) {
                this._lastReturned = null;
            }
        }

        @Override
        protected void checkForComod() {
            if (!this._valid) {
                throw new ConcurrentModificationException();
            }
        }

        protected void invalidate() {
            this._valid = false;
        }

        public void close() {
            if (this._valid) {
                this._valid = false;
                CursorableLinkedList.this.unregisterCursor(this);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class ListIter
    implements ListIterator<E> {
        Listable<E> _cur = null;
        Listable<E> _lastReturned = null;
        int _expectedModCount;
        int _nextIndex;

        ListIter(int index) {
            this._expectedModCount = CursorableLinkedList.this._modCount;
            this._nextIndex = 0;
            if (index == 0) {
                this._cur = new Listable<Object>(null, CursorableLinkedList.this._head.next(), null);
                this._nextIndex = 0;
            } else if (index == CursorableLinkedList.this._size) {
                this._cur = new Listable<Object>(CursorableLinkedList.this._head.prev(), null, null);
                this._nextIndex = CursorableLinkedList.this._size;
            } else {
                Listable temp = CursorableLinkedList.this.getListableAt(index);
                this._cur = new Listable<Object>(temp.prev(), temp, null);
                this._nextIndex = index;
            }
        }

        @Override
        public E previous() {
            this.checkForComod();
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            Object ret = this._cur.prev().value();
            this._lastReturned = this._cur.prev();
            this._cur.setNext(this._cur.prev());
            this._cur.setPrev(this._cur.prev().prev());
            --this._nextIndex;
            return ret;
        }

        @Override
        public boolean hasNext() {
            this.checkForComod();
            return null != this._cur.next() && this._cur.prev() != CursorableLinkedList.this._head.prev();
        }

        @Override
        public E next() {
            this.checkForComod();
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            Object ret = this._cur.next().value();
            this._lastReturned = this._cur.next();
            this._cur.setPrev(this._cur.next());
            this._cur.setNext(this._cur.next().next());
            ++this._nextIndex;
            return ret;
        }

        @Override
        public int previousIndex() {
            this.checkForComod();
            if (!this.hasPrevious()) {
                return -1;
            }
            return this._nextIndex - 1;
        }

        @Override
        public boolean hasPrevious() {
            this.checkForComod();
            return null != this._cur.prev() && this._cur.next() != CursorableLinkedList.this._head.next();
        }

        @Override
        public void set(E o) {
            this.checkForComod();
            try {
                this._lastReturned.setValue(o);
            }
            catch (NullPointerException e) {
                throw new IllegalStateException();
            }
        }

        @Override
        public int nextIndex() {
            this.checkForComod();
            if (!this.hasNext()) {
                return CursorableLinkedList.this.size();
            }
            return this._nextIndex;
        }

        @Override
        public void remove() {
            this.checkForComod();
            if (null == this._lastReturned) {
                throw new IllegalStateException();
            }
            this._cur.setNext(this._lastReturned == CursorableLinkedList.this._head.prev() ? null : this._lastReturned.next());
            this._cur.setPrev(this._lastReturned == CursorableLinkedList.this._head.next() ? null : this._lastReturned.prev());
            CursorableLinkedList.this.removeListable(this._lastReturned);
            this._lastReturned = null;
            --this._nextIndex;
            ++this._expectedModCount;
        }

        @Override
        public void add(E o) {
            this.checkForComod();
            this._cur.setPrev(CursorableLinkedList.this.insertListable(this._cur.prev(), this._cur.next(), o));
            this._lastReturned = null;
            ++this._nextIndex;
            ++this._expectedModCount;
        }

        protected void checkForComod() {
            if (this._expectedModCount != CursorableLinkedList.this._modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class Listable<E>
    implements Serializable {
        private Listable<E> _prev = null;
        private Listable<E> _next = null;
        private E _val = null;

        Listable(Listable<E> prev, Listable<E> next, E val) {
            this._prev = prev;
            this._next = next;
            this._val = val;
        }

        Listable<E> next() {
            return this._next;
        }

        Listable<E> prev() {
            return this._prev;
        }

        E value() {
            return this._val;
        }

        void setNext(Listable<E> next) {
            this._next = next;
        }

        void setPrev(Listable<E> prev) {
            this._prev = prev;
        }

        E setValue(E val) {
            E temp = this._val;
            this._val = val;
            return temp;
        }
    }
}

