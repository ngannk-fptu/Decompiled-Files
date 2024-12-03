/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.mutable;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.mutable.MutableValue;

public class MutableValueStr
extends MutableValue {
    public BytesRef value = new BytesRef();

    @Override
    public Object toObject() {
        return this.exists ? this.value.utf8ToString() : null;
    }

    @Override
    public void copy(MutableValue source) {
        MutableValueStr s = (MutableValueStr)source;
        this.exists = s.exists;
        this.value.copyBytes(s.value);
    }

    @Override
    public MutableValue duplicate() {
        MutableValueStr v = new MutableValueStr();
        v.value.copyBytes(this.value);
        v.exists = this.exists;
        return v;
    }

    @Override
    public boolean equalsSameType(Object other) {
        MutableValueStr b = (MutableValueStr)other;
        return this.value.equals(b.value) && this.exists == b.exists;
    }

    @Override
    public int compareSameType(Object other) {
        MutableValueStr b = (MutableValueStr)other;
        int c = this.value.compareTo(b.value);
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
        return this.value.hashCode();
    }
}

