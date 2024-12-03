/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.util.BytesRef;

@Deprecated
public class SortedBytesDocValuesField
extends SortedDocValuesField {
    public static final FieldType TYPE_FIXED_LEN = SortedDocValuesField.TYPE;
    public static final FieldType TYPE_VAR_LEN = SortedDocValuesField.TYPE;

    public SortedBytesDocValuesField(String name, BytesRef bytes) {
        super(name, bytes);
    }

    public SortedBytesDocValuesField(String name, BytesRef bytes, boolean isFixedLength) {
        super(name, bytes);
    }
}

