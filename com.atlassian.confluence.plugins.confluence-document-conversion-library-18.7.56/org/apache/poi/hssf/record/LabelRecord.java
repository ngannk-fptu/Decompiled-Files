/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.RecordFormatException;

public final class LabelRecord
extends Record
implements CellValueRecordInterface {
    private static final Logger LOG = LogManager.getLogger(LabelRecord.class);
    public static final short sid = 516;
    private int field_1_row;
    private short field_2_column;
    private short field_3_xf_index;
    private short field_4_string_len;
    private byte field_5_unicode_flag;
    private String field_6_value;

    public LabelRecord() {
    }

    public LabelRecord(LabelRecord other) {
        super(other);
        this.field_1_row = other.field_1_row;
        this.field_2_column = other.field_2_column;
        this.field_3_xf_index = other.field_3_xf_index;
        this.field_4_string_len = other.field_4_string_len;
        this.field_5_unicode_flag = other.field_5_unicode_flag;
        this.field_6_value = other.field_6_value;
    }

    public LabelRecord(RecordInputStream in) {
        this.field_1_row = in.readUShort();
        this.field_2_column = in.readShort();
        this.field_3_xf_index = in.readShort();
        this.field_4_string_len = in.readShort();
        this.field_5_unicode_flag = in.readByte();
        this.field_6_value = this.field_4_string_len > 0 ? (this.isUnCompressedUnicode() ? in.readUnicodeLEString(this.field_4_string_len) : in.readCompressedUnicode(this.field_4_string_len)) : "";
        if (in.remaining() > 0) {
            LOG.atInfo().log("LabelRecord data remains: {} : {}", (Object)Unbox.box(in.remaining()), (Object)HexDump.toHex(in.readRemainder()));
        }
    }

    @Override
    public int getRow() {
        return this.field_1_row;
    }

    @Override
    public short getColumn() {
        return this.field_2_column;
    }

    @Override
    public short getXFIndex() {
        return this.field_3_xf_index;
    }

    public short getStringLength() {
        return this.field_4_string_len;
    }

    public boolean isUnCompressedUnicode() {
        return (this.field_5_unicode_flag & 1) != 0;
    }

    public String getValue() {
        return this.field_6_value;
    }

    @Override
    public int serialize(int offset, byte[] data) {
        throw new RecordFormatException("Label Records are supported READ ONLY...convert to LabelSST");
    }

    @Override
    public int getRecordSize() {
        throw new RecordFormatException("Label Records are supported READ ONLY...convert to LabelSST");
    }

    @Override
    public short getSid() {
        return 516;
    }

    @Override
    public void setColumn(short col) {
    }

    @Override
    public void setRow(int row) {
    }

    @Override
    public void setXFIndex(short xf) {
    }

    @Override
    public LabelRecord copy() {
        return new LabelRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.LABEL;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("row", this::getRow, "column", this::getColumn, "xfIndex", this::getXFIndex, "stringLen", this::getStringLength, "unCompressedUnicode", this::isUnCompressedUnicode, "value", this::getValue);
    }
}

