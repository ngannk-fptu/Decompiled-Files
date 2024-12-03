/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.typehandling;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.groovy.runtime.typehandling.BigDecimalMath;
import org.codehaus.groovy.runtime.typehandling.BigIntegerMath;
import org.codehaus.groovy.runtime.typehandling.FloatingPointMath;
import org.codehaus.groovy.runtime.typehandling.IntegerMath;
import org.codehaus.groovy.runtime.typehandling.LongMath;

public abstract class NumberMath {
    public static Number abs(Number number) {
        return NumberMath.getMath(number).absImpl(number);
    }

    public static Number add(Number left, Number right) {
        return NumberMath.getMath(left, right).addImpl(left, right);
    }

    public static Number subtract(Number left, Number right) {
        return NumberMath.getMath(left, right).subtractImpl(left, right);
    }

    public static Number multiply(Number left, Number right) {
        return NumberMath.getMath(left, right).multiplyImpl(left, right);
    }

    public static Number divide(Number left, Number right) {
        return NumberMath.getMath(left, right).divideImpl(left, right);
    }

    public static int compareTo(Number left, Number right) {
        return NumberMath.getMath(left, right).compareToImpl(left, right);
    }

    public static Number or(Number left, Number right) {
        return NumberMath.getMath(left, right).orImpl(left, right);
    }

    public static Number and(Number left, Number right) {
        return NumberMath.getMath(left, right).andImpl(left, right);
    }

    public static Number xor(Number left, Number right) {
        return NumberMath.getMath(left, right).xorImpl(left, right);
    }

    public static Number intdiv(Number left, Number right) {
        return NumberMath.getMath(left, right).intdivImpl(left, right);
    }

    public static Number mod(Number left, Number right) {
        return NumberMath.getMath(left, right).modImpl(left, right);
    }

    public static Number leftShift(Number left, Number right) {
        if (NumberMath.isFloatingPoint(right) || NumberMath.isBigDecimal(right)) {
            throw new UnsupportedOperationException("Shift distance must be an integral type, but " + right + " (" + right.getClass().getName() + ") was supplied");
        }
        return NumberMath.getMath(left).leftShiftImpl(left, right);
    }

    public static Number rightShift(Number left, Number right) {
        if (NumberMath.isFloatingPoint(right) || NumberMath.isBigDecimal(right)) {
            throw new UnsupportedOperationException("Shift distance must be an integral type, but " + right + " (" + right.getClass().getName() + ") was supplied");
        }
        return NumberMath.getMath(left).rightShiftImpl(left, right);
    }

    public static Number rightShiftUnsigned(Number left, Number right) {
        if (NumberMath.isFloatingPoint(right) || NumberMath.isBigDecimal(right)) {
            throw new UnsupportedOperationException("Shift distance must be an integral type, but " + right + " (" + right.getClass().getName() + ") was supplied");
        }
        return NumberMath.getMath(left).rightShiftUnsignedImpl(left, right);
    }

    public static Number bitwiseNegate(Number left) {
        return NumberMath.getMath(left).bitwiseNegateImpl(left);
    }

    public static Number unaryMinus(Number left) {
        return NumberMath.getMath(left).unaryMinusImpl(left);
    }

    public static Number unaryPlus(Number left) {
        return NumberMath.getMath(left).unaryPlusImpl(left);
    }

    public static boolean isFloatingPoint(Number number) {
        return number instanceof Double || number instanceof Float;
    }

    public static boolean isInteger(Number number) {
        return number instanceof Integer;
    }

    public static boolean isLong(Number number) {
        return number instanceof Long;
    }

    public static boolean isBigDecimal(Number number) {
        return number instanceof BigDecimal;
    }

    public static boolean isBigInteger(Number number) {
        return number instanceof BigInteger;
    }

    public static BigDecimal toBigDecimal(Number n) {
        return n instanceof BigDecimal ? (BigDecimal)n : new BigDecimal(n.toString());
    }

    public static BigInteger toBigInteger(Number n) {
        return n instanceof BigInteger ? (BigInteger)n : new BigInteger(n.toString());
    }

    public static NumberMath getMath(Number left, Number right) {
        if (NumberMath.isFloatingPoint(left) || NumberMath.isFloatingPoint(right)) {
            return FloatingPointMath.INSTANCE;
        }
        if (NumberMath.isBigDecimal(left) || NumberMath.isBigDecimal(right)) {
            return BigDecimalMath.INSTANCE;
        }
        if (NumberMath.isBigInteger(left) || NumberMath.isBigInteger(right)) {
            return BigIntegerMath.INSTANCE;
        }
        if (NumberMath.isLong(left) || NumberMath.isLong(right)) {
            return LongMath.INSTANCE;
        }
        return IntegerMath.INSTANCE;
    }

    private static NumberMath getMath(Number number) {
        if (NumberMath.isLong(number)) {
            return LongMath.INSTANCE;
        }
        if (NumberMath.isFloatingPoint(number)) {
            return FloatingPointMath.INSTANCE;
        }
        if (NumberMath.isBigDecimal(number)) {
            return BigDecimalMath.INSTANCE;
        }
        if (NumberMath.isBigInteger(number)) {
            return BigIntegerMath.INSTANCE;
        }
        return IntegerMath.INSTANCE;
    }

    protected abstract Number absImpl(Number var1);

    public abstract Number addImpl(Number var1, Number var2);

    public abstract Number subtractImpl(Number var1, Number var2);

    public abstract Number multiplyImpl(Number var1, Number var2);

    public abstract Number divideImpl(Number var1, Number var2);

    public abstract int compareToImpl(Number var1, Number var2);

    protected abstract Number unaryMinusImpl(Number var1);

    protected abstract Number unaryPlusImpl(Number var1);

    protected Number bitwiseNegateImpl(Number left) {
        throw this.createUnsupportedException("bitwiseNegate()", left);
    }

    protected Number orImpl(Number left, Number right) {
        throw this.createUnsupportedException("or()", left);
    }

    protected Number andImpl(Number left, Number right) {
        throw this.createUnsupportedException("and()", left);
    }

    protected Number xorImpl(Number left, Number right) {
        throw this.createUnsupportedException("xor()", left);
    }

    protected Number modImpl(Number left, Number right) {
        throw this.createUnsupportedException("mod()", left);
    }

    protected Number intdivImpl(Number left, Number right) {
        throw this.createUnsupportedException("intdiv()", left);
    }

    protected Number leftShiftImpl(Number left, Number right) {
        throw this.createUnsupportedException("leftShift()", left);
    }

    protected Number rightShiftImpl(Number left, Number right) {
        throw this.createUnsupportedException("rightShift()", left);
    }

    protected Number rightShiftUnsignedImpl(Number left, Number right) {
        throw this.createUnsupportedException("rightShiftUnsigned()", left);
    }

    protected UnsupportedOperationException createUnsupportedException(String operation, Number left) {
        return new UnsupportedOperationException("Cannot use " + operation + " on this number type: " + left.getClass().getName() + " with value: " + left);
    }
}

