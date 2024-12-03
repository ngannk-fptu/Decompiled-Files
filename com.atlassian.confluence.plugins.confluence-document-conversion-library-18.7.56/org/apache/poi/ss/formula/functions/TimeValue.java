/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.time.DateTimeException;
import java.time.LocalDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Fixed1ArgFunction;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.DateParser;

public class TimeValue
extends Fixed1ArgFunction {
    private static final Logger LOG = LogManager.getLogger(TimeValue.class);

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval dateTimeTextArg) {
        try {
            String dateTimeText = OperandResolver.coerceValueToString(OperandResolver.getSingleValue(dateTimeTextArg, srcRowIndex, srcColumnIndex));
            if (dateTimeText == null || dateTimeText.isEmpty()) {
                return BlankEval.instance;
            }
            try {
                return this.parseTimeFromDateTime(dateTimeText);
            }
            catch (Exception e) {
                try {
                    return this.parseTimeFromDateTime("1/01/2000 " + dateTimeText);
                }
                catch (Exception e2) {
                    LocalDate ld = DateParser.parseLocalDate(dateTimeText);
                    return new NumberEval(0.0);
                }
            }
        }
        catch (DateTimeException dte) {
            LOG.atInfo().log("Failed to parse date/time", (Object)dte);
            return ErrorEval.VALUE_INVALID;
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private NumberEval parseTimeFromDateTime(String dateTimeText) throws EvaluationException {
        double dateTimeValue = DateUtil.parseDateTime(dateTimeText);
        return new NumberEval(dateTimeValue - DateUtil.getExcelDate(DateParser.parseLocalDate(dateTimeText)));
    }
}

