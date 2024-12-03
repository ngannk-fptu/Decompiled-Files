/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Baseifs;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public final class Averageifs
extends Baseifs {
    public static final FreeRefFunction instance = new Averageifs();

    @Override
    protected boolean hasInitialRange() {
        return true;
    }

    @Override
    protected Baseifs.Aggregator createAggregator() {
        return new Baseifs.Aggregator(){
            Double sum = 0.0;
            Integer count = 0;

            @Override
            public void addValue(ValueEval value) {
                if (!(value instanceof NumberEval)) {
                    return;
                }
                double d = ((NumberEval)value).getNumberValue();
                this.sum = this.sum + d;
                Integer n = this.count;
                Integer n2 = this.count = Integer.valueOf(this.count + 1);
            }

            @Override
            public ValueEval getResult() {
                return this.count == 0 ? ErrorEval.DIV_ZERO : new NumberEval(this.sum / (double)this.count.intValue());
            }
        };
    }
}

