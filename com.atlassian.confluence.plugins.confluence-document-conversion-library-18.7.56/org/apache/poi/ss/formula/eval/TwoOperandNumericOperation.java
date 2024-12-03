/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.eval;

import java.math.BigDecimal;
import java.math.MathContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.ArrayFunction;
import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;
import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.util.NumberToTextConverter;

public abstract class TwoOperandNumericOperation
extends Fixed2ArgFunction
implements ArrayFunction {
    public static final Function AddEval = new TwoOperandNumericOperation(){

        @Override
        protected double evaluate(double d0, double d1) {
            return d0 + d1;
        }
    };
    public static final Function DivideEval = new TwoOperandNumericOperation(){

        @Override
        protected double evaluate(double d0, double d1) throws EvaluationException {
            if (d1 == 0.0) {
                throw new EvaluationException(ErrorEval.DIV_ZERO);
            }
            BigDecimal bd0 = new BigDecimal(NumberToTextConverter.toText(d0));
            BigDecimal bd1 = new BigDecimal(NumberToTextConverter.toText(d1));
            return bd0.divide(bd1, MathContext.DECIMAL128).doubleValue();
        }
    };
    public static final Function MultiplyEval = new TwoOperandNumericOperation(){

        @Override
        protected double evaluate(double d0, double d1) {
            BigDecimal bd0 = new BigDecimal(NumberToTextConverter.toText(d0));
            BigDecimal bd1 = new BigDecimal(NumberToTextConverter.toText(d1));
            return bd0.multiply(bd1).doubleValue();
        }
    };
    public static final Function PowerEval = new TwoOperandNumericOperation(){

        @Override
        protected double evaluate(double d0, double d1) {
            if (d0 < 0.0 && Math.abs(d1) > 0.0 && Math.abs(d1) < 1.0) {
                return -1.0 * Math.pow(d0 * -1.0, d1);
            }
            return Math.pow(d0, d1);
        }
    };
    public static final Function SubtractEval = new SubtractEvalClass();

    protected final double singleOperandEvaluate(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToDouble(ve);
    }

    @Override
    public ValueEval evaluateArray(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluateTwoArrayArgs(args[0], args[1], srcRowIndex, srcColumnIndex, (vA, vB) -> {
            try {
                double d0 = OperandResolver.coerceValueToDouble(vA);
                double d1 = OperandResolver.coerceValueToDouble(vB);
                double result = this.evaluate(d0, d1);
                return new NumberEval(result);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
        });
    }

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        double result;
        try {
            double d0 = this.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            double d1 = this.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            result = this.evaluate(d0, d1);
            if (result == 0.0 && !(this instanceof SubtractEvalClass)) {
                return NumberEval.ZERO;
            }
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                return ErrorEval.NUM_ERROR;
            }
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }

    protected abstract double evaluate(double var1, double var3) throws EvaluationException;

    private static final class SubtractEvalClass
    extends TwoOperandNumericOperation {
        @Override
        protected double evaluate(double d0, double d1) {
            return d0 - d1;
        }
    }
}

