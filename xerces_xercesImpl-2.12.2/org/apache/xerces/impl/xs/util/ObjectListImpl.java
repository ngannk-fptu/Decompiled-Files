/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.util;

import java.lang.reflect.Array;
import java.util.AbstractList;
import org.apache.xerces.xs.datatypes.ObjectList;

public final class ObjectListImpl
extends AbstractList
implements ObjectList {
    public static final ObjectListImpl EMPTY_LIST = new ObjectListImpl(new Object[0], 0);
    private final Object[] fArray;
    private final int fLength;

    public ObjectListImpl(Object[] objectArray, int n) {
        this.fArray = objectArray;
        this.fLength = n;
    }

    @Override
    public int getLength() {
        return this.fLength;
    }

    @Override
    public boolean contains(Object object) {
        if (object == null) {
            for (int i = 0; i < this.fLength; ++i) {
                if (this.fArray[i] != null) continue;
                return true;
            }
        } else {
            for (int i = 0; i < this.fLength; ++i) {
                if (!object.equals(this.fArray[i])) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public Object item(int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fArray[n];
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
}

