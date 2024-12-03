/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.Area2DPtgBase;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.util.LittleEndianInput;

public final class AreaPtg
extends Area2DPtgBase {
    public static final short sid = 37;

    public AreaPtg(int firstRow, int lastRow, int firstColumn, int lastColumn, boolean firstRowRelative, boolean lastRowRelative, boolean firstColRelative, boolean lastColRelative) {
        super(firstRow, lastRow, firstColumn, lastColumn, firstRowRelative, lastRowRelative, firstColRelative, lastColRelative);
    }

    public AreaPtg(AreaPtg other) {
        super(other);
    }

    public AreaPtg(LittleEndianInput in) {
        super(in);
    }

    public AreaPtg(AreaReference arearef) {
        super(arearef);
    }

    @Override
    public byte getSid() {
        return 37;
    }

    @Override
    public AreaPtg copy() {
        return new AreaPtg(this);
    }
}

