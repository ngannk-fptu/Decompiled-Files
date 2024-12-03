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

public final class CalcModeRecord
extends StandardRecord {
    public static final short sid = 13;
    public static final short MANUAL = 0;
    public static final short AUTOMATIC = 1;
    public static final short AUTOMATIC_EXCEPT_TABLES = -1;
    private short field_1_calcmode;

    public CalcModeRecord() {
    }

    public CalcModeRecord(CalcModeRecord other) {
        super(other);
        this.field_1_calcmode = other.field_1_calcmode;
    }

    public CalcModeRecord(RecordInputStream in) {
        this.field_1_calcmode = in.readShort();
    }

    public void setCalcMode(short calcmode) {
        this.field_1_calcmode = calcmode;
    }

    public short getCalcMode() {
        return this.field_1_calcmode;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getCalcMode());
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 13;
    }

    @Override
    public CalcModeRecord copy() {
        return new CalcModeRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CALC_MODE;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("calcMode", this::getCalcMode);
    }
}

