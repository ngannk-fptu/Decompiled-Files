/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.functions.XYNumericFunction;

public final class Sumxmy2
extends XYNumericFunction {
    private static final XYNumericFunction.Accumulator XMinusYSquaredAccumulator = (x, y) -> {
        double xmy = x - y;
        return xmy * xmy;
    };

    @Override
    protected XYNumericFunction.Accumulator createAccumulator() {
        return XMinusYSquaredAccumulator;
    }
}

