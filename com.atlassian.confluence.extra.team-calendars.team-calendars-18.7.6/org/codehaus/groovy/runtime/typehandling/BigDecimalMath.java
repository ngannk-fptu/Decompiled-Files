/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.typehandling;

import java.math.BigDecimal;
import java.math.MathContext;
import org.codehaus.groovy.runtime.typehandling.NumberMath;

public final class BigDecimalMath
extends NumberMath {
    public static final int DIVISION_EXTRA_PRECISION = 10;
    public static final int DIVISION_MIN_SCALE = 10;
    public static final BigDecimalMath INSTANCE = new BigDecimalMath();

    private BigDecimalMath() {
    }

    @Override
    protected Number absImpl(Number number) {
        return BigDecimalMath.toBigDecimal(number).abs();
    }

    @Override
    public Number addImpl(Number left, Number right) {
        return BigDecimalMath.toBigDecimal(left).add(BigDecimalMath.toBigDecimal(right));
    }

    @Override
    public Number subtractImpl(Number left, Number right) {
        return BigDecimalMath.toBigDecimal(left).subtract(BigDecimalMath.toBigDecimal(right));
    }

    @Override
    public Number multiplyImpl(Number left, Number right) {
        return BigDecimalMath.toBigDecimal(left).multiply(BigDecimalMath.toBigDecimal(right));
    }

    @Override
    public Number divideImpl(Number left, Number right) {
        BigDecimal bigLeft = BigDecimalMath.toBigDecimal(left);
        BigDecimal bigRight = BigDecimalMath.toBigDecimal(right);
        try {
            return bigLeft.divide(bigRight);
        }
        catch (ArithmeticException e) {
            int precision = Math.max(bigLeft.precision(), bigRight.precision()) + 10;
            BigDecimal result = bigLeft.divide(bigRight, new MathContext(precision));
            int scale = Math.max(Math.max(bigLeft.scale(), bigRight.scale()), 10);
            if (result.scale() > scale) {
                result = result.setScale(scale, 4);
            }
            return result;
        }
    }

    @Override
    public int compareToImpl(Number left, Number right) {
        return BigDecimalMath.toBigDecimal(left).compareTo(BigDecimalMath.toBigDecimal(right));
    }

    @Override
    protected Number unaryMinusImpl(Number left) {
        return BigDecimalMath.toBigDecimal(left).negate();
    }

    @Override
    protected Number unaryPlusImpl(Number left) {
        return BigDecimalMath.toBigDecimal(left);
    }
}

