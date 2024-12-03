/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.apache.poi.hssf.record.RecordInputStream;

public final class HorizontalPageBreakRecord
extends PageBreakRecord {
    public static final short sid = 27;

    public HorizontalPageBreakRecord() {
    }

    public HorizontalPageBreakRecord(HorizontalPageBreakRecord other) {
        super(other);
    }

    public HorizontalPageBreakRecord(RecordInputStream in) {
        super(in);
    }

    @Override
    public short getSid() {
        return 27;
    }

    @Override
    public HorizontalPageBreakRecord copy() {
        return new HorizontalPageBreakRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.HORIZONTAL_PAGE_BREAK;
    }
}

