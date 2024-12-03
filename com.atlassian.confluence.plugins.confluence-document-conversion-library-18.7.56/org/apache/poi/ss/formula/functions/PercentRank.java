/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.util.Internal;

public final class PercentRank
implements Function {
    public static final Function instance = new PercentRank();

    private PercentRank() {
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
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
        if (recurse) {
            for (Double d : numbers) {
                if (d <= x && d > closestMatchBelow) {
                    closestMatchBelow = d;
                }
                if (!(d > x) || !(d < closestMatchAbove)) continue;
                closestMatchAbove = d;
            }
        }
        if (!recurse || closestMatchBelow == x || closestMatchAbove == x) {
            int lessThanCount = 0;
            int greaterThanCount = 0;
            for (Double d : numbers) {
                if (d < x) {
                    ++lessThanCount;
                    continue;
                }
                if (!(d > x)) continue;
                ++greaterThanCount;
            }
            if (greaterThanCount == numbers.size() || lessThanCount == numbers.size()) {
                return ErrorEval.NA;
            }
            if (lessThanCount + greaterThanCount == 0) {
                return new NumberEval(0.0);
            }
            BigDecimal result = BigDecimal.valueOf((double)lessThanCount / (double)(lessThanCount + greaterThanCount));
            return new NumberEval(PercentRank.round(result, significance));
        }
        int intermediateSignificance = significance < 5 ? 8 : significance + 3;
        ValueEval belowRank = this.calculateRank(numbers, closestMatchBelow, intermediateSignificance, false);
        if (!(belowRank instanceof NumberEval)) {
            return belowRank;
        }
        ValueEval aboveRank = this.calculateRank(numbers, closestMatchAbove, intermediateSignificance, false);
        if (!(aboveRank instanceof NumberEval)) {
            return aboveRank;
        }
        return PercentRank.interpolate(x, closestMatchBelow, closestMatchAbove, (NumberEval)belowRank, (NumberEval)aboveRank, significance);
    }

    @Internal
    public static NumberEval interpolate(double x, double closestMatchBelow, double closestMatchAbove, NumberEval belowRank, NumberEval aboveRank, int significance) {
        double diff = closestMatchAbove - closestMatchBelow;
        double pos = x - closestMatchBelow;
        BigDecimal rankDiff = new BigDecimal(NumberToTextConverter.toText(aboveRank.getNumberValue() - belowRank.getNumberValue()));
        BigDecimal result = BigDecimal.valueOf(belowRank.getNumberValue()).add(rankDiff.multiply(BigDecimal.valueOf(pos / diff)));
        return new NumberEval(PercentRank.round(result, significance));
    }

    @Internal
    public static double round(BigDecimal bd, int significance) {
        BigDecimal bd2 = bd.setScale(significance + 3, RoundingMode.HALF_UP);
        return bd2.setScale(significance, RoundingMode.DOWN).doubleValue();
    }

    @Internal
    public static List<ValueEval> getValues(ValueEval eval, int srcRowIndex, int srcColumnIndex) throws EvaluationException {
        if (eval instanceof AreaEval) {
            AreaEval ae = (AreaEval)eval;
            ArrayList<ValueEval> list = new ArrayList<ValueEval>();
            for (int r = ae.getFirstRow(); r <= ae.getLastRow(); ++r) {
                for (int c = ae.getFirstColumn(); c <= ae.getLastColumn(); ++c) {
                    list.add(OperandResolver.getSingleValue(ae.getAbsoluteValue(r, c), r, c));
                }
            }
            return list;
        }
        return Collections.singletonList(OperandResolver.getSingleValue(eval, srcRowIndex, srcColumnIndex));
    }
}

