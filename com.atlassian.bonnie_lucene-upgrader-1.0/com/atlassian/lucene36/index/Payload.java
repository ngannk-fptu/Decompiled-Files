/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.util.ArrayUtil;
import java.io.Serializable;

public class Payload
implements Serializable,
Cloneable {
    protected byte[] data;
    protected int offset;
    protected int length;

    public Payload() {
    }

    public Payload(byte[] data) {
        this(data, 0, data.length);
    }

    public Payload(byte[] data, int offset, int length) {
        if (offset < 0 || offset + length > data.length) {
            throw new IllegalArgumentException();
        }
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public void setData(byte[] data) {
        this.setData(data, 0, data.length);
    }

    public void setData(byte[] data, int offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public byte[] getData() {
        return this.data;
    }

    public int getOffset() {
        return this.offset;
    }

    public int length() {
        return this.length;
    }

    public byte byteAt(int index) {
        if (0 <= index && index < this.length) {
            return this.data[this.offset + index];
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    public byte[] toByteArray() {
        byte[] retArray = new byte[this.length];
        System.arraycopy(this.data, this.offset, retArray, 0, this.length);
        return retArray;
    }

    public void copyTo(byte[] target, int targetOffset) {
        if (this.length > target.length + targetOffset) {
            throw new ArrayIndexOutOfBoundsException();
        }
        System.arraycopy(this.data, this.offset, target, targetOffset, this.length);
    }

    public Object clone() {
        try {
            Payload clone = (Payload)super.clone();
            if (this.offset == 0 && this.length == this.data.length) {
                clone.data = (byte[])this.data.clone();
            } else {
                clone.data = this.toByteArray();
                clone.offset = 0;
            }
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Payload) {
            Payload other = (Payload)obj;
            if (this.length == other.length) {
                for (int i = 0; i < this.length; ++i) {
                    if (this.data[this.offset + i] == other.data[other.offset + i]) continue;
                    return false;
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        return ArrayUtil.hashCode(this.data, this.offset, this.offset + this.length);
    }
}

