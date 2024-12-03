/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexableFieldType;

public class FieldType
implements IndexableFieldType {
    private boolean indexed;
    private boolean stored;
    private boolean tokenized = true;
    private boolean storeTermVectors;
    private boolean storeTermVectorOffsets;
    private boolean storeTermVectorPositions;
    private boolean storeTermVectorPayloads;
    private boolean omitNorms;
    private FieldInfo.IndexOptions indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
    private NumericType numericType;
    private boolean frozen;
    private int numericPrecisionStep = 4;
    private FieldInfo.DocValuesType docValueType;

    public FieldType(FieldType ref) {
        this.indexed = ref.indexed();
        this.stored = ref.stored();
        this.tokenized = ref.tokenized();
        this.storeTermVectors = ref.storeTermVectors();
        this.storeTermVectorOffsets = ref.storeTermVectorOffsets();
        this.storeTermVectorPositions = ref.storeTermVectorPositions();
        this.storeTermVectorPayloads = ref.storeTermVectorPayloads();
        this.omitNorms = ref.omitNorms();
        this.indexOptions = ref.indexOptions();
        this.docValueType = ref.docValueType();
        this.numericType = ref.numericType();
    }

    public FieldType() {
    }

    private void checkIfFrozen() {
        if (this.frozen) {
            throw new IllegalStateException("this FieldType is already frozen and cannot be changed");
        }
    }

    public void freeze() {
        this.frozen = true;
    }

    @Override
    public boolean indexed() {
        return this.indexed;
    }

    public void setIndexed(boolean value) {
        this.checkIfFrozen();
        this.indexed = value;
    }

    @Override
    public boolean stored() {
        return this.stored;
    }

    public void setStored(boolean value) {
        this.checkIfFrozen();
        this.stored = value;
    }

    @Override
    public boolean tokenized() {
        return this.tokenized;
    }

    public void setTokenized(boolean value) {
        this.checkIfFrozen();
        this.tokenized = value;
    }

    @Override
    public boolean storeTermVectors() {
        return this.storeTermVectors;
    }

    public void setStoreTermVectors(boolean value) {
        this.checkIfFrozen();
        this.storeTermVectors = value;
    }

    @Override
    public boolean storeTermVectorOffsets() {
        return this.storeTermVectorOffsets;
    }

    public void setStoreTermVectorOffsets(boolean value) {
        this.checkIfFrozen();
        this.storeTermVectorOffsets = value;
    }

    @Override
    public boolean storeTermVectorPositions() {
        return this.storeTermVectorPositions;
    }

    public void setStoreTermVectorPositions(boolean value) {
        this.checkIfFrozen();
        this.storeTermVectorPositions = value;
    }

    @Override
    public boolean storeTermVectorPayloads() {
        return this.storeTermVectorPayloads;
    }

    public void setStoreTermVectorPayloads(boolean value) {
        this.checkIfFrozen();
        this.storeTermVectorPayloads = value;
    }

    @Override
    public boolean omitNorms() {
        return this.omitNorms;
    }

    public void setOmitNorms(boolean value) {
        this.checkIfFrozen();
        this.omitNorms = value;
    }

    @Override
    public FieldInfo.IndexOptions indexOptions() {
        return this.indexOptions;
    }

    public void setIndexOptions(FieldInfo.IndexOptions value) {
        this.checkIfFrozen();
        this.indexOptions = value;
    }

    public void setNumericType(NumericType type) {
        this.checkIfFrozen();
        this.numericType = type;
    }

    public NumericType numericType() {
        return this.numericType;
    }

    public void setNumericPrecisionStep(int precisionStep) {
        this.checkIfFrozen();
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >= 1 (got " + precisionStep + ")");
        }
        this.numericPrecisionStep = precisionStep;
    }

    public int numericPrecisionStep() {
        return this.numericPrecisionStep;
    }

    public final String toString() {
        StringBuilder result = new StringBuilder();
        if (this.stored()) {
            result.append("stored");
        }
        if (this.indexed()) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append("indexed");
            if (this.tokenized()) {
                result.append(",tokenized");
            }
            if (this.storeTermVectors()) {
                result.append(",termVector");
            }
            if (this.storeTermVectorOffsets()) {
                result.append(",termVectorOffsets");
            }
            if (this.storeTermVectorPositions()) {
                result.append(",termVectorPosition");
                if (this.storeTermVectorPayloads()) {
                    result.append(",termVectorPayloads");
                }
            }
            if (this.omitNorms()) {
                result.append(",omitNorms");
            }
            if (this.indexOptions != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
                result.append(",indexOptions=");
                result.append((Object)this.indexOptions);
            }
            if (this.numericType != null) {
                result.append(",numericType=");
                result.append((Object)this.numericType);
                result.append(",numericPrecisionStep=");
                result.append(this.numericPrecisionStep);
            }
        }
        if (this.docValueType != null) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append("docValueType=");
            result.append((Object)this.docValueType);
        }
        return result.toString();
    }

    @Override
    public FieldInfo.DocValuesType docValueType() {
        return this.docValueType;
    }

    public void setDocValueType(FieldInfo.DocValuesType type) {
        this.checkIfFrozen();
        this.docValueType = type;
    }

    public static enum NumericType {
        INT,
        LONG,
        FLOAT,
        DOUBLE;

    }
}

