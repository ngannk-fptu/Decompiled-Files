/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;

public final class EndSubRecord
extends SubRecord {
    public static final short sid = 0;
    private static final int ENCODED_SIZE = 0;

    public EndSubRecord() {
    }

    public EndSubRecord(LittleEndianInput in, int size) {
        this(in, size, -1);
    }

    EndSubRecord(LittleEndianInput in, int size, int cmoOt) {
        if ((size & 0xFF) != 0) {
            throw new RecordFormatException("Unexpected size (" + size + ")");
        }
    }

    @Override
    public boolean isTerminating() {
        return true;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(0);
        out.writeShort(0);
    }

    @Override
    protected int getDataSize() {
        return 0;
    }

    public short getSid() {
        return 0;
    }

    @Override
    public EndSubRecord copy() {
        return new EndSubRecord();
    }

    @Override
    public SubRecord.SubRecordTypes getGenericRecordType() {
        return SubRecord.SubRecordTypes.END;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }
}

