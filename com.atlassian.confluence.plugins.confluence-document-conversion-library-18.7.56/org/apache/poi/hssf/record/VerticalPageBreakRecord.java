/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.apache.poi.hssf.record.RecordInputStream;

public final class VerticalPageBreakRecord
extends PageBreakRecord {
    public static final short sid = 26;

    public VerticalPageBreakRecord() {
    }

    public VerticalPageBreakRecord(VerticalPageBreakRecord other) {
        super(other);
    }

    public VerticalPageBreakRecord(RecordInputStream in) {
        super(in);
    }

    @Override
    public short getSid() {
        return 26;
    }

    @Override
    public VerticalPageBreakRecord copy() {
        return new VerticalPageBreakRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.VERTICAL_PAGE_BREAK;
    }
}

