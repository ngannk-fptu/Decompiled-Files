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

public final class VCenterRecord
extends StandardRecord {
    public static final short sid = 132;
    private int field_1_vcenter;

    public VCenterRecord() {
    }

    public VCenterRecord(VCenterRecord other) {
        super(other);
        this.field_1_vcenter = other.field_1_vcenter;
    }

    public VCenterRecord(RecordInputStream in) {
        this.field_1_vcenter = in.readShort();
    }

    public void setVCenter(boolean hc) {
        this.field_1_vcenter = hc ? 1 : 0;
    }

    public boolean getVCenter() {
        return this.field_1_vcenter == 1;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_vcenter);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 132;
    }

    @Override
    public VCenterRecord copy() {
        return new VCenterRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.V_CENTER;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("vcenter", this::getVCenter);
    }
}

