/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.atp.ArgumentsEvaluator;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.functions.LookupUtils;

final class XMatchFunction
implements FreeRefFunction {
    public static final FreeRefFunction instance = new XMatchFunction(ArgumentsEvaluator.instance);
    private final ArgumentsEvaluator evaluator;

    private XMatchFunction(ArgumentsEvaluator anEvaluator) {
        this.evaluator = anEvaluator;
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        int srcRowIndex = ec.getRowIndex();
        int srcColumnIndex = ec.getColumnIndex();
        return this._evaluate(args, srcRowIndex, srcColumnIndex);
    }

    private ValueEval _evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length < 2) {
            return ErrorEval.VALUE_INVALID;
        }
        LookupUtils.MatchMode matchMode = LookupUtils.MatchMode.ExactMatch;
        if (args.length > 2) {
            try {
                ValueEval matchModeValue = OperandResolver.getSingleValue(args[2], srcRowIndex, srcColumnIndex);
                int matchInt = OperandResolver.coerceValueToInt(matchModeValue);
                matchMode = LookupUtils.matchMode(matchInt);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
            catch (Exception e) {
                return ErrorEval.VALUE_INVALID;
            }
        }
        LookupUtils.SearchMode searchMode = LookupUtils.SearchMode.IterateForward;
        if (args.length > 3) {
            try {
                ValueEval searchModeValue = OperandResolver.getSingleValue(args[3], srcRowIndex, srcColumnIndex);
                int searchInt = OperandResolver.coerceValueToInt(searchModeValue);
                searchMode = LookupUtils.searchMode(searchInt);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
            catch (Exception e) {
                return ErrorEval.VALUE_INVALID;
            }
        }
        return this.evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], matchMode, searchMode);
    }

    private ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval lookupEval, ValueEval indexEval, LookupUtils.MatchMode matchMode, LookupUtils.SearchMode searchMode) {
        try {
            ValueEval lookupValue = OperandResolver.getSingleValue(lookupEval, srcRowIndex, srcColumnIndex);
            TwoDEval tableArray = LookupUtils.resolveTableArrayArg(indexEval);
            LookupUtils.ValueVector vector = tableArray.isColumn() ? LookupUtils.createColumnVector(tableArray, 0) : LookupUtils.createRowVector(tableArray, 0);
            int matchedIdx = LookupUtils.xlookupIndexOfValue(lookupValue, vector, matchMode, searchMode);
            return new NumberEval((double)matchedIdx + 1.0);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}

