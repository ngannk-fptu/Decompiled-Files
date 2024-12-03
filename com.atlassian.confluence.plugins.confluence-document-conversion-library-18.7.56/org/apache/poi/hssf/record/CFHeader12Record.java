/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.CFHeaderBase;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.common.FtrHeader;
import org.apache.poi.hssf.record.common.FutureRecord;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class CFHeader12Record
extends CFHeaderBase
implements FutureRecord {
    public static final short sid = 2169;
    private FtrHeader futureHeader;

    public CFHeader12Record() {
        this.createEmpty();
        this.futureHeader = new FtrHeader();
        this.futureHeader.setRecordType((short)2169);
    }

    public CFHeader12Record(CFHeader12Record other) {
        super(other);
        this.futureHeader = other.futureHeader.copy();
    }

    public CFHeader12Record(CellRangeAddress[] regions, int nRules) {
        super(regions, nRules);
        this.futureHeader = new FtrHeader();
        this.futureHeader.setRecordType((short)2169);
    }

    public CFHeader12Record(RecordInputStream in) {
        this.futureHeader = new FtrHeader(in);
        this.read(in);
    }

    @Override
    protected String getRecordName() {
        return "CFHEADER12";
    }

    @Override
    protected int getDataSize() {
        return FtrHeader.getDataSize() + super.getDataSize();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        this.futureHeader.setAssociatedRange(this.getEnclosingCellRange());
        this.futureHeader.serialize(out);
        super.serialize(out);
    }

    @Override
    public short getSid() {
        return 2169;
    }

    @Override
    public short getFutureRecordType() {
        return this.futureHeader.getRecordType();
    }

    @Override
    public FtrHeader getFutureHeader() {
        return this.futureHeader;
    }

    @Override
    public CellRangeAddress getAssociatedRange() {
        return this.futureHeader.getAssociatedRange();
    }

    @Override
    public CFHeader12Record copy() {
        return new CFHeader12Record(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CF_HEADER_12;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "futureHeader", this::getFutureHeader);
    }
}

