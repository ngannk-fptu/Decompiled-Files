/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.atp.ArgumentsEvaluator;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.functions.PercentRank;

final class PercentRankIncFunction
implements FreeRefFunction {
    public static final FreeRefFunction instance = new PercentRankIncFunction(ArgumentsEvaluator.instance);
    private ArgumentsEvaluator evaluator;

    private PercentRankIncFunction(ArgumentsEvaluator anEvaluator) {
        this.evaluator = anEvaluator;
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        return PercentRank.instance.evaluate(args, ec.getRowIndex(), ec.getColumnIndex());
    }
}

