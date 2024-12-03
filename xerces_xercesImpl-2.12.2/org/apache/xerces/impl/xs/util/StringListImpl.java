/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.util;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Vector;
import org.apache.xerces.xs.StringList;

public final class StringListImpl
extends AbstractList
implements StringList {
    public static final StringListImpl EMPTY_LIST = new StringListImpl(new String[0], 0);
    private final String[] fArray;
    private final int fLength;
    private final Vector fVector;

    public StringListImpl(Vector vector) {
        this.fVector = vector;
        this.fLength = vector == null ? 0 : vector.size();
        this.fArray = null;
    }

    public StringListImpl(String[] stringArray, int n) {
        this.fArray = stringArray;
        this.fLength = n;
        this.fVector = null;
    }

    @Override
    public int getLength() {
        return this.fLength;
    }

    @Override
    public boolean contains(String string) {
        if (this.fVector != null) {
            return this.fVector.contains(string);
        }
        if (string == null) {
            for (int i = 0; i < this.fLength; ++i) {
                if (this.fArray[i] != null) continue;
                return true;
            }
        } else {
            for (int i = 0; i < this.fLength; ++i) {
                if (!string.equals(this.fArray[i])) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public String item(int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        if (this.fVector != null) {
            return (String)this.fVector.elementAt(n);
        }
        return this.fArray[n];
    }

    public Object get(int n) {
        if (n >= 0 && n < this.fLength) {
            if (this.fVector != null) {
                return this.fVector.elementAt(n);
            }
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
        if (this.fVector != null) {
            return this.fVector.toArray();
        }
        Object[] objectArray = new Object[this.fLength];
        this.toArray0(objectArray);
        return objectArray;
    }

    @Override
    public Object[] toArray(Object[] objectArray) {
        if (this.fVector != null) {
            return this.fVector.toArray(objectArray);
        }
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

