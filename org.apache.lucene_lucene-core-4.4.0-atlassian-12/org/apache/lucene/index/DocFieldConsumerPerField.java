/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexableField;

abstract class DocFieldConsumerPerField {
    DocFieldConsumerPerField() {
    }

    abstract void processFields(IndexableField[] var1, int var2) throws IOException;

    abstract void abort();

    abstract FieldInfo getFieldInfo();
}

