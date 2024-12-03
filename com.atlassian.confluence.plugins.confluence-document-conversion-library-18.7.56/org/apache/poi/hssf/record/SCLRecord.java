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

public final class SCLRecord
extends StandardRecord {
    public static final short sid = 160;
    private short field_1_numerator;
    private short field_2_denominator;

    public SCLRecord() {
    }

    public SCLRecord(SCLRecord other) {
        super(other);
        this.field_1_numerator = other.field_1_numerator;
        this.field_2_denominator = other.field_2_denominator;
    }

    public SCLRecord(RecordInputStream in) {
        this.field_1_numerator = in.readShort();
        this.field_2_denominator = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_numerator);
        out.writeShort(this.field_2_denominator);
    }

    @Override
    protected int getDataSize() {
        return 4;
    }

    @Override
    public short getSid() {
        return 160;
    }

    @Override
    public SCLRecord copy() {
        return new SCLRecord(this);
    }

    public short getNumerator() {
        return this.field_1_numerator;
    }

    public void setNumerator(short field_1_numerator) {
        this.field_1_numerator = field_1_numerator;
    }

    public short getDenominator() {
        return this.field_2_denominator;
    }

    public void setDenominator(short field_2_denominator) {
        this.field_2_denominator = field_2_denominator;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.SCL;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("numerator", this::getNumerator, "denominator", this::getDenominator);
    }
}

