/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.atp.ArgumentsEvaluator;
import org.apache.poi.ss.formula.atp.WorkdayCalculator;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.usermodel.DateUtil;

final class WorkdayIntlFunction
implements FreeRefFunction {
    public static final FreeRefFunction instance = new WorkdayIntlFunction(ArgumentsEvaluator.instance);
    private ArgumentsEvaluator evaluator;

    private WorkdayIntlFunction(ArgumentsEvaluator anEvaluator) {
        this.evaluator = anEvaluator;
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length < 2 || args.length > 4) {
            return ErrorEval.VALUE_INVALID;
        }
        int srcCellRow = ec.getRowIndex();
        int srcCellCol = ec.getColumnIndex();
        int weekendType = 1;
        try {
            double start = this.evaluator.evaluateDateArg(args[0], srcCellRow, srcCellCol);
            int days = (int)Math.floor(this.evaluator.evaluateNumberArg(args[1], srcCellRow, srcCellCol));
            if (args.length >= 3) {
                if (args[2] != BlankEval.instance) {
                    weekendType = (int)this.evaluator.evaluateNumberArg(args[2], srcCellRow, srcCellCol);
                }
                if (!WorkdayCalculator.instance.getValidWeekendTypes().contains(weekendType)) {
                    return ErrorEval.NUM_ERROR;
                }
            }
            ValueEval holidaysCell = args.length >= 4 ? args[3] : null;
            double[] holidays = this.evaluator.evaluateDatesArg(holidaysCell, srcCellRow, srcCellCol);
            return new NumberEval(DateUtil.getExcelDate(WorkdayCalculator.instance.calculateWorkdays(start, days, weekendType, holidays)));
        }
        catch (EvaluationException e) {
            return ErrorEval.VALUE_INVALID;
        }
    }
}

