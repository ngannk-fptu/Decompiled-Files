/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.util.BytesRef;

@Deprecated
public class DerefBytesDocValuesField
extends BinaryDocValuesField {
    public static final FieldType TYPE_FIXED_LEN = BinaryDocValuesField.TYPE;
    public static final FieldType TYPE_VAR_LEN = BinaryDocValuesField.TYPE;

    public DerefBytesDocValuesField(String name, BytesRef bytes) {
        super(name, bytes);
    }

    public DerefBytesDocValuesField(String name, BytesRef bytes, boolean isFixedLength) {
        super(name, bytes);
    }
}

