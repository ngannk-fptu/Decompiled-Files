/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.PositionIncrementAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.TypeAttribute;
import com.atlassian.lucene36.util.AttributeSource;
import com.atlassian.lucene36.util.NumericUtils;

public final class NumericTokenStream
extends TokenStream {
    public static final String TOKEN_TYPE_FULL_PREC = "fullPrecNumeric";
    public static final String TOKEN_TYPE_LOWER_PREC = "lowerPrecNumeric";
    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);
    private final TypeAttribute typeAtt = this.addAttribute(TypeAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = this.addAttribute(PositionIncrementAttribute.class);
    private int shift = 0;
    private int valSize = 0;
    private final int precisionStep;
    private long value = 0L;

    public NumericTokenStream() {
        this(4);
    }

    public NumericTokenStream(int precisionStep) {
        this.precisionStep = precisionStep;
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >=1");
        }
    }

    public NumericTokenStream(AttributeSource source, int precisionStep) {
        super(source);
        this.precisionStep = precisionStep;
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >=1");
        }
    }

    public NumericTokenStream(AttributeSource.AttributeFactory factory, int precisionStep) {
        super(factory);
        this.precisionStep = precisionStep;
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >=1");
        }
    }

    public NumericTokenStream setLongValue(long value) {
        this.value = value;
        this.valSize = 64;
        this.shift = 0;
        return this;
    }

    public NumericTokenStream setIntValue(int value) {
        this.value = value;
        this.valSize = 32;
        this.shift = 0;
        return this;
    }

    public NumericTokenStream setDoubleValue(double value) {
        this.value = NumericUtils.doubleToSortableLong(value);
        this.valSize = 64;
        this.shift = 0;
        return this;
    }

    public NumericTokenStream setFloatValue(float value) {
        this.value = NumericUtils.floatToSortableInt(value);
        this.valSize = 32;
        this.shift = 0;
        return this;
    }

    public void reset() {
        if (this.valSize == 0) {
            throw new IllegalStateException("call set???Value() before usage");
        }
        this.shift = 0;
    }

    public boolean incrementToken() {
        if (this.valSize == 0) {
            throw new IllegalStateException("call set???Value() before usage");
        }
        if (this.shift >= this.valSize) {
            return false;
        }
        this.clearAttributes();
        switch (this.valSize) {
            case 64: {
                char[] buffer = this.termAtt.resizeBuffer(11);
                this.termAtt.setLength(NumericUtils.longToPrefixCoded(this.value, this.shift, buffer));
                break;
            }
            case 32: {
                char[] buffer = this.termAtt.resizeBuffer(6);
                this.termAtt.setLength(NumericUtils.intToPrefixCoded((int)this.value, this.shift, buffer));
                break;
            }
            default: {
                throw new IllegalArgumentException("valSize must be 32 or 64");
            }
        }
        this.typeAtt.setType(this.shift == 0 ? TOKEN_TYPE_FULL_PREC : TOKEN_TYPE_LOWER_PREC);
        this.posIncrAtt.setPositionIncrement(this.shift == 0 ? 1 : 0);
        this.shift += this.precisionStep;
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("(numeric,valSize=").append(this.valSize);
        sb.append(",precisionStep=").append(this.precisionStep).append(')');
        return sb.toString();
    }

    public int getPrecisionStep() {
        return this.precisionStep;
    }
}

