/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.chart;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.LittleEndianOutput;

public final class PlotAreaRecord
extends StandardRecord {
    public static final short sid = 4149;

    public PlotAreaRecord() {
    }

    public PlotAreaRecord(RecordInputStream in) {
    }

    @Override
    public void serialize(LittleEndianOutput out) {
    }

    @Override
    protected int getDataSize() {
        return 0;
    }

    @Override
    public short getSid() {
        return 4149;
    }

    @Override
    public PlotAreaRecord copy() {
        return new PlotAreaRecord();
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.PLOT_AREA;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }
}

