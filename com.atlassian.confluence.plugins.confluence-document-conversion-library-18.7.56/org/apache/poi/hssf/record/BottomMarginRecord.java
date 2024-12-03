/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.Margin;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class BottomMarginRecord
extends StandardRecord
implements Margin {
    public static final short sid = 41;
    private double field_1_margin;

    public BottomMarginRecord() {
    }

    public BottomMarginRecord(BottomMarginRecord other) {
        super(other);
        this.field_1_margin = other.field_1_margin;
    }

    public BottomMarginRecord(RecordInputStream in) {
        this.field_1_margin = in.readDouble();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeDouble(this.field_1_margin);
    }

    @Override
    protected int getDataSize() {
        return 8;
    }

    @Override
    public short getSid() {
        return 41;
    }

    @Override
    public double getMargin() {
        return this.field_1_margin;
    }

    @Override
    public void setMargin(double field_1_margin) {
        this.field_1_margin = field_1_margin;
    }

    @Override
    public BottomMarginRecord copy() {
        return new BottomMarginRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.BOTTOM_MARGIN;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("margin", this::getMargin);
    }
}

