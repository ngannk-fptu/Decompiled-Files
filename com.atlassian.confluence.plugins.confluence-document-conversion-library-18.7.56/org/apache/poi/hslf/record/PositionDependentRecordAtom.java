/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.util.Map;
import org.apache.poi.hslf.record.PositionDependentRecord;
import org.apache.poi.hslf.record.RecordAtom;

public abstract class PositionDependentRecordAtom
extends RecordAtom
implements PositionDependentRecord {
    private int myLastOnDiskOffset;

    @Override
    public int getLastOnDiskOffset() {
        return this.myLastOnDiskOffset;
    }

    @Override
    public void setLastOnDiskOffset(int offset) {
        this.myLastOnDiskOffset = offset;
    }

    @Override
    public abstract void updateOtherRecordReferences(Map<Integer, Integer> var1);
}

