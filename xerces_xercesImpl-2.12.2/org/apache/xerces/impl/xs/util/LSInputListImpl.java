/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.util;

import java.lang.reflect.Array;
import java.util.AbstractList;
import org.apache.xerces.xs.LSInputList;
import org.w3c.dom.ls.LSInput;

public final class LSInputListImpl
extends AbstractList
implements LSInputList {
    public static final LSInputListImpl EMPTY_LIST = new LSInputListImpl(new LSInput[0], 0);
    private final LSInput[] fArray;
    private final int fLength;

    public LSInputListImpl(LSInput[] lSInputArray, int n) {
        this.fArray = lSInputArray;
        this.fLength = n;
    }

    @Override
    public int getLength() {
        return this.fLength;
    }

    @Override
    public LSInput item(int n) {
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

