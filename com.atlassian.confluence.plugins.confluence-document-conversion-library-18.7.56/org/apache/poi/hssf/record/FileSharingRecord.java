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
import org.apache.poi.util.StringUtil;

public final class FileSharingRecord
extends StandardRecord {
    public static final short sid = 91;
    private short field_1_readonly;
    private short field_2_password;
    private byte field_3_username_unicode_options;
    private String field_3_username_value;

    public FileSharingRecord() {
    }

    public FileSharingRecord(FileSharingRecord other) {
        super(other);
        this.field_1_readonly = other.field_1_readonly;
        this.field_2_password = other.field_2_password;
        this.field_3_username_unicode_options = other.field_3_username_unicode_options;
        this.field_3_username_value = other.field_3_username_value;
    }

    public FileSharingRecord(RecordInputStream in) {
        this.field_1_readonly = in.readShort();
        this.field_2_password = in.readShort();
        short nameLen = in.readShort();
        if (nameLen > 0) {
            this.field_3_username_unicode_options = in.readByte();
            this.field_3_username_value = in.readCompressedUnicode(nameLen);
        } else {
            this.field_3_username_value = "";
        }
    }

    public void setReadOnly(short readonly) {
        this.field_1_readonly = readonly;
    }

    public short getReadOnly() {
        return this.field_1_readonly;
    }

    public void setPassword(short password) {
        this.field_2_password = password;
    }

    public short getPassword() {
        return this.field_2_password;
    }

    public String getUsername() {
        return this.field_3_username_value;
    }

    public void setUsername(String username) {
        this.field_3_username_value = username;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getReadOnly());
        out.writeShort(this.getPassword());
        out.writeShort(this.field_3_username_value.length());
        if (this.field_3_username_value.length() > 0) {
            out.writeByte(this.field_3_username_unicode_options);
            StringUtil.putCompressedUnicode(this.getUsername(), out);
        }
    }

    @Override
    protected int getDataSize() {
        int nameLen = this.field_3_username_value.length();
        if (nameLen < 1) {
            return 6;
        }
        return 7 + nameLen;
    }

    @Override
    public short getSid() {
        return 91;
    }

    @Override
    public FileSharingRecord copy() {
        return new FileSharingRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.FILE_SHARING;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("readOnly", this::getReadOnly, "password", this::getPassword, "username", this::getUsername);
    }
}

