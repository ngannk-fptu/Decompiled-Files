/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.mutable;

import org.apache.lucene.util.mutable.MutableValue;

public class MutableValueFloat
extends MutableValue {
    public float value;

    @Override
    public Object toObject() {
        return this.exists ? Float.valueOf(this.value) : null;
    }

    @Override
    public void copy(MutableValue source) {
        MutableValueFloat s = (MutableValueFloat)source;
        this.value = s.value;
        this.exists = s.exists;
    }

    @Override
    public MutableValue duplicate() {
        MutableValueFloat v = new MutableValueFloat();
        v.value = this.value;
        v.exists = this.exists;
        return v;
    }

    @Override
    public boolean equalsSameType(Object other) {
        MutableValueFloat b = (MutableValueFloat)other;
        return this.value == b.value && this.exists == b.exists;
    }

    @Override
    public int compareSameType(Object other) {
        MutableValueFloat b = (MutableValueFloat)other;
        int c = Float.compare(this.value, b.value);
        if (c != 0) {
            return c;
        }
        if (this.exists == b.exists) {
            return 0;
        }
        return this.exists ? 1 : -1;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }
}

