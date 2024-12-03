/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.transform;

import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

public class SimpleTypeIonUnmarshallers {

    public static class ShortIonUnmarshaller
    implements Unmarshaller<Short, JsonUnmarshallerContext> {
        private static final ShortIonUnmarshaller instance = new ShortIonUnmarshaller();

        @Override
        public Short unmarshall(JsonUnmarshallerContext context) throws Exception {
            return context.getJsonParser().getShortValue();
        }

        public static ShortIonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class ByteBufferIonUnmarshaller
    implements Unmarshaller<ByteBuffer, JsonUnmarshallerContext> {
        private static final ByteBufferIonUnmarshaller instance = new ByteBufferIonUnmarshaller();

        @Override
        public ByteBuffer unmarshall(JsonUnmarshallerContext context) throws Exception {
            return (ByteBuffer)context.getJsonParser().getEmbeddedObject();
        }

        public static ByteBufferIonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class DateIonUnmarshaller
    implements Unmarshaller<Date, JsonUnmarshallerContext> {
        private static final DateIonUnmarshaller instance = new DateIonUnmarshaller();

        @Override
        public Date unmarshall(JsonUnmarshallerContext context) throws Exception {
            return (Date)context.getJsonParser().getEmbeddedObject();
        }

        public static DateIonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class ByteIonUnmarshaller
    implements Unmarshaller<Byte, JsonUnmarshallerContext> {
        private static final ByteIonUnmarshaller instance = new ByteIonUnmarshaller();

        @Override
        public Byte unmarshall(JsonUnmarshallerContext context) throws Exception {
            return context.getJsonParser().getByteValue();
        }

        public static ByteIonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class LongIonUnmarshaller
    implements Unmarshaller<Long, JsonUnmarshallerContext> {
        private static final LongIonUnmarshaller instance = new LongIonUnmarshaller();

        @Override
        public Long unmarshall(JsonUnmarshallerContext context) throws Exception {
            return context.getJsonParser().getLongValue();
        }

        public static LongIonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class FloatIonUnmarshaller
    implements Unmarshaller<Float, JsonUnmarshallerContext> {
        private static final FloatIonUnmarshaller instance = new FloatIonUnmarshaller();

        @Override
        public Float unmarshall(JsonUnmarshallerContext context) throws Exception {
            return Float.valueOf(context.getJsonParser().getFloatValue());
        }

        public static FloatIonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class BooleanIonUnmarshaller
    implements Unmarshaller<Boolean, JsonUnmarshallerContext> {
        private static final BooleanIonUnmarshaller instance = new BooleanIonUnmarshaller();

        @Override
        public Boolean unmarshall(JsonUnmarshallerContext context) throws Exception {
            return context.getJsonParser().getBooleanValue();
        }

        public static BooleanIonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class BigDecimalIonUnmarshaller
    implements Unmarshaller<BigDecimal, JsonUnmarshallerContext> {
        private static final BigDecimalIonUnmarshaller instance = new BigDecimalIonUnmarshaller();

        @Override
        public BigDecimal unmarshall(JsonUnmarshallerContext context) throws Exception {
            return context.getJsonParser().getDecimalValue();
        }

        public static BigDecimalIonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class BigIntegerIonUnmarshaller
    implements Unmarshaller<BigInteger, JsonUnmarshallerContext> {
        private static final BigIntegerIonUnmarshaller instance = new BigIntegerIonUnmarshaller();

        @Override
        public BigInteger unmarshall(JsonUnmarshallerContext context) throws Exception {
            return context.getJsonParser().getBigIntegerValue();
        }

        public static BigIntegerIonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class IntegerIonUnmarshaller
    implements Unmarshaller<Integer, JsonUnmarshallerContext> {
        private static final IntegerIonUnmarshaller instance = new IntegerIonUnmarshaller();

        @Override
        public Integer unmarshall(JsonUnmarshallerContext context) throws Exception {
            return context.getJsonParser().getIntValue();
        }

        public static IntegerIonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class DoubleIonUnmarshaller
    implements Unmarshaller<Double, JsonUnmarshallerContext> {
        private static final DoubleIonUnmarshaller instance = new DoubleIonUnmarshaller();

        @Override
        public Double unmarshall(JsonUnmarshallerContext context) throws Exception {
            return context.getJsonParser().getDoubleValue();
        }

        public static DoubleIonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class StringIonUnmarshaller
    implements Unmarshaller<String, JsonUnmarshallerContext> {
        private static final StringIonUnmarshaller instance = new StringIonUnmarshaller();

        @Override
        public String unmarshall(JsonUnmarshallerContext context) throws Exception {
            return context.readText();
        }

        public static StringIonUnmarshaller getInstance() {
            return instance;
        }
    }
}

