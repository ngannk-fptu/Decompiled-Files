/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.CFHeaderBase;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.ss.util.CellRangeAddress;

public final class CFHeaderRecord
extends CFHeaderBase {
    public static final short sid = 432;

    public CFHeaderRecord() {
        this.createEmpty();
    }

    public CFHeaderRecord(CFHeaderRecord other) {
        super(other);
    }

    public CFHeaderRecord(CellRangeAddress[] regions, int nRules) {
        super(regions, nRules);
    }

    public CFHeaderRecord(RecordInputStream in) {
        this.read(in);
    }

    @Override
    protected String getRecordName() {
        return "CFHEADER";
    }

    @Override
    public short getSid() {
        return 432;
    }

    @Override
    public CFHeaderRecord copy() {
        return new CFHeaderRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CF_HEADER;
    }
}

