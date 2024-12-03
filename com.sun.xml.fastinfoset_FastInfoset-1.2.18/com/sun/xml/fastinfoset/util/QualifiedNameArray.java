/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.util;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.util.ValueArray;
import com.sun.xml.fastinfoset.util.ValueArrayResourceException;

public class QualifiedNameArray
extends ValueArray {
    public QualifiedName[] _array;
    private QualifiedNameArray _readOnlyArray;

    public QualifiedNameArray(int initialCapacity, int maximumCapacity) {
        this._array = new QualifiedName[initialCapacity];
        this._maximumCapacity = maximumCapacity;
    }

    public QualifiedNameArray() {
        this(10, Integer.MAX_VALUE);
    }

    @Override
    public final void clear() {
        this._size = this._readOnlyArraySize;
    }

    public final QualifiedName[] getArray() {
        if (this._array == null) {
            return null;
        }
        QualifiedName[] clonedArray = new QualifiedName[this._array.length];
        System.arraycopy(this._array, 0, clonedArray, 0, this._array.length);
        return clonedArray;
    }

    @Override
    public final void setReadOnlyArray(ValueArray readOnlyArray, boolean clear) {
        if (!(readOnlyArray instanceof QualifiedNameArray)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[]{readOnlyArray}));
        }
        this.setReadOnlyArray((QualifiedNameArray)readOnlyArray, clear);
    }

    public final void setReadOnlyArray(QualifiedNameArray readOnlyArray, boolean clear) {
        if (readOnlyArray != null) {
            this._readOnlyArray = readOnlyArray;
            this._readOnlyArraySize = readOnlyArray.getSize();
            if (clear) {
                this.clear();
            }
            this._array = this.getCompleteArray();
            this._size = this._readOnlyArraySize;
        }
    }

    public final QualifiedName[] getCompleteArray() {
        if (this._readOnlyArray == null) {
            return this.getArray();
        }
        QualifiedName[] ra = this._readOnlyArray.getCompleteArray();
        QualifiedName[] a = new QualifiedName[this._readOnlyArraySize + this._array.length];
        System.arraycopy(ra, 0, a, 0, this._readOnlyArraySize);
        return a;
    }

    public final QualifiedName getNext() {
        return this._size == this._array.length ? null : this._array[this._size];
    }

    public final void add(QualifiedName s) {
        if (this._size == this._array.length) {
            this.resize();
        }
        this._array[this._size++] = s;
    }

    protected final void resize() {
        if (this._size == this._maximumCapacity) {
            throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity"));
        }
        int newSize = this._size * 3 / 2 + 1;
        if (newSize > this._maximumCapacity) {
            newSize = this._maximumCapacity;
        }
        QualifiedName[] newArray = new QualifiedName[newSize];
        System.arraycopy(this._array, 0, newArray, 0, this._size);
        this._array = newArray;
    }
}

