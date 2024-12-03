/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.index.FieldInfo;

public interface IndexableFieldType {
    public boolean indexed();

    public boolean stored();

    public boolean tokenized();

    public boolean storeTermVectors();

    public boolean storeTermVectorOffsets();

    public boolean storeTermVectorPositions();

    public boolean storeTermVectorPayloads();

    public boolean omitNorms();

    public FieldInfo.IndexOptions indexOptions();

    public FieldInfo.DocValuesType docValueType();
}

