/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.util.BytesRef;

public class SortedSetDocValuesField
extends Field {
    public static final FieldType TYPE = new FieldType();

    public SortedSetDocValuesField(String name, BytesRef bytes) {
        super(name, TYPE);
        this.fieldsData = bytes;
    }

    static {
        TYPE.setDocValueType(FieldInfo.DocValuesType.SORTED_SET);
        TYPE.freeze();
    }
}

