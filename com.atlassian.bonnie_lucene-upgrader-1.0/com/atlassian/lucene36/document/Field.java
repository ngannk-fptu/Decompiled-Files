/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.document;

import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.document.AbstractField;
import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.util.StringHelper;
import java.io.Reader;
import java.io.Serializable;

public final class Field
extends AbstractField
implements Fieldable,
Serializable {
    public String stringValue() {
        return this.fieldsData instanceof String ? (String)this.fieldsData : null;
    }

    public Reader readerValue() {
        return this.fieldsData instanceof Reader ? (Reader)this.fieldsData : null;
    }

    public TokenStream tokenStreamValue() {
        return this.tokenStream;
    }

    public void setValue(String value) {
        if (this.isBinary) {
            throw new IllegalArgumentException("cannot set a String value on a binary field");
        }
        this.fieldsData = value;
    }

    public void setValue(Reader value) {
        if (this.isBinary) {
            throw new IllegalArgumentException("cannot set a Reader value on a binary field");
        }
        if (this.isStored) {
            throw new IllegalArgumentException("cannot set a Reader value on a stored field");
        }
        this.fieldsData = value;
    }

    public void setValue(byte[] value) {
        if (!this.isBinary) {
            throw new IllegalArgumentException("cannot set a byte[] value on a non-binary field");
        }
        this.fieldsData = value;
        this.binaryLength = value.length;
        this.binaryOffset = 0;
    }

    public void setValue(byte[] value, int offset, int length) {
        if (!this.isBinary) {
            throw new IllegalArgumentException("cannot set a byte[] value on a non-binary field");
        }
        this.fieldsData = value;
        this.binaryLength = length;
        this.binaryOffset = offset;
    }

    public void setTokenStream(TokenStream tokenStream) {
        this.isIndexed = true;
        this.isTokenized = true;
        this.tokenStream = tokenStream;
    }

    public Field(String name, String value, Store store, Index index) {
        this(name, value, store, index, TermVector.NO);
    }

    public Field(String name, String value, Store store, Index index, TermVector termVector) {
        this(name, true, value, store, index, termVector);
    }

    public Field(String name, boolean internName, String value, Store store, Index index, TermVector termVector) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        if (index == Index.NO && store == Store.NO) {
            throw new IllegalArgumentException("it doesn't make sense to have a field that is neither indexed nor stored");
        }
        if (index == Index.NO && termVector != TermVector.NO) {
            throw new IllegalArgumentException("cannot store term vector information for a field that is not indexed");
        }
        if (internName) {
            name = StringHelper.intern(name);
        }
        this.name = name;
        this.fieldsData = value;
        this.isStored = store.isStored();
        this.isIndexed = index.isIndexed();
        this.isTokenized = index.isAnalyzed();
        this.omitNorms = index.omitNorms();
        if (index == Index.NO) {
            this.indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
        }
        this.isBinary = false;
        this.setStoreTermVector(termVector);
    }

    public Field(String name, Reader reader) {
        this(name, reader, TermVector.NO);
    }

    public Field(String name, Reader reader, TermVector termVector) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        if (reader == null) {
            throw new NullPointerException("reader cannot be null");
        }
        this.name = StringHelper.intern(name);
        this.fieldsData = reader;
        this.isStored = false;
        this.isIndexed = true;
        this.isTokenized = true;
        this.isBinary = false;
        this.setStoreTermVector(termVector);
    }

    public Field(String name, TokenStream tokenStream) {
        this(name, tokenStream, TermVector.NO);
    }

    public Field(String name, TokenStream tokenStream, TermVector termVector) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        if (tokenStream == null) {
            throw new NullPointerException("tokenStream cannot be null");
        }
        this.name = StringHelper.intern(name);
        this.fieldsData = null;
        this.tokenStream = tokenStream;
        this.isStored = false;
        this.isIndexed = true;
        this.isTokenized = true;
        this.isBinary = false;
        this.setStoreTermVector(termVector);
    }

    @Deprecated
    public Field(String name, byte[] value, Store store) {
        this(name, value, 0, value.length);
        if (store == Store.NO) {
            throw new IllegalArgumentException("binary values can't be unstored");
        }
    }

    public Field(String name, byte[] value) {
        this(name, value, 0, value.length);
    }

    @Deprecated
    public Field(String name, byte[] value, int offset, int length, Store store) {
        this(name, value, offset, length);
        if (store == Store.NO) {
            throw new IllegalArgumentException("binary values can't be unstored");
        }
    }

    public Field(String name, byte[] value, int offset, int length) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        this.name = StringHelper.intern(name);
        this.fieldsData = value;
        this.isStored = true;
        this.isIndexed = false;
        this.isTokenized = false;
        this.indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
        this.omitNorms = true;
        this.isBinary = true;
        this.binaryLength = length;
        this.binaryOffset = offset;
        this.setStoreTermVector(TermVector.NO);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TermVector {
        NO{

            public boolean isStored() {
                return false;
            }

            public boolean withPositions() {
                return false;
            }

            public boolean withOffsets() {
                return false;
            }
        }
        ,
        YES{

            public boolean isStored() {
                return true;
            }

            public boolean withPositions() {
                return false;
            }

            public boolean withOffsets() {
                return false;
            }
        }
        ,
        WITH_POSITIONS{

            public boolean isStored() {
                return true;
            }

            public boolean withPositions() {
                return true;
            }

            public boolean withOffsets() {
                return false;
            }
        }
        ,
        WITH_OFFSETS{

            public boolean isStored() {
                return true;
            }

            public boolean withPositions() {
                return false;
            }

            public boolean withOffsets() {
                return true;
            }
        }
        ,
        WITH_POSITIONS_OFFSETS{

            public boolean isStored() {
                return true;
            }

            public boolean withPositions() {
                return true;
            }

            public boolean withOffsets() {
                return true;
            }
        };


        public static TermVector toTermVector(boolean stored, boolean withOffsets, boolean withPositions) {
            if (!stored) {
                return NO;
            }
            if (withOffsets) {
                if (withPositions) {
                    return WITH_POSITIONS_OFFSETS;
                }
                return WITH_OFFSETS;
            }
            if (withPositions) {
                return WITH_POSITIONS;
            }
            return YES;
        }

        public abstract boolean isStored();

        public abstract boolean withPositions();

        public abstract boolean withOffsets();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Index {
        NO{

            public boolean isIndexed() {
                return false;
            }

            public boolean isAnalyzed() {
                return false;
            }

            public boolean omitNorms() {
                return true;
            }
        }
        ,
        ANALYZED{

            public boolean isIndexed() {
                return true;
            }

            public boolean isAnalyzed() {
                return true;
            }

            public boolean omitNorms() {
                return false;
            }
        }
        ,
        NOT_ANALYZED{

            public boolean isIndexed() {
                return true;
            }

            public boolean isAnalyzed() {
                return false;
            }

            public boolean omitNorms() {
                return false;
            }
        }
        ,
        NOT_ANALYZED_NO_NORMS{

            public boolean isIndexed() {
                return true;
            }

            public boolean isAnalyzed() {
                return false;
            }

            public boolean omitNorms() {
                return true;
            }
        }
        ,
        ANALYZED_NO_NORMS{

            public boolean isIndexed() {
                return true;
            }

            public boolean isAnalyzed() {
                return true;
            }

            public boolean omitNorms() {
                return true;
            }
        };


        public static Index toIndex(boolean indexed, boolean analyzed) {
            return Index.toIndex(indexed, analyzed, false);
        }

        public static Index toIndex(boolean indexed, boolean analyzed, boolean omitNorms) {
            if (!indexed) {
                return NO;
            }
            if (!omitNorms) {
                if (analyzed) {
                    return ANALYZED;
                }
                return NOT_ANALYZED;
            }
            if (analyzed) {
                return ANALYZED_NO_NORMS;
            }
            return NOT_ANALYZED_NO_NORMS;
        }

        public abstract boolean isIndexed();

        public abstract boolean isAnalyzed();

        public abstract boolean omitNorms();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Store {
        YES{

            public boolean isStored() {
                return true;
            }
        }
        ,
        NO{

            public boolean isStored() {
                return false;
            }
        };


        public abstract boolean isStored();
    }
}

