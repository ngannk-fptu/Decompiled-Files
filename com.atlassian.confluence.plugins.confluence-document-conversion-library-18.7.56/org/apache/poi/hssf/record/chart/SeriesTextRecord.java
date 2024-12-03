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
import org.apache.poi.util.StringUtil;

public final class SeriesTextRecord
extends StandardRecord {
    public static final short sid = 4109;
    private static final int MAX_LEN = 255;
    private int field_1_id;
    private boolean is16bit;
    private String field_4_text;

    public SeriesTextRecord() {
        this.field_4_text = "";
        this.is16bit = false;
    }

    public SeriesTextRecord(SeriesTextRecord other) {
        super(other);
        this.field_1_id = other.field_1_id;
        this.is16bit = other.is16bit;
        this.field_4_text = other.field_4_text;
    }

    public SeriesTextRecord(RecordInputStream in) {
        this.field_1_id = in.readUShort();
        int field_2_textLength = in.readUByte();
        this.is16bit = (in.readUByte() & 1) != 0;
        this.field_4_text = this.is16bit ? in.readUnicodeLEString(field_2_textLength) : in.readCompressedUnicode(field_2_textLength);
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_id);
        out.writeByte(this.field_4_text.length());
        if (this.is16bit) {
            out.writeByte(1);
            StringUtil.putUnicodeLE(this.field_4_text, out);
        } else {
            out.writeByte(0);
            StringUtil.putCompressedUnicode(this.field_4_text, out);
        }
    }

    @Override
    protected int getDataSize() {
        return 4 + this.field_4_text.length() * (this.is16bit ? 2 : 1);
    }

    @Override
    public short getSid() {
        return 4109;
    }

    @Override
    public SeriesTextRecord copy() {
        return new SeriesTextRecord(this);
    }

    public int getId() {
        return this.field_1_id;
    }

    public void setId(int id) {
        this.field_1_id = id;
    }

    public String getText() {
        return this.field_4_text;
    }

    public void setText(String text) {
        if (text.length() > 255) {
            throw new IllegalArgumentException("Text is too long (" + text.length() + ">" + 255 + ")");
        }
        this.field_4_text = text;
        this.is16bit = StringUtil.hasMultibyte(text);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.SERIES_TEXT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("id", this::getId, "bit16", () -> this.is16bit, "text", this::getText);
    }
}

