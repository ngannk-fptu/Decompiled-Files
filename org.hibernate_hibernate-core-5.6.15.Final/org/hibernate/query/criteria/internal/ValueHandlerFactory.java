/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ValueHandlerFactory {
    private ValueHandlerFactory() {
    }

    public static boolean isCharacter(Class type) {
        return String.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type) || Character.TYPE.equals(type);
    }

    public static boolean isCharacter(Object value) {
        return String.class.isInstance(value) || Character.class.isInstance(value) || Character.TYPE.isInstance(value);
    }

    public static boolean isNumeric(Class type) {
        return Number.class.isAssignableFrom(type) || Byte.TYPE.equals(type) || Short.TYPE.equals(type) || Integer.TYPE.equals(type) || Long.TYPE.equals(type) || Float.TYPE.equals(type) || Double.TYPE.equals(type);
    }

    public static boolean isNumeric(Object value) {
        return Number.class.isInstance(value) || Byte.TYPE.isInstance(value) || Short.TYPE.isInstance(value) || Integer.TYPE.isInstance(value) || Long.TYPE.isInstance(value) || Float.TYPE.isInstance(value) || Double.TYPE.isInstance(value);
    }

    public static boolean isBoolean(Object value) {
        return Boolean.class.isInstance(value);
    }

    private static IllegalArgumentException unknownConversion(Object value, Class type) {
        return new IllegalArgumentException("Unaware how to convert value [" + value + " : " + ValueHandlerFactory.typeName(value) + "] to requested type [" + type.getName() + "]");
    }

    private static String typeName(Object value) {
        return value == null ? "???" : value.getClass().getName();
    }

    public static <T> T convert(Object value, Class<T> targetType) {
        if (value == null) {
            return null;
        }
        if (targetType.equals(value.getClass())) {
            return (T)value;
        }
        ValueHandler<T> valueHandler = ValueHandlerFactory.determineAppropriateHandler(targetType);
        if (valueHandler == null) {
            throw ValueHandlerFactory.unknownConversion(value, targetType);
        }
        return valueHandler.convert(value);
    }

    public static <T> ValueHandler<T> determineAppropriateHandler(Class<T> targetType) {
        if (String.class.equals(targetType)) {
            return StringValueHandler.INSTANCE;
        }
        if (Byte.class.equals(targetType) || Byte.TYPE.equals(targetType)) {
            return ByteValueHandler.INSTANCE;
        }
        if (Short.class.equals(targetType) || Short.TYPE.equals(targetType)) {
            return ShortValueHandler.INSTANCE;
        }
        if (Integer.class.equals(targetType) || Integer.TYPE.equals(targetType)) {
            return IntegerValueHandler.INSTANCE;
        }
        if (Long.class.equals(targetType) || Long.TYPE.equals(targetType)) {
            return LongValueHandler.INSTANCE;
        }
        if (Float.class.equals(targetType) || Float.TYPE.equals(targetType)) {
            return FloatValueHandler.INSTANCE;
        }
        if (Double.class.equals(targetType) || Double.TYPE.equals(targetType)) {
            return DoubleValueHandler.INSTANCE;
        }
        if (BigInteger.class.equals(targetType)) {
            return BigIntegerValueHandler.INSTANCE;
        }
        if (BigDecimal.class.equals(targetType)) {
            return BigDecimalValueHandler.INSTANCE;
        }
        if (Boolean.class.equals(targetType)) {
            return BooleanValueHandler.INSTANCE;
        }
        return null;
    }

    public static class StringValueHandler
    extends BaseValueHandler<String>
    implements Serializable {
        public static final StringValueHandler INSTANCE = new StringValueHandler();

        @Override
        public String convert(Object value) {
            return value == null ? null : value.toString();
        }
    }

    public static class BooleanValueHandler
    extends BaseValueHandler<Boolean>
    implements Serializable {
        public static final BooleanValueHandler INSTANCE = new BooleanValueHandler();

        @Override
        public Boolean convert(Object value) {
            if (value == null) {
                return null;
            }
            if (Boolean.class.isInstance(value)) {
                return (Boolean)value;
            }
            if (String.class.isInstance(value)) {
                return Boolean.getBoolean((String)value);
            }
            throw ValueHandlerFactory.unknownConversion(value, Boolean.class);
        }
    }

    public static class BigDecimalValueHandler
    extends BaseValueHandler<BigDecimal>
    implements Serializable {
        public static final BigDecimalValueHandler INSTANCE = new BigDecimalValueHandler();

        @Override
        public BigDecimal convert(Object value) {
            if (value == null) {
                return null;
            }
            if (BigDecimal.class.isInstance(value)) {
                return (BigDecimal)value;
            }
            if (BigInteger.class.isInstance(value)) {
                return new BigDecimal((BigInteger)value);
            }
            if (Number.class.isInstance(value)) {
                return BigDecimal.valueOf(((Number)value).doubleValue());
            }
            if (String.class.isInstance(value)) {
                return new BigDecimal((String)value);
            }
            throw ValueHandlerFactory.unknownConversion(value, BigDecimal.class);
        }

        @Override
        public String render(BigDecimal value) {
            return value.toString() + "BD";
        }
    }

    public static class BigIntegerValueHandler
    extends BaseValueHandler<BigInteger>
    implements Serializable {
        public static final BigIntegerValueHandler INSTANCE = new BigIntegerValueHandler();

        @Override
        public BigInteger convert(Object value) {
            if (value == null) {
                return null;
            }
            if (BigInteger.class.isInstance(value)) {
                return (BigInteger)value;
            }
            if (BigDecimal.class.isInstance(value)) {
                return ((BigDecimal)value).toBigInteger();
            }
            if (Number.class.isInstance(value)) {
                return BigInteger.valueOf(((Number)value).longValue());
            }
            if (String.class.isInstance(value)) {
                return new BigInteger((String)value);
            }
            throw ValueHandlerFactory.unknownConversion(value, BigInteger.class);
        }

        @Override
        public String render(BigInteger value) {
            return value.toString() + "BI";
        }
    }

    public static class DoubleValueHandler
    extends BaseValueHandler<Double>
    implements Serializable {
        public static final DoubleValueHandler INSTANCE = new DoubleValueHandler();

        @Override
        public Double convert(Object value) {
            if (value == null) {
                return null;
            }
            if (Number.class.isInstance(value)) {
                return ((Number)value).doubleValue();
            }
            if (String.class.isInstance(value)) {
                return Double.valueOf((String)value);
            }
            throw ValueHandlerFactory.unknownConversion(value, Double.class);
        }

        @Override
        public String render(Double value) {
            return value.toString() + 'D';
        }
    }

    public static class FloatValueHandler
    extends BaseValueHandler<Float>
    implements Serializable {
        public static final FloatValueHandler INSTANCE = new FloatValueHandler();

        @Override
        public Float convert(Object value) {
            if (value == null) {
                return null;
            }
            if (Number.class.isInstance(value)) {
                return Float.valueOf(((Number)value).floatValue());
            }
            if (String.class.isInstance(value)) {
                return Float.valueOf((String)value);
            }
            throw ValueHandlerFactory.unknownConversion(value, Float.class);
        }

        @Override
        public String render(Float value) {
            return value.toString() + 'F';
        }
    }

    public static class LongValueHandler
    extends BaseValueHandler<Long>
    implements Serializable {
        public static final LongValueHandler INSTANCE = new LongValueHandler();

        @Override
        public Long convert(Object value) {
            if (value == null) {
                return null;
            }
            if (Number.class.isInstance(value)) {
                return ((Number)value).longValue();
            }
            if (String.class.isInstance(value)) {
                return Long.valueOf((String)value);
            }
            throw ValueHandlerFactory.unknownConversion(value, Long.class);
        }

        @Override
        public String render(Long value) {
            return value.toString() + 'L';
        }
    }

    public static class IntegerValueHandler
    extends BaseValueHandler<Integer>
    implements Serializable {
        public static final IntegerValueHandler INSTANCE = new IntegerValueHandler();

        @Override
        public Integer convert(Object value) {
            if (value == null) {
                return null;
            }
            if (Number.class.isInstance(value)) {
                return ((Number)value).intValue();
            }
            if (String.class.isInstance(value)) {
                return Integer.valueOf((String)value);
            }
            throw ValueHandlerFactory.unknownConversion(value, Integer.class);
        }
    }

    public static class ShortValueHandler
    extends BaseValueHandler<Short>
    implements Serializable {
        public static final ShortValueHandler INSTANCE = new ShortValueHandler();

        @Override
        public Short convert(Object value) {
            if (value == null) {
                return null;
            }
            if (Number.class.isInstance(value)) {
                return ((Number)value).shortValue();
            }
            if (String.class.isInstance(value)) {
                return Short.valueOf((String)value);
            }
            throw ValueHandlerFactory.unknownConversion(value, Short.class);
        }
    }

    public static class ByteValueHandler
    extends BaseValueHandler<Byte>
    implements Serializable {
        public static final ByteValueHandler INSTANCE = new ByteValueHandler();

        @Override
        public Byte convert(Object value) {
            if (value == null) {
                return null;
            }
            if (Number.class.isInstance(value)) {
                return ((Number)value).byteValue();
            }
            if (String.class.isInstance(value)) {
                return Byte.valueOf((String)value);
            }
            throw ValueHandlerFactory.unknownConversion(value, Byte.class);
        }
    }

    public static class NoOpValueHandler<T>
    extends BaseValueHandler<T> {
        @Override
        public T convert(Object value) {
            return (T)value;
        }
    }

    public static abstract class BaseValueHandler<T>
    implements ValueHandler<T>,
    Serializable {
        @Override
        public String render(T value) {
            return value.toString();
        }
    }

    public static interface ValueHandler<T> {
        public T convert(Object var1);

        public String render(T var1);
    }
}

