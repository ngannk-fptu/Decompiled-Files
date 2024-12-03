/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.functions.NumericFunction;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.DateParser;
import org.apache.poi.util.LocaleUtil;

public class Days
implements FreeRefFunction {
    public static final Days instance = new Days();

    private Days() {
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1]);
    }

    private ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        double result;
        try {
            LocalDate d0 = Days.getDate(arg0, srcRowIndex, srcColumnIndex);
            LocalDate d1 = Days.getDate(arg1, srcRowIndex, srcColumnIndex);
            result = Days.evaluate(d0, d1);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }

    private static double evaluate(LocalDate endDate, LocalDate startDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    static LocalDate getDate(ValueEval eval, int srcRowIndex, int srcColumnIndex) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(eval, srcRowIndex, srcColumnIndex);
        try {
            double d0 = NumericFunction.singleOperandEvaluate(ve, srcRowIndex, srcColumnIndex);
            return Days.getDate(d0);
        }
        catch (Exception e) {
            String strText1 = OperandResolver.coerceValueToString(ve);
            return DateParser.parseLocalDate(strText1);
        }
    }

    private static LocalDate getDate(double date) {
        Date d = DateUtil.getJavaDate(date, false);
        return d.toInstant().atZone(LocaleUtil.getUserTimeZone().toZoneId()).toLocalDate();
    }
}

