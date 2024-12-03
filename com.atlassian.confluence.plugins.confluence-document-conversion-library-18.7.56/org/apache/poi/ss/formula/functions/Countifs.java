/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Baseifs;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public class Countifs
extends Baseifs {
    public static final FreeRefFunction instance = new Countifs();

    @Override
    protected boolean hasInitialRange() {
        return false;
    }

    @Override
    protected Baseifs.Aggregator createAggregator() {
        return new Baseifs.Aggregator(){
            double accumulator = 0.0;

            @Override
            public void addValue(ValueEval value) {
                this.accumulator += 1.0;
            }

            @Override
            public ValueEval getResult() {
                return new NumberEval(this.accumulator);
            }
        };
    }
}

