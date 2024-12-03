/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.NumericTokenStream;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

public class Field
implements IndexableField {
    protected final FieldType type;
    protected final String name;
    protected Object fieldsData;
    protected TokenStream tokenStream;
    private transient TokenStream internalTokenStream;
    protected float boost = 1.0f;

    protected Field(String name, FieldType type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.name = name;
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        this.type = type;
    }

    public Field(String name, Reader reader, FieldType type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (reader == null) {
            throw new NullPointerException("reader cannot be null");
        }
        if (type.stored()) {
            throw new IllegalArgumentException("fields with a Reader value cannot be stored");
        }
        if (type.indexed() && !type.tokenized()) {
            throw new IllegalArgumentException("non-tokenized fields must use String values");
        }
        this.name = name;
        this.fieldsData = reader;
        this.type = type;
    }

    public Field(String name, TokenStream tokenStream, FieldType type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (tokenStream == null) {
            throw new NullPointerException("tokenStream cannot be null");
        }
        if (!type.indexed() || !type.tokenized()) {
            throw new IllegalArgumentException("TokenStream fields must be indexed and tokenized");
        }
        if (type.stored()) {
            throw new IllegalArgumentException("TokenStream fields cannot be stored");
        }
        this.name = name;
        this.fieldsData = null;
        this.tokenStream = tokenStream;
        this.type = type;
    }

    public Field(String name, byte[] value, FieldType type) {
        this(name, value, 0, value.length, type);
    }

    public Field(String name, byte[] value, int offset, int length, FieldType type) {
        this(name, new BytesRef(value, offset, length), type);
    }

    public Field(String name, BytesRef bytes, FieldType type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (type.indexed()) {
            throw new IllegalArgumentException("Fields with BytesRef values cannot be indexed");
        }
        this.fieldsData = bytes;
        this.type = type;
        this.name = name;
    }

    public Field(String name, String value, FieldType type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        if (!type.stored() && !type.indexed()) {
            throw new IllegalArgumentException("it doesn't make sense to have a field that is neither indexed nor stored");
        }
        if (!type.indexed() && type.storeTermVectors()) {
            throw new IllegalArgumentException("cannot store term vector information for a field that is not indexed");
        }
        this.type = type;
        this.name = name;
        this.fieldsData = value;
    }

    @Override
    public String stringValue() {
        if (this.fieldsData instanceof String || this.fieldsData instanceof Number) {
            return this.fieldsData.toString();
        }
        return null;
    }

    @Override
    public Reader readerValue() {
        return this.fieldsData instanceof Reader ? (Reader)this.fieldsData : null;
    }

    public TokenStream tokenStreamValue() {
        return this.tokenStream;
    }

    public void setStringValue(String value) {
        if (!(this.fieldsData instanceof String)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to String");
        }
        this.fieldsData = value;
    }

    public void setReaderValue(Reader value) {
        if (!(this.fieldsData instanceof Reader)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Reader");
        }
        this.fieldsData = value;
    }

    public void setBytesValue(byte[] value) {
        this.setBytesValue(new BytesRef(value));
    }

    public void setBytesValue(BytesRef value) {
        if (!(this.fieldsData instanceof BytesRef)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to BytesRef");
        }
        if (this.type.indexed()) {
            throw new IllegalArgumentException("cannot set a BytesRef value on an indexed field");
        }
        this.fieldsData = value;
    }

    public void setByteValue(byte value) {
        if (!(this.fieldsData instanceof Byte)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Byte");
        }
        this.fieldsData = value;
    }

    public void setShortValue(short value) {
        if (!(this.fieldsData instanceof Short)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Short");
        }
        this.fieldsData = value;
    }

    public void setIntValue(int value) {
        if (!(this.fieldsData instanceof Integer)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Integer");
        }
        this.fieldsData = value;
    }

    public void setLongValue(long value) {
        if (!(this.fieldsData instanceof Long)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Long");
        }
        this.fieldsData = value;
    }

    public void setFloatValue(float value) {
        if (!(this.fieldsData instanceof Float)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Float");
        }
        this.fieldsData = Float.valueOf(value);
    }

    public void setDoubleValue(double value) {
        if (!(this.fieldsData instanceof Double)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Double");
        }
        this.fieldsData = value;
    }

    public void setTokenStream(TokenStream tokenStream) {
        if (!this.type.indexed() || !this.type.tokenized()) {
            throw new IllegalArgumentException("TokenStream fields must be indexed and tokenized");
        }
        if (this.type.numericType() != null) {
            throw new IllegalArgumentException("cannot set private TokenStream on numeric fields");
        }
        this.tokenStream = tokenStream;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public float boost() {
        return this.boost;
    }

    public void setBoost(float boost) {
        if (boost != 1.0f && (!this.type.indexed() || this.type.omitNorms())) {
            throw new IllegalArgumentException("You cannot set an index-time boost on an unindexed field, or one that omits norms");
        }
        this.boost = boost;
    }

    @Override
    public Number numericValue() {
        if (this.fieldsData instanceof Number) {
            return (Number)this.fieldsData;
        }
        return null;
    }

    @Override
    public BytesRef binaryValue() {
        if (this.fieldsData instanceof BytesRef) {
            return (BytesRef)this.fieldsData;
        }
        return null;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.type.toString());
        result.append('<');
        result.append(this.name);
        result.append(':');
        if (this.fieldsData != null) {
            result.append(this.fieldsData);
        }
        result.append('>');
        return result.toString();
    }

    @Override
    public FieldType fieldType() {
        return this.type;
    }

    @Override
    public TokenStream tokenStream(Analyzer analyzer) throws IOException {
        if (!this.fieldType().indexed()) {
            return null;
        }
        FieldType.NumericType numericType = this.fieldType().numericType();
        if (numericType != null) {
            if (!(this.internalTokenStream instanceof NumericTokenStream)) {
                this.internalTokenStream = new NumericTokenStream(this.type.numericPrecisionStep());
            }
            NumericTokenStream nts = (NumericTokenStream)this.internalTokenStream;
            Number val = (Number)this.fieldsData;
            switch (numericType) {
                case INT: {
                    nts.setIntValue(val.intValue());
                    break;
                }
                case LONG: {
                    nts.setLongValue(val.longValue());
                    break;
                }
                case FLOAT: {
                    nts.setFloatValue(val.floatValue());
                    break;
                }
                case DOUBLE: {
                    nts.setDoubleValue(val.doubleValue());
                    break;
                }
                default: {
                    throw new AssertionError((Object)"Should never get here");
                }
            }
            return this.internalTokenStream;
        }
        if (!this.fieldType().tokenized()) {
            if (this.stringValue() == null) {
                throw new IllegalArgumentException("Non-Tokenized Fields must have a String value");
            }
            if (!(this.internalTokenStream instanceof StringTokenStream)) {
                this.internalTokenStream = new StringTokenStream();
            }
            ((StringTokenStream)this.internalTokenStream).setValue(this.stringValue());
            return this.internalTokenStream;
        }
        if (this.tokenStream != null) {
            return this.tokenStream;
        }
        if (this.readerValue() != null) {
            return analyzer.tokenStream(this.name(), this.readerValue());
        }
        if (this.stringValue() != null) {
            return analyzer.tokenStream(this.name(), this.stringValue());
        }
        throw new IllegalArgumentException("Field must have either TokenStream, String, Reader or Number value");
    }

    @Deprecated
    public static final FieldType translateFieldType(Store store, Index index, TermVector termVector) {
        FieldType ft = new FieldType();
        ft.setStored(store == Store.YES);
        switch (index) {
            case ANALYZED: {
                ft.setIndexed(true);
                ft.setTokenized(true);
                break;
            }
            case ANALYZED_NO_NORMS: {
                ft.setIndexed(true);
                ft.setTokenized(true);
                ft.setOmitNorms(true);
                break;
            }
            case NOT_ANALYZED: {
                ft.setIndexed(true);
                ft.setTokenized(false);
                break;
            }
            case NOT_ANALYZED_NO_NORMS: {
                ft.setIndexed(true);
                ft.setTokenized(false);
                ft.setOmitNorms(true);
                break;
            }
        }
        switch (termVector) {
            case NO: {
                break;
            }
            case YES: {
                ft.setStoreTermVectors(true);
                break;
            }
            case WITH_POSITIONS: {
                ft.setStoreTermVectors(true);
                ft.setStoreTermVectorPositions(true);
                break;
            }
            case WITH_OFFSETS: {
                ft.setStoreTermVectors(true);
                ft.setStoreTermVectorOffsets(true);
                break;
            }
            case WITH_POSITIONS_OFFSETS: {
                ft.setStoreTermVectors(true);
                ft.setStoreTermVectorPositions(true);
                ft.setStoreTermVectorOffsets(true);
            }
        }
        ft.freeze();
        return ft;
    }

    @Deprecated
    public Field(String name, String value, Store store, Index index) {
        this(name, value, Field.translateFieldType(store, index, TermVector.NO));
    }

    @Deprecated
    public Field(String name, String value, Store store, Index index, TermVector termVector) {
        this(name, value, Field.translateFieldType(store, index, termVector));
    }

    @Deprecated
    public Field(String name, Reader reader) {
        this(name, reader, TermVector.NO);
    }

    @Deprecated
    public Field(String name, Reader reader, TermVector termVector) {
        this(name, reader, Field.translateFieldType(Store.NO, Index.ANALYZED, termVector));
    }

    @Deprecated
    public Field(String name, TokenStream tokenStream) {
        this(name, tokenStream, TermVector.NO);
    }

    @Deprecated
    public Field(String name, TokenStream tokenStream, TermVector termVector) {
        this(name, tokenStream, Field.translateFieldType(Store.NO, Index.ANALYZED, termVector));
    }

    @Deprecated
    public Field(String name, byte[] value) {
        this(name, value, Field.translateFieldType(Store.YES, Index.NO, TermVector.NO));
    }

    @Deprecated
    public Field(String name, byte[] value, int offset, int length) {
        this(name, value, offset, length, Field.translateFieldType(Store.YES, Index.NO, TermVector.NO));
    }

    @Deprecated
    public static enum TermVector {
        NO{

            @Override
            public boolean isStored() {
                return false;
            }

            @Override
            public boolean withPositions() {
                return false;
            }

            @Override
            public boolean withOffsets() {
                return false;
            }
        }
        ,
        YES{

            @Override
            public boolean isStored() {
                return true;
            }

            @Override
            public boolean withPositions() {
                return false;
            }

            @Override
            public boolean withOffsets() {
                return false;
            }
        }
        ,
        WITH_POSITIONS{

            @Override
            public boolean isStored() {
                return true;
            }

            @Override
            public boolean withPositions() {
                return true;
            }

            @Override
            public boolean withOffsets() {
                return false;
            }
        }
        ,
        WITH_OFFSETS{

            @Override
            public boolean isStored() {
                return true;
            }

            @Override
            public boolean withPositions() {
                return false;
            }

            @Override
            public boolean withOffsets() {
                return true;
            }
        }
        ,
        WITH_POSITIONS_OFFSETS{

            @Override
            public boolean isStored() {
                return true;
            }

            @Override
            public boolean withPositions() {
                return true;
            }

            @Override
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

    @Deprecated
    public static enum Index {
        NO{

            @Override
            public boolean isIndexed() {
                return false;
            }

            @Override
            public boolean isAnalyzed() {
                return false;
            }

            @Override
            public boolean omitNorms() {
                return true;
            }
        }
        ,
        ANALYZED{

            @Override
            public boolean isIndexed() {
                return true;
            }

            @Override
            public boolean isAnalyzed() {
                return true;
            }

            @Override
            public boolean omitNorms() {
                return false;
            }
        }
        ,
        NOT_ANALYZED{

            @Override
            public boolean isIndexed() {
                return true;
            }

            @Override
            public boolean isAnalyzed() {
                return false;
            }

            @Override
            public boolean omitNorms() {
                return false;
            }
        }
        ,
        NOT_ANALYZED_NO_NORMS{

            @Override
            public boolean isIndexed() {
                return true;
            }

            @Override
            public boolean isAnalyzed() {
                return false;
            }

            @Override
            public boolean omitNorms() {
                return true;
            }
        }
        ,
        ANALYZED_NO_NORMS{

            @Override
            public boolean isIndexed() {
                return true;
            }

            @Override
            public boolean isAnalyzed() {
                return true;
            }

            @Override
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

    public static enum Store {
        YES,
        NO;

    }

    static final class StringTokenStream
    extends TokenStream {
        private final CharTermAttribute termAttribute = this.addAttribute(CharTermAttribute.class);
        private final OffsetAttribute offsetAttribute = this.addAttribute(OffsetAttribute.class);
        private boolean used = false;
        private String value = null;

        StringTokenStream() {
        }

        void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean incrementToken() {
            if (this.used) {
                return false;
            }
            this.clearAttributes();
            this.termAttribute.append(this.value);
            this.offsetAttribute.setOffset(0, this.value.length());
            this.used = true;
            return true;
        }

        @Override
        public void end() {
            int finalOffset = this.value.length();
            this.offsetAttribute.setOffset(finalOffset, finalOffset);
        }

        @Override
        public void reset() {
            this.used = false;
        }

        @Override
        public void close() {
            this.value = null;
        }
    }
}

