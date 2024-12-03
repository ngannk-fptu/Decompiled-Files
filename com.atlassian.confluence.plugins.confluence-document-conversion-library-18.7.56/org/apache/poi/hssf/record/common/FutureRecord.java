/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.common;

import org.apache.poi.hssf.record.common.FtrHeader;
import org.apache.poi.ss.util.CellRangeAddress;

public interface FutureRecord {
    public short getFutureRecordType();

    public FtrHeader getFutureHeader();

    public CellRangeAddress getAssociatedRange();
}

