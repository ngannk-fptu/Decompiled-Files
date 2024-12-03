/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.document;

import com.atlassian.lucene36.analysis.NumericTokenStream;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.document.AbstractField;
import com.atlassian.lucene36.document.Field;
import com.atlassian.lucene36.index.FieldInfo;
import java.io.Reader;

public final class NumericField
extends AbstractField {
    private transient NumericTokenStream numericTS;
    private DataType type;
    private final int precisionStep;

    public NumericField(String name) {
        this(name, 4, Field.Store.NO, true);
    }

    public NumericField(String name, Field.Store store, boolean index) {
        this(name, 4, store, index);
    }

    public NumericField(String name, int precisionStep) {
        this(name, precisionStep, Field.Store.NO, true);
    }

    public NumericField(String name, int precisionStep, Field.Store store, boolean index) {
        super(name, store, index ? Field.Index.ANALYZED_NO_NORMS : Field.Index.NO, Field.TermVector.NO);
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >=1");
        }
        this.precisionStep = precisionStep;
        this.setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);
    }

    public TokenStream tokenStreamValue() {
        if (!this.isIndexed()) {
            return null;
        }
        if (this.numericTS == null) {
            this.numericTS = new NumericTokenStream(this.precisionStep);
            if (this.fieldsData != null) {
                assert (this.type != null);
                Number val = (Number)this.fieldsData;
                switch (this.type) {
                    case INT: {
                        this.numericTS.setIntValue(val.intValue());
                        break;
                    }
                    case LONG: {
                        this.numericTS.setLongValue(val.longValue());
                        break;
                    }
                    case FLOAT: {
                        this.numericTS.setFloatValue(val.floatValue());
                        break;
                    }
                    case DOUBLE: {
                        this.numericTS.setDoubleValue(val.doubleValue());
                        break;
                    }
                    default: {
                        assert (false) : "Should never get here";
                        break;
                    }
                }
            }
        }
        return this.numericTS;
    }

    public byte[] getBinaryValue(byte[] result) {
        return null;
    }

    public Reader readerValue() {
        return null;
    }

    public String stringValue() {
        return this.fieldsData == null ? null : this.fieldsData.toString();
    }

    public Number getNumericValue() {
        return (Number)this.fieldsData;
    }

    public int getPrecisionStep() {
        return this.precisionStep;
    }

    public DataType getDataType() {
        return this.type;
    }

    public NumericField setLongValue(long value) {
        if (this.numericTS != null) {
            this.numericTS.setLongValue(value);
        }
        this.fieldsData = value;
        this.type = DataType.LONG;
        return this;
    }

    public NumericField setIntValue(int value) {
        if (this.numericTS != null) {
            this.numericTS.setIntValue(value);
        }
        this.fieldsData = value;
        this.type = DataType.INT;
        return this;
    }

    public NumericField setDoubleValue(double value) {
        if (this.numericTS != null) {
            this.numericTS.setDoubleValue(value);
        }
        this.fieldsData = value;
        this.type = DataType.DOUBLE;
        return this;
    }

    public NumericField setFloatValue(float value) {
        if (this.numericTS != null) {
            this.numericTS.setFloatValue(value);
        }
        this.fieldsData = Float.valueOf(value);
        this.type = DataType.FLOAT;
        return this;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum DataType {
        INT,
        LONG,
        FLOAT,
        DOUBLE;

    }
}

