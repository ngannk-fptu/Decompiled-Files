/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.FieldCache;
import com.atlassian.lucene36.search.FieldComparator;
import com.atlassian.lucene36.search.FieldComparatorSource;
import com.atlassian.lucene36.util.StringHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Locale;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SortField
implements Serializable {
    public static final int SCORE = 0;
    public static final int DOC = 1;
    public static final int STRING = 3;
    public static final int INT = 4;
    public static final int FLOAT = 5;
    public static final int LONG = 6;
    public static final int DOUBLE = 7;
    public static final int SHORT = 8;
    public static final int CUSTOM = 9;
    public static final int BYTE = 10;
    public static final int STRING_VAL = 11;
    public static final SortField FIELD_SCORE = new SortField(null, 0);
    public static final SortField FIELD_DOC = new SortField(null, 1);
    private String field;
    private int type;
    private Locale locale;
    boolean reverse = false;
    private FieldCache.Parser parser;
    private FieldComparatorSource comparatorSource;
    private Object missingValue;

    public SortField(String field, int type) {
        this.initFieldType(field, type);
    }

    public SortField(String field, int type, boolean reverse) {
        this.initFieldType(field, type);
        this.reverse = reverse;
    }

    public SortField(String field, FieldCache.Parser parser) {
        this(field, parser, false);
    }

    public SortField(String field, FieldCache.Parser parser, boolean reverse) {
        if (parser instanceof FieldCache.IntParser) {
            this.initFieldType(field, 4);
        } else if (parser instanceof FieldCache.FloatParser) {
            this.initFieldType(field, 5);
        } else if (parser instanceof FieldCache.ShortParser) {
            this.initFieldType(field, 8);
        } else if (parser instanceof FieldCache.ByteParser) {
            this.initFieldType(field, 10);
        } else if (parser instanceof FieldCache.LongParser) {
            this.initFieldType(field, 6);
        } else if (parser instanceof FieldCache.DoubleParser) {
            this.initFieldType(field, 7);
        } else {
            throw new IllegalArgumentException("Parser instance does not subclass existing numeric parser from FieldCache (got " + parser + ")");
        }
        this.reverse = reverse;
        this.parser = parser;
    }

    public SortField(String field, Locale locale) {
        this.initFieldType(field, 3);
        this.locale = locale;
    }

    public SortField(String field, Locale locale, boolean reverse) {
        this.initFieldType(field, 3);
        this.locale = locale;
        this.reverse = reverse;
    }

    public SortField(String field, FieldComparatorSource comparator) {
        this.initFieldType(field, 9);
        this.comparatorSource = comparator;
    }

    public SortField(String field, FieldComparatorSource comparator, boolean reverse) {
        this.initFieldType(field, 9);
        this.reverse = reverse;
        this.comparatorSource = comparator;
    }

    public SortField setMissingValue(Object missingValue) {
        if (this.type != 10 && this.type != 8 && this.type != 4 && this.type != 5 && this.type != 6 && this.type != 7) {
            throw new IllegalArgumentException("Missing value only works for numeric types");
        }
        this.missingValue = missingValue;
        return this;
    }

    private void initFieldType(String field, int type) {
        this.type = type;
        if (field == null) {
            if (type != 0 && type != 1) {
                throw new IllegalArgumentException("field can only be null when type is SCORE or DOC");
            }
        } else {
            this.field = StringHelper.intern(field);
        }
    }

    public String getField() {
        return this.field;
    }

    public int getType() {
        return this.type;
    }

    public Locale getLocale() {
        return this.locale;
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
            case 0: {
                buffer.append("<score>");
                break;
            }
            case 1: {
                buffer.append("<doc>");
                break;
            }
            case 3: {
                buffer.append("<string: \"").append(this.field).append("\">");
                break;
            }
            case 11: {
                buffer.append("<string_val: \"").append(this.field).append("\">");
                break;
            }
            case 10: {
                buffer.append("<byte: \"").append(this.field).append("\">");
                break;
            }
            case 8: {
                buffer.append("<short: \"").append(this.field).append("\">");
                break;
            }
            case 4: {
                buffer.append("<int: \"").append(this.field).append("\">");
                break;
            }
            case 6: {
                buffer.append("<long: \"").append(this.field).append("\">");
                break;
            }
            case 5: {
                buffer.append("<float: \"").append(this.field).append("\">");
                break;
            }
            case 7: {
                buffer.append("<double: \"").append(this.field).append("\">");
                break;
            }
            case 9: {
                buffer.append("<custom:\"").append(this.field).append("\": ").append(this.comparatorSource).append('>');
                break;
            }
            default: {
                buffer.append("<???: \"").append(this.field).append("\">");
            }
        }
        if (this.locale != null) {
            buffer.append('(').append(this.locale).append(')');
        }
        if (this.parser != null) {
            buffer.append('(').append(this.parser).append(')');
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
        return other.field == this.field && other.type == this.type && other.reverse == this.reverse && (other.locale == null ? this.locale == null : other.locale.equals(this.locale)) && (other.comparatorSource == null ? this.comparatorSource == null : other.comparatorSource.equals(this.comparatorSource)) && (other.parser == null ? this.parser == null : other.parser.equals(this.parser));
    }

    public int hashCode() {
        int hash = this.type ^ 879060445 + Boolean.valueOf(this.reverse).hashCode() ^ 0xAF5998BB;
        if (this.field != null) {
            hash += this.field.hashCode() ^ 0xFF5685DD;
        }
        if (this.locale != null) {
            hash += this.locale.hashCode() ^ 0x8150815;
        }
        if (this.comparatorSource != null) {
            hash += this.comparatorSource.hashCode();
        }
        if (this.parser != null) {
            hash += this.parser.hashCode() ^ 0x3AAF56FF;
        }
        return hash;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.field != null) {
            this.field = StringHelper.intern(this.field);
        }
    }

    public FieldComparator<?> getComparator(int numHits, int sortPos) throws IOException {
        if (this.locale != null) {
            return new FieldComparator.StringComparatorLocale(numHits, this.field, this.locale);
        }
        switch (this.type) {
            case 0: {
                return new FieldComparator.RelevanceComparator(numHits);
            }
            case 1: {
                return new FieldComparator.DocComparator(numHits);
            }
            case 4: {
                return new FieldComparator.IntComparator(numHits, this.field, this.parser, (Integer)this.missingValue);
            }
            case 5: {
                return new FieldComparator.FloatComparator(numHits, this.field, this.parser, (Float)this.missingValue);
            }
            case 6: {
                return new FieldComparator.LongComparator(numHits, this.field, this.parser, (Long)this.missingValue);
            }
            case 7: {
                return new FieldComparator.DoubleComparator(numHits, this.field, this.parser, (Double)this.missingValue);
            }
            case 10: {
                return new FieldComparator.ByteComparator(numHits, this.field, this.parser, (Byte)this.missingValue);
            }
            case 8: {
                return new FieldComparator.ShortComparator(numHits, this.field, this.parser, (Short)this.missingValue);
            }
            case 9: {
                assert (this.comparatorSource != null);
                return this.comparatorSource.newComparator(this.field, numHits, sortPos, this.reverse);
            }
            case 3: {
                return new FieldComparator.StringOrdValComparator(numHits, this.field, sortPos, this.reverse);
            }
            case 11: {
                return new FieldComparator.StringValComparator(numHits, this.field);
            }
        }
        throw new IllegalStateException("Illegal sort type: " + this.type);
    }
}

