/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.UserDefinedFunction;
import org.apache.poi.ss.formula.eval.ConcatEval;
import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.formula.eval.IntersectionEval;
import org.apache.poi.ss.formula.eval.PercentEval;
import org.apache.poi.ss.formula.eval.RangeEval;
import org.apache.poi.ss.formula.eval.RelationalOperationEval;
import org.apache.poi.ss.formula.eval.TwoOperandNumericOperation;
import org.apache.poi.ss.formula.eval.UnaryMinusEval;
import org.apache.poi.ss.formula.eval.UnaryPlusEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.ArrayFunction;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.Indirect;
import org.apache.poi.ss.formula.ptg.AbstractFunctionPtg;
import org.apache.poi.ss.formula.ptg.AddPtg;
import org.apache.poi.ss.formula.ptg.ConcatPtg;
import org.apache.poi.ss.formula.ptg.DividePtg;
import org.apache.poi.ss.formula.ptg.EqualPtg;
import org.apache.poi.ss.formula.ptg.GreaterEqualPtg;
import org.apache.poi.ss.formula.ptg.GreaterThanPtg;
import org.apache.poi.ss.formula.ptg.IntersectionPtg;
import org.apache.poi.ss.formula.ptg.LessEqualPtg;
import org.apache.poi.ss.formula.ptg.LessThanPtg;
import org.apache.poi.ss.formula.ptg.MultiplyPtg;
import org.apache.poi.ss.formula.ptg.NotEqualPtg;
import org.apache.poi.ss.formula.ptg.OperationPtg;
import org.apache.poi.ss.formula.ptg.PercentPtg;
import org.apache.poi.ss.formula.ptg.PowerPtg;
import org.apache.poi.ss.formula.ptg.RangePtg;
import org.apache.poi.ss.formula.ptg.SubtractPtg;
import org.apache.poi.ss.formula.ptg.UnaryMinusPtg;
import org.apache.poi.ss.formula.ptg.UnaryPlusPtg;
import org.apache.poi.ss.util.CellRangeAddress;

final class OperationEvaluatorFactory {
    private static final Map<Byte, Function> _instancesByPtgClass = OperationEvaluatorFactory.initialiseInstancesMap();

    private OperationEvaluatorFactory() {
    }

    private static Map<Byte, Function> initialiseInstancesMap() {
        HashMap<Byte, Function> m = new HashMap<Byte, Function>(32);
        m.put(AddPtg.instance.getSid(), TwoOperandNumericOperation.AddEval);
        m.put(SubtractPtg.instance.getSid(), TwoOperandNumericOperation.SubtractEval);
        m.put(MultiplyPtg.instance.getSid(), TwoOperandNumericOperation.MultiplyEval);
        m.put(DividePtg.instance.getSid(), TwoOperandNumericOperation.DivideEval);
        m.put(PowerPtg.instance.getSid(), TwoOperandNumericOperation.PowerEval);
        m.put(ConcatPtg.instance.getSid(), ConcatEval.instance);
        m.put(LessThanPtg.instance.getSid(), RelationalOperationEval.LessThanEval);
        m.put(LessEqualPtg.instance.getSid(), RelationalOperationEval.LessEqualEval);
        m.put(EqualPtg.instance.getSid(), RelationalOperationEval.EqualEval);
        m.put(GreaterEqualPtg.instance.getSid(), RelationalOperationEval.GreaterEqualEval);
        m.put(GreaterThanPtg.instance.getSid(), RelationalOperationEval.GreaterThanEval);
        m.put(NotEqualPtg.instance.getSid(), RelationalOperationEval.NotEqualEval);
        m.put(IntersectionPtg.instance.getSid(), IntersectionEval.instance);
        m.put(RangePtg.instance.getSid(), RangeEval.instance);
        m.put(UnaryPlusPtg.instance.getSid(), UnaryPlusEval.instance);
        m.put(UnaryMinusPtg.instance.getSid(), UnaryMinusEval.instance);
        m.put(PercentPtg.instance.getSid(), PercentEval.instance);
        return m;
    }

    public static ValueEval evaluate(OperationPtg ptg, ValueEval[] args, OperationEvaluationContext ec) {
        if (ptg == null) {
            throw new IllegalArgumentException("ptg must not be null");
        }
        Function result = _instancesByPtgClass.get(ptg.getSid());
        FreeRefFunction udfFunc = null;
        if (result == null && ptg instanceof AbstractFunctionPtg) {
            AbstractFunctionPtg fptg = (AbstractFunctionPtg)ptg;
            short functionIndex = fptg.getFunctionIndex();
            switch (functionIndex) {
                case 148: {
                    udfFunc = Indirect.instance;
                    break;
                }
                case 255: {
                    udfFunc = UserDefinedFunction.instance;
                    break;
                }
                default: {
                    result = FunctionEval.getBasicFunction(functionIndex);
                }
            }
        }
        if (result != null) {
            ArrayFunction func;
            ValueEval eval;
            if (result instanceof ArrayFunction && (eval = OperationEvaluatorFactory.evaluateArrayFunction(func = (ArrayFunction)((Object)result), args, ec)) != null) {
                return eval;
            }
            return result.evaluate(args, ec.getRowIndex(), ec.getColumnIndex());
        }
        if (udfFunc != null) {
            return udfFunc.evaluate(args, ec);
        }
        throw new RuntimeException("Unexpected operation ptg class (" + ptg.getClass().getName() + ")");
    }

    static ValueEval evaluateArrayFunction(ArrayFunction func, ValueEval[] args, OperationEvaluationContext ec) {
        EvaluationSheet evalSheet = ec.getWorkbook().getSheet(ec.getSheetIndex());
        EvaluationCell evalCell = evalSheet.getCell(ec.getRowIndex(), ec.getColumnIndex());
        if (evalCell != null) {
            if (evalCell.isPartOfArrayFormulaGroup()) {
                CellRangeAddress ca = evalCell.getArrayFormulaRange();
                return func.evaluateArray(args, ca.getFirstRow(), ca.getFirstColumn());
            }
            if (ec.isArraymode()) {
                return func.evaluateArray(args, ec.getRowIndex(), ec.getColumnIndex());
            }
        }
        return null;
    }
}

