/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.typehandling;

import org.codehaus.groovy.runtime.typehandling.NumberMath;

public final class FloatingPointMath
extends NumberMath {
    public static final FloatingPointMath INSTANCE = new FloatingPointMath();

    private FloatingPointMath() {
    }

    @Override
    protected Number absImpl(Number number) {
        return new Double(Math.abs(number.doubleValue()));
    }

    @Override
    public Number addImpl(Number left, Number right) {
        return new Double(left.doubleValue() + right.doubleValue());
    }

    @Override
    public Number subtractImpl(Number left, Number right) {
        return new Double(left.doubleValue() - right.doubleValue());
    }

    @Override
    public Number multiplyImpl(Number left, Number right) {
        return new Double(left.doubleValue() * right.doubleValue());
    }

    @Override
    public Number divideImpl(Number left, Number right) {
        return new Double(left.doubleValue() / right.doubleValue());
    }

    @Override
    public int compareToImpl(Number left, Number right) {
        return Double.compare(left.doubleValue(), right.doubleValue());
    }

    @Override
    protected Number modImpl(Number left, Number right) {
        return new Double(left.doubleValue() % right.doubleValue());
    }

    @Override
    protected Number unaryMinusImpl(Number left) {
        return new Double(-left.doubleValue());
    }

    @Override
    protected Number unaryPlusImpl(Number left) {
        return new Double(left.doubleValue());
    }
}

