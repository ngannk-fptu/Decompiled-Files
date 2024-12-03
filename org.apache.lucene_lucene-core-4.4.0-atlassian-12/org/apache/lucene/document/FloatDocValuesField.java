/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.NumericDocValuesField;

public class FloatDocValuesField
extends NumericDocValuesField {
    public FloatDocValuesField(String name, float value) {
        super(name, Float.floatToRawIntBits(value));
    }

    @Override
    public void setFloatValue(float value) {
        super.setLongValue(Float.floatToRawIntBits(value));
    }

    @Override
    public void setLongValue(long value) {
        throw new IllegalArgumentException("cannot change value type from Float to Long");
    }
}

