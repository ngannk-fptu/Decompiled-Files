/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.pivottable;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class StreamIDRecord
extends StandardRecord {
    public static final short sid = 213;
    private int idstm;

    public StreamIDRecord(StreamIDRecord other) {
        super(other);
        this.idstm = other.idstm;
    }

    public StreamIDRecord(RecordInputStream in) {
        this.idstm = in.readShort();
    }

    @Override
    protected void serialize(LittleEndianOutput out) {
        out.writeShort(this.idstm);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 213;
    }

    @Override
    public StreamIDRecord copy() {
        return new StreamIDRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.STREAM_ID;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("idstm", () -> this.idstm);
    }
}

