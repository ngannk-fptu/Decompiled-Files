/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;

public final class FtCblsSubRecord
extends SubRecord {
    public static final short sid = 12;
    private static final int ENCODED_SIZE = 20;
    private final byte[] reserved;

    public FtCblsSubRecord() {
        this.reserved = new byte[20];
    }

    public FtCblsSubRecord(FtCblsSubRecord other) {
        super(other);
        this.reserved = (byte[])other.reserved.clone();
    }

    public FtCblsSubRecord(LittleEndianInput in, int size) {
        this(in, size, -1);
    }

    FtCblsSubRecord(LittleEndianInput in, int size, int cmoOt) {
        if (size != 20) {
            throw new RecordFormatException("Unexpected size (" + size + ")");
        }
        byte[] buf = IOUtils.safelyAllocate(size, 20);
        in.readFully(buf);
        this.reserved = buf;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(12);
        out.writeShort(this.reserved.length);
        out.write(this.reserved);
    }

    @Override
    protected int getDataSize() {
        return this.reserved.length;
    }

    public short getSid() {
        return 12;
    }

    @Override
    public FtCblsSubRecord copy() {
        return new FtCblsSubRecord(this);
    }

    @Override
    public SubRecord.SubRecordTypes getGenericRecordType() {
        return SubRecord.SubRecordTypes.FT_CBLS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("reserved", () -> this.reserved);
    }
}

