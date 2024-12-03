/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.atp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.atp.ArgumentsEvaluator;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.functions.PercentRank;

final class PercentRankExcFunction
implements FreeRefFunction {
    public static final FreeRefFunction instance = new PercentRankExcFunction(ArgumentsEvaluator.instance);
    private ArgumentsEvaluator evaluator;

    private PercentRankExcFunction(ArgumentsEvaluator anEvaluator) {
        this.evaluator = anEvaluator;
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        return this.evaluate(args, ec.getRowIndex(), ec.getColumnIndex());
    }

    private ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        double x;
        if (args.length < 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            ValueEval ev = OperandResolver.getSingleValue(args[1], srcRowIndex, srcColumnIndex);
            x = OperandResolver.coerceValueToDouble(ev);
        }
        catch (EvaluationException e) {
            ErrorEval error = e.getErrorEval();
            if (error == ErrorEval.NUM_ERROR) {
                return error;
            }
            return ErrorEval.NUM_ERROR;
        }
        ArrayList<Double> numbers = new ArrayList<Double>();
        try {
            List<ValueEval> values = PercentRank.getValues(args[0], srcRowIndex, srcColumnIndex);
            for (ValueEval ev : values) {
                if (ev instanceof BlankEval || ev instanceof MissingArgEval) continue;
                numbers.add(OperandResolver.coerceValueToDouble(ev));
            }
        }
        catch (EvaluationException e) {
            ErrorEval error = e.getErrorEval();
            if (error != ErrorEval.NA) {
                return error;
            }
            return ErrorEval.NUM_ERROR;
        }
        if (numbers.isEmpty()) {
            return ErrorEval.NUM_ERROR;
        }
        int significance = 3;
        if (args.length > 2) {
            try {
                ValueEval ev = OperandResolver.getSingleValue(args[2], srcRowIndex, srcColumnIndex);
                significance = OperandResolver.coerceValueToInt(ev);
                if (significance < 1) {
                    return ErrorEval.NUM_ERROR;
                }
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }
        return this.calculateRank(numbers, x, significance, true);
    }

    private ValueEval calculateRank(List<Double> numbers, double x, int significance, boolean recurse) {
        double closestMatchBelow = Double.MIN_VALUE;
        double closestMatchAbove = Double.MAX_VALUE;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        if (recurse) {
            for (Double d : numbers) {
                if (d <= x && d > closestMatchBelow) {
                    closestMatchBelow = d;
                }
                if (d > x && d < closestMatchAbove) {
                    closestMatchAbove = d;
                }
                if (d < min) {
                    min = d;
                }
                if (!(d > max)) continue;
                max = d;
            }
            if (x < min || x > max) {
                return ErrorEval.NA;
            }
        }
        if (!recurse || closestMatchBelow == x || closestMatchAbove == x) {
            int lessThanCount = 0;
            for (Double d : numbers) {
                if (!(d < x)) continue;
                ++lessThanCount;
            }
            BigDecimal bigDecimal = BigDecimal.valueOf((double)(lessThanCount + 1) / (double)(numbers.size() + 1));
            return new NumberEval(PercentRank.round(bigDecimal, significance));
        }
        int intermediateSignificance = significance < 5 ? 8 : significance + 3;
        ValueEval valueEval = this.calculateRank(numbers, closestMatchBelow, intermediateSignificance, false);
        if (!(valueEval instanceof NumberEval)) {
            return valueEval;
        }
        ValueEval aboveRank = this.calculateRank(numbers, closestMatchAbove, intermediateSignificance, false);
        if (!(aboveRank instanceof NumberEval)) {
            return aboveRank;
        }
        return PercentRank.interpolate(x, closestMatchBelow, closestMatchAbove, (NumberEval)valueEval, (NumberEval)aboveRank, significance);
    }
}

