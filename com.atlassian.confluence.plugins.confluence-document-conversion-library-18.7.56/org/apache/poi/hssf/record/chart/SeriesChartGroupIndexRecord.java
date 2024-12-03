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

public final class SeriesChartGroupIndexRecord
extends StandardRecord {
    public static final short sid = 4165;
    private short field_1_chartGroupIndex;

    public SeriesChartGroupIndexRecord() {
    }

    public SeriesChartGroupIndexRecord(SeriesChartGroupIndexRecord other) {
        super(other);
        this.field_1_chartGroupIndex = other.field_1_chartGroupIndex;
    }

    public SeriesChartGroupIndexRecord(RecordInputStream in) {
        this.field_1_chartGroupIndex = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_chartGroupIndex);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 4165;
    }

    @Override
    public SeriesChartGroupIndexRecord copy() {
        return new SeriesChartGroupIndexRecord(this);
    }

    public short getChartGroupIndex() {
        return this.field_1_chartGroupIndex;
    }

    public void setChartGroupIndex(short field_1_chartGroupIndex) {
        this.field_1_chartGroupIndex = field_1_chartGroupIndex;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.SERIES_CHART_GROUP_INDEX;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("chartGroupIndex", this::getChartGroupIndex);
    }
}

