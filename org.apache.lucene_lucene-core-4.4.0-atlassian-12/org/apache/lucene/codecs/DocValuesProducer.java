/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;

public abstract class DocValuesProducer
implements Closeable {
    protected DocValuesProducer() {
    }

    public abstract NumericDocValues getNumeric(FieldInfo var1) throws IOException;

    public abstract BinaryDocValues getBinary(FieldInfo var1) throws IOException;

    public abstract SortedDocValues getSorted(FieldInfo var1) throws IOException;

    public abstract SortedSetDocValues getSortedSet(FieldInfo var1) throws IOException;
}

