/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import org.apache.poi.hslf.record.RecordContainer;

public interface ParentAwareRecord {
    public RecordContainer getParentRecord();

    public void setParentRecord(RecordContainer var1);
}

