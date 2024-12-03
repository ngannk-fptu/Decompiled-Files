/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.typehandling;

import org.codehaus.groovy.runtime.typehandling.BigDecimalMath;
import org.codehaus.groovy.runtime.typehandling.NumberMath;

public final class BigIntegerMath
extends NumberMath {
    public static final BigIntegerMath INSTANCE = new BigIntegerMath();

    private BigIntegerMath() {
    }

    @Override
    protected Number absImpl(Number number) {
        return BigIntegerMath.toBigInteger(number).abs();
    }

    @Override
    public Number addImpl(Number left, Number right) {
        return BigIntegerMath.toBigInteger(left).add(BigIntegerMath.toBigInteger(right));
    }

    @Override
    public Number subtractImpl(Number left, Number right) {
        return BigIntegerMath.toBigInteger(left).subtract(BigIntegerMath.toBigInteger(right));
    }

    @Override
    public Number multiplyImpl(Number left, Number right) {
        return BigIntegerMath.toBigInteger(left).multiply(BigIntegerMath.toBigInteger(right));
    }

    @Override
    public Number divideImpl(Number left, Number right) {
        return BigDecimalMath.INSTANCE.divideImpl(left, right);
    }

    @Override
    public int compareToImpl(Number left, Number right) {
        return BigIntegerMath.toBigInteger(left).compareTo(BigIntegerMath.toBigInteger(right));
    }

    @Override
    protected Number intdivImpl(Number left, Number right) {
        return BigIntegerMath.toBigInteger(left).divide(BigIntegerMath.toBigInteger(right));
    }

    @Override
    protected Number modImpl(Number left, Number right) {
        return BigIntegerMath.toBigInteger(left).mod(BigIntegerMath.toBigInteger(right));
    }

    @Override
    protected Number unaryMinusImpl(Number left) {
        return BigIntegerMath.toBigInteger(left).negate();
    }

    @Override
    protected Number unaryPlusImpl(Number left) {
        return BigIntegerMath.toBigInteger(left);
    }

    @Override
    protected Number bitwiseNegateImpl(Number left) {
        return BigIntegerMath.toBigInteger(left).not();
    }

    @Override
    protected Number orImpl(Number left, Number right) {
        return BigIntegerMath.toBigInteger(left).or(BigIntegerMath.toBigInteger(right));
    }

    @Override
    protected Number andImpl(Number left, Number right) {
        return BigIntegerMath.toBigInteger(left).and(BigIntegerMath.toBigInteger(right));
    }

    @Override
    protected Number xorImpl(Number left, Number right) {
        return BigIntegerMath.toBigInteger(left).xor(BigIntegerMath.toBigInteger(right));
    }
}

