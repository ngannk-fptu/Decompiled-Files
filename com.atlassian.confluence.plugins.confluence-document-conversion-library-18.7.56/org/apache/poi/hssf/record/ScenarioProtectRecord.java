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

public final class ScenarioProtectRecord
extends StandardRecord {
    public static final short sid = 221;
    private short field_1_protect;

    public ScenarioProtectRecord() {
    }

    public ScenarioProtectRecord(ScenarioProtectRecord other) {
        super(other);
        this.field_1_protect = other.field_1_protect;
    }

    public ScenarioProtectRecord(RecordInputStream in) {
        this.field_1_protect = in.readShort();
    }

    public void setProtect(boolean protect) {
        this.field_1_protect = protect ? (short)1 : 0;
    }

    public boolean getProtect() {
        return this.field_1_protect == 1;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_protect);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 221;
    }

    @Override
    public ScenarioProtectRecord copy() {
        return new ScenarioProtectRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.SCENARIO_PROTECT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("protect", this::getProtect);
    }
}

