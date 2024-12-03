/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ss.formula.ptg.Ptg;

public abstract class OperationPtg
extends Ptg {
    public static final int TYPE_UNARY = 0;
    public static final int TYPE_BINARY = 1;
    public static final int TYPE_FUNCTION = 2;

    protected OperationPtg() {
    }

    public abstract String toFormulaString(String[] var1);

    public abstract int getNumberOfOperands();

    @Override
    public byte getDefaultOperandClass() {
        return 32;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }
}

