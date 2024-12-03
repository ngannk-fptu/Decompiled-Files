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

public final class SeriesListRecord
extends StandardRecord {
    public static final short sid = 4118;
    private short[] field_1_seriesNumbers;

    public SeriesListRecord(SeriesListRecord other) {
        super(other);
        this.field_1_seriesNumbers = other.field_1_seriesNumbers == null ? null : (short[])other.field_1_seriesNumbers.clone();
    }

    public SeriesListRecord(short[] seriesNumbers) {
        this.field_1_seriesNumbers = seriesNumbers == null ? null : (short[])seriesNumbers.clone();
    }

    public SeriesListRecord(RecordInputStream in) {
        int nItems = in.readUShort();
        short[] ss = new short[nItems];
        for (int i = 0; i < nItems; ++i) {
            ss[i] = in.readShort();
        }
        this.field_1_seriesNumbers = ss;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        int nItems = this.field_1_seriesNumbers.length;
        out.writeShort(nItems);
        for (short field_1_seriesNumber : this.field_1_seriesNumbers) {
            out.writeShort(field_1_seriesNumber);
        }
    }

    @Override
    protected int getDataSize() {
        return this.field_1_seriesNumbers.length * 2 + 2;
    }

    @Override
    public short getSid() {
        return 4118;
    }

    @Override
    public SeriesListRecord copy() {
        return new SeriesListRecord(this);
    }

    public short[] getSeriesNumbers() {
        return this.field_1_seriesNumbers;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.SERIES_LIST;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("seriesNumbers", this::getSeriesNumbers);
    }
}

