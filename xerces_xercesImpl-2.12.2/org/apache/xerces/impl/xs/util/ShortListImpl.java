/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.util;

import java.util.AbstractList;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSException;

public final class ShortListImpl
extends AbstractList
implements ShortList {
    public static final ShortListImpl EMPTY_LIST = new ShortListImpl(new short[0], 0);
    private final short[] fArray;
    private final int fLength;

    public ShortListImpl(short[] sArray, int n) {
        this.fArray = sArray;
        this.fLength = n;
    }

    @Override
    public int getLength() {
        return this.fLength;
    }

    @Override
    public boolean contains(short s) {
        for (int i = 0; i < this.fLength; ++i) {
            if (this.fArray[i] != s) continue;
            return true;
        }
        return false;
    }

    @Override
    public short item(int n) throws XSException {
        if (n < 0 || n >= this.fLength) {
            throw new XSException(2, null);
        }
        return this.fArray[n];
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof ShortList)) {
            return false;
        }
        ShortList shortList = (ShortList)object;
        if (this.fLength != shortList.getLength()) {
            return false;
        }
        for (int i = 0; i < this.fLength; ++i) {
            if (this.fArray[i] == shortList.item(i)) continue;
            return false;
        }
        return true;
    }

    public Object get(int n) {
        if (n >= 0 && n < this.fLength) {
            return new Short(this.fArray[n]);
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }

    @Override
    public int size() {
        return this.getLength();
    }
}

