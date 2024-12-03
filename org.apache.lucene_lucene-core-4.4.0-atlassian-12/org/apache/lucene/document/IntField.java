/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo;

public final class IntField
extends Field {
    public static final FieldType TYPE_NOT_STORED = new FieldType();
    public static final FieldType TYPE_STORED;

    public IntField(String name, int value, Field.Store stored) {
        super(name, stored == Field.Store.YES ? TYPE_STORED : TYPE_NOT_STORED);
        this.fieldsData = value;
    }

    public IntField(String name, int value, FieldType type) {
        super(name, type);
        if (type.numericType() != FieldType.NumericType.INT) {
            throw new IllegalArgumentException("type.numericType() must be INT but got " + (Object)((Object)type.numericType()));
        }
        this.fieldsData = value;
    }

    static {
        TYPE_NOT_STORED.setIndexed(true);
        TYPE_NOT_STORED.setTokenized(true);
        TYPE_NOT_STORED.setOmitNorms(true);
        TYPE_NOT_STORED.setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);
        TYPE_NOT_STORED.setNumericType(FieldType.NumericType.INT);
        TYPE_NOT_STORED.freeze();
        TYPE_STORED = new FieldType();
        TYPE_STORED.setIndexed(true);
        TYPE_STORED.setTokenized(true);
        TYPE_STORED.setOmitNorms(true);
        TYPE_STORED.setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);
        TYPE_STORED.setNumericType(FieldType.NumericType.INT);
        TYPE_STORED.setStored(true);
        TYPE_STORED.freeze();
    }
}

