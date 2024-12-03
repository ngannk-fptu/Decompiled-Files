/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.Ptg;

public abstract class ScalarConstantPtg
extends Ptg {
    @Override
    public final boolean isBaseToken() {
        return true;
    }

    @Override
    public final byte getDefaultOperandClass() {
        return 32;
    }
}

