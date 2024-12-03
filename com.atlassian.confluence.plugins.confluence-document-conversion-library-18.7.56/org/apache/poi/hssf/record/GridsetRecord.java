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

public final class GridsetRecord
extends StandardRecord {
    public static final short sid = 130;
    private short field_1_gridset_flag;

    public GridsetRecord() {
    }

    public GridsetRecord(GridsetRecord other) {
        super(other);
        this.field_1_gridset_flag = other.field_1_gridset_flag;
    }

    public GridsetRecord(RecordInputStream in) {
        this.field_1_gridset_flag = in.readShort();
    }

    public void setGridset(boolean gridset) {
        this.field_1_gridset_flag = gridset ? (short)1 : 0;
    }

    public boolean getGridset() {
        return this.field_1_gridset_flag == 1;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_gridset_flag);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 130;
    }

    @Override
    public GridsetRecord copy() {
        return new GridsetRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.GRIDSET;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("gridset", this::getGridset);
    }
}

