/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.chart;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class ChartStartBlockRecord
extends StandardRecord {
    public static final short sid = 2130;
    private short rt;
    private short grbitFrt;
    private short iObjectKind;
    private short iObjectContext;
    private short iObjectInstance1;
    private short iObjectInstance2;

    public ChartStartBlockRecord() {
    }

    public ChartStartBlockRecord(ChartStartBlockRecord other) {
        super(other);
        this.rt = other.rt;
        this.grbitFrt = other.grbitFrt;
        this.iObjectKind = other.iObjectKind;
        this.iObjectContext = other.iObjectContext;
        this.iObjectInstance1 = other.iObjectInstance1;
        this.iObjectInstance2 = other.iObjectInstance2;
    }

    public ChartStartBlockRecord(RecordInputStream in) {
        this.rt = in.readShort();
        this.grbitFrt = in.readShort();
        this.iObjectKind = in.readShort();
        this.iObjectContext = in.readShort();
        this.iObjectInstance1 = in.readShort();
        this.iObjectInstance2 = in.readShort();
    }

    @Override
    protected int getDataSize() {
        return 12;
    }

    @Override
    public short getSid() {
        return 2130;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.rt);
        out.writeShort(this.grbitFrt);
        out.writeShort(this.iObjectKind);
        out.writeShort(this.iObjectContext);
        out.writeShort(this.iObjectInstance1);
        out.writeShort(this.iObjectInstance2);
    }

    @Override
    public ChartStartBlockRecord copy() {
        return new ChartStartBlockRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CHART_START_BLOCK;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("rt", () -> this.rt, "grbitFrt", () -> this.grbitFrt, "iObjectKind", () -> this.iObjectKind, "iObjectContext", () -> this.iObjectContext, "iObjectInstance1", () -> this.iObjectInstance1, "iObjectInstance2", () -> this.iObjectInstance2);
    }
}

