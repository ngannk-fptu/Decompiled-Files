/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.StringHelper;

public class SortField {
    public static final SortField FIELD_SCORE = new SortField(null, Type.SCORE);
    public static final SortField FIELD_DOC = new SortField(null, Type.DOC);
    private String field;
    private Type type;
    boolean reverse = false;
    private FieldCache.Parser parser;
    private FieldComparatorSource comparatorSource;
    public Object missingValue = null;
    private Comparator<BytesRef> bytesComparator = BytesRef.getUTF8SortedAsUnicodeComparator();

    public SortField(String field, Type type) {
        this.initFieldType(field, type);
    }

    public SortField(String field, Type type, boolean reverse) {
        this.initFieldType(field, type);
        this.reverse = reverse;
    }

    public SortField(String field, FieldCache.Parser parser) {
        this(field, parser, false);
    }

    public SortField(String field, FieldCache.Parser parser, boolean reverse) {
        if (parser instanceof FieldCache.IntParser) {
            this.initFieldType(field, Type.INT);
        } else if (parser instanceof FieldCache.FloatParser) {
            this.initFieldType(field, Type.FLOAT);
        } else if (parser instanceof FieldCache.ShortParser) {
            this.initFieldType(field, Type.SHORT);
        } else if (parser instanceof FieldCache.ByteParser) {
            this.initFieldType(field, Type.BYTE);
        } else if (parser instanceof FieldCache.LongParser) {
            this.initFieldType(field, Type.LONG);
        } else if (parser instanceof FieldCache.DoubleParser) {
            this.initFieldType(field, Type.DOUBLE);
        } else {
            throw new IllegalArgumentException("Parser instance does not subclass existing numeric parser from FieldCache (got " + parser + ")");
        }
        this.reverse = reverse;
        this.parser = parser;
    }

    public SortField setMissingValue(Object missingValue) {
        if (this.type != Type.BYTE && this.type != Type.SHORT && this.type != Type.INT && this.type != Type.FLOAT && this.type != Type.LONG && this.type != Type.DOUBLE) {
            throw new IllegalArgumentException("Missing value only works for numeric types");
        }
        this.missingValue = missingValue;
        return this;
    }

    public SortField(String field, FieldComparatorSource comparator) {
        this.initFieldType(field, Type.CUSTOM);
        this.comparatorSource = comparator;
    }

    public SortField(String field, FieldComparatorSource comparator, boolean reverse) {
        this.initFieldType(field, Type.CUSTOM);
        this.reverse = reverse;
        this.comparatorSource = comparator;
    }

    private void initFieldType(String field, Type type) {
        this.type = type;
        if (field == null) {
            if (type != Type.SCORE && type != Type.DOC) {
                throw new IllegalArgumentException("field can only be null when type is SCORE or DOC");
            }
        } else {
            this.field = field;
        }
    }

    public String getField() {
        return this.field;
    }

    public Type getType() {
        return this.type;
    }

    public FieldCache.Parser getParser() {
        return this.parser;
    }

    public boolean getReverse() {
        return this.reverse;
    }

    public FieldComparatorSource getComparatorSource() {
        return this.comparatorSource;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        switch (this.type) {
            case SCORE: {
                buffer.append("<score>");
                break;
            }
            case DOC: {
                buffer.append("<doc>");
                break;
            }
            case STRING: {
                buffer.append("<string: \"").append(this.field).append("\">");
                break;
            }
            case STRING_VAL: {
                buffer.append("<string_val: \"").append(this.field).append("\">");
                break;
            }
            case BYTE: {
                buffer.append("<byte: \"").append(this.field).append("\">");
                break;
            }
            case SHORT: {
                buffer.append("<short: \"").append(this.field).append("\">");
                break;
            }
            case INT: {
                buffer.append("<int: \"").append(this.field).append("\">");
                break;
            }
            case LONG: {
                buffer.append("<long: \"").append(this.field).append("\">");
                break;
            }
            case FLOAT: {
                buffer.append("<float: \"").append(this.field).append("\">");
                break;
            }
            case DOUBLE: {
                buffer.append("<double: \"").append(this.field).append("\">");
                break;
            }
            case CUSTOM: {
                buffer.append("<custom:\"").append(this.field).append("\": ").append(this.comparatorSource).append('>');
                break;
            }
            case REWRITEABLE: {
                buffer.append("<rewriteable: \"").append(this.field).append("\">");
                break;
            }
            default: {
                buffer.append("<???: \"").append(this.field).append("\">");
            }
        }
        if (this.reverse) {
            buffer.append('!');
        }
        return buffer.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SortField)) {
            return false;
        }
        SortField other = (SortField)o;
        return StringHelper.equals(other.field, this.field) && other.type == this.type && other.reverse == this.reverse && (other.comparatorSource == null ? this.comparatorSource == null : other.comparatorSource.equals(this.comparatorSource));
    }

    public int hashCode() {
        int hash = this.type.hashCode() ^ 879060445 + Boolean.valueOf(this.reverse).hashCode() ^ 0xAF5998BB;
        if (this.field != null) {
            hash += this.field.hashCode() ^ 0xFF5685DD;
        }
        if (this.comparatorSource != null) {
            hash += this.comparatorSource.hashCode();
        }
        return hash;
    }

    public void setBytesComparator(Comparator<BytesRef> b) {
        this.bytesComparator = b;
    }

    public Comparator<BytesRef> getBytesComparator() {
        return this.bytesComparator;
    }

    public FieldComparator<?> getComparator(int numHits, int sortPos) throws IOException {
        switch (this.type) {
            case SCORE: {
                return new FieldComparator.RelevanceComparator(numHits);
            }
            case DOC: {
                return new FieldComparator.DocComparator(numHits);
            }
            case INT: {
                return new FieldComparator.IntComparator(numHits, this.field, this.parser, (Integer)this.missingValue);
            }
            case FLOAT: {
                return new FieldComparator.FloatComparator(numHits, this.field, this.parser, (Float)this.missingValue);
            }
            case LONG: {
                return new FieldComparator.LongComparator(numHits, this.field, this.parser, (Long)this.missingValue);
            }
            case DOUBLE: {
                return new FieldComparator.DoubleComparator(numHits, this.field, this.parser, (Double)this.missingValue);
            }
            case BYTE: {
                return new FieldComparator.ByteComparator(numHits, this.field, this.parser, (Byte)this.missingValue);
            }
            case SHORT: {
                return new FieldComparator.ShortComparator(numHits, this.field, this.parser, (Short)this.missingValue);
            }
            case CUSTOM: {
                assert (this.comparatorSource != null);
                return this.comparatorSource.newComparator(this.field, numHits, sortPos, this.reverse);
            }
            case STRING: {
                return new FieldComparator.TermOrdValComparator(numHits, this.field);
            }
            case STRING_VAL: {
                return new FieldComparator.TermValComparator(numHits, this.field);
            }
            case REWRITEABLE: {
                throw new IllegalStateException("SortField needs to be rewritten through Sort.rewrite(..) and SortField.rewrite(..)");
            }
        }
        throw new IllegalStateException("Illegal sort type: " + (Object)((Object)this.type));
    }

    public SortField rewrite(IndexSearcher searcher) throws IOException {
        return this;
    }

    public static enum Type {
        SCORE,
        DOC,
        STRING,
        INT,
        FLOAT,
        LONG,
        DOUBLE,
        SHORT,
        CUSTOM,
        BYTE,
        STRING_VAL,
        BYTES,
        REWRITEABLE;

    }
}

