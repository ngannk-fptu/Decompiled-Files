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

public final class HCenterRecord
extends StandardRecord {
    public static final short sid = 131;
    private short field_1_hcenter;

    public HCenterRecord() {
    }

    public HCenterRecord(HCenterRecord other) {
        super(other);
        this.field_1_hcenter = other.field_1_hcenter;
    }

    public HCenterRecord(RecordInputStream in) {
        this.field_1_hcenter = in.readShort();
    }

    public void setHCenter(boolean hc) {
        this.field_1_hcenter = (short)(hc ? 1 : 0);
    }

    public boolean getHCenter() {
        return this.field_1_hcenter == 1;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_hcenter);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 131;
    }

    @Override
    public HCenterRecord copy() {
        return new HCenterRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.H_CENTER;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("hcenter", this::getHCenter);
    }
}

