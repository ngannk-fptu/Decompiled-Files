/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.mutable;

import org.apache.lucene.util.mutable.MutableValue;

public class MutableValueDouble
extends MutableValue {
    public double value;

    @Override
    public Object toObject() {
        return this.exists ? Double.valueOf(this.value) : null;
    }

    @Override
    public void copy(MutableValue source) {
        MutableValueDouble s = (MutableValueDouble)source;
        this.value = s.value;
        this.exists = s.exists;
    }

    @Override
    public MutableValue duplicate() {
        MutableValueDouble v = new MutableValueDouble();
        v.value = this.value;
        v.exists = this.exists;
        return v;
    }

    @Override
    public boolean equalsSameType(Object other) {
        MutableValueDouble b = (MutableValueDouble)other;
        return this.value == b.value && this.exists == b.exists;
    }

    @Override
    public int compareSameType(Object other) {
        MutableValueDouble b = (MutableValueDouble)other;
        int c = Double.compare(this.value, b.value);
        if (c != 0) {
            return c;
        }
        if (!this.exists) {
            return -1;
        }
        if (!b.exists) {
            return 1;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        long x = Double.doubleToLongBits(this.value);
        return (int)x + (int)(x >>> 32);
    }
}

