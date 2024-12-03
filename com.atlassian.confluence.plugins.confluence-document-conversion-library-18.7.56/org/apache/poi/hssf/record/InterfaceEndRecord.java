/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.InterfaceHdrRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;

public final class InterfaceEndRecord
extends StandardRecord {
    public static final short sid = 226;
    public static final InterfaceEndRecord instance = new InterfaceEndRecord();

    private InterfaceEndRecord() {
    }

    public static Record create(RecordInputStream in) {
        switch (in.remaining()) {
            case 0: {
                return instance;
            }
            case 2: {
                return new InterfaceHdrRecord(in);
            }
        }
        throw new RecordFormatException("Invalid record data size: " + in.remaining());
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
        return 226;
    }

    @Override
    public InterfaceEndRecord copy() {
        return instance;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.INTERFACE_END;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }
}

