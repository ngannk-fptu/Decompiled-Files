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

public final class MMSRecord
extends StandardRecord {
    public static final short sid = 193;
    private byte field_1_addMenuCount;
    private byte field_2_delMenuCount;

    public MMSRecord() {
    }

    public MMSRecord(MMSRecord other) {
        this.field_1_addMenuCount = other.field_1_addMenuCount;
        this.field_2_delMenuCount = other.field_2_delMenuCount;
    }

    public MMSRecord(RecordInputStream in) {
        if (in.remaining() == 0) {
            return;
        }
        this.field_1_addMenuCount = in.readByte();
        this.field_2_delMenuCount = in.readByte();
    }

    public void setAddMenuCount(byte am) {
        this.field_1_addMenuCount = am;
    }

    public void setDelMenuCount(byte dm) {
        this.field_2_delMenuCount = dm;
    }

    public byte getAddMenuCount() {
        return this.field_1_addMenuCount;
    }

    public byte getDelMenuCount() {
        return this.field_2_delMenuCount;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeByte(this.getAddMenuCount());
        out.writeByte(this.getDelMenuCount());
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 193;
    }

    @Override
    public MMSRecord copy() {
        return new MMSRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.MMS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("addMenuCount", this::getAddMenuCount, "delMenuCount", this::getDelMenuCount);
    }
}

