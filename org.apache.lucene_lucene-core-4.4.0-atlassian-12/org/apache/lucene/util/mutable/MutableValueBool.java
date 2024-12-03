/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.mutable;

import org.apache.lucene.util.mutable.MutableValue;

public class MutableValueBool
extends MutableValue {
    public boolean value;

    @Override
    public Object toObject() {
        return this.exists ? Boolean.valueOf(this.value) : null;
    }

    @Override
    public void copy(MutableValue source) {
        MutableValueBool s = (MutableValueBool)source;
        this.value = s.value;
        this.exists = s.exists;
    }

    @Override
    public MutableValue duplicate() {
        MutableValueBool v = new MutableValueBool();
        v.value = this.value;
        v.exists = this.exists;
        return v;
    }

    @Override
    public boolean equalsSameType(Object other) {
        MutableValueBool b = (MutableValueBool)other;
        return this.value == b.value && this.exists == b.exists;
    }

    @Override
    public int compareSameType(Object other) {
        MutableValueBool b = (MutableValueBool)other;
        if (this.value != b.value) {
            return this.value ? 1 : 0;
        }
        if (this.exists == b.exists) {
            return 0;
        }
        return this.exists ? 1 : -1;
    }

    @Override
    public int hashCode() {
        return this.value ? 2 : (this.exists ? 1 : 0);
    }
}

