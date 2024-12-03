/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.OperationEvaluatorFactory;
import org.apache.poi.ss.formula.eval.FunctionNameEval;
import org.apache.poi.ss.formula.eval.NotImplementedFunctionException;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.ArrayFunction;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

final class UserDefinedFunction
implements FreeRefFunction {
    public static final FreeRefFunction instance = new UserDefinedFunction();

    private UserDefinedFunction() {
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        ArrayFunction func;
        ValueEval eval;
        int nIncomingArgs = args.length;
        if (nIncomingArgs < 1) {
            throw new RuntimeException("function name argument missing");
        }
        ValueEval nameArg = args[0];
        if (!(nameArg instanceof FunctionNameEval)) {
            throw new RuntimeException("First argument should be a NameEval, but got (" + nameArg.getClass().getName() + ")");
        }
        String functionName = ((FunctionNameEval)nameArg).getFunctionName();
        FreeRefFunction targetFunc = ec.findUserDefinedFunction(functionName);
        if (targetFunc == null) {
            throw new NotImplementedFunctionException(functionName);
        }
        int nOutGoingArgs = nIncomingArgs - 1;
        ValueEval[] outGoingArgs = new ValueEval[nOutGoingArgs];
        System.arraycopy(args, 1, outGoingArgs, 0, nOutGoingArgs);
        if (targetFunc instanceof ArrayFunction && (eval = OperationEvaluatorFactory.evaluateArrayFunction(func = (ArrayFunction)((Object)targetFunc), outGoingArgs, ec)) != null) {
            return eval;
        }
        return targetFunc.evaluate(outGoingArgs, ec);
    }
}

