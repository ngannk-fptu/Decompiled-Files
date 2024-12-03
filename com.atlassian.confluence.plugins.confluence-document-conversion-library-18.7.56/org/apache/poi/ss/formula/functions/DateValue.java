/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.time.DateTimeException;
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

public class DateValue
extends Fixed1ArgFunction {
    private static final Logger LOG = LogManager.getLogger(DateValue.class);

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval dateTextArg) {
        try {
            String dateText = OperandResolver.coerceValueToString(OperandResolver.getSingleValue(dateTextArg, srcRowIndex, srcColumnIndex));
            if (dateText == null || dateText.isEmpty()) {
                return BlankEval.instance;
            }
            return new NumberEval(DateUtil.getExcelDate(DateParser.parseLocalDate(dateText)));
        }
        catch (DateTimeException dte) {
            LOG.atInfo().log("Failed to parse date", (Object)dte);
            return ErrorEval.VALUE_INVALID;
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}

