/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.NumericDocValuesField;

public class DoubleDocValuesField
extends NumericDocValuesField {
    public DoubleDocValuesField(String name, double value) {
        super(name, Double.doubleToRawLongBits(value));
    }

    @Override
    public void setDoubleValue(double value) {
        super.setLongValue(Double.doubleToRawLongBits(value));
    }

    @Override
    public void setLongValue(long value) {
        throw new IllegalArgumentException("cannot change value type from Double to Long");
    }
}

