/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.transform;

import com.amazonaws.SdkClientException;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.amazonaws.util.Base64;
import com.amazonaws.util.DateUtils;
import com.amazonaws.util.TimestampFormat;
import com.fasterxml.jackson.core.JsonToken;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Date;

public class SimpleTypeJsonUnmarshallers {

    public static class ShortJsonUnmarshaller
    implements Unmarshaller<Short, JsonUnmarshallerContext> {
        private static final ShortJsonUnmarshaller instance = new ShortJsonUnmarshaller();

        @Override
        public Short unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            String shortString = unmarshallerContext.readText();
            return shortString == null ? null : Short.valueOf(shortString);
        }

        public static ShortJsonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class CharacterJsonUnmarshaller
    implements Unmarshaller<Character, JsonUnmarshallerContext> {
        private static final CharacterJsonUnmarshaller instance = new CharacterJsonUnmarshaller();

        @Override
        public Character unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            String charString = unmarshallerContext.readText();
            if (charString == null) {
                return null;
            }
            if ((charString = charString.trim()).isEmpty() || charString.length() > 1) {
                throw new SdkClientException("'" + charString + "' cannot be converted to Character");
            }
            return Character.valueOf(charString.charAt(0));
        }

        public static CharacterJsonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class ByteBufferJsonUnmarshaller
    implements Unmarshaller<ByteBuffer, JsonUnmarshallerContext> {
        private static final ByteBufferJsonUnmarshaller instance = new ByteBufferJsonUnmarshaller();

        @Override
        public ByteBuffer unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            String base64EncodedString = unmarshallerContext.readText();
            if (base64EncodedString == null) {
                return null;
            }
            byte[] decodedBytes = Base64.decode(base64EncodedString);
            return ByteBuffer.wrap(decodedBytes);
        }

        public static ByteBufferJsonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class DateJsonUnmarshallerFactory
    implements Unmarshaller<Date, JsonUnmarshallerContext> {
        private final String dateFormatType;

        private DateJsonUnmarshallerFactory(String dateFormatType) {
            this.dateFormatType = dateFormatType;
        }

        @Override
        public Date unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            String dateString = unmarshallerContext.readText();
            if (dateString == null) {
                return null;
            }
            try {
                if (TimestampFormat.RFC_822.getFormat().equals(this.dateFormatType)) {
                    return DateUtils.parseRFC822Date(dateString);
                }
                if (TimestampFormat.UNIX_TIMESTAMP.getFormat().equals(this.dateFormatType)) {
                    return DateUtils.parseServiceSpecificDate(dateString);
                }
                if (TimestampFormat.UNIX_TIMESTAMP_IN_MILLIS.getFormat().equals(this.dateFormatType)) {
                    return DateUtils.parseUnixTimestampInMillis(dateString);
                }
                return DateUtils.parseISO8601Date(dateString);
            }
            catch (Exception exception) {
                return DateJsonUnmarshaller.getInstance().unmarshall(unmarshallerContext);
            }
        }

        public static DateJsonUnmarshallerFactory getInstance(String dateFormatType) {
            return new DateJsonUnmarshallerFactory(dateFormatType);
        }
    }

    public static class DateJsonUnmarshaller
    implements Unmarshaller<Date, JsonUnmarshallerContext> {
        private static final DateJsonUnmarshaller instance = new DateJsonUnmarshaller();

        @Override
        public Date unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            if (unmarshallerContext.getCurrentToken() == JsonToken.VALUE_STRING) {
                return DateUtils.parseISO8601Date(unmarshallerContext.readText());
            }
            return DateUtils.parseServiceSpecificDate(unmarshallerContext.readText());
        }

        public static DateJsonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class ByteJsonUnmarshaller
    implements Unmarshaller<Byte, JsonUnmarshallerContext> {
        private static final ByteJsonUnmarshaller instance = new ByteJsonUnmarshaller();

        @Override
        public Byte unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            String byteString = unmarshallerContext.readText();
            return byteString == null ? null : Byte.valueOf(byteString);
        }

        public static ByteJsonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class LongJsonUnmarshaller
    implements Unmarshaller<Long, JsonUnmarshallerContext> {
        private static final LongJsonUnmarshaller instance = new LongJsonUnmarshaller();

        @Override
        public Long unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            String longString = unmarshallerContext.readText();
            return longString == null ? null : Long.valueOf(Long.parseLong(longString));
        }

        public static LongJsonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class FloatJsonUnmarshaller
    implements Unmarshaller<Float, JsonUnmarshallerContext> {
        private static final FloatJsonUnmarshaller instance = new FloatJsonUnmarshaller();

        @Override
        public Float unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            String floatString = unmarshallerContext.readText();
            return floatString == null ? null : Float.valueOf(floatString);
        }

        public static FloatJsonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class BooleanJsonUnmarshaller
    implements Unmarshaller<Boolean, JsonUnmarshallerContext> {
        private static final BooleanJsonUnmarshaller instance = new BooleanJsonUnmarshaller();

        @Override
        public Boolean unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            String booleanString = unmarshallerContext.readText();
            return booleanString == null ? null : Boolean.valueOf(Boolean.parseBoolean(booleanString));
        }

        public static BooleanJsonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class BigDecimalJsonUnmarshaller
    implements Unmarshaller<BigDecimal, JsonUnmarshallerContext> {
        private static final BigDecimalJsonUnmarshaller instance = new BigDecimalJsonUnmarshaller();

        @Override
        public BigDecimal unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            String s = unmarshallerContext.readText();
            return s == null ? null : new BigDecimal(s);
        }

        public static BigDecimalJsonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class BigIntegerJsonUnmarshaller
    implements Unmarshaller<BigInteger, JsonUnmarshallerContext> {
        private static final BigIntegerJsonUnmarshaller instance = new BigIntegerJsonUnmarshaller();

        @Override
        public BigInteger unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            String intString = unmarshallerContext.readText();
            return intString == null ? null : new BigInteger(intString);
        }

        public static BigIntegerJsonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class IntegerJsonUnmarshaller
    implements Unmarshaller<Integer, JsonUnmarshallerContext> {
        private static final IntegerJsonUnmarshaller instance = new IntegerJsonUnmarshaller();

        @Override
        public Integer unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            String intString = unmarshallerContext.readText();
            return intString == null ? null : Integer.valueOf(Integer.parseInt(intString));
        }

        public static IntegerJsonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class DoubleJsonUnmarshaller
    implements Unmarshaller<Double, JsonUnmarshallerContext> {
        private static final DoubleJsonUnmarshaller instance = new DoubleJsonUnmarshaller();

        @Override
        public Double unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            String doubleString = unmarshallerContext.readText();
            return doubleString == null ? null : Double.valueOf(Double.parseDouble(doubleString));
        }

        public static DoubleJsonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class JsonValueStringUnmarshaller
    extends StringJsonUnmarshaller {
        private static final JsonValueStringUnmarshaller INSTANCE = new JsonValueStringUnmarshaller();

        @Override
        public String unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            String stringValue = super.unmarshall(unmarshallerContext);
            return !unmarshallerContext.isInsideResponseHeader() ? stringValue : new String(Base64.decode(stringValue), Charset.forName("utf-8"));
        }

        public static JsonValueStringUnmarshaller getInstance() {
            return INSTANCE;
        }
    }

    public static class StringJsonUnmarshaller
    implements Unmarshaller<String, JsonUnmarshallerContext> {
        private static final StringJsonUnmarshaller instance = new StringJsonUnmarshaller();

        @Override
        public String unmarshall(JsonUnmarshallerContext unmarshallerContext) throws Exception {
            return unmarshallerContext.readText();
        }

        public static StringJsonUnmarshaller getInstance() {
            return instance;
        }
    }
}

