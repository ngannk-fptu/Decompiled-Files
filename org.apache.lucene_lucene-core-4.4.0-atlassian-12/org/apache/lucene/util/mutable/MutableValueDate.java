/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.mutable;

import java.util.Date;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueLong;

public class MutableValueDate
extends MutableValueLong {
    @Override
    public Object toObject() {
        return this.exists ? new Date(this.value) : null;
    }

    @Override
    public MutableValue duplicate() {
        MutableValueDate v = new MutableValueDate();
        v.value = this.value;
        v.exists = this.exists;
        return v;
    }
}

