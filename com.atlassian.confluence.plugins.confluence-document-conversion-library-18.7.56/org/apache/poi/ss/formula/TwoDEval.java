/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.ValueEval;

public interface TwoDEval
extends ValueEval {
    public ValueEval getValue(int var1, int var2);

    public int getWidth();

    public int getHeight();

    default public boolean isRow() {
        return false;
    }

    public boolean isColumn();

    public TwoDEval getRow(int var1);

    public TwoDEval getColumn(int var1);

    public boolean isSubTotal(int var1, int var2);

    public boolean isRowHidden(int var1);
}

