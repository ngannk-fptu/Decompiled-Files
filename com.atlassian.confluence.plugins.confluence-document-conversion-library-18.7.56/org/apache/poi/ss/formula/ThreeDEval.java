/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.SheetRange;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public interface ThreeDEval
extends TwoDEval,
SheetRange {
    public ValueEval getValue(int var1, int var2, int var3);
}

