/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

public final class FieldInfo {
    public final String name;
    public final int number;
    public boolean isIndexed;
    public boolean storeTermVector;
    public boolean omitNorms;
    public IndexOptions indexOptions;
    public boolean storePayloads;

    FieldInfo(String na, boolean tk, int nu, boolean storeTermVector, boolean omitNorms, boolean storePayloads, IndexOptions indexOptions) {
        this.name = na;
        this.isIndexed = tk;
        this.number = nu;
        if (this.isIndexed) {
            this.storeTermVector = storeTermVector;
            this.storePayloads = storePayloads;
            this.omitNorms = omitNorms;
            this.indexOptions = indexOptions;
        } else {
            this.storeTermVector = false;
            this.storePayloads = false;
            this.omitNorms = true;
            this.indexOptions = IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
        }
        assert (indexOptions == IndexOptions.DOCS_AND_FREQS_AND_POSITIONS || !storePayloads);
    }

    public Object clone() {
        return new FieldInfo(this.name, this.isIndexed, this.number, this.storeTermVector, this.omitNorms, this.storePayloads, this.indexOptions);
    }

    void update(boolean isIndexed, boolean storeTermVector, boolean omitNorms, boolean storePayloads, IndexOptions indexOptions) {
        if (this.isIndexed != isIndexed) {
            this.isIndexed = true;
        }
        if (isIndexed) {
            if (this.storeTermVector != storeTermVector) {
                this.storeTermVector = true;
            }
            if (this.storePayloads != storePayloads) {
                this.storePayloads = true;
            }
            if (this.omitNorms != omitNorms) {
                this.omitNorms = false;
            }
            if (this.indexOptions != indexOptions) {
                this.indexOptions = this.indexOptions.compareTo(indexOptions) < 0 ? this.indexOptions : indexOptions;
                this.storePayloads = false;
            }
        }
        assert (this.indexOptions == IndexOptions.DOCS_AND_FREQS_AND_POSITIONS || !this.storePayloads);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum IndexOptions {
        DOCS_ONLY,
        DOCS_AND_FREQS,
        DOCS_AND_FREQS_AND_POSITIONS;

    }
}

