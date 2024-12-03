/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.Area2DPtgBase;
import org.apache.poi.util.LittleEndianInput;

public final class AreaNPtg
extends Area2DPtgBase {
    public static final short sid = 45;

    public AreaNPtg(AreaNPtg other) {
        super(other);
    }

    public AreaNPtg(LittleEndianInput in) {
        super(in);
    }

    @Override
    public byte getSid() {
        return 45;
    }

    @Override
    public AreaNPtg copy() {
        return new AreaNPtg(this);
    }
}

