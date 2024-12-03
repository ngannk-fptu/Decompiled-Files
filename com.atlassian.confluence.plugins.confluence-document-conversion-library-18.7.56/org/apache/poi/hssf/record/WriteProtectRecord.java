/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.LittleEndianOutput;

public final class WriteProtectRecord
extends StandardRecord {
    public static final short sid = 134;

    public WriteProtectRecord() {
    }

    public WriteProtectRecord(RecordInputStream in) {
        if (in.remaining() == 2) {
            in.readShort();
        }
    }

    @Override
    public void serialize(LittleEndianOutput out) {
    }

    @Override
    protected int getDataSize() {
        return 0;
    }

    @Override
    public short getSid() {
        return 134;
    }

    @Override
    public WriteProtectRecord copy() {
        return new WriteProtectRecord();
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.WRITE_PROTECT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }
}

