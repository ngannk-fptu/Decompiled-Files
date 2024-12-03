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

public final class CatLabRecord
extends StandardRecord {
    public static final short sid = 2134;
    private short rt;
    private short grbitFrt;
    private short wOffset;
    private short at;
    private short grbit;
    private Short unused;

    public CatLabRecord(CatLabRecord other) {
        super(other);
        this.rt = other.rt;
        this.grbitFrt = other.grbitFrt;
        this.wOffset = other.wOffset;
        this.at = other.at;
        this.grbit = other.grbit;
        this.unused = other.unused;
    }

    public CatLabRecord(RecordInputStream in) {
        this.rt = in.readShort();
        this.grbitFrt = in.readShort();
        this.wOffset = in.readShort();
        this.at = in.readShort();
        this.grbit = in.readShort();
        this.unused = in.available() == 0 ? null : Short.valueOf(in.readShort());
    }

    @Override
    protected int getDataSize() {
        return 10 + (this.unused == null ? 0 : 2);
    }

    @Override
    public short getSid() {
        return 2134;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.rt);
        out.writeShort(this.grbitFrt);
        out.writeShort(this.wOffset);
        out.writeShort(this.at);
        out.writeShort(this.grbit);
        if (this.unused != null) {
            out.writeShort(this.unused.shortValue());
        }
    }

    @Override
    public CatLabRecord copy() {
        return new CatLabRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CAT_LAB;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("rt", () -> this.rt, "grbitFrt", () -> this.grbitFrt, "wOffset", () -> this.wOffset, "at", () -> this.at, "grbit", () -> this.grbit, "unused", () -> this.unused);
    }
}

