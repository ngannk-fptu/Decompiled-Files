/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.util.BytesRef;

@Deprecated
public class StraightBytesDocValuesField
extends BinaryDocValuesField {
    public static final FieldType TYPE_FIXED_LEN = BinaryDocValuesField.TYPE;
    public static final FieldType TYPE_VAR_LEN = BinaryDocValuesField.TYPE;

    public StraightBytesDocValuesField(String name, BytesRef bytes) {
        super(name, bytes);
    }

    public StraightBytesDocValuesField(String name, BytesRef bytes, boolean isFixedLength) {
        super(name, bytes);
    }
}

