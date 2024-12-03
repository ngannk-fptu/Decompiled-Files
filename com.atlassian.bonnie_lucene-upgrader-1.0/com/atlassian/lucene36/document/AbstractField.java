/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.document;

import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.document.Field;
import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.util.StringHelper;

public abstract class AbstractField
implements Fieldable {
    protected String name = "body";
    protected boolean storeTermVector = false;
    protected boolean storeOffsetWithTermVector = false;
    protected boolean storePositionWithTermVector = false;
    protected boolean omitNorms = false;
    protected boolean isStored = false;
    protected boolean isIndexed = true;
    protected boolean isTokenized = true;
    protected boolean isBinary = false;
    protected boolean lazy = false;
    protected FieldInfo.IndexOptions indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
    protected float boost = 1.0f;
    protected Object fieldsData = null;
    protected TokenStream tokenStream;
    protected int binaryLength;
    protected int binaryOffset;

    protected AbstractField() {
    }

    protected AbstractField(String name, Field.Store store, Field.Index index, Field.TermVector termVector) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        this.name = StringHelper.intern(name);
        this.isStored = store.isStored();
        this.isIndexed = index.isIndexed();
        this.isTokenized = index.isAnalyzed();
        this.omitNorms = index.omitNorms();
        this.isBinary = false;
        this.setStoreTermVector(termVector);
    }

    public void setBoost(float boost) {
        this.boost = boost;
    }

    public float getBoost() {
        return this.boost;
    }

    public String name() {
        return this.name;
    }

    protected void setStoreTermVector(Field.TermVector termVector) {
        this.storeTermVector = termVector.isStored();
        this.storePositionWithTermVector = termVector.withPositions();
        this.storeOffsetWithTermVector = termVector.withOffsets();
    }

    public final boolean isStored() {
        return this.isStored;
    }

    public final boolean isIndexed() {
        return this.isIndexed;
    }

    public final boolean isTokenized() {
        return this.isTokenized;
    }

    public final boolean isTermVectorStored() {
        return this.storeTermVector;
    }

    public boolean isStoreOffsetWithTermVector() {
        return this.storeOffsetWithTermVector;
    }

    public boolean isStorePositionWithTermVector() {
        return this.storePositionWithTermVector;
    }

    public final boolean isBinary() {
        return this.isBinary;
    }

    public byte[] getBinaryValue() {
        return this.getBinaryValue(null);
    }

    public byte[] getBinaryValue(byte[] result) {
        if (this.isBinary || this.fieldsData instanceof byte[]) {
            return (byte[])this.fieldsData;
        }
        return null;
    }

    public int getBinaryLength() {
        if (this.isBinary) {
            return this.binaryLength;
        }
        if (this.fieldsData instanceof byte[]) {
            return ((byte[])this.fieldsData).length;
        }
        return 0;
    }

    public int getBinaryOffset() {
        return this.binaryOffset;
    }

    public boolean getOmitNorms() {
        return this.omitNorms;
    }

    @Deprecated
    public boolean getOmitTermFreqAndPositions() {
        return this.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY;
    }

    public FieldInfo.IndexOptions getIndexOptions() {
        return this.indexOptions;
    }

    public void setOmitNorms(boolean omitNorms) {
        this.omitNorms = omitNorms;
    }

    @Deprecated
    public void setOmitTermFreqAndPositions(boolean omitTermFreqAndPositions) {
        this.indexOptions = omitTermFreqAndPositions ? FieldInfo.IndexOptions.DOCS_ONLY : FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
    }

    public void setIndexOptions(FieldInfo.IndexOptions indexOptions) {
        this.indexOptions = indexOptions;
    }

    public boolean isLazy() {
        return this.lazy;
    }

    public final String toString() {
        StringBuilder result = new StringBuilder();
        if (this.isStored) {
            result.append("stored");
        }
        if (this.isIndexed) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append("indexed");
        }
        if (this.isTokenized) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append("tokenized");
        }
        if (this.storeTermVector) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append("termVector");
        }
        if (this.storeOffsetWithTermVector) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append("termVectorOffsets");
        }
        if (this.storePositionWithTermVector) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append("termVectorPosition");
        }
        if (this.isBinary) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append("binary");
        }
        if (this.omitNorms) {
            result.append(",omitNorms");
        }
        if (this.indexOptions != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
            result.append(",indexOptions=");
            result.append((Object)this.indexOptions);
        }
        if (this.lazy) {
            result.append(",lazy");
        }
        result.append('<');
        result.append(this.name);
        result.append(':');
        if (this.fieldsData != null && !this.lazy) {
            result.append(this.fieldsData);
        }
        result.append('>');
        return result.toString();
    }
}

