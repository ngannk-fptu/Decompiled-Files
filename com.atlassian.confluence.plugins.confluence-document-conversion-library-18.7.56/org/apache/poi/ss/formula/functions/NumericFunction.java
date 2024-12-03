/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.Log;
import org.apache.poi.ss.formula.functions.MathX;
import org.apache.poi.ss.formula.functions.Poisson;
import org.apache.poi.util.LocaleUtil;

public abstract class NumericFunction
implements Function {
    private static final double ZERO = 0.0;
    private static final double TEN = 10.0;
    private static final double LOG_10_TO_BASE_e = Math.log(10.0);
    private static final long PARITY_MASK = -2L;
    public static final Function ABS = NumericFunction.oneDouble(Math::abs);
    public static final Function ACOS = NumericFunction.oneDouble(Math::acos);
    public static final Function ACOSH = NumericFunction.oneDouble(MathX::acosh);
    public static final Function ASIN = NumericFunction.oneDouble(Math::asin);
    public static final Function ASINH = NumericFunction.oneDouble(MathX::asinh);
    public static final Function ATAN = NumericFunction.oneDouble(Math::atan);
    public static final Function ATANH = NumericFunction.oneDouble(MathX::atanh);
    public static final Function COS = NumericFunction.oneDouble(Math::cos);
    public static final Function COSH = NumericFunction.oneDouble(MathX::cosh);
    public static final Function DEGREES = NumericFunction.oneDouble(Math::toDegrees);
    public static final Function DOLLAR = NumericFunction::evaluateDollar;
    public static final Function EXP = NumericFunction.oneDouble(d -> Math.pow(Math.E, d));
    public static final Function FACT = NumericFunction.oneDouble(MathX::factorial);
    public static final Function INT = NumericFunction.oneDouble(d -> Math.round(d - 0.5));
    public static final Function LN = NumericFunction.oneDouble(Math::log);
    public static final Function LOG10 = NumericFunction.oneDouble(d -> Math.log(d) / LOG_10_TO_BASE_e);
    public static final Function RADIANS = NumericFunction.oneDouble(Math::toRadians);
    public static final Function SIGN = NumericFunction.oneDouble(MathX::sign);
    public static final Function SIN = NumericFunction.oneDouble(Math::sin);
    public static final Function SINH = NumericFunction.oneDouble(MathX::sinh);
    public static final Function SQRT = NumericFunction.oneDouble(Math::sqrt);
    public static final Function TAN = NumericFunction.oneDouble(Math::tan);
    public static final Function TANH = NumericFunction.oneDouble(MathX::tanh);
    public static final Function ATAN2 = NumericFunction.twoDouble((d0, d1) -> d0 == 0.0 && d1 == 0.0 ? ErrorEval.DIV_ZERO : Double.valueOf(Math.atan2(d1, d0)));
    public static final Function CEILING = NumericFunction.twoDouble(MathX::ceiling);
    public static final Function COMBIN = NumericFunction.twoDouble((d0, d1) -> d0 > 2.147483647E9 || d1 > 2.147483647E9 ? ErrorEval.NUM_ERROR : Double.valueOf(MathX.nChooseK((int)d0, (int)d1)));
    public static final Function FLOOR = NumericFunction.twoDouble((d0, d1) -> d1 == 0.0 ? (d0 == 0.0 ? Double.valueOf(0.0) : ErrorEval.DIV_ZERO) : Double.valueOf(MathX.floor(d0, d1)));
    public static final Function MOD = NumericFunction.twoDouble((d0, d1) -> d1 == 0.0 ? ErrorEval.DIV_ZERO : Double.valueOf(MathX.mod(d0, d1)));
    public static final Function POWER = NumericFunction.twoDouble(Math::pow);
    public static final Function ROUND = NumericFunction.twoDouble(MathX::round);
    public static final Function ROUNDDOWN = NumericFunction.twoDouble(MathX::roundDown);
    public static final Function ROUNDUP = NumericFunction.twoDouble(MathX::roundUp);
    public static final Function TRUNC = NumericFunction::evaluateTrunc;
    public static final Function LOG = Log::evaluate;
    static final NumberEval PI_EVAL = new NumberEval(Math.PI);
    public static final Function PI = NumericFunction::evaluatePI;
    public static final Function RAND = NumericFunction::evaluateRand;
    public static final Function POISSON = Poisson::evaluate;
    public static final Function ODD = NumericFunction.oneDouble(NumericFunction::evaluateOdd);
    public static final Function EVEN = NumericFunction.oneDouble(NumericFunction::evaluateEven);

    protected static double singleOperandEvaluate(ValueEval arg, int srcRowIndex, int srcColumnIndex) throws EvaluationException {
        if (arg == null) {
            throw new IllegalArgumentException("arg must not be null");
        }
        ValueEval ve = OperandResolver.getSingleValue(arg, srcRowIndex, srcColumnIndex);
        double result = OperandResolver.coerceValueToDouble(ve);
        NumericFunction.checkValue(result);
        return result;
    }

    public static void checkValue(double result) throws EvaluationException {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new EvaluationException(ErrorEval.NUM_ERROR);
        }
    }

    @Override
    public final ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
        double result;
        try {
            result = this.eval(args, srcCellRow, srcCellCol);
            NumericFunction.checkValue(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }

    protected abstract double eval(ValueEval[] var1, int var2, int var3) throws EvaluationException;

    private static ValueEval evaluateDollar(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length != 1 && args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            double val = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
            double d1 = args.length == 1 ? 2.0 : NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
            int nPlaces = (int)d1;
            if (nPlaces > 127) {
                return ErrorEval.VALUE_INVALID;
            }
            if (nPlaces < 0) {
                BigDecimal divisor = BigDecimal.valueOf(Math.pow(10.0, -nPlaces));
                BigInteger bigInt = BigDecimal.valueOf(val).divide(divisor, MathContext.DECIMAL128).toBigInteger().multiply(divisor.toBigInteger());
                val = bigInt.doubleValue();
            }
            DecimalFormat nf = (DecimalFormat)NumberFormat.getCurrencyInstance(LocaleUtil.getUserLocale());
            int decimalPlaces = Math.max(nPlaces, 0);
            if (LocaleUtil.getUserLocale().getCountry().equalsIgnoreCase("US")) {
                nf.setNegativePrefix("(" + nf.getDecimalFormatSymbols().getCurrencySymbol());
                nf.setNegativeSuffix(")");
            }
            nf.setMinimumFractionDigits(decimalPlaces);
            nf.setMaximumFractionDigits(decimalPlaces);
            return new StringEval(nf.format(val).replace("\u00a0", " "));
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static ValueEval evaluateTrunc(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length != 1 && args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            double d0 = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
            double d1 = args.length == 1 ? 0.0 : NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
            double result = MathX.roundDown(d0, d1);
            NumericFunction.checkValue(result);
            return new NumberEval(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static ValueEval evaluatePI(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        return args.length != 0 ? ErrorEval.VALUE_INVALID : PI_EVAL;
    }

    private static ValueEval evaluateRand(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        return args.length != 0 ? ErrorEval.VALUE_INVALID : new NumberEval(Math.random());
    }

    private static double evaluateOdd(double d) {
        if (d == 0.0) {
            return 1.0;
        }
        double dpm = Math.abs(d) + 1.0;
        long x = (long)dpm & 0xFFFFFFFFFFFFFFFEL;
        return (double)MathX.sign(d) * (double)(Double.compare(x, dpm) == 0 ? x - 1L : x + 1L);
    }

    private static double evaluateEven(double d) {
        if (d == 0.0) {
            return 0.0;
        }
        double dpm = Math.abs(d);
        long x = (long)dpm & 0xFFFFFFFFFFFFFFFEL;
        return (double)MathX.sign(d) * (double)(Double.compare(x, dpm) == 0 ? x : x + 2L);
    }

    private static Function oneDouble(OneDoubleIf doubleFun) {
        return (args, srcCellRow, srcCellCol) -> {
            if (args.length != 1) {
                return ErrorEval.VALUE_INVALID;
            }
            try {
                double d = NumericFunction.singleOperandEvaluate(args[0], srcCellRow, srcCellCol);
                double res = doubleFun.apply(d);
                return Double.isNaN(res) || Double.isInfinite(res) ? ErrorEval.NUM_ERROR : new NumberEval(res);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
        };
    }

    private static Function twoDouble(TwoDoubleIf doubleFun) {
        return (args, srcCellRow, srcCellCol) -> {
            if (args.length != 2) {
                return ErrorEval.VALUE_INVALID;
            }
            try {
                double d1 = NumericFunction.singleOperandEvaluate(args[0], srcCellRow, srcCellCol);
                double d2 = NumericFunction.singleOperandEvaluate(args[1], srcCellRow, srcCellCol);
                Object res = doubleFun.apply(d1, d2);
                if (res instanceof ErrorEval) {
                    return (ErrorEval)res;
                }
                assert (res instanceof Double);
                double d = (Double)res;
                return Double.isNaN(d) || Double.isInfinite(d) ? ErrorEval.NUM_ERROR : new NumberEval(d);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
        };
    }

    private static interface TwoDoubleIf {
        public Object apply(double var1, double var3);
    }

    private static interface OneDoubleIf {
        public double apply(double var1);
    }
}

