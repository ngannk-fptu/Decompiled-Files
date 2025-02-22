/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.formula.ThreeDEval;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.StringValueEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.DoubleList;

final class ArrayFunctionUtils {
    ArrayFunctionUtils() {
    }

    static List<DoubleList> getNumberArrays(ValueEval operand0, ValueEval operand1) throws EvaluationException {
        double[] retval1;
        double[] retval0 = ArrayFunctionUtils.collectValuesWithBlanks(operand0).toArray();
        if (retval0.length != (retval1 = ArrayFunctionUtils.collectValuesWithBlanks(operand1).toArray()).length) {
            throw new EvaluationException(ErrorEval.NA);
        }
        DoubleList filtered0 = new DoubleList();
        DoubleList filtered1 = new DoubleList();
        int len = Math.min(retval0.length, retval1.length);
        for (int i = 0; i < len; ++i) {
            if (Double.isNaN(retval0[i]) || Double.isNaN(retval1[i])) continue;
            filtered0.add(retval0[i]);
            filtered1.add(retval1[i]);
        }
        return Arrays.asList(filtered0, filtered1);
    }

    private static DoubleList collectValuesWithBlanks(ValueEval operand) throws EvaluationException {
        DoubleList doubleList = new DoubleList();
        if (operand instanceof ThreeDEval) {
            ThreeDEval ae = (ThreeDEval)operand;
            for (int sIx = ae.getFirstSheetIndex(); sIx <= ae.getLastSheetIndex(); ++sIx) {
                int width = ae.getWidth();
                int height = ae.getHeight();
                for (int rrIx = 0; rrIx < height; ++rrIx) {
                    for (int rcIx = 0; rcIx < width; ++rcIx) {
                        ValueEval ve = ae.getValue(sIx, rrIx, rcIx);
                        Double d = ArrayFunctionUtils.collectValue(ve);
                        if (d == null) {
                            doubleList.add(Double.NaN);
                            continue;
                        }
                        doubleList.add(d);
                    }
                }
            }
            return doubleList;
        }
        if (operand instanceof TwoDEval) {
            TwoDEval ae = (TwoDEval)operand;
            int width = ae.getWidth();
            int height = ae.getHeight();
            for (int rrIx = 0; rrIx < height; ++rrIx) {
                for (int rcIx = 0; rcIx < width; ++rcIx) {
                    ValueEval ve = ae.getValue(rrIx, rcIx);
                    Double d = ArrayFunctionUtils.collectValue(ve);
                    if (d == null) {
                        doubleList.add(Double.NaN);
                        continue;
                    }
                    doubleList.add(d);
                }
            }
            return doubleList;
        }
        if (operand instanceof RefEval) {
            RefEval re = (RefEval)operand;
            for (int sIx = re.getFirstSheetIndex(); sIx <= re.getLastSheetIndex(); ++sIx) {
                Double d = ArrayFunctionUtils.collectValue(re.getInnerValueEval(sIx));
                if (d == null) {
                    doubleList.add(Double.NaN);
                    continue;
                }
                doubleList.add(d);
            }
            return doubleList;
        }
        Double d = ArrayFunctionUtils.collectValue(operand);
        if (d == null) {
            doubleList.add(Double.NaN);
        } else {
            doubleList.add(d);
        }
        return doubleList;
    }

    private static Double collectValue(ValueEval ve) throws EvaluationException {
        if (ve == null) {
            throw new IllegalArgumentException("ve must not be null");
        }
        if (ve instanceof NumericValueEval) {
            NumericValueEval ne = (NumericValueEval)ve;
            return ne.getNumberValue();
        }
        if (ve instanceof StringValueEval) {
            String s = ((StringValueEval)ve).getStringValue().trim();
            return OperandResolver.parseDouble(s);
        }
        if (ve instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval)ve);
        }
        if (ve == BlankEval.instance) {
            return null;
        }
        throw new RuntimeException("Invalid ValueEval type passed for conversion: (" + ve.getClass() + ")");
    }
}

