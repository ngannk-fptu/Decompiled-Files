/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.util;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;

public class XSObjectListImpl
extends AbstractList
implements XSObjectList {
    public static final XSObjectListImpl EMPTY_LIST = new XSObjectListImpl(new XSObject[0], 0);
    private static final ListIterator EMPTY_ITERATOR = new ListIterator(){

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        public Object previous() {
            throw new NoSuchElementException();
        }

        @Override
        public int nextIndex() {
            return 0;
        }

        @Override
        public int previousIndex() {
            return -1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void set(Object object) {
            throw new UnsupportedOperationException();
        }

        public void add(Object object) {
            throw new UnsupportedOperationException();
        }
    };
    private static final int DEFAULT_SIZE = 4;
    private XSObject[] fArray = null;
    private int fLength = 0;

    public XSObjectListImpl() {
        this.fArray = new XSObject[4];
        this.fLength = 0;
    }

    public XSObjectListImpl(XSObject[] xSObjectArray, int n) {
        this.fArray = xSObjectArray;
        this.fLength = n;
    }

    @Override
    public int getLength() {
        return this.fLength;
    }

    @Override
    public XSObject item(int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fArray[n];
    }

    public void clearXSObjectList() {
        for (int i = 0; i < this.fLength; ++i) {
            this.fArray[i] = null;
        }
        this.fArray = null;
        this.fLength = 0;
    }

    public void addXSObject(XSObject xSObject) {
        if (this.fLength == this.fArray.length) {
            XSObject[] xSObjectArray = new XSObject[this.fLength + 4];
            System.arraycopy(this.fArray, 0, xSObjectArray, 0, this.fLength);
            this.fArray = xSObjectArray;
        }
        this.fArray[this.fLength++] = xSObject;
    }

    public void addXSObject(int n, XSObject xSObject) {
        this.fArray[n] = xSObject;
    }

    @Override
    public boolean contains(Object object) {
        return object == null ? this.containsNull() : this.containsObject(object);
    }

    public Object get(int n) {
        if (n >= 0 && n < this.fLength) {
            return this.fArray[n];
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }

    @Override
    public int size() {
        return this.getLength();
    }

    @Override
    public Iterator iterator() {
        return this.listIterator0(0);
    }

    public ListIterator listIterator() {
        return this.listIterator0(0);
    }

    public ListIterator listIterator(int n) {
        if (n >= 0 && n < this.fLength) {
            return this.listIterator0(n);
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }

    private ListIterator listIterator0(int n) {
        return this.fLength == 0 ? EMPTY_ITERATOR : new XSObjectListIterator(n);
    }

    private boolean containsObject(Object object) {
        for (int i = this.fLength - 1; i >= 0; --i) {
            if (!object.equals(this.fArray[i])) continue;
            return true;
        }
        return false;
    }

    private boolean containsNull() {
        for (int i = this.fLength - 1; i >= 0; --i) {
            if (this.fArray[i] != null) continue;
            return true;
        }
        return false;
    }

    @Override
    public Object[] toArray() {
        Object[] objectArray = new Object[this.fLength];
        this.toArray0(objectArray);
        return objectArray;
    }

    @Override
    public Object[] toArray(Object[] objectArray) {
        if (objectArray.length < this.fLength) {
            Class<?> clazz = objectArray.getClass();
            Class<?> clazz2 = clazz.getComponentType();
            objectArray = (Object[])Array.newInstance(clazz2, this.fLength);
        }
        this.toArray0(objectArray);
        if (objectArray.length > this.fLength) {
            objectArray[this.fLength] = null;
        }
        return objectArray;
    }

    private void toArray0(Object[] objectArray) {
        if (this.fLength > 0) {
            System.arraycopy(this.fArray, 0, objectArray, 0, this.fLength);
        }
    }

    private final class XSObjectListIterator
    implements ListIterator {
        private int index;

        public XSObjectListIterator(int n) {
            this.index = n;
        }

        @Override
        public boolean hasNext() {
            return this.index < XSObjectListImpl.this.fLength;
        }

        @Override
        public Object next() {
            if (this.index < XSObjectListImpl.this.fLength) {
                return XSObjectListImpl.this.fArray[this.index++];
            }
            throw new NoSuchElementException();
        }

        @Override
        public boolean hasPrevious() {
            return this.index > 0;
        }

        public Object previous() {
            if (this.index > 0) {
                return XSObjectListImpl.this.fArray[--this.index];
            }
            throw new NoSuchElementException();
        }

        @Override
        public int nextIndex() {
            return this.index;
        }

        @Override
        public int previousIndex() {
            return this.index - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void set(Object object) {
            throw new UnsupportedOperationException();
        }

        public void add(Object object) {
            throw new UnsupportedOperationException();
        }
    }
}

