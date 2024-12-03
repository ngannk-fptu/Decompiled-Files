/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.IDStarAlgorithm;

public final class DProduct
implements IDStarAlgorithm {
    private double product;
    private boolean initDone = false;

    @Override
    public boolean processMatch(ValueEval eval) {
        if (eval instanceof NumericValueEval) {
            if (this.initDone) {
                this.product *= ((NumericValueEval)eval).getNumberValue();
            } else {
                this.product = ((NumericValueEval)eval).getNumberValue();
                this.initDone = true;
            }
        }
        return true;
    }

    @Override
    public ValueEval getResult() {
        return new NumberEval(this.product);
    }
}

