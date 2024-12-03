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

public final class PasswordRecord
extends StandardRecord {
    public static final short sid = 19;
    private int field_1_password;

    public PasswordRecord(int password) {
        this.field_1_password = password;
    }

    public PasswordRecord(PasswordRecord other) {
        super(other);
        this.field_1_password = other.field_1_password;
    }

    public PasswordRecord(RecordInputStream in) {
        this.field_1_password = in.readShort();
    }

    public void setPassword(int password) {
        this.field_1_password = password;
    }

    public int getPassword() {
        return this.field_1_password;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_password);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 19;
    }

    @Override
    public PasswordRecord copy() {
        return new PasswordRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.PASSWORD;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("password", this::getPassword);
    }
}

