/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

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
import org.apache.commons.collections.CursorableSubList;

public class CursorableLinkedList
implements List,
Serializable {
    private static final long serialVersionUID = 8836393098519411393L;
    protected transient int _size = 0;
    protected transient Listable _head = new Listable(null, null, null);
    protected transient int _modCount = 0;
    protected transient List _cursors = new ArrayList();

    public boolean add(Object o) {
        this.insertListable(this._head.prev(), null, o);
        return true;
    }

    public void add(int index, Object element) {
        if (index == this._size) {
            this.add(element);
        } else {
            if (index < 0 || index > this._size) {
                throw new IndexOutOfBoundsException(String.valueOf(index) + " < 0 or " + String.valueOf(index) + " > " + this._size);
            }
            Listable succ = this.isEmpty() ? null : this.getListableAt(index);
            Listable pred = null == succ ? null : succ.prev();
            this.insertListable(pred, succ, element);
        }
    }

    public boolean addAll(Collection c) {
        if (c.isEmpty()) {
            return false;
        }
        Iterator it = c.iterator();
        while (it.hasNext()) {
            this.insertListable(this._head.prev(), null, it.next());
        }
        return true;
    }

    public boolean addAll(int index, Collection c) {
        if (c.isEmpty()) {
            return false;
        }
        if (this._size == index || this._size == 0) {
            return this.addAll(c);
        }
        Listable succ = this.getListableAt(index);
        Listable pred = null == succ ? null : succ.prev();
        Iterator it = c.iterator();
        while (it.hasNext()) {
            pred = this.insertListable(pred, succ, it.next());
        }
        return true;
    }

    public boolean addFirst(Object o) {
        this.insertListable(null, this._head.next(), o);
        return true;
    }

    public boolean addLast(Object o) {
        this.insertListable(this._head.prev(), null, o);
        return true;
    }

    public void clear() {
        Iterator it = this.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    public boolean contains(Object o) {
        Listable elt = this._head.next();
        Listable past = null;
        while (null != elt && past != this._head.prev()) {
            if (null == o && null == elt.value() || o != null && o.equals(elt.value())) {
                return true;
            }
            past = elt;
            elt = past.next();
        }
        return false;
    }

    public boolean containsAll(Collection c) {
        Iterator it = c.iterator();
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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        ListIterator it = ((List)o).listIterator();
        Listable elt = this._head.next();
        Listable past = null;
        while (null != elt && past != this._head.prev()) {
            if (!it.hasNext() || (null == elt.value() ? null != it.next() : !elt.value().equals(it.next()))) {
                return false;
            }
            past = elt;
            elt = past.next();
        }
        return !it.hasNext();
    }

    public Object get(int index) {
        return this.getListableAt(index).value();
    }

    public Object getFirst() {
        try {
            return this._head.next().value();
        }
        catch (NullPointerException e) {
            throw new NoSuchElementException();
        }
    }

    public Object getLast() {
        try {
            return this._head.prev().value();
        }
        catch (NullPointerException e) {
            throw new NoSuchElementException();
        }
    }

    public int hashCode() {
        int hash = 1;
        Listable elt = this._head.next();
        Listable past = null;
        while (null != elt && past != this._head.prev()) {
            hash = 31 * hash + (null == elt.value() ? 0 : elt.value().hashCode());
            past = elt;
            elt = past.next();
        }
        return hash;
    }

    public int indexOf(Object o) {
        int ndx = 0;
        if (null == o) {
            Listable elt = this._head.next();
            Listable past = null;
            while (null != elt && past != this._head.prev()) {
                if (null == elt.value()) {
                    return ndx;
                }
                ++ndx;
                past = elt;
                elt = past.next();
            }
        } else {
            Listable elt = this._head.next();
            Listable past = null;
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

    public boolean isEmpty() {
        return 0 == this._size;
    }

    public Iterator iterator() {
        return this.listIterator(0);
    }

    public int lastIndexOf(Object o) {
        int ndx = this._size - 1;
        if (null == o) {
            Listable elt = this._head.prev();
            Listable past = null;
            while (null != elt && past != this._head.next()) {
                if (null == elt.value()) {
                    return ndx;
                }
                --ndx;
                past = elt;
                elt = past.prev();
            }
        } else {
            Listable elt = this._head.prev();
            Listable past = null;
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

    public ListIterator listIterator() {
        return this.listIterator(0);
    }

    public ListIterator listIterator(int index) {
        if (index < 0 || index > this._size) {
            throw new IndexOutOfBoundsException(index + " < 0 or > " + this._size);
        }
        return new ListIter(index);
    }

    public boolean remove(Object o) {
        Listable elt = this._head.next();
        Listable past = null;
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

    public Object remove(int index) {
        Listable elt = this.getListableAt(index);
        Object ret = elt.value();
        this.removeListable(elt);
        return ret;
    }

    public boolean removeAll(Collection c) {
        if (0 == c.size() || 0 == this._size) {
            return false;
        }
        boolean changed = false;
        Iterator it = this.iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) continue;
            it.remove();
            changed = true;
        }
        return changed;
    }

    public Object removeFirst() {
        if (this._head.next() != null) {
            Object val = this._head.next().value();
            this.removeListable(this._head.next());
            return val;
        }
        throw new NoSuchElementException();
    }

    public Object removeLast() {
        if (this._head.prev() != null) {
            Object val = this._head.prev().value();
            this.removeListable(this._head.prev());
            return val;
        }
        throw new NoSuchElementException();
    }

    public boolean retainAll(Collection c) {
        boolean changed = false;
        Iterator it = this.iterator();
        while (it.hasNext()) {
            if (c.contains(it.next())) continue;
            it.remove();
            changed = true;
        }
        return changed;
    }

    public Object set(int index, Object element) {
        Listable elt = this.getListableAt(index);
        Object val = elt.setValue(element);
        this.broadcastListableChanged(elt);
        return val;
    }

    public int size() {
        return this._size;
    }

    public Object[] toArray() {
        Object[] array = new Object[this._size];
        int i = 0;
        Listable elt = this._head.next();
        Listable past = null;
        while (null != elt && past != this._head.prev()) {
            array[i++] = elt.value();
            past = elt;
            elt = past.next();
        }
        return array;
    }

    public Object[] toArray(Object[] a) {
        if (a.length < this._size) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), this._size);
        }
        int i = 0;
        Listable elt = this._head.next();
        Listable past = null;
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
        Listable elt = this._head.next();
        Listable past = null;
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

    public List subList(int i, int j) {
        if (i < 0 || j > this._size || i > j) {
            throw new IndexOutOfBoundsException();
        }
        if (i == 0 && j == this._size) {
            return this;
        }
        return new CursorableSubList(this, i, j);
    }

    protected Listable insertListable(Listable before, Listable after, Object value) {
        ++this._modCount;
        ++this._size;
        Listable elt = new Listable(before, after, value);
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

    protected void removeListable(Listable elt) {
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

    protected Listable getListableAt(int index) {
        if (index < 0 || index >= this._size) {
            throw new IndexOutOfBoundsException(String.valueOf(index) + " < 0 or " + String.valueOf(index) + " >= " + this._size);
        }
        if (index <= this._size / 2) {
            Listable elt = this._head.next();
            for (int i = 0; i < index; ++i) {
                elt = elt.next();
            }
            return elt;
        }
        Listable elt = this._head.prev();
        for (int i = this._size - 1; i > index; --i) {
            elt = elt.prev();
        }
        return elt;
    }

    protected void registerCursor(Cursor cur) {
        Iterator it = this._cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference)it.next();
            if (ref.get() != null) continue;
            it.remove();
        }
        this._cursors.add(new WeakReference<Cursor>(cur));
    }

    protected void unregisterCursor(Cursor cur) {
        Iterator it = this._cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference)it.next();
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
        Iterator it = this._cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference)it.next();
            Cursor cursor = (Cursor)ref.get();
            if (cursor != null) {
                cursor.invalidate();
                ref.clear();
            }
            it.remove();
        }
    }

    protected void broadcastListableChanged(Listable elt) {
        Iterator it = this._cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference)it.next();
            Cursor cursor = (Cursor)ref.get();
            if (cursor == null) {
                it.remove();
                continue;
            }
            cursor.listableChanged(elt);
        }
    }

    protected void broadcastListableRemoved(Listable elt) {
        Iterator it = this._cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference)it.next();
            Cursor cursor = (Cursor)ref.get();
            if (cursor == null) {
                it.remove();
                continue;
            }
            cursor.listableRemoved(elt);
        }
    }

    protected void broadcastListableInserted(Listable elt) {
        Iterator it = this._cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference)it.next();
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
        for (Listable cur = this._head.next(); cur != null; cur = cur.next()) {
            out.writeObject(cur.value());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this._size = 0;
        this._modCount = 0;
        this._cursors = new ArrayList();
        this._head = new Listable(null, null, null);
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            this.add(in.readObject());
        }
    }

    public class Cursor
    extends ListIter
    implements ListIterator {
        boolean _valid = true;

        Cursor(int index) {
            super(index);
            CursorableLinkedList.this.registerCursor(this);
        }

        public int previousIndex() {
            throw new UnsupportedOperationException();
        }

        public int nextIndex() {
            throw new UnsupportedOperationException();
        }

        public void add(Object o) {
            this.checkForComod();
            Listable elt = CursorableLinkedList.this.insertListable(this._cur.prev(), this._cur.next(), o);
            this._cur.setPrev(elt);
            this._cur.setNext(elt.next());
            this._lastReturned = null;
            ++this._nextIndex;
            ++this._expectedModCount;
        }

        protected void listableRemoved(Listable elt) {
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

        protected void listableInserted(Listable elt) {
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

        protected void listableChanged(Listable elt) {
            if (this._lastReturned == elt) {
                this._lastReturned = null;
            }
        }

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

    class ListIter
    implements ListIterator {
        Listable _cur = null;
        Listable _lastReturned = null;
        int _expectedModCount;
        int _nextIndex;

        ListIter(int index) {
            this._expectedModCount = CursorableLinkedList.this._modCount;
            this._nextIndex = 0;
            if (index == 0) {
                this._cur = new Listable(null, CursorableLinkedList.this._head.next(), null);
                this._nextIndex = 0;
            } else if (index == CursorableLinkedList.this._size) {
                this._cur = new Listable(CursorableLinkedList.this._head.prev(), null, null);
                this._nextIndex = CursorableLinkedList.this._size;
            } else {
                Listable temp = CursorableLinkedList.this.getListableAt(index);
                this._cur = new Listable(temp.prev(), temp, null);
                this._nextIndex = index;
            }
        }

        public Object previous() {
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

        public boolean hasNext() {
            this.checkForComod();
            return null != this._cur.next() && this._cur.prev() != CursorableLinkedList.this._head.prev();
        }

        public Object next() {
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

        public int previousIndex() {
            this.checkForComod();
            if (!this.hasPrevious()) {
                return -1;
            }
            return this._nextIndex - 1;
        }

        public boolean hasPrevious() {
            this.checkForComod();
            return null != this._cur.prev() && this._cur.next() != CursorableLinkedList.this._head.next();
        }

        public void set(Object o) {
            this.checkForComod();
            try {
                this._lastReturned.setValue(o);
            }
            catch (NullPointerException e) {
                throw new IllegalStateException();
            }
        }

        public int nextIndex() {
            this.checkForComod();
            if (!this.hasNext()) {
                return CursorableLinkedList.this.size();
            }
            return this._nextIndex;
        }

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

        public void add(Object o) {
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

    static class Listable
    implements Serializable {
        private Listable _prev = null;
        private Listable _next = null;
        private Object _val = null;

        Listable(Listable prev, Listable next, Object val) {
            this._prev = prev;
            this._next = next;
            this._val = val;
        }

        Listable next() {
            return this._next;
        }

        Listable prev() {
            return this._prev;
        }

        Object value() {
            return this._val;
        }

        void setNext(Listable next) {
            this._next = next;
        }

        void setPrev(Listable prev) {
            this._prev = prev;
        }

        Object setValue(Object val) {
            Object temp = this._val;
            this._val = val;
            return temp;
        }
    }
}

