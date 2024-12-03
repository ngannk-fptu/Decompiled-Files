/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.DateHelper;
import com.hazelcast.query.impl.IdentityConverter;
import com.hazelcast.query.impl.Numbers;
import com.hazelcast.query.impl.UUIDConverter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;

public final class TypeConverters {
    public static final TypeConverter BIG_INTEGER_CONVERTER = new BigIntegerConverter();
    public static final TypeConverter BIG_DECIMAL_CONVERTER = new BigDecimalConverter();
    public static final TypeConverter DOUBLE_CONVERTER = new DoubleConverter();
    public static final TypeConverter LONG_CONVERTER = new LongConverter();
    public static final TypeConverter INTEGER_CONVERTER = new IntegerConverter();
    public static final TypeConverter BOOLEAN_CONVERTER = new BooleanConverter();
    public static final TypeConverter SHORT_CONVERTER = new ShortConverter();
    public static final TypeConverter FLOAT_CONVERTER = new FloatConverter();
    public static final TypeConverter STRING_CONVERTER = new StringConverter();
    public static final TypeConverter CHAR_CONVERTER = new CharConverter();
    public static final TypeConverter BYTE_CONVERTER = new ByteConverter();
    public static final TypeConverter ENUM_CONVERTER = new EnumConverter();
    public static final TypeConverter SQL_DATE_CONVERTER = new SqlDateConverter();
    public static final TypeConverter SQL_TIMESTAMP_CONVERTER = new SqlTimestampConverter();
    public static final TypeConverter DATE_CONVERTER = new DateConverter();
    public static final TypeConverter IDENTITY_CONVERTER = new IdentityConverter();
    public static final TypeConverter NULL_CONVERTER = new IdentityConverter();
    public static final TypeConverter UUID_CONVERTER = new UUIDConverter();
    public static final TypeConverter PORTABLE_CONVERTER = new PortableConverter();

    private TypeConverters() {
    }

    static class PortableConverter
    extends BaseTypeConverter {
        PortableConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            if (value instanceof Portable) {
                return value;
            }
            throw new IllegalArgumentException("Cannot convert [" + value + "]");
        }
    }

    static class CharConverter
    extends BaseTypeConverter {
        CharConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            String string;
            if (value.getClass() == Character.TYPE) {
                return value;
            }
            if (value instanceof Character) {
                return value;
            }
            if (value instanceof Number) {
                Number number = (Number)((Object)value);
                return Character.valueOf((char)number.intValue());
            }
            if (value instanceof String && (string = (String)((Object)value)).length() == 1) {
                return Character.valueOf(string.charAt(0));
            }
            throw new IllegalArgumentException("Cannot convert [" + value + "] to char");
        }
    }

    static class ByteConverter
    extends BaseTypeConverter {
        ByteConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            Class<?> clazz = value.getClass();
            if (clazz == Byte.class) {
                return value;
            }
            if (value instanceof Number) {
                Number number = (Number)((Object)value);
                if (clazz == Long.class) {
                    byte byteValue = number.byteValue();
                    if (number.longValue() == (long)byteValue) {
                        return Byte.valueOf(byteValue);
                    }
                } else if (clazz == Double.class) {
                    byte byteValue = number.byteValue();
                    if (Numbers.equalDoubles(number.doubleValue(), byteValue)) {
                        return Byte.valueOf(byteValue);
                    }
                } else if (clazz == Integer.class) {
                    byte byteValue = number.byteValue();
                    if (number.intValue() == byteValue) {
                        return Byte.valueOf(byteValue);
                    }
                } else if (clazz == Float.class) {
                    byte byteValue = number.byteValue();
                    if (Numbers.equalFloats(number.floatValue(), byteValue)) {
                        return Byte.valueOf(byteValue);
                    }
                } else if (clazz == Short.class) {
                    byte byteValue = number.byteValue();
                    if (number.shortValue() == (short)byteValue) {
                        return Byte.valueOf(byteValue);
                    }
                }
                return value;
            }
            if (value instanceof String) {
                String string = (String)((Object)value);
                try {
                    return Byte.valueOf(Byte.parseByte(string));
                }
                catch (NumberFormatException e1) {
                    try {
                        return Long.valueOf(Long.parseLong(string));
                    }
                    catch (NumberFormatException e2) {
                        double parsedDouble = Double.parseDouble(string);
                        byte byteValue = (byte)parsedDouble;
                        if (Numbers.equalDoubles(parsedDouble, byteValue)) {
                            return Byte.valueOf(byteValue);
                        }
                        return Double.valueOf(parsedDouble);
                    }
                }
            }
            throw new IllegalArgumentException("Cannot convert [" + value + "] to number");
        }
    }

    static class BooleanConverter
    extends BaseTypeConverter {
        BooleanConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            if (value instanceof Boolean) {
                return value;
            }
            if (value instanceof String) {
                return Boolean.valueOf(Boolean.parseBoolean((String)((Object)value)));
            }
            if (value instanceof Number) {
                Number number = (Number)((Object)value);
                return Boolean.valueOf(number.intValue() != 0);
            }
            throw new IllegalArgumentException("Cannot convert [" + value + "] to boolean");
        }
    }

    static class ShortConverter
    extends BaseTypeConverter {
        ShortConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            Class<?> clazz = value.getClass();
            if (clazz == Short.class) {
                return value;
            }
            if (value instanceof Number) {
                Number number = (Number)((Object)value);
                if (clazz == Long.class) {
                    short shortValue = number.shortValue();
                    if (number.longValue() == (long)shortValue) {
                        return Short.valueOf(shortValue);
                    }
                } else if (clazz == Double.class) {
                    short shortValue = number.shortValue();
                    if (Numbers.equalDoubles(number.doubleValue(), shortValue)) {
                        return Short.valueOf(shortValue);
                    }
                } else if (clazz == Integer.class) {
                    short shortValue = number.shortValue();
                    if (number.intValue() == shortValue) {
                        return Short.valueOf(shortValue);
                    }
                } else if (clazz == Float.class) {
                    short shortValue = number.shortValue();
                    if (Numbers.equalFloats(number.floatValue(), shortValue)) {
                        return Short.valueOf(shortValue);
                    }
                } else if (clazz == Byte.class) {
                    return Short.valueOf(number.shortValue());
                }
                return value;
            }
            if (value instanceof String) {
                String string = (String)((Object)value);
                try {
                    return Short.valueOf(Short.parseShort(string));
                }
                catch (NumberFormatException e1) {
                    try {
                        return Long.valueOf(Long.parseLong(string));
                    }
                    catch (NumberFormatException e2) {
                        double parsedDouble = Double.parseDouble(string);
                        short shortValue = (short)parsedDouble;
                        if (Numbers.equalDoubles(parsedDouble, shortValue)) {
                            return Short.valueOf(shortValue);
                        }
                        return Double.valueOf(parsedDouble);
                    }
                }
            }
            throw new IllegalArgumentException("Cannot convert [" + value + "] to number");
        }
    }

    static class FloatConverter
    extends BaseTypeConverter {
        FloatConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            Class<?> clazz = value.getClass();
            if (clazz == Float.class) {
                return value;
            }
            if (value instanceof Number) {
                Number number = (Number)((Object)value);
                if (clazz == Double.class) {
                    float floatValue = number.floatValue();
                    if (number.doubleValue() == (double)floatValue) {
                        return Float.valueOf(floatValue);
                    }
                } else if (clazz == Long.class) {
                    float floatValue = number.floatValue();
                    if (Numbers.equalLongAndDouble(number.longValue(), floatValue)) {
                        return Float.valueOf(floatValue);
                    }
                } else if (clazz == Integer.class) {
                    float floatValue = number.floatValue();
                    if (Numbers.equalLongAndDouble(number.intValue(), floatValue)) {
                        return Float.valueOf(floatValue);
                    }
                } else if (clazz == Short.class || clazz == Byte.class) {
                    return Float.valueOf(number.floatValue());
                }
                return value;
            }
            if (value instanceof String) {
                float floatValue;
                double parsedDouble = Double.parseDouble((String)((Object)value));
                if (parsedDouble == (double)(floatValue = (float)parsedDouble)) {
                    return Float.valueOf(floatValue);
                }
                return Double.valueOf(parsedDouble);
            }
            throw new IllegalArgumentException("Cannot convert [" + value + "] to number");
        }
    }

    static class StringConverter
    extends BaseTypeConverter {
        StringConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            if (value instanceof String) {
                return value;
            }
            return value.toString();
        }
    }

    static class IntegerConverter
    extends BaseTypeConverter {
        IntegerConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            Class<?> clazz = value.getClass();
            if (clazz == Integer.class) {
                return value;
            }
            if (value instanceof Number) {
                Number number = (Number)((Object)value);
                if (clazz == Long.class) {
                    int intValue = number.intValue();
                    if (number.longValue() == (long)intValue) {
                        return Integer.valueOf(intValue);
                    }
                } else if (clazz == Double.class) {
                    int intValue = number.intValue();
                    if (Numbers.equalDoubles(number.doubleValue(), intValue)) {
                        return Integer.valueOf(intValue);
                    }
                } else if (clazz == Float.class) {
                    int intValue = number.intValue();
                    if (Numbers.equalFloats(number.floatValue(), intValue)) {
                        return Integer.valueOf(intValue);
                    }
                } else if (clazz == Short.class || clazz == Byte.class) {
                    return Integer.valueOf(number.intValue());
                }
                return value;
            }
            if (value instanceof String) {
                String string = (String)((Object)value);
                try {
                    return Integer.valueOf(Integer.parseInt(string));
                }
                catch (NumberFormatException e1) {
                    try {
                        return Long.valueOf(Long.parseLong(string));
                    }
                    catch (NumberFormatException e2) {
                        double parsedDouble = Double.parseDouble(string);
                        int intValue = (int)parsedDouble;
                        if (Numbers.equalDoubles(parsedDouble, intValue)) {
                            return Integer.valueOf(intValue);
                        }
                        return Double.valueOf(parsedDouble);
                    }
                }
            }
            throw new IllegalArgumentException("Cannot convert [" + value + "] to number");
        }
    }

    static class BigDecimalConverter
    extends BaseTypeConverter {
        BigDecimalConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            if (value instanceof BigDecimal) {
                return value;
            }
            if (value instanceof BigInteger) {
                return new BigDecimal((BigInteger)value);
            }
            if (this.isIntegralDataType(value)) {
                Number number = (Number)((Object)value);
                return BigDecimal.valueOf(number.longValue());
            }
            if (this.isFloatingPointDataType(value)) {
                Number number = (Number)((Object)value);
                return BigDecimal.valueOf(number.doubleValue());
            }
            if (value instanceof Boolean) {
                return (Boolean)value != false ? BigDecimal.ONE : BigDecimal.ZERO;
            }
            return new BigDecimal(value.toString());
        }

        private boolean isFloatingPointDataType(Comparable value) {
            return value instanceof Double || value instanceof Float;
        }

        private boolean isIntegralDataType(Comparable value) {
            return value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long;
        }
    }

    static class BigIntegerConverter
    extends BaseTypeConverter {
        BigIntegerConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            if (value instanceof BigInteger) {
                return value;
            }
            if (value instanceof BigDecimal) {
                BigDecimal decimal = (BigDecimal)value;
                return decimal.toBigInteger();
            }
            if (value instanceof Number) {
                Number number = (Number)((Object)value);
                return BigInteger.valueOf(number.longValue());
            }
            if (value instanceof Boolean) {
                return (Boolean)value != false ? BigInteger.ONE : BigInteger.ZERO;
            }
            return new BigInteger(value.toString());
        }
    }

    static class LongConverter
    extends BaseTypeConverter {
        LongConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            Class<?> clazz = value.getClass();
            if (clazz == Long.class) {
                return value;
            }
            if (value instanceof Number) {
                Number number = (Number)((Object)value);
                if (clazz == Double.class) {
                    long longValue = number.longValue();
                    if (Numbers.equalDoubles(number.doubleValue(), longValue)) {
                        return Long.valueOf(longValue);
                    }
                } else if (clazz == Float.class) {
                    long longValue = number.longValue();
                    if (Numbers.equalFloats(number.floatValue(), longValue)) {
                        return Long.valueOf(longValue);
                    }
                } else if (clazz == Integer.class || clazz == Short.class || clazz == Byte.class) {
                    return Long.valueOf(number.longValue());
                }
                return value;
            }
            if (value instanceof String) {
                String string = (String)((Object)value);
                try {
                    return Long.valueOf(Long.parseLong(string));
                }
                catch (NumberFormatException e) {
                    double parsedDouble = Double.parseDouble(string);
                    long longValue = (long)parsedDouble;
                    if (Numbers.equalDoubles(parsedDouble, longValue)) {
                        return Long.valueOf(longValue);
                    }
                    return Double.valueOf(parsedDouble);
                }
            }
            throw new IllegalArgumentException("Cannot convert [" + value + "] to number");
        }
    }

    static class DoubleConverter
    extends BaseTypeConverter {
        DoubleConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            Class<?> clazz = value.getClass();
            if (clazz == Double.class) {
                return value;
            }
            if (value instanceof Number) {
                Number number = (Number)((Object)value);
                if (clazz == Long.class) {
                    double doubleValue = number.doubleValue();
                    if (Numbers.equalLongAndDouble(number.longValue(), doubleValue)) {
                        return Double.valueOf(doubleValue);
                    }
                } else if (clazz == Integer.class || clazz == Float.class || clazz == Short.class || clazz == Byte.class) {
                    return Double.valueOf(number.doubleValue());
                }
                return value;
            }
            if (value instanceof String) {
                return Double.valueOf(Double.parseDouble((String)((Object)value)));
            }
            throw new IllegalArgumentException("Cannot convert [" + value + "] to number");
        }
    }

    static class DateConverter
    extends BaseTypeConverter {
        DateConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            if (value instanceof java.util.Date) {
                return value;
            }
            if (value instanceof String) {
                return DateHelper.parseDate((String)((Object)value));
            }
            if (value instanceof Number) {
                Number number = (Number)((Object)value);
                return new java.util.Date(number.longValue());
            }
            throw new IllegalArgumentException("Cannot convert [" + value + "] to java.util.Date");
        }
    }

    static class SqlTimestampConverter
    extends BaseTypeConverter {
        SqlTimestampConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            if (value instanceof Timestamp) {
                return value;
            }
            if (value instanceof java.util.Date) {
                return new Timestamp(((java.util.Date)value).getTime());
            }
            if (value instanceof String) {
                return DateHelper.parseTimeStamp((String)((Object)value));
            }
            if (value instanceof Number) {
                Number number = (Number)((Object)value);
                return new Timestamp(number.longValue());
            }
            throw new IllegalArgumentException("Cannot convert [" + value + "] to java.sql.Timestamp");
        }
    }

    static class SqlDateConverter
    extends BaseTypeConverter {
        SqlDateConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            if (value instanceof java.util.Date) {
                return value;
            }
            if (value instanceof String) {
                return DateHelper.parseSqlDate((String)((Object)value));
            }
            if (value instanceof Number) {
                Number number = (Number)((Object)value);
                return new Date(number.longValue());
            }
            throw new IllegalArgumentException("Cannot convert [" + value + "] to java.sql.Date");
        }
    }

    static class EnumConverter
    extends BaseTypeConverter {
        EnumConverter() {
        }

        @Override
        Comparable convertInternal(Comparable value) {
            String enumString = value.toString();
            if (enumString.contains(".")) {
                enumString = enumString.substring(1 + enumString.lastIndexOf(46));
            }
            return enumString;
        }
    }

    public static abstract class BaseTypeConverter
    implements TypeConverter {
        abstract Comparable convertInternal(Comparable var1);

        @Override
        public final Comparable convert(Comparable value) {
            if (value == null) {
                return AbstractIndex.NULL;
            }
            return this.convertInternal(value);
        }
    }
}

