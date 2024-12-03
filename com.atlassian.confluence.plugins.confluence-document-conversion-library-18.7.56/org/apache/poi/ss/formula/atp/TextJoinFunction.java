/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.atp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.atp.ArgumentsEvaluator;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

final class TextJoinFunction
implements FreeRefFunction {
    public static final FreeRefFunction instance = new TextJoinFunction(ArgumentsEvaluator.instance);
    private ArgumentsEvaluator evaluator;

    private TextJoinFunction(ArgumentsEvaluator anEvaluator) {
        this.evaluator = anEvaluator;
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length < 3 || args.length > 254) {
            return ErrorEval.VALUE_INVALID;
        }
        int srcRowIndex = ec.getRowIndex();
        int srcColumnIndex = ec.getColumnIndex();
        try {
            List<ValueEval> delimiterArgs = this.getValues(args[0], srcRowIndex, srcColumnIndex, true);
            ValueEval ignoreEmptyArg = OperandResolver.getSingleValue(args[1], srcRowIndex, srcColumnIndex);
            boolean ignoreEmpty = OperandResolver.coerceValueToBoolean(ignoreEmptyArg, false);
            ArrayList<String> textValues = new ArrayList<String>();
            for (int i = 2; i < args.length; ++i) {
                List<ValueEval> textArgs = this.getValues(args[i], srcRowIndex, srcColumnIndex, false);
                Iterator<ValueEval> iterator = textArgs.iterator();
                while (iterator.hasNext()) {
                    ValueEval textArg = iterator.next();
                    String textValue = OperandResolver.coerceValueToString(textArg);
                    if (ignoreEmpty && (textValue == null || textValue.length() <= 0)) continue;
                    textValues.add(textValue);
                }
            }
            if (delimiterArgs.isEmpty()) {
                return new StringEval(String.join((CharSequence)"", textValues));
            }
            if (delimiterArgs.size() == 1) {
                String delimiter = this.laxValueToString(delimiterArgs.get(0));
                return new StringEval(String.join((CharSequence)delimiter, textValues));
            }
            ArrayList<String> delimiters = new ArrayList<String>();
            for (ValueEval delimiterArg : delimiterArgs) {
                delimiters.add(this.laxValueToString(delimiterArg));
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < textValues.size(); ++i) {
                if (i > 0) {
                    int delimiterIndex = (i - 1) % delimiters.size();
                    sb.append((String)delimiters.get(delimiterIndex));
                }
                sb.append((String)textValues.get(i));
            }
            return new StringEval(sb.toString());
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private String laxValueToString(ValueEval eval) {
        return eval instanceof MissingArgEval ? "" : OperandResolver.coerceValueToString(eval);
    }

    private List<ValueEval> getValues(ValueEval eval, int srcRowIndex, int srcColumnIndex, boolean lastRowOnly) throws EvaluationException {
        if (eval instanceof AreaEval) {
            int startRow;
            AreaEval ae = (AreaEval)eval;
            ArrayList<ValueEval> list = new ArrayList<ValueEval>();
            for (int r = startRow = lastRowOnly ? ae.getLastRow() : ae.getFirstRow(); r <= ae.getLastRow(); ++r) {
                for (int c = ae.getFirstColumn(); c <= ae.getLastColumn(); ++c) {
                    list.add(OperandResolver.getSingleValue(ae.getAbsoluteValue(r, c), r, c));
                }
            }
            return list;
        }
        return Collections.singletonList(OperandResolver.getSingleValue(eval, srcRowIndex, srcColumnIndex));
    }
}

