/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.index.IndexableFieldType;

public final class FieldInfo {
    public final String name;
    public final int number;
    private boolean indexed;
    private DocValuesType docValueType;
    private boolean storeTermVector;
    private DocValuesType normType;
    private boolean omitNorms;
    private IndexOptions indexOptions;
    private boolean storePayloads;
    private Map<String, String> attributes;

    public FieldInfo(String name, boolean indexed, int number, boolean storeTermVector, boolean omitNorms, boolean storePayloads, IndexOptions indexOptions, DocValuesType docValues, DocValuesType normsType, Map<String, String> attributes) {
        this.name = name;
        this.indexed = indexed;
        this.number = number;
        this.docValueType = docValues;
        if (indexed) {
            this.storeTermVector = storeTermVector;
            this.storePayloads = storePayloads;
            this.omitNorms = omitNorms;
            this.indexOptions = indexOptions;
            this.normType = !omitNorms ? normsType : null;
        } else {
            this.storeTermVector = false;
            this.storePayloads = false;
            this.omitNorms = false;
            this.indexOptions = null;
            this.normType = null;
        }
        this.attributes = attributes;
        assert (this.checkConsistency());
    }

    private boolean checkConsistency() {
        if (!this.indexed) {
            assert (!this.storeTermVector);
            assert (!this.storePayloads);
            assert (!this.omitNorms);
            assert (this.normType == null);
            assert (this.indexOptions == null);
        } else {
            assert (this.indexOptions != null);
            if (this.omitNorms) assert (this.normType == null);
            assert (this.indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0 || !this.storePayloads);
        }
        return true;
    }

    void update(IndexableFieldType ft) {
        this.update(ft.indexed(), false, ft.omitNorms(), false, ft.indexOptions());
    }

    void update(boolean indexed, boolean storeTermVector, boolean omitNorms, boolean storePayloads, IndexOptions indexOptions) {
        if (this.indexed != indexed) {
            this.indexed = true;
        }
        if (indexed) {
            if (this.storeTermVector != storeTermVector) {
                this.storeTermVector = true;
            }
            if (this.storePayloads != storePayloads) {
                this.storePayloads = true;
            }
            if (this.omitNorms != omitNorms) {
                this.omitNorms = true;
                this.normType = null;
            }
            if (this.indexOptions != indexOptions) {
                if (this.indexOptions == null) {
                    this.indexOptions = indexOptions;
                } else {
                    IndexOptions indexOptions2 = this.indexOptions = this.indexOptions.compareTo(indexOptions) < 0 ? this.indexOptions : indexOptions;
                }
                if (this.indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) < 0) {
                    this.storePayloads = false;
                }
            }
        }
        assert (this.checkConsistency());
    }

    void setDocValuesType(DocValuesType type) {
        if (this.docValueType != null && this.docValueType != type) {
            throw new IllegalArgumentException("cannot change DocValues type from " + (Object)((Object)this.docValueType) + " to " + (Object)((Object)type) + " for field \"" + this.name + "\"");
        }
        this.docValueType = type;
        assert (this.checkConsistency());
    }

    public IndexOptions getIndexOptions() {
        return this.indexOptions;
    }

    public boolean hasDocValues() {
        return this.docValueType != null;
    }

    public DocValuesType getDocValuesType() {
        return this.docValueType;
    }

    public DocValuesType getNormType() {
        return this.normType;
    }

    void setStoreTermVectors() {
        this.storeTermVector = true;
        assert (this.checkConsistency());
    }

    void setStorePayloads() {
        if (this.indexed && this.indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0) {
            this.storePayloads = true;
        }
        assert (this.checkConsistency());
    }

    void setNormValueType(DocValuesType type) {
        if (this.normType != null && this.normType != type) {
            throw new IllegalArgumentException("cannot change Norm type from " + (Object)((Object)this.normType) + " to " + (Object)((Object)type) + " for field \"" + this.name + "\"");
        }
        this.normType = type;
        assert (this.checkConsistency());
    }

    public boolean omitsNorms() {
        return this.omitNorms;
    }

    public boolean hasNorms() {
        return this.normType != null;
    }

    public boolean isIndexed() {
        return this.indexed;
    }

    public boolean hasPayloads() {
        return this.storePayloads;
    }

    public boolean hasVectors() {
        return this.storeTermVector;
    }

    public String getAttribute(String key) {
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.get(key);
    }

    public String putAttribute(String key, String value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, String>();
        }
        return this.attributes.put(key, value);
    }

    public Map<String, String> attributes() {
        return this.attributes;
    }

    public static enum DocValuesType {
        NUMERIC,
        BINARY,
        SORTED,
        SORTED_SET;

    }

    public static enum IndexOptions {
        DOCS_ONLY,
        DOCS_AND_FREQS,
        DOCS_AND_FREQS_AND_POSITIONS,
        DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;

    }
}

