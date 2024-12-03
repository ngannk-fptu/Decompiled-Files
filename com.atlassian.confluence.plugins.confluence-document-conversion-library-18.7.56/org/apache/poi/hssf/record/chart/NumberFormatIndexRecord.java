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

public final class NumberFormatIndexRecord
extends StandardRecord {
    public static final short sid = 4174;
    private short field_1_formatIndex;

    public NumberFormatIndexRecord() {
    }

    public NumberFormatIndexRecord(NumberFormatIndexRecord other) {
        super(other);
        this.field_1_formatIndex = other.field_1_formatIndex;
    }

    public NumberFormatIndexRecord(RecordInputStream in) {
        this.field_1_formatIndex = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_formatIndex);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 4174;
    }

    @Override
    public NumberFormatIndexRecord copy() {
        return new NumberFormatIndexRecord(this);
    }

    public short getFormatIndex() {
        return this.field_1_formatIndex;
    }

    public void setFormatIndex(short field_1_formatIndex) {
        this.field_1_formatIndex = field_1_formatIndex;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.NUMBER_FORMAT_INDEX;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("formatIndex", this::getFormatIndex);
    }
}

