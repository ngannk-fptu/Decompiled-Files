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

public final class RefModeRecord
extends StandardRecord {
    public static final short sid = 15;
    public static final short USE_A1_MODE = 1;
    public static final short USE_R1C1_MODE = 0;
    private short field_1_mode;

    public RefModeRecord() {
    }

    public RefModeRecord(RefModeRecord other) {
        this.field_1_mode = other.field_1_mode;
    }

    public RefModeRecord(RecordInputStream in) {
        this.field_1_mode = in.readShort();
    }

    public void setMode(short mode) {
        this.field_1_mode = mode;
    }

    public short getMode() {
        return this.field_1_mode;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getMode());
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 15;
    }

    @Override
    public RefModeRecord copy() {
        return new RefModeRecord();
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.REF_MODE;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("mode", this::getMode);
    }
}

