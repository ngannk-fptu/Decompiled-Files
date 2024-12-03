/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.IDStarAlgorithm;

public final class DCountA
implements IDStarAlgorithm {
    private long count;

    @Override
    public boolean processMatch(ValueEval eval) {
        if (!(eval instanceof BlankEval)) {
            ++this.count;
        }
        return true;
    }

    @Override
    public ValueEval getResult() {
        return new NumberEval(this.count);
    }

    @Override
    public boolean allowEmptyMatchField() {
        return true;
    }
}

