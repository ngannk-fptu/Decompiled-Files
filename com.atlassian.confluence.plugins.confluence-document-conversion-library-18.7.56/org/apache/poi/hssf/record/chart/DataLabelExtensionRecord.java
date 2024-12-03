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

public final class DataLabelExtensionRecord
extends StandardRecord {
    public static final short sid = 2154;
    private int rt;
    private int grbitFrt;
    private final byte[] unused = new byte[8];

    public DataLabelExtensionRecord(DataLabelExtensionRecord other) {
        super(other);
        this.rt = other.rt;
        this.grbitFrt = other.grbitFrt;
        System.arraycopy(other.unused, 0, this.unused, 0, this.unused.length);
    }

    public DataLabelExtensionRecord(RecordInputStream in) {
        this.rt = in.readShort();
        this.grbitFrt = in.readShort();
        in.readFully(this.unused);
    }

    @Override
    protected int getDataSize() {
        return 12;
    }

    @Override
    public short getSid() {
        return 2154;
    }

    @Override
    protected void serialize(LittleEndianOutput out) {
        out.writeShort(this.rt);
        out.writeShort(this.grbitFrt);
        out.write(this.unused);
    }

    @Override
    public DataLabelExtensionRecord copy() {
        return new DataLabelExtensionRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.DATA_LABEL_EXTENSION;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("rt", () -> this.rt, "grbitFrt", () -> this.grbitFrt, "unused", () -> this.unused);
    }
}

