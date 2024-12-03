/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FastArray
implements Cloneable {
    private Object[] data;
    public int size;
    public static final FastArray EMPTY_LIST = new FastArray(0);

    public FastArray(int initialCapacity) {
        this.data = new Object[initialCapacity];
    }

    public FastArray() {
        this(8);
    }

    public FastArray(Collection c) {
        this(c.toArray());
    }

    public FastArray(Object[] objects) {
        this.data = objects;
        this.size = objects.length;
    }

    public Object get(int index) {
        return this.data[index];
    }

    public void add(Object o) {
        if (this.size == this.data.length) {
            Object[] newData = new Object[this.size == 0 ? 8 : this.size * 2];
            System.arraycopy(this.data, 0, newData, 0, this.size);
            this.data = newData;
        }
        this.data[this.size++] = o;
    }

    public void set(int index, Object o) {
        this.data[index] = o;
    }

    public int size() {
        return this.size;
    }

    public void clear() {
        this.data = new Object[this.data.length];
        this.size = 0;
    }

    public void addAll(FastArray newData) {
        this.addAll(newData.data, newData.size);
    }

    public void addAll(Object[] newData, int size) {
        if (size == 0) {
            return;
        }
        int newSize = this.size + size;
        if (newSize > this.data.length) {
            Object[] nd = new Object[newSize];
            System.arraycopy(this.data, 0, nd, 0, this.size);
            this.data = nd;
        }
        System.arraycopy(newData, 0, this.data, this.size, size);
        this.size = newSize;
    }

    public FastArray copy() {
        Object[] newData = new Object[this.size];
        System.arraycopy(this.data, 0, newData, 0, this.size);
        return new FastArray(newData);
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void addAll(List coll) {
        Object[] newData = coll.toArray();
        this.addAll(newData, newData.length);
    }

    public void remove(int index) {
        int numMoved = this.size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(this.data, index + 1, this.data, index, numMoved);
        }
        this.data[--this.size] = null;
    }

    public List toList() {
        if (this.size == 0) {
            return Collections.emptyList();
        }
        if (this.size == 1) {
            return Collections.singletonList(this.data[0]);
        }
        return new AbstractList(){

            @Override
            public Object get(int index) {
                return FastArray.this.get(index);
            }

            @Override
            public int size() {
                return FastArray.this.size;
            }
        };
    }

    public Object[] getArray() {
        return this.data;
    }

    public String toString() {
        if (this.size() == 0) {
            return "[]";
        }
        return this.toList().toString();
    }
}

