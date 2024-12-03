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

public final class DBCellRecord
extends StandardRecord {
    public static final short sid = 215;
    public static final int BLOCK_SIZE = 32;
    private final int field_1_row_offset;
    private final short[] field_2_cell_offsets;

    public DBCellRecord(int rowOffset, short[] cellOffsets) {
        this.field_1_row_offset = rowOffset;
        this.field_2_cell_offsets = cellOffsets;
    }

    public DBCellRecord(RecordInputStream in) {
        this.field_1_row_offset = in.readUShort();
        int size = in.remaining();
        this.field_2_cell_offsets = new short[size / 2];
        for (int i = 0; i < this.field_2_cell_offsets.length; ++i) {
            this.field_2_cell_offsets[i] = in.readShort();
        }
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeInt(this.field_1_row_offset);
        for (short field_2_cell_offset : this.field_2_cell_offsets) {
            out.writeShort(field_2_cell_offset);
        }
    }

    @Override
    protected int getDataSize() {
        return 4 + this.field_2_cell_offsets.length * 2;
    }

    @Override
    public short getSid() {
        return 215;
    }

    @Override
    public DBCellRecord copy() {
        return this;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.DB_CELL;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("rowOffset", () -> this.field_1_row_offset, "cellOffsets", () -> this.field_2_cell_offsets);
    }
}

