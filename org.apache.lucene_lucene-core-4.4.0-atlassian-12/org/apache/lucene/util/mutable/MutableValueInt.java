/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.mutable;

import org.apache.lucene.util.mutable.MutableValue;

public class MutableValueInt
extends MutableValue {
    public int value;

    @Override
    public Object toObject() {
        return this.exists ? Integer.valueOf(this.value) : null;
    }

    @Override
    public void copy(MutableValue source) {
        MutableValueInt s = (MutableValueInt)source;
        this.value = s.value;
        this.exists = s.exists;
    }

    @Override
    public MutableValue duplicate() {
        MutableValueInt v = new MutableValueInt();
        v.value = this.value;
        v.exists = this.exists;
        return v;
    }

    @Override
    public boolean equalsSameType(Object other) {
        MutableValueInt b = (MutableValueInt)other;
        return this.value == b.value && this.exists == b.exists;
    }

    @Override
    public int compareSameType(Object other) {
        MutableValueInt b = (MutableValueInt)other;
        int ai = this.value;
        int bi = b.value;
        if (ai < bi) {
            return -1;
        }
        if (ai > bi) {
            return 1;
        }
        if (this.exists == b.exists) {
            return 0;
        }
        return this.exists ? 1 : -1;
    }

    @Override
    public int hashCode() {
        return (this.value >> 8) + (this.value >> 16);
    }
}

