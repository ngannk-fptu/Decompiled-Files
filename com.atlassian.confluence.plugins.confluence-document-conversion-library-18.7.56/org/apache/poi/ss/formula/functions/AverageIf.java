/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Baseifs;
import org.apache.poi.ss.formula.functions.CountUtils;
import org.apache.poi.ss.formula.functions.Countif;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public class AverageIf
extends Baseifs {
    public static final FreeRefFunction instance = new AverageIf();

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length < 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            AreaEval sumRange = AverageIf.convertRangeArg(args[0]);
            if (args.length == 3) {
                sumRange = AverageIf.convertRangeArg(args[2]);
            }
            AreaEval ae = AverageIf.convertRangeArg(args[0]);
            CountUtils.I_MatchPredicate mp = Countif.createCriteriaPredicate(args[1], ec.getRowIndex(), ec.getColumnIndex());
            if (mp instanceof Countif.ErrorMatcher) {
                throw new EvaluationException(ErrorEval.valueOf(((Countif.ErrorMatcher)mp).getValue()));
            }
            return this.aggregateMatchingCells(this.createAggregator(), sumRange, ae, mp);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    protected ValueEval aggregateMatchingCells(Baseifs.Aggregator aggregator, AreaEval sumRange, AreaEval testRange, CountUtils.I_MatchPredicate mp) throws EvaluationException {
        int height = testRange.getHeight();
        int width = testRange.getWidth();
        for (int r = 0; r < height; ++r) {
            for (int c = 0; c < width; ++c) {
                ValueEval _testValue = testRange.getRelativeValue(r, c);
                ValueEval _sumValue = sumRange.getRelativeValue(r, c);
                if (mp == null || !mp.matches(_testValue)) continue;
                if (_testValue instanceof ErrorEval) {
                    throw new EvaluationException((ErrorEval)_testValue);
                }
                aggregator.addValue(_sumValue);
            }
        }
        return aggregator.getResult();
    }

    @Override
    protected boolean hasInitialRange() {
        return false;
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

