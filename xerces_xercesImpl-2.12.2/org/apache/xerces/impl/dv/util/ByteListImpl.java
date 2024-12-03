/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.util;

import java.util.AbstractList;
import org.apache.xerces.xs.XSException;
import org.apache.xerces.xs.datatypes.ByteList;

public class ByteListImpl
extends AbstractList
implements ByteList {
    protected final byte[] data;
    protected String canonical;

    public ByteListImpl(byte[] byArray) {
        this.data = byArray;
    }

    @Override
    public int getLength() {
        return this.data.length;
    }

    @Override
    public boolean contains(byte by) {
        for (int i = 0; i < this.data.length; ++i) {
            if (this.data[i] != by) continue;
            return true;
        }
        return false;
    }

    @Override
    public byte item(int n) throws XSException {
        if (n < 0 || n > this.data.length - 1) {
            throw new XSException(2, null);
        }
        return this.data[n];
    }

    public Object get(int n) {
        if (n >= 0 && n < this.data.length) {
            return new Byte(this.data[n]);
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }

    @Override
    public int size() {
        return this.getLength();
    }

    @Override
    public byte[] toByteArray() {
        byte[] byArray = new byte[this.data.length];
        System.arraycopy(this.data, 0, byArray, 0, this.data.length);
        return byArray;
    }
}

