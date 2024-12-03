/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.math3.stat.correlation.Covariance
 */
package org.apache.poi.ss.formula.functions;

import java.util.List;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.ArrayFunctionUtils;
import org.apache.poi.ss.formula.functions.DoubleList;
import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public class Covar
extends Fixed2ArgFunction
implements FreeRefFunction {
    public static final Covar instanceP = new Covar(false);
    public static final Covar instanceS = new Covar(true);
    private final boolean sampleBased;

    private Covar(boolean sampleBased) {
        this.sampleBased = sampleBased;
    }

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        try {
            List<DoubleList> arrays = ArrayFunctionUtils.getNumberArrays(arg0, arg1);
            Covariance covar = new Covariance();
            double result = covar.covariance(arrays.get(0).toArray(), arrays.get(1).toArray(), this.sampleBased);
            return new NumberEval(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        catch (Exception e) {
            return ErrorEval.NA;
        }
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1]);
    }
}

