/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.document.FieldType$NumericType
 */
package org.apache.lucene.queryparser.flexible.standard.config;

import java.text.NumberFormat;
import org.apache.lucene.document.FieldType;

public class NumericConfig {
    private int precisionStep;
    private NumberFormat format;
    private FieldType.NumericType type;

    public NumericConfig(int precisionStep, NumberFormat format, FieldType.NumericType type) {
        this.setPrecisionStep(precisionStep);
        this.setNumberFormat(format);
        this.setType(type);
    }

    public int getPrecisionStep() {
        return this.precisionStep;
    }

    public void setPrecisionStep(int precisionStep) {
        this.precisionStep = precisionStep;
    }

    public NumberFormat getNumberFormat() {
        return this.format;
    }

    public FieldType.NumericType getType() {
        return this.type;
    }

    public void setType(FieldType.NumericType type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null!");
        }
        this.type = type;
    }

    public void setNumberFormat(NumberFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("format cannot be null!");
        }
        this.format = format;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof NumericConfig) {
            NumericConfig other = (NumericConfig)obj;
            if (this.precisionStep == other.precisionStep && this.type == other.type && (this.format == other.format || this.format.equals(other.format))) {
                return true;
            }
        }
        return false;
    }
}

