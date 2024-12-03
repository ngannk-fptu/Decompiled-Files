/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Baseifs;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public final class Maxifs
extends Baseifs {
    public static final FreeRefFunction instance = new Maxifs();

    @Override
    protected boolean hasInitialRange() {
        return true;
    }

    @Override
    protected Baseifs.Aggregator createAggregator() {
        return new Baseifs.Aggregator(){
            Double accumulator = null;

            @Override
            public void addValue(ValueEval value) {
                double d;
                double d2 = d = value instanceof NumberEval ? ((NumberEval)value).getNumberValue() : 0.0;
                if (this.accumulator == null || this.accumulator < d) {
                    this.accumulator = d;
                }
            }

            @Override
            public ValueEval getResult() {
                return new NumberEval(this.accumulator == null ? 0.0 : this.accumulator);
            }
        };
    }
}

