/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.ArrayFunction;
import org.apache.poi.ss.formula.functions.Function;

public abstract class BooleanFunction
implements Function,
ArrayFunction {
    public static final Function AND = new BooleanFunction(){

        @Override
        protected boolean getInitialResultValue() {
            return true;
        }

        @Override
        protected boolean partialEvaluate(boolean cumulativeResult, boolean currentValue) {
            return cumulativeResult && currentValue;
        }
    };
    public static final Function OR = new BooleanFunction(){

        @Override
        protected boolean getInitialResultValue() {
            return false;
        }

        @Override
        protected boolean partialEvaluate(boolean cumulativeResult, boolean currentValue) {
            return cumulativeResult || currentValue;
        }
    };
    public static final Function FALSE = BooleanFunction::evaluateFalse;
    public static final Function TRUE = BooleanFunction::evaluateTrue;
    public static final Function NOT = BooleanFunction::evaluateNot;

    @Override
    public final ValueEval evaluate(ValueEval[] args, int srcRow, int srcCol) {
        boolean boolResult;
        if (args.length < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            boolResult = this.calculate(args);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return BoolEval.valueOf(boolResult);
    }

    private boolean calculate(ValueEval[] args) throws EvaluationException {
        boolean result = this.getInitialResultValue();
        boolean atLeastOneNonBlank = false;
        for (ValueEval arg : args) {
            Boolean tempVe;
            if (arg instanceof TwoDEval) {
                TwoDEval ae = (TwoDEval)arg;
                int height = ae.getHeight();
                int width = ae.getWidth();
                for (int rrIx = 0; rrIx < height; ++rrIx) {
                    for (int rcIx = 0; rcIx < width; ++rcIx) {
                        ValueEval ve = ae.getValue(rrIx, rcIx);
                        tempVe = OperandResolver.coerceValueToBoolean(ve, true);
                        if (tempVe == null) continue;
                        result = this.partialEvaluate(result, tempVe);
                        atLeastOneNonBlank = true;
                    }
                }
                continue;
            }
            if (arg instanceof RefEval) {
                RefEval re = (RefEval)arg;
                int firstSheetIndex = re.getFirstSheetIndex();
                int lastSheetIndex = re.getLastSheetIndex();
                for (int sIx = firstSheetIndex; sIx <= lastSheetIndex; ++sIx) {
                    ValueEval ve = re.getInnerValueEval(sIx);
                    tempVe = OperandResolver.coerceValueToBoolean(ve, true);
                    if (tempVe == null) continue;
                    result = this.partialEvaluate(result, tempVe);
                    atLeastOneNonBlank = true;
                }
                continue;
            }
            tempVe = arg == MissingArgEval.instance ? Boolean.valueOf(false) : OperandResolver.coerceValueToBoolean(arg, false);
            if (tempVe == null) continue;
            result = this.partialEvaluate(result, tempVe);
            atLeastOneNonBlank = true;
        }
        if (!atLeastOneNonBlank) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        return result;
    }

    protected abstract boolean getInitialResultValue();

    protected abstract boolean partialEvaluate(boolean var1, boolean var2);

    @Override
    public ValueEval evaluateArray(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        return this.evaluate(args, srcRowIndex, srcColumnIndex);
    }

    private static ValueEval evaluateFalse(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        return args.length != 0 ? ErrorEval.VALUE_INVALID : BoolEval.FALSE;
    }

    private static ValueEval evaluateTrue(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        return args.length != 0 ? ErrorEval.VALUE_INVALID : BoolEval.TRUE;
    }

    private static ValueEval evaluateNot(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length != 1) {
            return ErrorEval.VALUE_INVALID;
        }
        java.util.function.Function<ValueEval, ValueEval> notInner = va -> {
            try {
                ValueEval ve = OperandResolver.getSingleValue(va, srcRowIndex, srcColumnIndex);
                Boolean b = OperandResolver.coerceValueToBoolean(ve, false);
                boolean boolArgVal = b != null && b != false;
                return BoolEval.valueOf(!boolArgVal);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
        };
        return ArrayFunction._evaluateOneArrayArg(args[0], srcRowIndex, srcColumnIndex, notInner);
    }
}

