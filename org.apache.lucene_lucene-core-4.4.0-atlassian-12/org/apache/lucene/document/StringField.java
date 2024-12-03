/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo;

public final class StringField
extends Field {
    public static final FieldType TYPE_NOT_STORED = new FieldType();
    public static final FieldType TYPE_STORED = new FieldType();

    public StringField(String name, String value, Field.Store stored) {
        super(name, value, stored == Field.Store.YES ? TYPE_STORED : TYPE_NOT_STORED);
    }

    static {
        TYPE_NOT_STORED.setIndexed(true);
        TYPE_NOT_STORED.setOmitNorms(true);
        TYPE_NOT_STORED.setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);
        TYPE_NOT_STORED.setTokenized(false);
        TYPE_NOT_STORED.freeze();
        TYPE_STORED.setIndexed(true);
        TYPE_STORED.setOmitNorms(true);
        TYPE_STORED.setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);
        TYPE_STORED.setStored(true);
        TYPE_STORED.setTokenized(false);
        TYPE_STORED.freeze();
    }
}

