/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

final class Ifs
implements FreeRefFunction {
    public static final FreeRefFunction instance = new Ifs();

    private Ifs() {
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length % 2 != 0) {
            return ErrorEval.VALUE_INVALID;
        }
        for (int i = 0; i < args.length; i += 2) {
            BoolEval logicalTest = (BoolEval)args[i];
            if (!logicalTest.getBooleanValue()) continue;
            return args[i + 1];
        }
        return ErrorEval.NA;
    }
}

