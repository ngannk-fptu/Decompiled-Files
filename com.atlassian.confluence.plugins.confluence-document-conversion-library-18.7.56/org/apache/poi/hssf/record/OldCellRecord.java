/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;

public abstract class OldCellRecord
implements GenericRecord {
    private final short sid;
    private final boolean isBiff2;
    private final int field_1_row;
    private final short field_2_column;
    private int field_3_cell_attrs;
    private short field_3_xf_index;

    protected OldCellRecord(RecordInputStream in, boolean isBiff2) {
        this.sid = in.getSid();
        this.isBiff2 = isBiff2;
        this.field_1_row = in.readUShort();
        this.field_2_column = in.readShort();
        if (isBiff2) {
            this.field_3_cell_attrs = in.readUShort() << 8;
            this.field_3_cell_attrs += in.readUByte();
        } else {
            this.field_3_xf_index = in.readShort();
        }
    }

    public final int getRow() {
        return this.field_1_row;
    }

    public final short getColumn() {
        return this.field_2_column;
    }

    public final short getXFIndex() {
        return this.field_3_xf_index;
    }

    public int getCellAttrs() {
        return this.field_3_cell_attrs;
    }

    public boolean isBiff2() {
        return this.isBiff2;
    }

    public short getSid() {
        return this.sid;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("row", this::getRow, "column", this::getColumn, "biff2", this::isBiff2, "biff2CellAttrs", this::getCellAttrs, "xfIndex", this::getXFIndex);
    }

    public final String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }
}

