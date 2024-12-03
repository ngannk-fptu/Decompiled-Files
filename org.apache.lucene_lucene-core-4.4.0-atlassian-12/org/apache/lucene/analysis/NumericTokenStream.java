/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

public final class NumericTokenStream
extends TokenStream {
    public static final String TOKEN_TYPE_FULL_PREC = "fullPrecNumeric";
    public static final String TOKEN_TYPE_LOWER_PREC = "lowerPrecNumeric";
    private final NumericTermAttribute numericAtt = this.addAttribute(NumericTermAttribute.class);
    private final TypeAttribute typeAtt = this.addAttribute(TypeAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = this.addAttribute(PositionIncrementAttribute.class);
    private int valSize = 0;
    private final int precisionStep;

    public NumericTokenStream() {
        this(AttributeSource.AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, 4);
    }

    public NumericTokenStream(int precisionStep) {
        this(AttributeSource.AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, precisionStep);
    }

    public NumericTokenStream(AttributeSource.AttributeFactory factory, int precisionStep) {
        super(new NumericAttributeFactory(factory));
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >=1");
        }
        this.precisionStep = precisionStep;
        this.numericAtt.setShift(-precisionStep);
    }

    public NumericTokenStream setLongValue(long value) {
        this.valSize = 64;
        this.numericAtt.init(value, 64, this.precisionStep, -this.precisionStep);
        return this;
    }

    public NumericTokenStream setIntValue(int value) {
        this.valSize = 32;
        this.numericAtt.init(value, 32, this.precisionStep, -this.precisionStep);
        return this;
    }

    public NumericTokenStream setDoubleValue(double value) {
        this.valSize = 64;
        this.numericAtt.init(NumericUtils.doubleToSortableLong(value), 64, this.precisionStep, -this.precisionStep);
        return this;
    }

    public NumericTokenStream setFloatValue(float value) {
        this.valSize = 32;
        this.numericAtt.init(NumericUtils.floatToSortableInt(value), 32, this.precisionStep, -this.precisionStep);
        return this;
    }

    @Override
    public void reset() {
        if (this.valSize == 0) {
            throw new IllegalStateException("call set???Value() before usage");
        }
        this.numericAtt.setShift(-this.precisionStep);
    }

    @Override
    public boolean incrementToken() {
        if (this.valSize == 0) {
            throw new IllegalStateException("call set???Value() before usage");
        }
        this.clearAttributes();
        int shift = this.numericAtt.incShift();
        this.typeAtt.setType(shift == 0 ? TOKEN_TYPE_FULL_PREC : TOKEN_TYPE_LOWER_PREC);
        this.posIncrAtt.setPositionIncrement(shift == 0 ? 1 : 0);
        return shift < this.valSize;
    }

    public int getPrecisionStep() {
        return this.precisionStep;
    }

    public static final class NumericTermAttributeImpl
    extends AttributeImpl
    implements NumericTermAttribute,
    TermToBytesRefAttribute {
        private long value = 0L;
        private int valueSize = 0;
        private int shift = 0;
        private int precisionStep = 0;
        private BytesRef bytes = new BytesRef();

        @Override
        public BytesRef getBytesRef() {
            return this.bytes;
        }

        @Override
        public int fillBytesRef() {
            try {
                assert (this.valueSize == 64 || this.valueSize == 32);
                return this.valueSize == 64 ? NumericUtils.longToPrefixCoded(this.value, this.shift, this.bytes) : NumericUtils.intToPrefixCoded((int)this.value, this.shift, this.bytes);
            }
            catch (IllegalArgumentException iae) {
                this.bytes.length = 0;
                return 0;
            }
        }

        @Override
        public int getShift() {
            return this.shift;
        }

        @Override
        public void setShift(int shift) {
            this.shift = shift;
        }

        @Override
        public int incShift() {
            return this.shift += this.precisionStep;
        }

        @Override
        public long getRawValue() {
            return this.value & ((1L << this.shift) - 1L ^ 0xFFFFFFFFFFFFFFFFL);
        }

        @Override
        public int getValueSize() {
            return this.valueSize;
        }

        @Override
        public void init(long value, int valueSize, int precisionStep, int shift) {
            this.value = value;
            this.valueSize = valueSize;
            this.precisionStep = precisionStep;
            this.shift = shift;
        }

        @Override
        public void clear() {
        }

        @Override
        public void reflectWith(AttributeReflector reflector) {
            this.fillBytesRef();
            reflector.reflect(TermToBytesRefAttribute.class, "bytes", BytesRef.deepCopyOf(this.bytes));
            reflector.reflect(NumericTermAttribute.class, "shift", this.shift);
            reflector.reflect(NumericTermAttribute.class, "rawValue", this.getRawValue());
            reflector.reflect(NumericTermAttribute.class, "valueSize", this.valueSize);
        }

        @Override
        public void copyTo(AttributeImpl target) {
            NumericTermAttribute a = (NumericTermAttribute)((Object)target);
            a.init(this.value, this.valueSize, this.precisionStep, this.shift);
        }
    }

    private static final class NumericAttributeFactory
    extends AttributeSource.AttributeFactory {
        private final AttributeSource.AttributeFactory delegate;

        NumericAttributeFactory(AttributeSource.AttributeFactory delegate) {
            this.delegate = delegate;
        }

        @Override
        public AttributeImpl createAttributeInstance(Class<? extends Attribute> attClass) {
            if (CharTermAttribute.class.isAssignableFrom(attClass)) {
                throw new IllegalArgumentException("NumericTokenStream does not support CharTermAttribute.");
            }
            return this.delegate.createAttributeInstance(attClass);
        }
    }

    public static interface NumericTermAttribute
    extends Attribute {
        public int getShift();

        public long getRawValue();

        public int getValueSize();

        public void init(long var1, int var3, int var4, int var5);

        public void setShift(int var1);

        public int incShift();
    }
}

