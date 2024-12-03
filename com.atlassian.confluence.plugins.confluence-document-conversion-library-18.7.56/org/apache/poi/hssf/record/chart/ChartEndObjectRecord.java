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

public final class ChartEndObjectRecord
extends StandardRecord {
    public static final short sid = 2133;
    private short rt;
    private short grbitFrt;
    private short iObjectKind;
    private byte[] reserved;

    public ChartEndObjectRecord(ChartEndObjectRecord other) {
        super(other);
        this.rt = other.rt;
        this.grbitFrt = other.grbitFrt;
        this.iObjectKind = other.iObjectKind;
        this.reserved = other.reserved == null ? null : (byte[])other.reserved.clone();
    }

    public ChartEndObjectRecord(RecordInputStream in) {
        this.rt = in.readShort();
        this.grbitFrt = in.readShort();
        this.iObjectKind = in.readShort();
        this.reserved = new byte[6];
        if (in.available() != 0) {
            in.readFully(this.reserved);
        }
    }

    @Override
    protected int getDataSize() {
        return 12;
    }

    @Override
    public short getSid() {
        return 2133;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.rt);
        out.writeShort(this.grbitFrt);
        out.writeShort(this.iObjectKind);
        out.write(this.reserved);
    }

    @Override
    public ChartEndObjectRecord copy() {
        return new ChartEndObjectRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CHART_END_OBJECT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("rt", () -> this.rt, "grbitFrt", () -> this.grbitFrt, "iObjectKind", () -> this.iObjectKind, "reserved", () -> this.reserved);
    }
}

