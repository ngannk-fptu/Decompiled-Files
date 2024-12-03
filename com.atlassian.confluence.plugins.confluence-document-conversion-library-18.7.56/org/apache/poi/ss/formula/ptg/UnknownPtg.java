/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.util.LittleEndianOutput;

public class UnknownPtg
extends Ptg {
    private final short size = 1;
    private final int _sid;

    public UnknownPtg(int sid) {
        this._sid = sid;
    }

    @Override
    public boolean isBaseToken() {
        return true;
    }

    @Override
    public void write(LittleEndianOutput out) {
        out.writeByte(this._sid);
    }

    @Override
    public byte getSid() {
        return (byte)this._sid;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public String toFormulaString() {
        return "UNKNOWN";
    }

    @Override
    public byte getDefaultOperandClass() {
        return 32;
    }

    @Override
    public UnknownPtg copy() {
        return this;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }
}

