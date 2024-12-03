/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.NumericDocValuesField;

@Deprecated
public class ShortDocValuesField
extends NumericDocValuesField {
    public ShortDocValuesField(String name, short value) {
        super(name, value);
    }

    @Override
    public void setShortValue(short value) {
        this.setLongValue(value);
    }
}

