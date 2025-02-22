/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MathUtils {
    protected static final BigDecimal DECIMAL_ZERO = new BigDecimal(BigInteger.ZERO);
    protected static final int BASE_LONG = 0;
    protected static final int BASE_FLOAT = 1;
    protected static final int BASE_DOUBLE = 2;
    protected static final int BASE_BIGINTEGER = 3;
    protected static final int BASE_BIGDECIMAL = 4;
    protected static final Map ints = new HashMap();
    protected static final List typesBySize;

    public static BigDecimal toBigDecimal(Number n) {
        if (n instanceof BigDecimal) {
            return (BigDecimal)n;
        }
        if (n instanceof BigInteger) {
            return new BigDecimal((BigInteger)n);
        }
        return new BigDecimal(n.doubleValue());
    }

    public static BigInteger toBigInteger(Number n) {
        if (n instanceof BigInteger) {
            return (BigInteger)n;
        }
        return BigInteger.valueOf(n.longValue());
    }

    public static boolean isZero(Number n) {
        if (MathUtils.isInteger(n)) {
            if (n instanceof BigInteger) {
                return ((BigInteger)n).compareTo(BigInteger.ZERO) == 0;
            }
            return n.doubleValue() == 0.0;
        }
        if (n instanceof Float) {
            return n.floatValue() == 0.0f;
        }
        if (n instanceof Double) {
            return n.doubleValue() == 0.0;
        }
        return MathUtils.toBigDecimal(n).compareTo(DECIMAL_ZERO) == 0;
    }

    public static boolean isInteger(Number n) {
        return ints.containsKey(n.getClass());
    }

    public static Number wrapPrimitive(long value, Class type) {
        if (type == Byte.class) {
            if (value > 127L || value < -128L) {
                type = Short.class;
            } else {
                return new Byte((byte)value);
            }
        }
        if (type == Short.class) {
            if (value > 32767L || value < -32768L) {
                type = Integer.class;
            } else {
                return new Short((short)value);
            }
        }
        if (type == Integer.class) {
            if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
                type = Long.class;
            } else {
                return new Integer((int)value);
            }
        }
        if (type == Long.class) {
            return new Long(value);
        }
        return BigInteger.valueOf(value);
    }

    private static Number wrapPrimitive(long value, Number op1, Number op2) {
        if (typesBySize.indexOf(op1.getClass()) > typesBySize.indexOf(op2.getClass())) {
            return MathUtils.wrapPrimitive(value, op1.getClass());
        }
        return MathUtils.wrapPrimitive(value, op2.getClass());
    }

    private static int findCalculationBase(Number op1, Number op2) {
        boolean op1Int = MathUtils.isInteger(op1);
        boolean op2Int = MathUtils.isInteger(op2);
        if (op1 instanceof BigDecimal || op2 instanceof BigDecimal || (!op1Int || !op2Int) && (op1 instanceof BigInteger || op2 instanceof BigInteger)) {
            return 4;
        }
        if (op1Int && op2Int) {
            if (op1 instanceof BigInteger || op2 instanceof BigInteger) {
                return 3;
            }
            return 0;
        }
        if (op1 instanceof Double || op2 instanceof Double) {
            return 2;
        }
        return 1;
    }

    public static Number add(Number op1, Number op2) {
        int calcBase = MathUtils.findCalculationBase(op1, op2);
        switch (calcBase) {
            case 3: {
                return MathUtils.toBigInteger(op1).add(MathUtils.toBigInteger(op2));
            }
            case 0: {
                long l1 = op1.longValue();
                long l2 = op2.longValue();
                long result = l1 + l2;
                if ((result ^ l1) < 0L && (result ^ l2) < 0L) {
                    return MathUtils.toBigInteger(op1).add(MathUtils.toBigInteger(op2));
                }
                return MathUtils.wrapPrimitive(result, op1, op2);
            }
            case 1: {
                return new Float(op1.floatValue() + op2.floatValue());
            }
            case 2: {
                return new Double(op1.doubleValue() + op2.doubleValue());
            }
        }
        return MathUtils.toBigDecimal(op1).add(MathUtils.toBigDecimal(op2));
    }

    public static Number subtract(Number op1, Number op2) {
        int calcBase = MathUtils.findCalculationBase(op1, op2);
        switch (calcBase) {
            case 3: {
                return MathUtils.toBigInteger(op1).subtract(MathUtils.toBigInteger(op2));
            }
            case 0: {
                long l1 = op1.longValue();
                long l2 = op2.longValue();
                long result = l1 - l2;
                if ((result ^ l1) < 0L && (result ^ (l2 ^ 0xFFFFFFFFFFFFFFFFL)) < 0L) {
                    return MathUtils.toBigInteger(op1).subtract(MathUtils.toBigInteger(op2));
                }
                return MathUtils.wrapPrimitive(result, op1, op2);
            }
            case 1: {
                return new Float(op1.floatValue() - op2.floatValue());
            }
            case 2: {
                return new Double(op1.doubleValue() - op2.doubleValue());
            }
        }
        return MathUtils.toBigDecimal(op1).subtract(MathUtils.toBigDecimal(op2));
    }

    public static Number multiply(Number op1, Number op2) {
        int calcBase = MathUtils.findCalculationBase(op1, op2);
        switch (calcBase) {
            case 3: {
                return MathUtils.toBigInteger(op1).multiply(MathUtils.toBigInteger(op2));
            }
            case 0: {
                long l1 = op1.longValue();
                long l2 = op2.longValue();
                long result = l1 * l2;
                if (l2 != 0L && result / l2 != l1) {
                    return MathUtils.toBigInteger(op1).multiply(MathUtils.toBigInteger(op2));
                }
                return MathUtils.wrapPrimitive(result, op1, op2);
            }
            case 1: {
                return new Float(op1.floatValue() * op2.floatValue());
            }
            case 2: {
                return new Double(op1.doubleValue() * op2.doubleValue());
            }
        }
        return MathUtils.toBigDecimal(op1).multiply(MathUtils.toBigDecimal(op2));
    }

    public static Number divide(Number op1, Number op2) {
        int calcBase = MathUtils.findCalculationBase(op1, op2);
        switch (calcBase) {
            case 3: {
                BigInteger b1 = MathUtils.toBigInteger(op1);
                BigInteger b2 = MathUtils.toBigInteger(op2);
                return b1.divide(b2);
            }
            case 0: {
                long l1 = op1.longValue();
                long l2 = op2.longValue();
                return MathUtils.wrapPrimitive(l1 / l2, op1, op2);
            }
            case 1: {
                return new Float(op1.floatValue() / op2.floatValue());
            }
            case 2: {
                return new Double(op1.doubleValue() / op2.doubleValue());
            }
        }
        return MathUtils.toBigDecimal(op1).divide(MathUtils.toBigDecimal(op2), 5);
    }

    public static Number modulo(Number op1, Number op2) throws ArithmeticException {
        int calcBase = MathUtils.findCalculationBase(op1, op2);
        switch (calcBase) {
            case 3: {
                return MathUtils.toBigInteger(op1).mod(MathUtils.toBigInteger(op2));
            }
            case 0: {
                return MathUtils.wrapPrimitive(op1.longValue() % op2.longValue(), op1, op2);
            }
            case 1: {
                return new Float(op1.floatValue() % op2.floatValue());
            }
            case 2: {
                return new Double(op1.doubleValue() % op2.doubleValue());
            }
        }
        throw new ArithmeticException("Cannot calculate the modulo of BigDecimals.");
    }

    public static int compare(Number op1, Number op2) {
        int calcBase = MathUtils.findCalculationBase(op1, op2);
        switch (calcBase) {
            case 3: {
                return MathUtils.toBigInteger(op1).compareTo(MathUtils.toBigInteger(op2));
            }
            case 0: {
                long l1 = op1.longValue();
                long l2 = op2.longValue();
                if (l1 < l2) {
                    return -1;
                }
                if (l1 > l2) {
                    return 1;
                }
                return 0;
            }
            case 1: {
                float f1 = op1.floatValue();
                float f2 = op2.floatValue();
                if (f1 < f2) {
                    return -1;
                }
                if (f1 > f2) {
                    return 1;
                }
                return 0;
            }
            case 2: {
                double d1 = op1.doubleValue();
                double d2 = op2.doubleValue();
                if (d1 < d2) {
                    return -1;
                }
                if (d1 > d2) {
                    return 1;
                }
                return 0;
            }
        }
        return MathUtils.toBigDecimal(op1).compareTo(MathUtils.toBigDecimal(op2));
    }

    static {
        ints.put(Byte.class, BigDecimal.valueOf(127L));
        ints.put(Short.class, BigDecimal.valueOf(32767L));
        ints.put(Integer.class, BigDecimal.valueOf(Integer.MAX_VALUE));
        ints.put(Long.class, BigDecimal.valueOf(Long.MAX_VALUE));
        ints.put(BigInteger.class, BigDecimal.valueOf(-1L));
        typesBySize = new ArrayList();
        typesBySize.add(Byte.class);
        typesBySize.add(Short.class);
        typesBySize.add(Integer.class);
        typesBySize.add(Long.class);
        typesBySize.add(Float.class);
        typesBySize.add(Double.class);
    }
}

