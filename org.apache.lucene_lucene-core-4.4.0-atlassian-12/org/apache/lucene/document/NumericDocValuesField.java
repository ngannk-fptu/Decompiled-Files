/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo;

public class NumericDocValuesField
extends Field {
    public static final FieldType TYPE = new FieldType();

    public NumericDocValuesField(String name, long value) {
        super(name, TYPE);
        this.fieldsData = value;
    }

    static {
        TYPE.setDocValueType(FieldInfo.DocValuesType.NUMERIC);
        TYPE.freeze();
    }
}

