/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.NumericDocValues
 */
package org.apache.lucene.index.memory;

import org.apache.lucene.index.NumericDocValues;

class MemoryIndexNormDocValues
extends NumericDocValues {
    private final long value;

    public MemoryIndexNormDocValues(long value) {
        this.value = value;
    }

    public long get(int docID) {
        if (docID != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this.value;
    }
}

