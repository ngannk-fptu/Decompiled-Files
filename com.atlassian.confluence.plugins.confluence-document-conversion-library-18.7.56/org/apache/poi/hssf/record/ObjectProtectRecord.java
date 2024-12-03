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

public final class ObjectProtectRecord
extends StandardRecord {
    public static final short sid = 99;
    private short field_1_protect;

    public ObjectProtectRecord() {
    }

    public ObjectProtectRecord(ObjectProtectRecord other) {
        super(other);
        this.field_1_protect = other.field_1_protect;
    }

    public ObjectProtectRecord(RecordInputStream in) {
        this.field_1_protect = in.readShort();
    }

    public void setProtect(boolean protect) {
        this.field_1_protect = (short)(protect ? 1 : 0);
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
        return 99;
    }

    @Override
    public ObjectProtectRecord copy() {
        return new ObjectProtectRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.OBJECT_PROTECT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("protect", this::getProtect);
    }
}

