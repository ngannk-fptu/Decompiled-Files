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

public final class ChartEndBlockRecord
extends StandardRecord {
    public static final short sid = 2131;
    private short rt;
    private short grbitFrt;
    private short iObjectKind;
    private byte[] unused;

    public ChartEndBlockRecord() {
    }

    public ChartEndBlockRecord(ChartEndBlockRecord other) {
        super(other);
        this.rt = other.rt;
        this.grbitFrt = other.grbitFrt;
        this.iObjectKind = other.iObjectKind;
        this.unused = other.unused == null ? null : (byte[])other.unused.clone();
    }

    public ChartEndBlockRecord(RecordInputStream in) {
        this.rt = in.readShort();
        this.grbitFrt = in.readShort();
        this.iObjectKind = in.readShort();
        if (in.available() == 0) {
            this.unused = new byte[0];
        } else {
            this.unused = new byte[6];
            in.readFully(this.unused);
        }
    }

    @Override
    protected int getDataSize() {
        return 6 + this.unused.length;
    }

    @Override
    public short getSid() {
        return 2131;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.rt);
        out.writeShort(this.grbitFrt);
        out.writeShort(this.iObjectKind);
        out.write(this.unused);
    }

    @Override
    public ChartEndBlockRecord copy() {
        return new ChartEndBlockRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CHART_END_BLOCK;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("rt", () -> this.rt, "grbitFrt", () -> this.grbitFrt, "iObjectKind", () -> this.iObjectKind, "unused", () -> this.unused);
    }
}

