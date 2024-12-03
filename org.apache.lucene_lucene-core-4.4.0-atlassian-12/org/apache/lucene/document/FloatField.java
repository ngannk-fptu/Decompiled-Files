/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo;

public final class FloatField
extends Field {
    public static final FieldType TYPE_NOT_STORED = new FieldType();
    public static final FieldType TYPE_STORED;

    public FloatField(String name, float value, Field.Store stored) {
        super(name, stored == Field.Store.YES ? TYPE_STORED : TYPE_NOT_STORED);
        this.fieldsData = Float.valueOf(value);
    }

    public FloatField(String name, float value, FieldType type) {
        super(name, type);
        if (type.numericType() != FieldType.NumericType.FLOAT) {
            throw new IllegalArgumentException("type.numericType() must be FLOAT but got " + (Object)((Object)type.numericType()));
        }
        this.fieldsData = Float.valueOf(value);
    }

    static {
        TYPE_NOT_STORED.setIndexed(true);
        TYPE_NOT_STORED.setTokenized(true);
        TYPE_NOT_STORED.setOmitNorms(true);
        TYPE_NOT_STORED.setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);
        TYPE_NOT_STORED.setNumericType(FieldType.NumericType.FLOAT);
        TYPE_NOT_STORED.freeze();
        TYPE_STORED = new FieldType();
        TYPE_STORED.setIndexed(true);
        TYPE_STORED.setTokenized(true);
        TYPE_STORED.setOmitNorms(true);
        TYPE_STORED.setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);
        TYPE_STORED.setNumericType(FieldType.NumericType.FLOAT);
        TYPE_STORED.setStored(true);
        TYPE_STORED.freeze();
    }
}

