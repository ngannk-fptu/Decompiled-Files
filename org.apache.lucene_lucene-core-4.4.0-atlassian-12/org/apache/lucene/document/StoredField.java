/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.util.BytesRef;

public final class StoredField
extends Field {
    public static final FieldType TYPE = new FieldType();

    public StoredField(String name, byte[] value) {
        super(name, value, TYPE);
    }

    public StoredField(String name, byte[] value, int offset, int length) {
        super(name, value, offset, length, TYPE);
    }

    public StoredField(String name, BytesRef value) {
        super(name, value, TYPE);
    }

    public StoredField(String name, String value) {
        super(name, value, TYPE);
    }

    public StoredField(String name, int value) {
        super(name, TYPE);
        this.fieldsData = value;
    }

    public StoredField(String name, float value) {
        super(name, TYPE);
        this.fieldsData = Float.valueOf(value);
    }

    public StoredField(String name, long value) {
        super(name, TYPE);
        this.fieldsData = value;
    }

    public StoredField(String name, double value) {
        super(name, TYPE);
        this.fieldsData = value;
    }

    static {
        TYPE.setStored(true);
        TYPE.freeze();
    }
}

