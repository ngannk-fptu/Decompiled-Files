/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.util;

import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class MathUtils {
    private MathUtils() {
    }

    public static <D extends Number> D result(D num1, Number num2, Operator operator) {
        BigDecimal d1 = MathUtils.toBigDecimal(num1);
        BigDecimal d2 = MathUtils.toBigDecimal(num2);
        if (operator == Ops.ADD) {
            return (D)MathUtils.cast(d1.add(d2), num1.getClass());
        }
        if (operator == Ops.SUB) {
            return (D)MathUtils.cast(d1.subtract(d2), num1.getClass());
        }
        if (operator == Ops.MULT) {
            return (D)MathUtils.cast(d1.multiply(d2), num1.getClass());
        }
        if (operator == Ops.DIV) {
            return (D)MathUtils.cast(d1.divide(d2), num1.getClass());
        }
        throw new IllegalArgumentException(operator.toString());
    }

    public static <D extends Number> D sum(D num1, Number num2) {
        BigDecimal res = MathUtils.toBigDecimal(num1).add(MathUtils.toBigDecimal(num2));
        return (D)MathUtils.cast(res, num1.getClass());
    }

    public static <D extends Number> D difference(D num1, Number num2) {
        BigDecimal res = MathUtils.toBigDecimal(num1).subtract(MathUtils.toBigDecimal(num2));
        return (D)MathUtils.cast(res, num1.getClass());
    }

    public static <D extends Number> D cast(Number num, Class<D> type) {
        Number rv;
        if (num == null || type.isInstance(num)) {
            rv = (Number)type.cast(num);
        } else if (type.equals(Byte.class)) {
            rv = (Number)type.cast(num.byteValue());
        } else if (type.equals(Double.class)) {
            rv = (Number)type.cast(num.doubleValue());
        } else if (type.equals(Float.class)) {
            rv = (Number)type.cast(Float.valueOf(num.floatValue()));
        } else if (type.equals(Integer.class)) {
            rv = (Number)type.cast(num.intValue());
        } else if (type.equals(Long.class)) {
            rv = (Number)type.cast(num.longValue());
        } else if (type.equals(Short.class)) {
            rv = (Number)type.cast(num.shortValue());
        } else if (type.equals(BigDecimal.class)) {
            rv = (Number)type.cast(new BigDecimal(num.toString()));
        } else if (type.equals(BigInteger.class)) {
            rv = num instanceof BigDecimal ? (Number)((Number)type.cast(((BigDecimal)num).toBigInteger())) : (Number)((Number)type.cast(new BigInteger(num.toString())));
        } else {
            throw new IllegalArgumentException(String.format("Unsupported target type : %s", type.getSimpleName()));
        }
        return (D)rv;
    }

    private static BigDecimal toBigDecimal(Number num) {
        if (num instanceof BigDecimal) {
            return (BigDecimal)num;
        }
        return new BigDecimal(num.toString());
    }
}

