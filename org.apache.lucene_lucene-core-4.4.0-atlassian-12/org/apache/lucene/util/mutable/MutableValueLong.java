/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.mutable;

import org.apache.lucene.util.mutable.MutableValue;

public class MutableValueLong
extends MutableValue {
    public long value;

    @Override
    public Object toObject() {
        return this.exists ? Long.valueOf(this.value) : null;
    }

    @Override
    public void copy(MutableValue source) {
        MutableValueLong s = (MutableValueLong)source;
        this.exists = s.exists;
        this.value = s.value;
    }

    @Override
    public MutableValue duplicate() {
        MutableValueLong v = new MutableValueLong();
        v.value = this.value;
        v.exists = this.exists;
        return v;
    }

    @Override
    public boolean equalsSameType(Object other) {
        MutableValueLong b = (MutableValueLong)other;
        return this.value == b.value && this.exists == b.exists;
    }

    @Override
    public int compareSameType(Object other) {
        MutableValueLong b = (MutableValueLong)other;
        long bv = b.value;
        if (this.value < bv) {
            return -1;
        }
        if (this.value > bv) {
            return 1;
        }
        if (this.exists == b.exists) {
            return 0;
        }
        return this.exists ? 1 : -1;
    }

    @Override
    public int hashCode() {
        return (int)this.value + (int)(this.value >> 32);
    }
}

