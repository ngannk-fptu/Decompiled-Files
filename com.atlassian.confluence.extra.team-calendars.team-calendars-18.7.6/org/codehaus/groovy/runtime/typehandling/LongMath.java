/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.typehandling;

import org.codehaus.groovy.runtime.typehandling.BigDecimalMath;
import org.codehaus.groovy.runtime.typehandling.NumberMath;

public final class LongMath
extends NumberMath {
    public static final LongMath INSTANCE = new LongMath();

    private LongMath() {
    }

    @Override
    protected Number absImpl(Number number) {
        return Math.abs(number.longValue());
    }

    @Override
    public Number addImpl(Number left, Number right) {
        return left.longValue() + right.longValue();
    }

    @Override
    public Number subtractImpl(Number left, Number right) {
        return left.longValue() - right.longValue();
    }

    @Override
    public Number multiplyImpl(Number left, Number right) {
        return left.longValue() * right.longValue();
    }

    @Override
    public Number divideImpl(Number left, Number right) {
        return BigDecimalMath.INSTANCE.divideImpl(left, right);
    }

    @Override
    public int compareToImpl(Number left, Number right) {
        long rightVal;
        long leftVal = left.longValue();
        return leftVal < (rightVal = right.longValue()) ? -1 : (leftVal == rightVal ? 0 : 1);
    }

    @Override
    protected Number intdivImpl(Number left, Number right) {
        return left.longValue() / right.longValue();
    }

    @Override
    protected Number modImpl(Number left, Number right) {
        return left.longValue() % right.longValue();
    }

    @Override
    protected Number unaryMinusImpl(Number left) {
        return -left.longValue();
    }

    @Override
    protected Number unaryPlusImpl(Number left) {
        return left.longValue();
    }

    @Override
    protected Number bitwiseNegateImpl(Number left) {
        return left.longValue() ^ 0xFFFFFFFFFFFFFFFFL;
    }

    @Override
    protected Number orImpl(Number left, Number right) {
        return left.longValue() | right.longValue();
    }

    @Override
    protected Number andImpl(Number left, Number right) {
        return left.longValue() & right.longValue();
    }

    @Override
    protected Number xorImpl(Number left, Number right) {
        return left.longValue() ^ right.longValue();
    }

    @Override
    protected Number leftShiftImpl(Number left, Number right) {
        return left.longValue() << (int)right.longValue();
    }

    @Override
    protected Number rightShiftImpl(Number left, Number right) {
        return left.longValue() >> (int)right.longValue();
    }

    @Override
    protected Number rightShiftUnsignedImpl(Number left, Number right) {
        return left.longValue() >>> (int)right.longValue();
    }

    protected Number bitAndImpl(Number left, Number right) {
        return left.longValue() & right.longValue();
    }
}

