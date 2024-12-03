/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.IDStarAlgorithm;
import org.apache.poi.ss.formula.functions.StatsLib;
import org.apache.poi.ss.util.NumberToTextConverter;

public final class DStdevp
implements IDStarAlgorithm {
    private final ArrayList<NumericValueEval> values = new ArrayList();

    @Override
    public boolean processMatch(ValueEval eval) {
        if (eval instanceof NumericValueEval) {
            this.values.add((NumericValueEval)eval);
        }
        return true;
    }

    @Override
    public ValueEval getResult() {
        double[] array = new double[this.values.size()];
        int pos = 0;
        for (NumericValueEval d : this.values) {
            array[pos++] = d.getNumberValue();
        }
        double stdev = StatsLib.stdevp(array);
        return new NumberEval(new BigDecimal(NumberToTextConverter.toText(stdev)).doubleValue());
    }
}

