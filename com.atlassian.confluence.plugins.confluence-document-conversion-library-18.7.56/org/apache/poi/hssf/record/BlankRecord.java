/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class BlankRecord
extends StandardRecord
implements CellValueRecordInterface {
    public static final short sid = 513;
    private int field_1_row;
    private short field_2_col;
    private short field_3_xf;

    public BlankRecord() {
    }

    public BlankRecord(BlankRecord other) {
        super(other);
        this.field_1_row = other.field_1_row;
        this.field_2_col = other.field_2_col;
        this.field_3_xf = other.field_3_xf;
    }

    public BlankRecord(RecordInputStream in) {
        this.field_1_row = in.readUShort();
        this.field_2_col = in.readShort();
        this.field_3_xf = in.readShort();
    }

    @Override
    public void setRow(int row) {
        this.field_1_row = row;
    }

    @Override
    public int getRow() {
        return this.field_1_row;
    }

    @Override
    public short getColumn() {
        return this.field_2_col;
    }

    @Override
    public void setXFIndex(short xf) {
        this.field_3_xf = xf;
    }

    @Override
    public short getXFIndex() {
        return this.field_3_xf;
    }

    @Override
    public void setColumn(short col) {
        this.field_2_col = col;
    }

    @Override
    public short getSid() {
        return 513;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getRow());
        out.writeShort(this.getColumn());
        out.writeShort(this.getXFIndex());
    }

    @Override
    protected int getDataSize() {
        return 6;
    }

    @Override
    public BlankRecord copy() {
        return new BlankRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.BLANK;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("row", this::getRow, "col", this::getColumn, "xfIndex", this::getXFIndex);
    }
}

