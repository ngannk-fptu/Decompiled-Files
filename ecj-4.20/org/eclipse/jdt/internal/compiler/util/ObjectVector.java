/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import java.util.Iterator;

public final class ObjectVector
implements Iterable<Object> {
    static int INITIAL_SIZE = 10;
    public int size;
    int maxSize;
    Object[] elements;

    public ObjectVector() {
        this(INITIAL_SIZE);
    }

    public ObjectVector(int initialSize) {
        this.maxSize = initialSize > 0 ? initialSize : INITIAL_SIZE;
        this.size = 0;
        this.elements = new Object[this.maxSize];
    }

    public void add(Object newElement) {
        if (this.size == this.maxSize) {
            this.elements = new Object[this.maxSize *= 2];
            System.arraycopy(this.elements, 0, this.elements, 0, this.size);
        }
        this.elements[this.size++] = newElement;
    }

    public void addAll(Object[] newElements) {
        if (this.size + newElements.length >= this.maxSize) {
            this.maxSize = this.size + newElements.length;
            this.elements = new Object[this.maxSize];
            System.arraycopy(this.elements, 0, this.elements, 0, this.size);
        }
        System.arraycopy(newElements, 0, this.elements, this.size, newElements.length);
        this.size += newElements.length;
    }

    public void addAll(ObjectVector newVector) {
        if (this.size + newVector.size >= this.maxSize) {
            this.maxSize = this.size + newVector.size;
            this.elements = new Object[this.maxSize];
            System.arraycopy(this.elements, 0, this.elements, 0, this.size);
        }
        System.arraycopy(newVector.elements, 0, this.elements, this.size, newVector.size);
        this.size += newVector.size;
    }

    public boolean containsIdentical(Object element) {
        int i = this.size;
        while (--i >= 0) {
            if (element != this.elements[i]) continue;
            return true;
        }
        return false;
    }

    public boolean contains(Object element) {
        int i = this.size;
        while (--i >= 0) {
            if (!element.equals(this.elements[i])) continue;
            return true;
        }
        return false;
    }

    public void copyInto(Object[] targetArray) {
        this.copyInto(targetArray, 0);
    }

    public void copyInto(Object[] targetArray, int index) {
        System.arraycopy(this.elements, 0, targetArray, index, this.size);
    }

    public Object elementAt(int index) {
        return this.elements[index];
    }

    public Object find(Object element) {
        int i = this.size;
        while (--i >= 0) {
            if (!element.equals(this.elements[i])) continue;
            return this.elements[i];
        }
        return null;
    }

    public Object remove(Object element) {
        int i = this.size;
        while (--i >= 0) {
            if (!element.equals(this.elements[i])) continue;
            System.arraycopy(this.elements, i + 1, this.elements, i, --this.size - i);
            this.elements[this.size] = null;
            return element;
        }
        return null;
    }

    public void removeAll() {
        int i = this.size;
        while (--i >= 0) {
            this.elements[i] = null;
        }
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public String toString() {
        String s = "";
        int i = 0;
        while (i < this.size) {
            s = String.valueOf(s) + this.elements[i].toString() + "\n";
            ++i;
        }
        return s;
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>(){
            int i = 0;

            @Override
            public boolean hasNext() {
                return this.i < ObjectVector.this.size;
            }

            @Override
            public Object next() {
                return ObjectVector.this.elementAt(this.i++);
            }
        };
    }
}

