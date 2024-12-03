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

public final class BackupRecord
extends StandardRecord {
    public static final short sid = 64;
    private short field_1_backup;

    public BackupRecord() {
    }

    public BackupRecord(BackupRecord other) {
        super(other);
        this.field_1_backup = other.field_1_backup;
    }

    public BackupRecord(RecordInputStream in) {
        this.field_1_backup = in.readShort();
    }

    public void setBackup(short backup) {
        this.field_1_backup = backup;
    }

    public short getBackup() {
        return this.field_1_backup;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getBackup());
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 64;
    }

    @Override
    public BackupRecord copy() {
        return new BackupRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.BACKUP;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("backup", this::getBackup);
    }
}

