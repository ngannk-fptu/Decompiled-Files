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

public final class SeriesIndexRecord
extends StandardRecord {
    public static final short sid = 4197;
    private short field_1_index;

    public SeriesIndexRecord() {
    }

    public SeriesIndexRecord(SeriesIndexRecord other) {
        super(other);
        this.field_1_index = other.field_1_index;
    }

    public SeriesIndexRecord(RecordInputStream in) {
        this.field_1_index = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_index);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 4197;
    }

    @Override
    public SeriesIndexRecord copy() {
        return new SeriesIndexRecord(this);
    }

    public short getIndex() {
        return this.field_1_index;
    }

    public void setIndex(short field_1_index) {
        this.field_1_index = field_1_index;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.SERIES_INDEX;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("index", this::getIndex);
    }
}

