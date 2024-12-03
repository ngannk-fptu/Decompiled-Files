/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Function;

public class Rate
implements Function {
    private static final Logger LOG = LogManager.getLogger(Rate.class);

    @Override
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        double rate;
        if (args.length < 3) {
            return ErrorEval.VALUE_INVALID;
        }
        double future_val = 0.0;
        double type = 0.0;
        double estimate = 0.1;
        try {
            ValueEval v1 = OperandResolver.getSingleValue(args[0], srcRowIndex, srcColumnIndex);
            ValueEval v2 = OperandResolver.getSingleValue(args[1], srcRowIndex, srcColumnIndex);
            ValueEval v3 = OperandResolver.getSingleValue(args[2], srcRowIndex, srcColumnIndex);
            ValueEval v4 = null;
            if (args.length >= 4) {
                v4 = OperandResolver.getSingleValue(args[3], srcRowIndex, srcColumnIndex);
            }
            ValueEval v5 = null;
            if (args.length >= 5) {
                v5 = OperandResolver.getSingleValue(args[4], srcRowIndex, srcColumnIndex);
            }
            ValueEval v6 = null;
            if (args.length >= 6) {
                v6 = OperandResolver.getSingleValue(args[5], srcRowIndex, srcColumnIndex);
            }
            double periods = OperandResolver.coerceValueToDouble(v1);
            double payment = OperandResolver.coerceValueToDouble(v2);
            double present_val = OperandResolver.coerceValueToDouble(v3);
            if (args.length >= 4) {
                future_val = OperandResolver.coerceValueToDouble(v4);
            }
            if (args.length >= 5) {
                type = OperandResolver.coerceValueToDouble(v5);
            }
            if (args.length >= 6) {
                estimate = OperandResolver.coerceValueToDouble(v6);
            }
            rate = Rate.calculateRate(periods, payment, present_val, future_val, type, estimate);
            Rate.checkValue(rate);
        }
        catch (EvaluationException e) {
            LOG.atError().withThrowable(e).log("Can't evaluate rate function");
            return e.getErrorEval();
        }
        return new NumberEval(rate);
    }

    private static double _g_div_gp(double r, double n, double p, double x, double y, double w) {
        double t1 = Math.pow(r + 1.0, n);
        double t2 = Math.pow(r + 1.0, n - 1.0);
        return (y + t1 * x + p * (t1 - 1.0) * (r * w + 1.0) / r) / (n * t2 * x - p * (t1 - 1.0) * (r * w + 1.0) / (Math.pow(r, 2.0) + n * p * t2 * (r * w + 1.0) / r + p * (t1 - 1.0) * w / r));
    }

    static double calculateRate(double nper, double pmt, double pv, double fv, double type, double guess) {
        double tol = 1.0E-8;
        double maxiter = 100.0;
        double rn = guess;
        int iter = 0;
        boolean close = false;
        while ((double)iter < maxiter && !close) {
            double rnp1 = rn - Rate._g_div_gp(rn, nper, pmt, pv, fv, type);
            double diff = Math.abs(rnp1 - rn);
            close = diff < tol;
            ++iter;
            rn = rnp1;
        }
        if (!close) {
            return Double.NaN;
        }
        return rn;
    }

    static void checkValue(double result) throws EvaluationException {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new EvaluationException(ErrorEval.NUM_ERROR);
        }
    }
}

