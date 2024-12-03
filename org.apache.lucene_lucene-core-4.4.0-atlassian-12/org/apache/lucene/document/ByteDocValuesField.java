/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.NumericDocValuesField;

@Deprecated
public class ByteDocValuesField
extends NumericDocValuesField {
    public ByteDocValuesField(String name, byte value) {
        super(name, value);
    }

    @Override
    public void setByteValue(byte value) {
        this.setLongValue(value);
    }
}

