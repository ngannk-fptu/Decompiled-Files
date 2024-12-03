/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.transform;

import com.amazonaws.SdkClientException;
import com.amazonaws.transform.StaxUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.amazonaws.util.Base64;
import com.amazonaws.util.DateUtils;
import com.amazonaws.util.TimestampFormat;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleTypeStaxUnmarshallers {
    private static Log log = LogFactory.getLog(SimpleTypeStaxUnmarshallers.class);

    public static class ShortJsonUnmarshaller
    implements Unmarshaller<Short, StaxUnmarshallerContext> {
        private static final ShortJsonUnmarshaller instance = new ShortJsonUnmarshaller();

        @Override
        public Short unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
            String shortString = unmarshallerContext.readText();
            return shortString == null ? null : Short.valueOf(shortString);
        }

        public static ShortJsonUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class CharacterJsonUnmarshaller
    implements Unmarshaller<Character, StaxUnmarshallerContext> {
        private static final CharacterJsonUnmarshaller instance = new CharacterJsonUnmarshaller();

        @Override
        public Character unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
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

    public static class ByteBufferStaxUnmarshaller
    implements Unmarshaller<ByteBuffer, StaxUnmarshallerContext> {
        private static final ByteBufferStaxUnmarshaller instance = new ByteBufferStaxUnmarshaller();

        @Override
        public ByteBuffer unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
            String base64EncodedString = unmarshallerContext.readText();
            byte[] decodedBytes = Base64.decode(base64EncodedString);
            return ByteBuffer.wrap(decodedBytes);
        }

        public static ByteBufferStaxUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class DateStaxUnmarshaller
    implements Unmarshaller<Date, StaxUnmarshallerContext> {
        private static final DateStaxUnmarshaller instance = new DateStaxUnmarshaller();

        @Override
        public Date unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
            String dateString = unmarshallerContext.readText();
            if (dateString == null) {
                return null;
            }
            try {
                return DateUtils.parseISO8601Date(dateString);
            }
            catch (Exception e) {
                log.warn((Object)("Unable to parse date '" + dateString + "':  " + e.getMessage()), (Throwable)e);
                return null;
            }
        }

        public static DateStaxUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class DateStaxUnmarshallerFactory
    implements Unmarshaller<Date, StaxUnmarshallerContext> {
        private final String dateFormatType;

        private DateStaxUnmarshallerFactory(String dateFormatType) {
            this.dateFormatType = dateFormatType;
        }

        @Override
        public Date unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
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
                return DateUtils.parseISO8601Date(dateString);
            }
            catch (Exception e) {
                log.warn((Object)("Unable to parse date '" + dateString + "':  " + e.getMessage()), (Throwable)e);
                return null;
            }
        }

        public static DateStaxUnmarshallerFactory getInstance(String dateFormatType) {
            return new DateStaxUnmarshallerFactory(dateFormatType);
        }
    }

    public static class ByteStaxUnmarshaller
    implements Unmarshaller<Byte, StaxUnmarshallerContext> {
        private static final ByteStaxUnmarshaller instance = new ByteStaxUnmarshaller();

        @Override
        public Byte unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
            String byteString = unmarshallerContext.readText();
            return byteString == null ? null : Byte.valueOf(byteString);
        }

        public static ByteStaxUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class LongStaxUnmarshaller
    implements Unmarshaller<Long, StaxUnmarshallerContext> {
        private static final LongStaxUnmarshaller instance = new LongStaxUnmarshaller();

        @Override
        public Long unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
            String longString = unmarshallerContext.readText();
            return longString == null ? null : Long.valueOf(Long.parseLong(longString));
        }

        public static LongStaxUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class FloatStaxUnmarshaller
    implements Unmarshaller<Float, StaxUnmarshallerContext> {
        private static final FloatStaxUnmarshaller instance = new FloatStaxUnmarshaller();

        @Override
        public Float unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
            String floatString = unmarshallerContext.readText();
            return floatString == null ? null : Float.valueOf(floatString);
        }

        public static FloatStaxUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class BooleanStaxUnmarshaller
    implements Unmarshaller<Boolean, StaxUnmarshallerContext> {
        private static final BooleanStaxUnmarshaller instance = new BooleanStaxUnmarshaller();

        @Override
        public Boolean unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
            String booleanString = unmarshallerContext.readText();
            return booleanString == null ? null : Boolean.valueOf(Boolean.parseBoolean(booleanString));
        }

        public static BooleanStaxUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class IntegerStaxUnmarshaller
    implements Unmarshaller<Integer, StaxUnmarshallerContext> {
        private static final IntegerStaxUnmarshaller instance = new IntegerStaxUnmarshaller();

        @Override
        public Integer unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
            String intString = unmarshallerContext.readText();
            return intString == null ? null : Integer.valueOf(Integer.parseInt(intString));
        }

        public static IntegerStaxUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class DoubleStaxUnmarshaller
    implements Unmarshaller<Double, StaxUnmarshallerContext> {
        private static final DoubleStaxUnmarshaller instance = new DoubleStaxUnmarshaller();

        @Override
        public Double unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
            String doubleString = unmarshallerContext.readText();
            return doubleString == null ? null : Double.valueOf(Double.parseDouble(doubleString));
        }

        public static DoubleStaxUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class BigIntegerStaxUnmarshaller
    implements Unmarshaller<BigInteger, StaxUnmarshallerContext> {
        private static final BigIntegerStaxUnmarshaller instance = new BigIntegerStaxUnmarshaller();

        @Override
        public BigInteger unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
            String s = unmarshallerContext.readText();
            return s == null ? null : new BigInteger(s);
        }

        public static BigIntegerStaxUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class BigDecimalStaxUnmarshaller
    implements Unmarshaller<BigDecimal, StaxUnmarshallerContext> {
        private static final BigDecimalStaxUnmarshaller instance = new BigDecimalStaxUnmarshaller();

        @Override
        public BigDecimal unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
            String s = unmarshallerContext.readText();
            return s == null ? null : new BigDecimal(s);
        }

        public static BigDecimalStaxUnmarshaller getInstance() {
            return instance;
        }
    }

    public static class StringStaxUnmarshaller
    implements Unmarshaller<String, StaxUnmarshallerContext> {
        private static final StringStaxUnmarshaller instance = new StringStaxUnmarshaller();

        @Override
        public String unmarshall(StaxUnmarshallerContext unmarshallerContext) throws Exception {
            return unmarshallerContext.readText();
        }

        public static StringStaxUnmarshaller getInstance() {
            return instance;
        }
    }
}

