/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.functions.XYNumericFunction;

public final class Sumx2py2
extends XYNumericFunction {
    private static final XYNumericFunction.Accumulator XSquaredPlusYSquaredAccumulator = (x, y) -> x * x + y * y;

    @Override
    protected XYNumericFunction.Accumulator createAccumulator() {
        return XSquaredPlusYSquaredAccumulator;
    }
}

