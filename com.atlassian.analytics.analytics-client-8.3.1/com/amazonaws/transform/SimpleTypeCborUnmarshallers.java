/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.transform;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

@SdkProtectedApi
public class SimpleTypeCborUnmarshallers {

    public static class ShortCborUnmarshaller
    implements Unmarshaller<Short, JsonUnmarshallerContext> {
        private static final ShortCborUnmarshaller instance = new ShortCborUnmarshaller();

        @Override
        public Short unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            return unmarshallerContext.getJsonParser().getShortValue();
        }

        public static ShortCborUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class ByteBufferCborUnmarshaller
    implements Unmarshaller<ByteBuffer, JsonUnmarshallerContext> {
        private static final ByteBufferCborUnmarshaller instance = new ByteBufferCborUnmarshaller();

        @Override
        public ByteBuffer unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            return ByteBuffer.wrap(unmarshallerContext.getJsonParser().getBinaryValue());
        }

        public static ByteBufferCborUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class DateCborUnmarshaller
    implements Unmarshaller<Date, JsonUnmarshallerContext> {
        private static final DateCborUnmarshaller instance = new DateCborUnmarshaller();

        @Override
        public Date unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            return new Date(unmarshallerContext.getJsonParser().getLongValue());
        }

        public static DateCborUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class ByteCborUnmarshaller
    implements Unmarshaller<Byte, JsonUnmarshallerContext> {
        private static final ByteCborUnmarshaller instance = new ByteCborUnmarshaller();

        @Override
        public Byte unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            return unmarshallerContext.getJsonParser().getByteValue();
        }

        public static ByteCborUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class LongCborUnmarshaller
    implements Unmarshaller<Long, JsonUnmarshallerContext> {
        private static final LongCborUnmarshaller instance = new LongCborUnmarshaller();

        @Override
        public Long unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            return unmarshallerContext.getJsonParser().getLongValue();
        }

        public static LongCborUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class FloatCborUnmarshaller
    implements Unmarshaller<Float, JsonUnmarshallerContext> {
        private static final FloatCborUnmarshaller instance = new FloatCborUnmarshaller();

        @Override
        public Float unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            return Float.valueOf(unmarshallerContext.getJsonParser().getFloatValue());
        }

        public static FloatCborUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class BooleanCborUnmarshaller
    implements Unmarshaller<Boolean, JsonUnmarshallerContext> {
        private static final BooleanCborUnmarshaller instance = new BooleanCborUnmarshaller();

        @Override
        public Boolean unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            return unmarshallerContext.getJsonParser().getBooleanValue();
        }

        public static BooleanCborUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class BigDecimalCborUnmarshaller
    implements Unmarshaller<BigDecimal, JsonUnmarshallerContext> {
        private static final BigDecimalCborUnmarshaller instance = new BigDecimalCborUnmarshaller();

        @Override
        public BigDecimal unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            JsonParser parser = unmarshallerContext.getJsonParser();
            Unmarshaller<BigInteger, JsonUnmarshallerContext> bigIntegerUnmarshaller = unmarshallerContext.getUnmarshaller(BigInteger.class);
            JsonToken current = parser.getCurrentToken();
            if (current != JsonToken.START_ARRAY) {
                throw new SdkClientException("Invalid BigDecimal Format.");
            }
            parser.nextToken();
            int exponent = parser.getIntValue();
            parser.nextToken();
            BigInteger mantissa = bigIntegerUnmarshaller.unmarshall(unmarshallerContext);
            return new BigDecimal(mantissa, exponent);
        }

        public static BigDecimalCborUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class BigIntegerCborUnmarshaller
    implements Unmarshaller<BigInteger, JsonUnmarshallerContext> {
        private static final BigIntegerCborUnmarshaller instance = new BigIntegerCborUnmarshaller();

        @Override
        public BigInteger unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            JsonParser parser = unmarshallerContext.getJsonParser();
            JsonToken current = parser.getCurrentToken();
            if (current == JsonToken.VALUE_NUMBER_INT) {
                return parser.getBigIntegerValue();
            }
            if (current == JsonToken.VALUE_EMBEDDED_OBJECT) {
                Object embedded = parser.getEmbeddedObject();
                return new BigInteger((byte[])embedded);
            }
            throw new SdkClientException("Invalid BigInteger Format.");
        }

        public static BigIntegerCborUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class IntegerCborUnmarshaller
    implements Unmarshaller<Integer, JsonUnmarshallerContext> {
        private static final IntegerCborUnmarshaller instance = new IntegerCborUnmarshaller();

        @Override
        public Integer unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            return unmarshallerContext.getJsonParser().getIntValue();
        }

        public static IntegerCborUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class DoubleCborUnmarshaller
    implements Unmarshaller<Double, JsonUnmarshallerContext> {
        private static final DoubleCborUnmarshaller instance = new DoubleCborUnmarshaller();

        @Override
        public Double unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            return unmarshallerContext.getJsonParser().getDoubleValue();
        }

        public static DoubleCborUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class StringCborUnmarshaller
    implements Unmarshaller<String, JsonUnmarshallerContext> {
        private static final StringCborUnmarshaller instance = new StringCborUnmarshaller();

        @Override
        public String unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            return unmarshallerContext.readText();
        }

        public static StringCborUnmarshaller getInstance() {
            return instance;
        }
    }
}

