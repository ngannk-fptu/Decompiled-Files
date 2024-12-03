/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.util.Map;

public interface PositionDependentRecord {
    public int getLastOnDiskOffset();

    public void setLastOnDiskOffset(int var1);

    public void updateOtherRecordReferences(Map<Integer, Integer> var1);
}

