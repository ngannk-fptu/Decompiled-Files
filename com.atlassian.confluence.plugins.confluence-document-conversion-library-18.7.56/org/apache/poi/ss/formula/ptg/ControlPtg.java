/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.Ptg;

public abstract class ControlPtg
extends Ptg {
    protected ControlPtg() {
    }

    @Override
    public boolean isBaseToken() {
        return true;
    }

    @Override
    public final byte getDefaultOperandClass() {
        throw new IllegalStateException("Control tokens are not classified");
    }
}

