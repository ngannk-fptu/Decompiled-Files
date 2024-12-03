/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class TableStylesRecord
extends StandardRecord {
    public static final short sid = 2190;
    private int rt;
    private int grbitFrt;
    private final byte[] unused = new byte[8];
    private int cts;
    private String rgchDefListStyle;
    private String rgchDefPivotStyle;

    public TableStylesRecord(TableStylesRecord other) {
        super(other);
        this.rt = other.rt;
        this.grbitFrt = other.grbitFrt;
        System.arraycopy(other.unused, 0, this.unused, 0, this.unused.length);
        this.cts = other.cts;
        this.rgchDefListStyle = other.rgchDefListStyle;
        this.rgchDefPivotStyle = other.rgchDefPivotStyle;
    }

    public TableStylesRecord(RecordInputStream in) {
        this.rt = in.readUShort();
        this.grbitFrt = in.readUShort();
        in.readFully(this.unused);
        this.cts = in.readInt();
        int cchDefListStyle = in.readUShort();
        int cchDefPivotStyle = in.readUShort();
        this.rgchDefListStyle = in.readUnicodeLEString(cchDefListStyle);
        this.rgchDefPivotStyle = in.readUnicodeLEString(cchDefPivotStyle);
    }

    @Override
    protected void serialize(LittleEndianOutput out) {
        out.writeShort(this.rt);
        out.writeShort(this.grbitFrt);
        out.write(this.unused);
        out.writeInt(this.cts);
        out.writeShort(this.rgchDefListStyle.length());
        out.writeShort(this.rgchDefPivotStyle.length());
        StringUtil.putUnicodeLE(this.rgchDefListStyle, out);
        StringUtil.putUnicodeLE(this.rgchDefPivotStyle, out);
    }

    @Override
    protected int getDataSize() {
        return 20 + 2 * this.rgchDefListStyle.length() + 2 * this.rgchDefPivotStyle.length();
    }

    @Override
    public short getSid() {
        return 2190;
    }

    @Override
    public TableStylesRecord copy() {
        return new TableStylesRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.TABLE_STYLES;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("rt", () -> this.rt, "grbitFrt", () -> this.grbitFrt, "unused", () -> this.unused, "cts", () -> this.cts, "rgchDefListStyle", () -> this.rgchDefListStyle, "rgchDefPivotStyle", () -> this.rgchDefPivotStyle);
    }
}

