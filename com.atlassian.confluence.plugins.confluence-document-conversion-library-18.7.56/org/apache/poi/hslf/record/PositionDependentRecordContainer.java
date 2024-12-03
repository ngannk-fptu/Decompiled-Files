/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.util.Map;
import org.apache.poi.hslf.record.PositionDependentRecord;
import org.apache.poi.hslf.record.RecordContainer;

public abstract class PositionDependentRecordContainer
extends RecordContainer
implements PositionDependentRecord {
    private int sheetId;
    private int myLastOnDiskOffset;

    public int getSheetId() {
        return this.sheetId;
    }

    public void setSheetId(int id) {
        this.sheetId = id;
    }

    @Override
    public int getLastOnDiskOffset() {
        return this.myLastOnDiskOffset;
    }

    @Override
    public void setLastOnDiskOffset(int offset) {
        this.myLastOnDiskOffset = offset;
    }

    @Override
    public void updateOtherRecordReferences(Map<Integer, Integer> oldToNewReferencesLookup) {
    }
}

