/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.index.StoredFieldVisitor;

public abstract class StoredFieldsReader
implements Cloneable,
Closeable {
    protected StoredFieldsReader() {
    }

    public abstract void visitDocument(int var1, StoredFieldVisitor var2) throws IOException;

    public abstract StoredFieldsReader clone();
}

