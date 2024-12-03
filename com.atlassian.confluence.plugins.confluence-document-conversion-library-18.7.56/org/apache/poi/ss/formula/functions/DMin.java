/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.IDStarAlgorithm;

public final class DMin
implements IDStarAlgorithm {
    private ValueEval minimumValue;

    @Override
    public boolean processMatch(ValueEval eval) {
        if (eval instanceof NumericValueEval) {
            if (this.minimumValue == null) {
                this.minimumValue = eval;
            } else {
                double oldValue;
                double currentValue = ((NumericValueEval)eval).getNumberValue();
                if (currentValue < (oldValue = ((NumericValueEval)this.minimumValue).getNumberValue())) {
                    this.minimumValue = eval;
                }
            }
        }
        return true;
    }

    @Override
    public ValueEval getResult() {
        if (this.minimumValue == null) {
            return NumberEval.ZERO;
        }
        return this.minimumValue;
    }
}

