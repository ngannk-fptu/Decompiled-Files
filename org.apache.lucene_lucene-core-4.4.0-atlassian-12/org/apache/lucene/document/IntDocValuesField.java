/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.NumericDocValuesField;

@Deprecated
public class IntDocValuesField
extends NumericDocValuesField {
    public IntDocValuesField(String name, int value) {
        super(name, value);
    }

    @Override
    public void setIntValue(int value) {
        this.setLongValue(value);
    }
}

