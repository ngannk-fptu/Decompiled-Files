/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.util.LocaleUtil;

public final class NumberValueFunction
implements FreeRefFunction {
    public static final FreeRefFunction instance = new NumberValueFunction();

    private NumberValueFunction() {
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        Locale locale = LocaleUtil.getUserLocale();
        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(locale);
        String text = null;
        String decSep = String.valueOf(decimalFormatSymbols.getDecimalSeparator());
        String groupSep = String.valueOf(decimalFormatSymbols.getGroupingSeparator());
        double result = Double.NaN;
        ValueEval v1 = null;
        ValueEval v2 = null;
        ValueEval v3 = null;
        try {
            if (args.length == 1) {
                v1 = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
                text = OperandResolver.coerceValueToString(v1);
            } else if (args.length == 2) {
                v1 = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
                v2 = OperandResolver.getSingleValue(args[1], ec.getRowIndex(), ec.getColumnIndex());
                text = OperandResolver.coerceValueToString(v1);
                decSep = OperandResolver.coerceValueToString(v2).substring(0, 1);
            } else if (args.length == 3) {
                v1 = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
                v2 = OperandResolver.getSingleValue(args[1], ec.getRowIndex(), ec.getColumnIndex());
                v3 = OperandResolver.getSingleValue(args[2], ec.getRowIndex(), ec.getColumnIndex());
                text = OperandResolver.coerceValueToString(v1);
                decSep = OperandResolver.coerceValueToString(v2).substring(0, 1);
                groupSep = OperandResolver.coerceValueToString(v3).substring(0, 1);
            }
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        if ("".equals(text) || text == null) {
            text = "0";
        }
        text = text.replace(" ", "");
        String[] parts = text.split("[" + decSep + "]");
        String sigPart = "";
        String decPart = "";
        if (parts.length > 2) {
            return ErrorEval.VALUE_INVALID;
        }
        if (parts.length > 1) {
            sigPart = parts[0];
            decPart = parts[1];
            if (decPart.contains(groupSep)) {
                return ErrorEval.VALUE_INVALID;
            }
            sigPart = sigPart.replace(groupSep, "");
            text = sigPart + "." + decPart;
        } else if (parts.length > 0) {
            sigPart = parts[0];
            text = sigPart = sigPart.replace(groupSep, "");
        }
        int countPercent = 0;
        while (text.endsWith("%")) {
            ++countPercent;
            text = text.substring(0, text.length() - 1);
        }
        try {
            result = Double.parseDouble(text);
            NumberValueFunction.checkValue(result /= Math.pow(100.0, countPercent));
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        catch (Exception e) {
            return ErrorEval.VALUE_INVALID;
        }
        return new NumberEval(result);
    }

    private static void checkValue(double result) throws EvaluationException {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new EvaluationException(ErrorEval.NUM_ERROR);
        }
    }
}

