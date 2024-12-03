/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import java.io.Reader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

public final class TextField
extends Field {
    public static final FieldType TYPE_NOT_STORED = new FieldType();
    public static final FieldType TYPE_STORED = new FieldType();

    public TextField(String name, Reader reader) {
        super(name, reader, TYPE_NOT_STORED);
    }

    public TextField(String name, String value, Field.Store store) {
        super(name, value, store == Field.Store.YES ? TYPE_STORED : TYPE_NOT_STORED);
    }

    public TextField(String name, TokenStream stream) {
        super(name, stream, TYPE_NOT_STORED);
    }

    static {
        TYPE_NOT_STORED.setIndexed(true);
        TYPE_NOT_STORED.setTokenized(true);
        TYPE_NOT_STORED.freeze();
        TYPE_STORED.setIndexed(true);
        TYPE_STORED.setTokenized(true);
        TYPE_STORED.setStored(true);
        TYPE_STORED.freeze();
    }
}

