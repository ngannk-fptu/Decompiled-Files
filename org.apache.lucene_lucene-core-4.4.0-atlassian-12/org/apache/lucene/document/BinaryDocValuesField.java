/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.util.BytesRef;

public class BinaryDocValuesField
extends Field {
    public static final FieldType TYPE = new FieldType();

    public BinaryDocValuesField(String name, BytesRef value) {
        super(name, TYPE);
        this.fieldsData = value;
    }

    static {
        TYPE.setDocValueType(FieldInfo.DocValuesType.BINARY);
        TYPE.freeze();
    }
}

