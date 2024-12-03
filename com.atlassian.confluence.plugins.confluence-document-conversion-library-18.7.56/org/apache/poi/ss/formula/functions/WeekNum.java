/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.functions.NumericFunction;
import org.apache.poi.ss.usermodel.DateUtil;

public class WeekNum
extends Fixed2ArgFunction
implements FreeRefFunction {
    public static final FreeRefFunction instance = new WeekNum();
    private static final NumberEval DEFAULT_RETURN_TYPE = new NumberEval(1.0);
    private static final HashSet<Integer> VALID_RETURN_TYPES = new HashSet<Integer>(Arrays.asList(1, 2, 11, 12, 13, 14, 15, 16, 17, 21));
    private WeekFields SUNDAY_START = WeekFields.of(DayOfWeek.SUNDAY, 1);
    private WeekFields MONDAY_START = WeekFields.of(DayOfWeek.MONDAY, 1);
    private WeekFields TUESDAY_START = WeekFields.of(DayOfWeek.TUESDAY, 1);
    private WeekFields WEDNESDAY_START = WeekFields.of(DayOfWeek.WEDNESDAY, 1);
    private WeekFields THURSDAY_START = WeekFields.of(DayOfWeek.THURSDAY, 1);
    private WeekFields FRIDAY_START = WeekFields.of(DayOfWeek.FRIDAY, 1);
    private WeekFields SATURDAY_START = WeekFields.of(DayOfWeek.SATURDAY, 1);

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval serialNumVE, ValueEval returnTypeVE) {
        int returnType;
        LocalDate localDate;
        double serialNum;
        try {
            serialNum = NumericFunction.singleOperandEvaluate(serialNumVE, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            Date dateToConvert = DateUtil.getJavaDate(serialNum, false);
            localDate = dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        catch (Exception e) {
            return ErrorEval.NUM_ERROR;
        }
        try {
            ValueEval ve = OperandResolver.getSingleValue(returnTypeVE, srcRowIndex, srcColumnIndex);
            returnType = ve instanceof MissingArgEval ? (int)DEFAULT_RETURN_TYPE.getNumberValue() : OperandResolver.coerceValueToInt(ve);
        }
        catch (EvaluationException e) {
            return ErrorEval.NUM_ERROR;
        }
        if (!VALID_RETURN_TYPES.contains(returnType)) {
            return ErrorEval.NUM_ERROR;
        }
        return new NumberEval(this.getWeekNo(localDate, returnType));
    }

    public int getWeekNo(LocalDate date, int weekStartOn) {
        if (weekStartOn == 1 || weekStartOn == 17) {
            return date.get(this.SUNDAY_START.weekOfYear());
        }
        if (weekStartOn == 2 || weekStartOn == 11) {
            return date.get(this.MONDAY_START.weekOfYear());
        }
        if (weekStartOn == 12) {
            return date.get(this.TUESDAY_START.weekOfYear());
        }
        if (weekStartOn == 13) {
            return date.get(this.WEDNESDAY_START.weekOfYear());
        }
        if (weekStartOn == 14) {
            return date.get(this.THURSDAY_START.weekOfYear());
        }
        if (weekStartOn == 15) {
            return date.get(this.FRIDAY_START.weekOfYear());
        }
        if (weekStartOn == 16) {
            return date.get(this.SATURDAY_START.weekOfYear());
        }
        return date.get(WeekFields.ISO.weekOfYear());
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length == 1) {
            return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], DEFAULT_RETURN_TYPE);
        }
        if (args.length == 2) {
            return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1]);
        }
        return ErrorEval.VALUE_INVALID;
    }
}

