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

public final class PasswordRev4Record
extends StandardRecord {
    public static final short sid = 444;
    private int field_1_password;

    public PasswordRev4Record(int pw) {
        this.field_1_password = pw;
    }

    public PasswordRev4Record(PasswordRev4Record other) {
        super(other);
        this.field_1_password = other.field_1_password;
    }

    public PasswordRev4Record(RecordInputStream in) {
        this.field_1_password = in.readShort();
    }

    public void setPassword(short pw) {
        this.field_1_password = pw;
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
        return 444;
    }

    @Override
    public PasswordRev4Record copy() {
        return new PasswordRev4Record(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.PASSWORD_REV_4;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("password", () -> this.field_1_password);
    }
}

